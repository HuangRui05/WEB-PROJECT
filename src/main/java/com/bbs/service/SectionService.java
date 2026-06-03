package com.bbs.service;

import com.bbs.dao.SectionDao;
import com.bbs.dao.UserDao;
import com.bbs.entity.Section;
import com.bbs.entity.User;

import java.util.List;

public class SectionService {
    private final SectionDao sectionDao = new SectionDao();
    private final UserDao userDao = new UserDao();

    public List<Section> getAll() {
        sectionDao.updateAllCounts();
        List<Section> list = sectionDao.findAll();
        for (Section s : list) {
            if (s.getModeratorId() != null) {
                User mod = userDao.findById(s.getModeratorId());
                s.setModerator(mod);
            }
        }
        return list;
    }

    public Section getById(Long id) {
        Section s = sectionDao.findById(id);
        if (s != null && s.getModeratorId() != null) {
            s.setModerator(userDao.findById(s.getModeratorId()));
        }
        return s;
    }

    public boolean create(Section section) {
        return sectionDao.insert(section);
    }

    public Section findByName(String name) {
        return sectionDao.findByName(name);
    }

    public boolean update(Section section) {
        return sectionDao.update(section);
    }

    public boolean delete(Long id) {
        return sectionDao.delete(id);
    }

    public boolean setModerator(Long sectionId, Long userId) {
        Section section = sectionDao.findById(sectionId);
        if (section == null) return false;
        if (userId != null) {
            User user = userDao.findById(userId);
            if (user == null) return false;
            user.setRole(1); // 设为版主
            userDao.update(user);
        }
        section.setModeratorId(userId);
        return sectionDao.update(section);
    }
}