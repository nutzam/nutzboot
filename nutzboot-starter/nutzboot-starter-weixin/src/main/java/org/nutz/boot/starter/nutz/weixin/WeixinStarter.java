package org.nutz.boot.starter.nutz.weixin;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.ioc.IocLoader;
import org.nutz.plugins.weixin.WeixinIocLoader;

public class WeixinStarter implements IocLoaderProvider {

    @PropDoc(value="微信公众号API被动消息的Token")
    public static final String PROP_MP_API_TOKEN = "weixin.token";
    @PropDoc(value="微信公众号的appid")
    public static final String PROP_MP_API_APP_ID = "weixin.appid";
    @PropDoc(value="微信公众号的appsecret")
    public static final String PROP_MP_API_APP_SECRET = "weixin.appsecret";
    @PropDoc(value="微信公众号的OpenId")
    public static final String PROP_MP_API_OPENID = "weixin.openid";
    @PropDoc(value="微信公众号API被动消息的AES秘钥")
    public static final String PROP_MP_API_AES = "weixin.aes";
    @PropDoc(value="微信登录所需要的appid")
    public static final String PROP_WXLOGIN_APP_ID = "wxlogin.appid";
    @PropDoc(value="微信登录所需要的appsecret")
    public static final String PROP_WXLOGIN_APP_SECRET = "wxlogin.appsecret";
    @PropDoc(value="微信登录时使用的host头,需要包含http或https")
    public static final String PROP_WXLOGIN_HOST = "wxlogin.host";

    public IocLoader getIocLoader() {
        return new WeixinIocLoader();
    }

}
