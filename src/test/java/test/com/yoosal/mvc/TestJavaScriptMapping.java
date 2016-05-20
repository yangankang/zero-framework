package test.com.yoosal.mvc;

import com.yoosal.mvc.EntryPointManager;
import com.yoosal.mvc.exception.ParseTemplateException;
import com.yoosal.mvc.support.ControllerMethodParse;
import com.yoosal.mvc.support.DefaultJavaScriptMapping;
import com.yoosal.mvc.support.JavaScriptMapping;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestJavaScriptMapping {

    @Test
    public void testCallJavaScript() throws ParseTemplateException {
        EntryPointManager.setProperty("mvc.api.prefix", "FromController");
        EntryPointManager.setProperty("mvc.key.method", "_METHOD");
        EntryPointManager.setProperty("mvc.key.class", "_CLASS");
        ControllerMethodParse methodParse = new ControllerMethodParse();
        methodParse.setControllerName("Abc");
        methodParse.setMethodName("hello");
        methodParse.setJavaMethodParamNames(new String[]{"p1", "p2"});
        methodParse.setClazz(ControllerMethodParse.class);
        List list = new ArrayList();
        list.add(methodParse);
        JavaScriptMapping mapping = new DefaultJavaScriptMapping();
        mapping.setMethodParses(list);
        System.out.println(mapping.parseTemplate());
    }
}
