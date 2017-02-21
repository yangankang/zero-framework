package test.com.yoosal.mvc;

import com.yoosal.mvc.support.AuthorityReply;
import com.yoosal.mvc.support.AuthoritySupport;
import com.yoosal.mvc.support.ControllerMethodParse;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service("authoritySupport")
public class TestAuthoritySupport implements AuthoritySupport {
    public AuthorityReply judge(AuthorityReply model) {
        if (model.getMethodName().equalsIgnoreCase("printer")) {
            model.setCanExecute(false);
            model.setMessage("权限不允许");
        }
        return model;
    }

    @Override
    public AuthorityReply judge(AuthorityReply model, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public List<ControllerMethodParse> canShowPrinter(List<ControllerMethodParse> parses) {
        return null;
    }
}
