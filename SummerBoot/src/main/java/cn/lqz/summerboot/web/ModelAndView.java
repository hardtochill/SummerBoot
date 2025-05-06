package cn.lqz.summerboot.web;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {
    // 静态资源路径
    private String view;
    // 模板渲染
    private Map<String,String> context = new HashMap<>();

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Map<String, String> getContext() {
        return context;
    }
}
