package cn.lqz.summerboot.pojo;

import cn.lqz.summerboot.annotations.Autowired;
import cn.lqz.summerboot.annotations.Component;
import cn.lqz.summerboot.annotations.PostStruct;

@Component
public class Dog {
    @Autowired
    private Cat cat;
    @PostStruct
    public void init(){
        System.out.println("Dog创建了，Dog里面有一个属性："+cat);
    }
}
