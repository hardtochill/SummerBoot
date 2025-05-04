package cn.lqz.summerboot.interfaces.impl;

import cn.lqz.summerboot.annotations.Component;
import cn.lqz.summerboot.interfaces.BeanPostProcessor;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object afterInitializeBean(Object bean, String beanName) {
        System.out.println(beanName+" 初始化完成");
        return bean;
    }
}
