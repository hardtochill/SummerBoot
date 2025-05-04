package cn.lqz.summerboot;

import cn.lqz.summerboot.annotations.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * Bean容器
 */
public class ApplicationContext {
    public ApplicationContext(String packageName) throws Exception {
        initContext(packageName);
    }
    public void initContext(String packageName) throws Exception {
        scanPackage(packageName).stream().filter(this::scanCreate).map(this::wrapper).forEach(this::createBean);
    }
    // 容器本体，beanName：bean
    private Map<String,Object> ioc = new HashMap<>();
    // 用于判断相同名字的bean是否已存在，beanName：beanDefinition
    private Map<String,BeanDefinition> beanDefinitionMap = new HashMap<>();
    /**
     * 根据BeanDefinition创建Bean
     * @param beanDefinition
     */
    protected void createBean(BeanDefinition beanDefinition){
        // 已经在容器
        String beanName = beanDefinition.getName();
        if(ioc.containsKey(beanName)){
            return;
        }
        // 创建Bean
        try {
            doCreateBean(beanDefinition);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 创建Bean
     * @param beanDefinition
     */
    private void doCreateBean(BeanDefinition beanDefinition) throws Exception {
        // 使用构造函数创建对象
        Constructor<?> constructor = beanDefinition.getConstructor();
        Object bean = constructor.newInstance();
        // 调用@PostConstruct方法
        Method postConstructMethod = beanDefinition.getPostConstructMethod();
        if(null!=postConstructMethod){
            postConstructMethod.invoke(bean);
        }
        // 将Bean加入ioc
        ioc.put(beanDefinition.getName(),bean);

    }

    /**
     * 为一个类创建BeanDefinition
     * @param type
     * @return
     */
    protected BeanDefinition wrapper(Class<?> type){
        BeanDefinition beanDefinition = new BeanDefinition(type);
        // bean名称不能重复
        if(beanDefinitionMap.containsKey(beanDefinition.getName())){
            throw new RuntimeException("Bean名称重复");
        }
        beanDefinitionMap.put(beanDefinition.getName(),beanDefinition);
        return beanDefinition;
    }

    /**
     * 是否要为该类创建Bean
     * @param type
     * @return
     */
    protected boolean scanCreate(Class<?> type){
        return type.isAnnotationPresent(Component.class);
    }

    /**
     * 扫描指定包，并为每个Class文件创建class对象
     * @param packageName
     * @return
     */
    private List<Class<?>> scanPackage(String packageName) throws Exception {
        List<Class<?>> classList = new ArrayList<>();
        // 获取要扫描的包URL
        URL resource = this.getClass().getClassLoader().getResource(packageName.replace(".", File.separator));
        Path path = Paths.get(resource.toURI());
        // 遍历包下所有文件，对每个遍历到的文件执行动作（访问者设计模式）
        Files.walkFileTree(path,new SimpleFileVisitor<>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                // 过滤出.class文件
                Path absolutePath = file.toAbsolutePath();
                if(absolutePath.toString().endsWith(".class")){
                    // 创建Class对象
                    String classPath = absolutePath.toString().replace(File.separator, ".");
                    int startIndex = classPath.indexOf(packageName);
                    // 得到全类名
                    classPath = classPath.substring(startIndex,classPath.length()-".class".length());
                    try {
                        classList.add(Class.forName(classPath));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return classList;
    }

    public Object getBean(String name){
        return ioc.get(name);
    }

    /**
     * ioc是根据beanName来存储Bean的，不是根据beanType来存储Bean
     * 这样做的原因是：如果根据beanType来存储Bean，那么假设外部要一个Bean，它的类型是一个父类类型或者接口，那么ioc中本来可以返回其子类或实现类来进行赋值；但由于类型不匹配因此无法返回其子类或实现类
     * 因此根据类型来注入Bean，是一定要遍历整个ioc容器的。即使ioc是根据BeanType来存储，那也要遍历ioc，对每一个Bean判断其是否是所要Bean的子类或实现类，就可以返回
     * @param beanType
     * @return
     * @param <T>
     */
    public <T> T getBean(Class<T> beanType){
        // A.isAssignableFrom(B)，说明B是A的子类，可以给A赋值
        // 当调用getBean()要一个指定类型的Bean时，也可以返回ioc中该类型的子类Bean，只返回第一个匹配的
        Optional<Object> first = ioc.values().stream().filter(iocBean -> beanType.isAssignableFrom(iocBean.getClass())).findFirst();
        return (T)first;
    }

    public <T> List<T> getBeans(Class<T> beanType){
        return ioc.values().stream().filter(iocBean -> beanType.isAssignableFrom(iocBean.getClass())).map(bean->(T)bean).toList();
    }

}
