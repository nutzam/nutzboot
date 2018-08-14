package io.nutz.demo.simple;

import org.nutz.boot.starter.prevent.duplicate.submit.error.PreventDuplicateSubmitError;
import org.nutz.json.JsonFormat;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.impl.processor.FailProcessor;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/8/14
 * 描述此类：
 */
public class CustomizFailProcessor extends FailProcessor {

    private static final Log log = Logs.get();

    @Override
    public void process(ActionContext ac) throws Throwable {
        Throwable throwable = ac.getError();
        if (throwable instanceof PreventDuplicateSubmitError && isAjax(ac.getRequest())) {
            if (log.isWarnEnabled()) {
                String uri = Mvcs.getRequestPath(ac.getRequest());
                log.warn(String.format("Error@%s :", uri), ac.getError());
            }
            Mvcs.write(ac.getResponse(), NutMap.NEW().setv("ok", false).setv("msg", ((PreventDuplicateSubmitError) throwable).getDetailMessage()), JsonFormat.full());
        } else {
            //继续执行原始逻辑
            super.process(ac);
        }
    }


    public static boolean isAjax(ServletRequest req) {
        String value = ((HttpServletRequest) req).getHeader("X-Requested-With");
        if (value != null && "XMLHttpRequest".equalsIgnoreCase(value.trim())) {
            return true;
        }
        return false;
    }
}
