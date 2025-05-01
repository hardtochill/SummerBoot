package cn.lqz.summerboot;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = new ApplicationContext("cn.lqz.summerboot");
        Object cat = applicationContext.getBean("Cat");
        System.out.println(cat);
    }
}
