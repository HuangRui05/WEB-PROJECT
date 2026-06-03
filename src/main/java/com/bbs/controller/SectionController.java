package com.bbs.controller;

import com.bbs.entity.Section;
import com.bbs.entity.User;
import com.bbs.service.SectionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/section")
public class SectionController {
    private final SectionService sectionService = new SectionService();

    @GetMapping("/list")
    public void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        List<Section> list = sectionService.getAll();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Section s = list.get(i);
            sb.append("{\"id\":").append(s.getId())
              .append(",\"name\":\"").append(s.getName() != null ? s.getName() : "").append("\"")
              .append(",\"description\":\"").append(s.getDescription() != null ? s.getDescription() : "").append("\"")
              .append(",\"icon\":\"").append(s.getIcon() != null ? s.getIcon() : "").append("\"")
              .append(",\"topicCount\":").append(s.getTopicCount())
              .append(",\"postCount\":").append(s.getPostCount());
            if (s.getModerator() != null) {
                sb.append(",\"moderator\":{\"id\":").append(s.getModerator().getId())
                  .append(",\"nickname\":\"").append(s.getModerator().getNickname() != null ? s.getModerator().getNickname() : "").append("\"}");
            }
            sb.append("}");
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        resp.getWriter().write("{\"success\":true,\"data\":" + sb + "}");
    }

    @GetMapping("/{id}")
    public void getById(@PathVariable Long id, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Section section = sectionService.getById(id);
        if (section != null) {
            String json = "{\"success\":true,\"section\":{\"id\":" + section.getId() + ",\"name\":\"" + section.getName() + "\",\"description\":\"" + (section.getDescription() != null ? section.getDescription() : "") + "\"}}";
            resp.getWriter().write(json);
        } else {
            resp.getWriter().write("{\"success\":false,\"message\":\"板块不存在\"}");
        }
    }

    @PostMapping
    public void handleAction(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            resp.getWriter().write("{\"success\":false,\"message\":\"无权限\"}");
            return;
        }

        String action = req.getParameter("action");
        if ("add".equals(action)) {
            try {
                String name = req.getParameter("name");
                String desc = req.getParameter("description");
                System.out.println("创建板块: name=[" + name + "], desc=[" + desc + "]");
                if (name == null || name.trim().isEmpty()) {
                    resp.getWriter().write("{\"success\":false,\"message\":\"板块名称不能为空\"}");
                    return;
                }
                if (sectionService.findByName(name.trim()) != null) {
                    resp.getWriter().write("{\"success\":false,\"message\":\"板块名称已存在\"}");
                    return;
                }
                Section section = new Section();
                section.setName(name.trim());
                section.setDescription(desc);
                if (sectionService.create(section)) {
                    resp.getWriter().write("{\"success\":true,\"message\":\"创建成功\"}");
                } else {
                    resp.getWriter().write("{\"success\":false,\"message\":\"创建失败\"}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                resp.getWriter().write("{\"success\":false,\"message\":\"创建失败:\" + e.getMessage()}");
            }
        } else if ("delete".equals(action)) {
            String idStr = req.getParameter("id");
            if (idStr == null || idStr.isEmpty()) {
                resp.getWriter().write("{\"success\":false,\"message\":\"缺少板块ID\"}");
                return;
            }
            try {
                Long id = Long.parseLong(idStr);
                if (sectionService.delete(id)) {
                    resp.getWriter().write("{\"success\":true,\"message\":\"删除成功\"}");
                } else {
                    resp.getWriter().write("{\"success\":false,\"message\":\"删除失败\"}");
                }
            } catch (NumberFormatException e) {
                resp.getWriter().write("{\"success\":false,\"message\":\"无效的板块ID\"}");
            }
        } else if ("update".equals(action)) {
            Section section = new Section();
            section.setId(Long.parseLong(req.getParameter("id")));
            section.setName(req.getParameter("name"));
            section.setDescription(req.getParameter("description"));
            section.setSortOrder(Integer.parseInt(req.getParameter("sortOrder")));
            String moderatorId = req.getParameter("moderatorId");
            section.setModeratorId(moderatorId != null && !moderatorId.isEmpty() ? Long.parseLong(moderatorId) : null);
            if (sectionService.update(section)) {
                resp.getWriter().write("{\"success\":true,\"message\":\"更新成功\"}");
            } else {
                resp.getWriter().write("{\"success\":false,\"message\":\"更新失败\"}");
            }
        }
    }
}
