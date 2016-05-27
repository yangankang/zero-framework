package test.com.yoosal.mvc;

import com.yoosal.mvc.support.AuthorityReply;
import com.yoosal.mvc.support.AuthoritySupport;
import org.springframework.stereotype.Service;

@Service("authoritySupport")
public class TestAuthoritySupport extends AuthoritySupport {
    @Override
    public AuthorityReply judge(AuthorityReply model) {
        if (model.getMethodName().equalsIgnoreCase("printer")) {
            model.setCanExecute(false);
            model.setMessage("权限不允许");
        }
        return model;
    }
}
