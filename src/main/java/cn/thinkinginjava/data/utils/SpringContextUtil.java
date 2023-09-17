package cn.thinkinginjava.data.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtil implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(SpringContextUtil.class);
    private static ApplicationContext applicationContext;

    public SpringContextUtil() {
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    public static Object getBean(String name) {
        if (null == applicationContext) {
            return null;
        } else {
            return applicationContext.getBean(name);
        }
    }

    public static <T> T getBean(Class<T> clazz) {
        if (null == applicationContext) {
            return null;
        } else {
            return applicationContext.getBean(clazz);
        }
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        if (null == applicationContext) {
            return null;
        } else {
            return applicationContext.getBean(name, clazz);
        }
    }

    public static final boolean containsBean(String name) {
        if (null == applicationContext) {
            return false;
        } else {
            return applicationContext.containsBean(name);
        }
    }
}
