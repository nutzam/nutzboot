package org.nutz.cloud.loach.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

/**
 * 为了减少 客户端与服务器端之间的流量, 做点黑魔法. <p/>
 * 如果客户端发送了ETag,与服务器端的计算结果一样,那就是没变更啦,304!
 * @author wendal
 *
 */
@IocBean(name="$views_json304")
public class Json304ViewMaker implements ViewMaker, View {
    
    private static JsonFormat jf = JsonFormat.compact();

    @Override
    public View make(Ioc ioc, String type, String value) {
        if ("json304".equals(type))
            return this;
        return null;
    }

    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
        String data = obj instanceof String ? obj.toString() : Json.toJson(obj, jf);
        String sha1 = Lang.sha1(data);
        byte[] re = data.getBytes();
        if (sha1.equalsIgnoreCase(req.getHeader("If-None-Match"))) {
            resp.setStatus(304);
        }
        else {
            resp.setContentLength(re.length);
            resp.setHeader("ETag", sha1);
            resp.getOutputStream().write(re);
        }
    }

}
