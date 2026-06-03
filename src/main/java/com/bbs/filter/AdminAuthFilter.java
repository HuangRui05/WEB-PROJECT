package com.bbs.filter;

import com.bbs.entity.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@Order(2)
public class AdminAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI();
        if (path.startsWith(req.getContextPath() + "/admin/")) {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write("{\"success\":false,\"message\":\"请先登录\"}");
                return;
            }
            User user = (User) session.getAttribute("user");
            if (!user.isAdmin() && !user.isModerator()) {
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write("{\"success\":false,\"message\":\"无权限\"}");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
