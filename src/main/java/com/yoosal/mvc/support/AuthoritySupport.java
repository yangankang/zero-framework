package com.yoosal.mvc.support;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 此抽象类用于支持用户自定义业务的权限，比如：用户实现了judge方法，查询当前请求在数据库中
 * 配置的权限(比如读写权限)是否可执行，然后设置canExecute属性，判断是否可以执行ApiController
 * 中的方法，只有在不可执行时自定义消息返回通知。
 */
public abstract class AuthoritySupport {
    private static final List<AuthoritySupport> authSupportList = new CopyOnWriteArrayList();

    public AuthoritySupport() {
        if (authSupportList.size() > 0) {
            throw new IllegalArgumentException("AuthoritySupport class must be one implement");
        }
        authSupportList.add(this);
    }

    public abstract AuthorityReply judge(AuthorityReply model);

    public static AuthoritySupport getAuthSupport() {
        if (authSupportList.size() > 0) {
            return authSupportList.get(0);
        }
        return null;
    }
}
