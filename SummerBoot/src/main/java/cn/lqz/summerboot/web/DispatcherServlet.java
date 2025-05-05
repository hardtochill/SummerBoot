package cn.lqz.summerboot.web;

import ch.qos.logback.core.util.ContentTypeUtil;
import cn.lqz.summerboot.annotations.Component;
import cn.lqz.summerboot.interfaces.BeanPostProcessor;
import cn.lqz.summerboot.web.annotations.Controller;
import cn.lqz.summerboot.web.annotations.Param;
import cn.lqz.summerboot.web.annotations.RequestMapping;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class DispatcherServlet extends HttpServlet implements BeanPostProcessor {
    private Map<String,WebHandler> handlerMap = new HashMap<>();
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 找到要执行的controller
        WebHandler webHandler = findController(req);
        if(null==webHandler){
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().write("<h1>Error! 该请求没有对应的处理器</h1><br>");
            return;
        }
        try {
            Object controller = webHandler.getBean();
            // 解析方法所需参数
            Object[] args = resolveArgs(req,webHandler.getMethod());
            // 拿到执行结果result
            Object result = webHandler.getMethod().invoke(controller,args);
            // 根据定义返回不同形式的数据
            WebHandler.ResultType resultType = webHandler.getResultType();
            switch (resultType){
                case JSON ->{
                    resp.setContentType("application/json;charset=UTF-8");
                    resp.getWriter().write(JSONObject.toJSONString(result));
                }
                case HTML -> {
                    resp.setContentType("text/html");
                    resp.getWriter().write(result.toString());
                }
                case LOCAL -> {

                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 解析请求携带的参数
     * @param req
     * @param method
     * @return
     */
    private Object[] resolveArgs(HttpServletRequest req, Method method) {
        // 方法的形参表
        Parameter[] parameters = method.getParameters();
        // 参数值
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            // 将方法形参与请求传参“按序”一一对应
            Parameter parameter = parameters[i];
            Param param = parameter.getAnnotation(Param.class);
            String parameterName = param==null?parameter.getName():param.value();
            Class<?> parameterType = parameter.getType();
            String value = req.getParameter(parameterName);
            if(String.class.isAssignableFrom(parameterType)){
                args[i] = value;
            }else if(Integer.class.isAssignableFrom(parameterType)){
                args[i] = Integer.parseInt(value);
            }else{
                args[i] = null;
            }
        }
        return args;
    }

    private WebHandler findController(HttpServletRequest req) {
        return handlerMap.get(req.getRequestURI());
    }

    /**
     * 所有Bean初始化完成后都会执行到此方法
     * 将保存所有Controller及其路径的映射关系
     * @param bean
     * @param beanName
     * @return
     */
    @Override
    public Object afterInitializeBean(Object bean, String beanName) {
        if(!bean.getClass().isAnnotationPresent(Controller.class)){
            return bean;
        }
        // 类上的RequestMapping
        RequestMapping classRM = bean.getClass().getAnnotation(RequestMapping.class);
        String classUri = classRM==null?"":classRM.value();
        // 方法上的RequestMapping
        Arrays.stream(bean.getClass().getDeclaredMethods())
                .filter(method->method.isAnnotationPresent(RequestMapping.class))
                .forEach(method->{
                    RequestMapping methodRM = method.getAnnotation(RequestMapping.class);
                    String key = classUri.concat(methodRM.value());
                    WebHandler webHandler = new WebHandler(bean, method);
                    // uri重复
                    if (handlerMap.put(key, webHandler) != null) {
                        throw new RuntimeException("Controller 定义重复 "+key);
                    }
                });
        return bean;
    }
}
