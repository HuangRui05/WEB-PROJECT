package com.bbs.service;

import com.bbs.dao.PointLogDao;
import com.bbs.dao.UserDao;
import com.bbs.entity.PointLog;
import com.bbs.entity.User;
import com.bbs.util.MD5Util;

public class UserService {
    private final UserDao userDao = new UserDao();
    private final PointLogDao pointLogDao = new PointLogDao();

    public User register(String username, String password, String nickname, String email) {
        System.out.println("[DEBUG] register called with username: " + username);
        User existing = userDao.findByUsername(username);
        System.out.println("[DEBUG] findByUsername result: " + (existing != null ? "user exists" : "user not found"));
        if (existing != null) {
            return null; // 用户名已存在
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(MD5Util.encode(password));
        user.setNickname(nickname);
        user.setEmail(email);
        if (userDao.register(user)) {
            // 注册奖励100积分
            userDao.updatePoints(user.getId(), 100);
            user = userDao.findById(user.getId());
            // 记录积分变动
            PointLog log = new PointLog();
            log.setUserId(user.getId());
            log.setChangeType("register");
            log.setPoints(100);
            log.setBalance(user.getPoints());
            log.setRemark("新用户注册奖励");
            pointLogDao.insert(log);
            return user;
        }
        return null;
    }

    public User login(String username, String password) {
        return userDao.login(username, password);
    }

    public User getById(Long id) {
        return userDao.findById(id);
    }

    public boolean updateProfile(User user) {
        return userDao.update(user);
    }

    public boolean changePassword(Long userId, String oldPwd, String newPwd) {
        User user = userDao.findById(userId);
        if (user == null || !com.bbs.util.MD5Util.verify(oldPwd, user.getPassword())) {
            return false;
        }
        user.setPassword(com.bbs.util.MD5Util.encode(newPwd));
        return userDao.update(user);
    }

    public boolean updatePoints(Long userId, int delta) {
        return userDao.updatePoints(userId, delta);
    }

    public int getUserCount() {
        return userDao.count();
    }
}