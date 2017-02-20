package com.yoosal.mvc.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 此抽象类用于支持用户自定义业务的权限，比如：用户实现了judge方法，查询当前请求在数据库中
 * 配置的权限(比如读写权限)是否可执行，然后设置canExecute属性，判断是否可以执行ApiController
 * 中的方法，只有在不可执行时自定义消息才会返回通知。
 */
public interface AuthoritySupport {

    AuthorityReply judge(AuthorityReply model, HttpServletRequest request, HttpServletResponse response);

    /**
     * 被生成的js api应该有哪些方法
     *
     * @return
     */
    List<ControllerMethodParse> canShowPrinter(List<ControllerMethodParse> parses);
}
