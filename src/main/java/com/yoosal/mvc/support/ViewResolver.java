package com.yoosal.mvc.support;

import com.yoosal.common.event.PublicEventContext;
import com.yoosal.mvc.exception.SceneInvokeException;
import com.yoosal.mvc.exception.ViewResolverException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ViewResolver {

    void resolver(HttpServletRequest request, HttpServletResponse response) throws SceneInvokeException, ViewResolverException;

    void setEventContext(PublicEventContext publicEventContext);
}
