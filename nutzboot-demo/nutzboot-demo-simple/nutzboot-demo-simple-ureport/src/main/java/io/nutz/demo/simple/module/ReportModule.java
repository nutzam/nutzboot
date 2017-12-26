package io.nutz.demo.simple.module;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;

import com.bstek.ureport.export.ExportManager;

@At("/report")
@IocBean
public class ReportModule {

    @Inject
    protected ExportManager exportManager;
    
    @At
    public void index() {}
}
