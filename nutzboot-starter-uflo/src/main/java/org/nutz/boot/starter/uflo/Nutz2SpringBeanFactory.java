package org.nutz.boot.starter.uflo;

import org.nutz.boot.AppContext;
import org.nutz.ioc.Ioc;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.ResolvableType;

public class Nutz2SpringBeanFactory implements BeanFactory {

    public Object getBean(String name) throws BeansException {
        if (name.startsWith("uflo."))
            return null;
        return ioc().get(null, name);
    }

    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        if (name.startsWith("uflo."))
            return null;
        return ioc().get(requiredType, name);
    }

    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return ioc().getByType(requiredType);
    }

    public Object getBean(String name, Object... args) throws BeansException {
        return getBean(name);
    }

    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        return getBean(requiredType);
    }

    public boolean containsBean(String name) {
        if (name.startsWith("uflo."))
            return false;
        return ioc().has(name);
    }

    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        if (!containsBean(name))
            throw new NoSuchBeanDefinitionException(name);
        return true;
    }

    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        if (!containsBean(name))
            throw new NoSuchBeanDefinitionException(name);
        return false;
    }

    public boolean isTypeMatch(String name, ResolvableType typeToMatch)
            throws NoSuchBeanDefinitionException {
        if (!containsBean(name))
            throw new NoSuchBeanDefinitionException(name);
        return typeToMatch.isInstance(ioc().get(null, name));
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch)
            throws NoSuchBeanDefinitionException {
        if (!containsBean(name))
            throw new NoSuchBeanDefinitionException(name);
        return typeToMatch.isInstance(ioc().get(null, name));
    }

    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        if (!containsBean(name))
            throw new NoSuchBeanDefinitionException(name);
        return ioc().get(null, name).getClass();
    }

    @Override
    public String[] getAliases(String name) {
        return new String[0];
    }

    protected Ioc ioc() {
        return AppContext.getDefault().getIoc();
    }
}
