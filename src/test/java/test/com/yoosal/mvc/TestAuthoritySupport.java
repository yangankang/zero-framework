package test.com.yoosal.mvc;

import com.yoosal.mvc.support.AuthorityReply;
import com.yoosal.mvc.support.AuthoritySupport;
import com.yoosal.mvc.support.ControllerMethodParse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("authoritySupport")
public class TestAuthoritySupport implements AuthoritySupport {
    @Override
    public AuthorityReply judge(AuthorityReply model) {
        if (model.getMethodName().equalsIgnoreCase("printer")) {
            model.setCanExecute(false);
            model.setMessage("权限不允许");
        }
        return model;
    }

    @Override
    public List<ControllerMethodParse> canShowPrinter(List<ControllerMethodParse> parses) {
        return null;
    }
}
