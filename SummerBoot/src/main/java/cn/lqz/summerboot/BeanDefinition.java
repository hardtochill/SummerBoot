package cn.lqz.summerboot;

import cn.lqz.summerboot.annotations.Autowired;
import cn.lqz.summerboot.annotations.Component;
import cn.lqz.summerboot.annotations.PostStruct;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BeanDefinition {
    private final String beanName;
    private final List<Field> autowiredFieldList;
    private final Constructor<?> constructor;
    private final Class<?> beanType;

    private Method postConstructMethod;
    public BeanDefinition(Class<?> type){
        beanType = type;
        Component component = type.getDeclaredAnnotation(Component.class);
        // 若@Component中未指定beanName，则使用默认的
        beanName = component.name().isEmpty()?type.getSimpleName(): component.name();
        try{
            constructor = type.getConstructor();
            // 寻找是否有属性加了@Autowired
            autowiredFieldList = Arrays.stream(type.getDeclaredFields()).filter(f->f.isAnnotationPresent(Autowired.class)).collect(Collectors.toList());
            // 寻找是否有方法加了@PostConstruct
            // 假设Bean初始化时只会调用第一个打了@PostConstruct的函数
            postConstructMethod = Arrays.stream(type.getDeclaredMethods()).filter(m->m.isAnnotationPresent(PostStruct.class)).findFirst().orElse(null);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public String getName(){return beanName;}

    public Constructor<?> getConstructor(){return constructor;}

    public List<Field> getAutowiredFieldList(){
        return autowiredFieldList;
    }

    public Method getPostConstructMethod(){
        return postConstructMethod;
    }
    public Class<?> getBeanType(){
        return beanType;
    }

}
