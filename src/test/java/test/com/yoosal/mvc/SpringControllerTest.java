package test.com.yoosal.mvc;

import com.yoosal.mvc.EntryPointManager;
import com.yoosal.mvc.exception.SceneInvokeException;
import com.yoosal.mvc.exception.ViewResolverException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class SpringControllerTest {

    @RequestMapping("/invoke.do")
    public void doing(HttpServletRequest request, HttpServletResponse response) {
        try {
            //这里使用GET/POST的传参方式，还有一种是Restful方式
            String classNameFromParam = request.getParameter(EntryPointManager.getClassKey());
            String methodNameFromParam = request.getParameter(EntryPointManager.getMethodKey());
            EntryPointManager.getViewResolver().resolver(request, response, classNameFromParam, methodNameFromParam);
        } catch (SceneInvokeException e) {
            e.printStackTrace();
        } catch (ViewResolverException e) {
            e.printStackTrace();
        }
    }
}
