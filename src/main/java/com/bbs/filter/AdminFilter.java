package com.bbs.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/admin/*")
public class AdminFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"success\":false,\"message\":\"请先登录\"}");
            return;
        }
        com.bbs.entity.User user = (com.bbs.entity.User) session.getAttribute("user");
        if (!user.isAdmin() && !user.isModerator()) {
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"success\":false,\"message\":\"无权限\"}");
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}