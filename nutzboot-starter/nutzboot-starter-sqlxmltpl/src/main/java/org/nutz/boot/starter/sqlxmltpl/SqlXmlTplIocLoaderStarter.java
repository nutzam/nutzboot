package org.nutz.boot.starter.sqlxmltpl;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.loader.json.JsonLoader;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019-04-03
 */
public class SqlXmlTplIocLoaderStarter implements IocLoaderProvider {

    protected static final String PRE = "sqlXmlTpl.";

    @PropDoc(group = "sqlXmlTpl", value = "sqlXmlTpl模版语言语句开始", need = false, defaultValue = "<exp>")
    public static final String statementStart = PRE + "statementStart";

    @PropDoc(group = "sqlXmlTpl", value = "sqlXmlTpl模版语言语句结束", need = false, defaultValue = "</exp>")
    public static final String statementEnd = PRE + "statementEnd";


    @Override
    public IocLoader getIocLoader() {
        return new JsonLoader("org/nutz/boot/starter/sqlxmltpl/sqlxmltpl.js");
    }
}