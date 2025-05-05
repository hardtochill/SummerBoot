package cn.lqz.summerboot.web;

import cn.lqz.summerboot.annotations.Autowired;
import cn.lqz.summerboot.annotations.Component;
import cn.lqz.summerboot.annotations.PostStruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.File;
import java.io.IOException;
import java.util.logging.LogManager;

@Component
public class TomcatServer {
    @Autowired
    private HttpServlet dispatcherServlet;

    @PostStruct
    public void start() throws LifecycleException {
        // 日志格式转换
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        int port = 8080;
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector();

        // 定义上下文路径。上下文路径是Web应用的根路径。
        String contextPath = "";
        // 定义文档基础路径，为当前工作目录的绝对路径。文档基础路径是Web应用的根目录，Tomcat将从这个目录加载Web应用的静态资源。
        String docBase = new File(".").getAbsolutePath();
        Context context = tomcat.addContext(contextPath, docBase);

        tomcat.addServlet(contextPath, "dispatcherServlet", dispatcherServlet);
        // 为helloServlet添加一个URL映射。/*表示这个Servlet将处理上下文根路径下的所有请求。
        context.addServletMappingDecoded("/*","dispatcherServlet");
        tomcat.start();
        System.out.println("tomcat start... port :"+port);
    }
}
