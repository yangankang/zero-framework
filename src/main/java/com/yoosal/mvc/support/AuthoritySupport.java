package com.yoosal.mvc.support;

/**
 * 用户可以实现此接口，返回AuthorityReply类中判断某个方法是否有执行权限
 */
public interface AuthoritySupport {
    AuthorityReply judge(AuthorityReply model);
}
