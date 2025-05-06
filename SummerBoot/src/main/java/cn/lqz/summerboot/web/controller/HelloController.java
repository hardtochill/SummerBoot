package cn.lqz.summerboot.web.controller;

import cn.lqz.summerboot.annotations.Component;
import cn.lqz.summerboot.pojo.User;
import cn.lqz.summerboot.web.ModelAndView;
import cn.lqz.summerboot.web.annotations.Controller;
import cn.lqz.summerboot.web.annotations.Param;
import cn.lqz.summerboot.web.annotations.RequestMapping;
import cn.lqz.summerboot.web.annotations.ResponseBody;

@Component
@Controller
@RequestMapping("/hello")
public class HelloController {

    @RequestMapping("/a")
    public String hello(@Param("name") String name,@Param("age") Integer age){
        return String.format("<h1>hello</h1><br> name:%s ; age:%s",name,age);
    }

    @RequestMapping("/json")
    @ResponseBody
    public User json(@Param("name") String name,@Param("age") Integer age){
        User user = new User(name, age);
        return user;
    }

    @RequestMapping("/html")
    public ModelAndView modelAndView(@Param("name") String name, @Param("age") Integer age){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView("index.html");
        modelAndView.getContext().put("name",name);
        return modelAndView;
    }
}
