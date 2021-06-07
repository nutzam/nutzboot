package org.nutz.cloud.perca;

import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.proxy.AsyncMiddleManServlet;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author wentao
 * @title 自定义请求内容转换器，用于判断灰度请求转发（可用于请求监听、过滤、修改等功能）
 * @description
 * @package org.nutz.cloud.perca
 * @create 2021-06-05 下午9:16
 */
@IocBean(create = "init")
public class RequestContentTransformer implements AsyncMiddleManServlet.ContentTransformer {

    /**
     * 配置方法：
     * grayscale.switch=1
     * grayscale.host=192.168.1.1
     * grayscale.port=80
     * grayscale.keys=userA,userB
     * ## 以上配置，所有内容包含字符串userA userB的请求都会转发到192.168.1.1:80服务
     */

    private static final Log log = Logs.get();

    public static final String GRAYSCALE_SWITCH = "grayscale.switch";
    public static final String GRAYSCALE_HOST = "grayscale.host";
    public static final String GRAYSCALE_PORT = "grayscale.port";
    public static final String GRAYSCALE_KEYS = "grayscale.keys";
    @Inject
    protected PropertiesProxy conf;

    private int grayScale;
    private String grayScaleHost;
    private int grayScalePort;
    private String[] grayScaleKeys;
    private Request proxyRequest;

    public void init() {
        this.grayScale = conf.getInt(GRAYSCALE_SWITCH, 0);
        if (this.grayScale == 1) {
            this.grayScaleHost = conf.get(GRAYSCALE_HOST, "");
            this.grayScalePort = conf.getInt(GRAYSCALE_PORT, 0);
            this.grayScaleKeys = Strings.splitIgnoreBlank(conf.get(GRAYSCALE_KEYS, ""));
            if (Strings.isBlank(this.grayScaleHost)) {
                throwNotConfigException(GRAYSCALE_HOST);
            }
            if (grayScalePort == 0) {
                throwNotConfigException(GRAYSCALE_PORT);
            }
            if(this.grayScaleKeys.length == 0) {
                throwNotConfigException(GRAYSCALE_KEYS);
            }
        }
    }

    private void throwNotConfigException(String configName) {
        throw new RuntimeException(configName + " is not config...");
    }

    public void setProxyRequest(Request proxyRequest) {
        this.proxyRequest = proxyRequest;
    }

    @Override
    public void transform(ByteBuffer input, boolean finished, List<ByteBuffer> output) throws IOException {
        ByteBuffer copy = ByteBuffer.allocate(input.remaining());
        copy.put(input).flip();
        String inputString = new String(copy.array());
        // 判断是否需要灰度
        if (this.grayScale == 1) {
            for (String grayScaleKey : this.grayScaleKeys) {
                // queryString和inputString任意一项包含关键词都执行灰度重定向
                if(checkKeys(this.proxyRequest.getQuery(), grayScaleKey) || checkKeys(inputString, grayScaleKey)) {
                    proxyRequest.host(this.grayScaleHost);
                    proxyRequest.port(this.grayScalePort);
                    if(log.isDebugEnabled()) {
                        log.debugf("grayscale forward request to %s", proxyRequest.getURI().toString());
                    }
                }
            }
        }
        output.add(copy);
    }

    private boolean checkKeys(String data, String key) {
        return Strings.isNotBlank(data) && data.contains(key);
    }

}
