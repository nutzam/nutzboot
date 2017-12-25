package org.nutz.boot.starter.feign;

import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;
import org.nutz.ioc.ValueProxyMaker;
import org.nutz.ioc.meta.IocValue;
import org.nutz.lang.Lang;

/**
 *
 */
public class FeignScanValueProxyMaker  implements ValueProxyMaker {


    @Override
    public String[] supportedTypes() {
        return new String[]{"feign"};
    }

    @Override
    public ValueProxy make(IocMaking ing, IocValue iv) {
        if ("feign".equals(iv.getType())) {
            final String zclassName = iv.getValue().toString();
            return new ValueProxy() {
                public Object get(IocMaking ing) {
                    // 根据 address 创建一个对象
                  return null;
                }

            };
        }
        return null;
    }
}
