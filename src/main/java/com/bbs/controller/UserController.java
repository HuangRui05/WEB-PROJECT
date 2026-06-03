package com.bbs.controller;

import com.bbs.entity.User;
import com.bbs.service.UserService;
import com.bbs.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService = new UserService();
    private final PostService postService = new PostService();

    @PostMapping("/register")
    public void register(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String nickname = req.getParameter("nickname");
        String email = req.getParameter("email");
        if (nickname == null || nickname.trim().isEmpty()) {
            nickname = username;
        }
        System.out.println("[DEBUG] /register - username: " + username + ", password length: " + (password != null ? password.length() : 0));

        resp.setContentType("application/json;charset=UTF-8");
        if (username == null || password == null) {
            resp.getWriter().write("{\"success\":false,\"message\":\"用户名和密码不能为空\"}");
            return;
        }
        User user = userService.register(username, password, nickname, email);
        if (user != null) {
            resp.getWriter().write("{\"success\":true,\"message\":\"注册成功\"}");
        } else {
            resp.getWriter().write("{\"success\":false,\"message\":\"用户名已存在\"}");
        }
    }

    @PostMapping("/login")
    public void login(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        resp.setContentType("application/json;charset=UTF-8");
        User user = userService.login(username, password);
        if (user != null) {
            session.setAttribute("user", user);
            resp.getWriter().write("{\"success\":true,\"message\":\"登录成功\",\"user\":{\"id\":" + user.getId() + ",\"username\":\"" + user.getUsername() + "\",\"role\":" + user.getRole() + "}}");
        } else {
            resp.getWriter().write("{\"success\":false,\"message\":\"用户名或密码错误\"}");
        }
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.getSession().invalidate();
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write("{\"success\":true,\"message\":\"已退出\"}");
    }

    @PostMapping("/update")
    public void update(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        User loginUser = (User) session.getAttribute("user");
        if (loginUser == null) {
            resp.getWriter().write("{\"success\":false,\"message\":\"请先登录\"}");
            return;
        }
        loginUser.setNickname(req.getParameter("nickname"));
        loginUser.setEmail(req.getParameter("email"));
        loginUser.setPhone(req.getParameter("phone"));
        loginUser.setContactType(req.getParameter("contactType"));
        loginUser.setWorkNature(req.getParameter("workNature"));
        loginUser.setWorkLocation(req.getParameter("workLocation"));
        if (userService.updateProfile(loginUser)) {
            session.setAttribute("user", userService.getById(loginUser.getId()));
            resp.getWriter().write("{\"success\":true,\"message\":\"更新成功\"}");
        } else {
            resp.getWriter().write("{\"success\":false,\"message\":\"更新失败\"}");
        }
    }

    @GetMapping("/info")
    public void info(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        User user = (User) session.getAttribute("user");
        if (user == null) {
            resp.getWriter().write("{\"success\":false,\"message\":\"未登录\"}");
        } else {
            User freshUser = userService.getById(user.getId());
            String json = String.format("{\"success\":true,\"user\":{\"id\":%d,\"username\":\"%s\",\"nickname\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\",\"contactType\":\"%s\",\"workNature\":\"%s\",\"workLocation\":\"%s\",\"points\":%d,\"frozenPoints\":%d}}",
                freshUser.getId(), freshUser.getUsername(), freshUser.getNickname() != null ? freshUser.getNickname() : "",
                freshUser.getEmail() != null ? freshUser.getEmail() : "", freshUser.getPhone() != null ? freshUser.getPhone() : "",
                freshUser.getContactType() != null ? freshUser.getContactType() : "", freshUser.getWorkNature() != null ? freshUser.getWorkNature() : "",
                freshUser.getWorkLocation() != null ? freshUser.getWorkLocation() : "", freshUser.getPoints(), freshUser.getFrozenPoints() != null ? freshUser.getFrozenPoints() : 0);
            resp.getWriter().write(json);
        }
    }

    @GetMapping("/stats")
    public void stats(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        int userCount = userService.getUserCount();
        int postCount = postService.getPostCount();
        resp.getWriter().write("{\"success\":true,\"userCount\":" + userCount + ",\"postCount\":" + postCount + "}");
    }

    @GetMapping("/logout")
    public void logoutGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.getSession().invalidate();
        resp.sendRedirect(req.getContextPath() + "/index.html");
    }
}
