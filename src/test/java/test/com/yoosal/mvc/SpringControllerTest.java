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
            EntryPointManager.getViewResolver().resolver(request, response);
        } catch (SceneInvokeException e) {
            e.printStackTrace();
        } catch (ViewResolverException e) {
            e.printStackTrace();
        }
    }
}
