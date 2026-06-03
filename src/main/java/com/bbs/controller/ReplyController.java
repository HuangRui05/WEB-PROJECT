package com.bbs.controller;

import com.bbs.dao.ReplyDao;
import com.bbs.entity.Reply;
import com.bbs.entity.User;
import com.bbs.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/reply")
public class ReplyController {
    private final PostService postService = new PostService();
    private final ReplyDao replyDao = new ReplyDao();

    @GetMapping("/listByUser")
    public void listByUser(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        User user = (User) session.getAttribute("user");
        if (user == null) {
            resp.getWriter().write("{\"success\":false,\"message\":\"请先登录\"}");
            return;
        }
        List<Reply> list = replyDao.findByUser(user.getId());
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Reply r = list.get(i);
            sb.append("{\"id\":").append(r.getId())
              .append(",\"postId\":").append(r.getPostId())
              .append(",\"userId\":").append(r.getUserId())
              .append(",\"authorName\":\"").append(r.getAuthor() != null && r.getAuthor().getNickname() != null ? r.getAuthor().getNickname() : "").append("\"")
              .append(",\"content\":\"").append(r.getContent() != null ? r.getContent().replace("\"", "\\\"") : "").append("\"")
              .append(",\"isAccept\":").append(r.getIsAccept())
              .append(",\"pointsEarned\":").append(r.getPointsEarned())
              .append(",\"createTime\":\"").append(r.getCreateTime() != null ? r.getCreateTime().toString() : "").append("\"")
              .append("}");
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        resp.getWriter().write("{\"success\":true,\"data\":" + sb + "}");
    }

    @GetMapping("/listAll")
    public void listAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        List<Reply> list = replyDao.findAll();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Reply r = list.get(i);
            sb.append("{\"id\":").append(r.getId())
              .append(",\"postId\":").append(r.getPostId())
              .append(",\"userId\":").append(r.getUserId())
              .append(",\"authorName\":\"").append(r.getAuthor() != null && r.getAuthor().getNickname() != null ? r.getAuthor().getNickname() : "").append("\"")
              .append(",\"content\":\"").append(r.getContent() != null ? r.getContent().replace("\"", "\\\"") : "").append("\"")
              .append(",\"isAccept\":").append(r.getIsAccept())
              .append(",\"pointsEarned\":").append(r.getPointsEarned())
              .append(",\"createTime\":\"").append(r.getCreateTime() != null ? r.getCreateTime().toString() : "").append("\"")
              .append("}");
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        resp.getWriter().write("{\"success\":true,\"data\":" + sb + "}");
    }

    @GetMapping("/{postId}")
    public void listByPost(@PathVariable Long postId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        List<Reply> list = postService.getReplies(postId);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Reply r = list.get(i);
            sb.append("{\"id\":").append(r.getId())
              .append(",\"postId\":").append(r.getPostId())
              .append(",\"userId\":").append(r.getUserId())
              .append(",\"authorName\":\"").append(r.getAuthor() != null && r.getAuthor().getNickname() != null ? r.getAuthor().getNickname() : "").append("\"")
              .append(",\"content\":\"").append(r.getContent() != null ? r.getContent().replace("\"", "\\\"") : "").append("\"")
              .append(",\"isAccept\":").append(r.getIsAccept())
              .append(",\"pointsEarned\":").append(r.getPointsEarned())
              .append(",\"createTime\":\"").append(r.getCreateTime() != null ? r.getCreateTime().toString() : "").append("\"")
              .append("}");
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        resp.getWriter().write("{\"success\":true,\"data\":" + sb + "}");
    }

    @PostMapping("/delete")
    public void delete(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        User user = (User) session.getAttribute("user");
        if (user == null) {
            resp.getWriter().write("{\"success\":false,\"message\":\"请先登录\"}");
            return;
        }
        Long replyId = Long.parseLong(req.getParameter("id"));
        Reply reply = replyDao.findById(replyId);
        if (reply == null) {
            resp.getWriter().write("{\"success\":false,\"message\":\"评论不存在\"}");
            return;
        }
        boolean isAdmin = user.isAdmin();
        boolean isAuthor = reply.getUserId().equals(user.getId());
        if (!isAdmin && !isAuthor) {
            resp.getWriter().write("{\"success\":false,\"message\":\"无权删除\"}");
            return;
        }
        if (replyDao.delete(replyId)) {
            resp.getWriter().write("{\"success\":true,\"message\":\"删除成功\"}");
        } else {
            resp.getWriter().write("{\"success\":false,\"message\":\"删除失败\"}");
        }
    }
}
