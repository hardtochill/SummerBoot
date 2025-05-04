package cn.lqz.summerboot;

import cn.lqz.summerboot.annotations.Component;
import cn.lqz.summerboot.annotations.PostStruct;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

public class BeanDefinition {
    private String beanName;
    private Constructor<?> constructor;

    private Method postConstructMethod;
    public BeanDefinition(Class<?> type){
        Component component = type.getDeclaredAnnotation(Component.class);
        // 若@Component中未指定beanName，则使用默认的
        beanName = component.name().isEmpty()?type.getSimpleName(): component.name();
        try{
            constructor = type.getConstructor();
            // 寻找是否有方法加了@PostConstruct
            // 假设Bean初始化时只会调用第一个打了@PostConstruct的函数
            postConstructMethod = Arrays.stream(type.getDeclaredMethods()).filter(m->m.isAnnotationPresent(PostStruct.class)).findFirst().orElse(null);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public String getName(){return beanName;}

    public Constructor<?> getConstructor(){return constructor;}

    public Method getPostConstructMethod(){
        return postConstructMethod;
    }

}
