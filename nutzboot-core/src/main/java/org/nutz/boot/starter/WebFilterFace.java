package org.nutz.boot.starter;

import java.util.EnumSet;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

/**
 * 提供一个Filter
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public interface WebFilterFace {

    /**
     * 一个全局唯一的名字
     */
    String getName();

    /**
     * 需要过滤的路径, 例如/*
     * 
     * @return 过滤的路径
     */
    String getPathSpec();

    /**
     * 需要支持哪些请求方式
     * 
     * @return 请求方式列表
     */
    EnumSet<DispatcherType> getDispatches();

    /**
     * Filter对象
     * 
     * @return Filter对象
     */
    Filter getFilter();

    /**
     * 初始化参数
     * 
     * @return 初始化参数
     */
    Map<String, String> getInitParameters();

    /**
     * 加载参数,非常重要,数字越小,优先级越高
     * 
     * @return
     */
    int getOrder();

    /**
     * 内置的默认顺序
     * 
     * @author wendal(wendal1985@gmail.com)
     *
     */
    public static interface FilterOrder {
        // hystrix,whale,druid,shiro,nutz
        int HystrixRequestFilter = 5;
        int WhaleFilter = 10;
        int DruidFilter = 20;
        int ShiroFilter = 30;
        int NutFilter = 50;
    }
}
