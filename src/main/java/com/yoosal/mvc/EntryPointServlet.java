package com.yoosal.mvc;

import javax.servlet.http.HttpServlet;

/**
 * MVC的入口Servlet,这里初始化整个配置,并且所有的MVC请求都要经过这里，由这里分发，当然这个入口不是唯一的
 * 入口还支持SpringMVC来分发地址
 */
public class EntryPointServlet extends HttpServlet {
}
