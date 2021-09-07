package org.nutz.boot.starter;

import org.nutz.boot.AppContext;
import org.nutz.boot.starter.nutz.mvc.PreventDuplicateSubmitProcessor;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * @author 黄川 306955302@qq.com
 * @date: 2018/8/14
 * 描述此类：
 */
@IocBean(create = "init")
public class PreventDuplicateSubmitStarter {

    private static final Log log = Logs.get();

    @Inject
    protected AppContext appContext;

    public void init() {
        if (appContext != null) {
            log.debug("PreventDuplicateSubmitStarter init ....");
            try {
                PreventDuplicateSubmitProcessor.redisService = appContext.getIoc().getByType(RedisService.class);
            } catch (Exception e) {
                log.debug("PreventDuplicateSubmitStarter 没有开启Redis采用，将采用session存储");
                //如果没有开启Redis就采用session存储，忽略错误
            }
        }
    }
}
