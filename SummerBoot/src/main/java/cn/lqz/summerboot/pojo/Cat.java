package cn.lqz.summerboot.pojo;

import cn.lqz.summerboot.annotations.Autowired;
import cn.lqz.summerboot.annotations.Component;
import cn.lqz.summerboot.annotations.PostStruct;

@Component
public class Cat {
    @Autowired
    private Dog dog;

    @PostStruct
    public void init(){
        System.out.println("Cat创建了，Cat里面有一个属性："+dog);
    }
}
