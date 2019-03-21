package org.nutz.boot.starter.fastdfs;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class FastdfsStarter {
    private static final String PRE = "fastdfs.";

    @PropDoc(value = "连接超时", defaultValue = "", type = "int")
    public static final String PROP_CONNECT_TIMEOUT_IN_SECONDS = PRE + "connect_timeout_in_seconds";

    @PropDoc(value = "传输超时", defaultValue = "", type = "int")
    public static final String PROP_NETWORK_TIMEOUT_IN_SECONDS = PRE + "network_timeout_in_seconds";

    @PropDoc(value = "网络超时", defaultValue = "", type = "string")
    public static final String PROP_CHARSET = PRE + "charset";

    @PropDoc(value = "Token访问限制是否启用", defaultValue = "", type = "boolean")
    public static final String PROP_HTTP_ANTI_STEAL_TOKEN = PRE + "http_anti_steal_token";

    @PropDoc(value = "Token密钥", defaultValue = "", type = "string")
    public static final String PROP_HTTP_SECRET_KEY = PRE + "http_secret_key";

    @PropDoc(value = "Token访问的根路径", defaultValue = "", type = "string")
    public static final String PROP_HTTP_TOKEN_BASE_URL = PRE + "http_token_base_url";

    @PropDoc(value = "访问端口", defaultValue = "", type = "int")
    public static final String PROP_HTTP_TRACKER_HTTP_PORT = PRE + "http_tracker_http_port";

    @PropDoc(value = "服务器IP", defaultValue = "", type = "string")
    public static final String PROP_TRACKER_SERVERS = PRE + "tracker_servers";

    @PropDoc(value = "连接池最大空闲数", defaultValue = "10", type = "int")
    public static final String PROP_POOL_MAXIDLE = PRE + "pool.maxIdle";

    @PropDoc(value = "连接池最小空闲数", defaultValue = "1", type = "int")
    public static final String PROP_POOL_MINIDLE = PRE + "pool.minIdle";

    @PropDoc(value = "连接池总数", defaultValue = "20", type = "int")
    public static final String PROP_POOL_MAXTOTAL = PRE + "pool.maxTotal";

    @PropDoc(value = "连接池超时时间", defaultValue = "6000", type = "int")
    public static final String PROP_POOL_MAXWAITMILLIS = PRE + "pool.maxWaitMillis";

    @PropDoc(value = "水印图后缀", defaultValue = "-wmark", type = "string")
    public static final String PROP_IMAGE_WATERMARKSUFFIX = PRE + "image.waterMarkSuffix";

    @PropDoc(value = "缩略图后缀", defaultValue = "-thumb", type = "string")
    public static final String PROP_IMAGE_THUMBSUFFIX = PRE + "image.thumbSuffix";

    @PropDoc(value = "缩略图宽度", defaultValue = "150", type = "int")
    public static final String PROP_IMAGE_THUMBWIDTH = PRE + "image.thumbWidth";

    @PropDoc(value = "缩略图高度", defaultValue = "150", type = "int")
    public static final String PROP_IMAGE_THUMBHEIGHT = PRE + "image.thumbHeight";

}
