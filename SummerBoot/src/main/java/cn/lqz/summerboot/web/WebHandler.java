package cn.lqz.summerboot.web;

import cn.lqz.summerboot.web.annotations.ResponseBody;

import java.lang.reflect.Method;

public class WebHandler {
    // 要调用的ControllerBean
    private final Object bean;
    // 要调用的方法
    private final Method method;
    // 返回数据格式
    private final ResultType resultType;
    public WebHandler(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
        this.resultType = getResultType(bean,method);
    }

    private ResultType getResultType(Object bean, Method method) {
        if(method.isAnnotationPresent(ResponseBody.class)){
            return ResultType.JSON;
        }
        return ResultType.HTML;
    }

    /**
     * 该controller方法的返回格式
     */
    enum ResultType{
        // json
        JSON,
        // html
        HTML,
        // 本地静态资源
        LOCAL;
    }

    public Object getBean() {
        return bean;
    }

    public Method getMethod() {
        return method;
    }

    public ResultType getResultType() {
        return resultType;
    }
}
