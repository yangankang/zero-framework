package test.com.yoosal.mvc;

import com.yoosal.mvc.EntryPointManager;
import com.yoosal.mvc.exception.ParseTemplateException;
import com.yoosal.mvc.support.ControllerMethodParse;
import com.yoosal.mvc.support.DefaultJavaScriptMapping;
import com.yoosal.mvc.support.JavaScriptMapping;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestJavaScriptMapping {

    @Test
    public void testCallJavaScript() throws ParseTemplateException {
        EntryPointManager entryPointManager = new EntryPointManager();
        entryPointManager.setProperty("mvc.api.prefix", "FromController");
        entryPointManager.setProperty("mvc.key.method", "_METHOD");
        entryPointManager.setProperty("mvc.key.class", "_CLASS");
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

    @Test
    public void testCreateFileJavaScript() throws ParseTemplateException, IOException {
        EntryPointManager entryPointManager = new EntryPointManager();
        entryPointManager.setProperty("mvc.api.prefix", "FromController");
        entryPointManager.setProperty("mvc.key.method", "_METHOD");
        entryPointManager.setProperty("mvc.key.class", "_CLASS");
        ControllerMethodParse methodParse = new ControllerMethodParse();
        methodParse.setControllerName("Abc");
        methodParse.setMethodName("hello");
        methodParse.setJavaMethodParamNames(new String[]{"p1", "p2"});
        methodParse.setClazz(ControllerMethodParse.class);
        List list = new ArrayList();
        list.add(methodParse);
        JavaScriptMapping mapping = new DefaultJavaScriptMapping();
        mapping.setMethodParses(list);
        mapping.generateToFile("D:\\a.js", true);
    }
}
