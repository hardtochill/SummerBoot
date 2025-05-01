package cn.lqz.summerboot;

import cn.lqz.summerboot.annotations.Component;

import java.lang.reflect.Constructor;

public class BeanDefinition {
    private String beanName;
    private Constructor<?> constructor;
    public BeanDefinition(Class<?> type){
        Component component = type.getDeclaredAnnotation(Component.class);
        // 若@Component中未指定beanName，则使用默认的
        beanName = component.name().isEmpty()?type.getSimpleName(): component.name();
        try{
            constructor = type.getConstructor();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public String getName(){return beanName;}

    public Constructor<?> getConstructor(){return constructor;}

}
