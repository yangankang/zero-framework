package test.com.yoosal.mvc.apicontroller;

import com.yoosal.json.JSONObject;
import com.yoosal.mvc.annotation.APIController;
import com.yoosal.mvc.annotation.Printer;
import test.com.yoosal.mvc.model.BeanModel;

@APIController
public class TestApiControllerA {

    /**
     * 测试访问地址
     * http://localhost:9999/invoke.do?_class=TestApiControllerA&_method=hello
     *
     * @return
     */
    @Printer
    public String hello() {
        return "hello world";
    }

    /**
     * 测试访问地址
     * http://localhost:9999/invoke.do?_class=TestApiControllerA&_method=printer&name=ankang
     *
     * @param name
     * @return
     */
    @Printer
    public String printer(String name) {
        System.out.println("your name is : " + name);
        return name;
    }

    /**
     * 测试访问地址
     * http://localhost:9999/invoke.do?_class=TestApiControllerA&_method=bean&bean={id:10,name:"ankang"}
     *
     * @param bean
     * @return
     */
    @Printer
    public BeanModel bean(BeanModel bean) {
        System.out.println(JSONObject.toJSONString(bean));
        return bean;
    }
}
