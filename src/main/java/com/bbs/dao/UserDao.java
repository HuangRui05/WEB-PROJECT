package com.bbs.dao;

import com.bbs.entity.User;
import com.bbs.util.DBUtil;
import com.bbs.util.MD5Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public User findByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User findById(Long id) {
        String sql = "SELECT * FROM user WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean register(User user) {
        String sql = "INSERT INTO user (username, password, nickname, email, `points`, frozen_points) VALUES (?, ?, ?, ?, 100, 0)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getNickname() != null ? user.getNickname() : user.getUsername());
            ps.setString(4, user.getEmail());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User login(String username, String password) {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ? AND status = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, MD5Util.encode(password));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(User user) {
        String sql = "UPDATE user SET nickname=?, email=?, phone=?, contact_type=?, work_nature=?, work_location=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getNickname());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getContactType());
            ps.setString(5, user.getWorkNature());
            ps.setString(6, user.getWorkLocation());
            ps.setLong(7, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePoints(Long userId, int delta) {
        String sql = "UPDATE user SET points = points + ? WHERE id = ? AND (points + ?) >= 0";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setLong(2, userId);
            ps.setInt(3, delta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean freezePoints(Long userId, int amount) {
        String sql = "UPDATE user SET frozen_points = frozen_points + ? WHERE id = ? AND (points - ?) >= 0";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setLong(2, userId);
            ps.setInt(3, amount);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean unfreezePoints(Long userId, int amount) {
        String sql = "UPDATE user SET points = points + ?, frozen_points = frozen_points - ? WHERE id = ? AND frozen_points >= ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setInt(2, amount);
            ps.setLong(3, userId);
            ps.setInt(4, amount);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean unfreezeAndTransfer(Long fromUserId, Long toUserId, int amount) {
        String sql = "UPDATE user SET frozen_points = frozen_points - ? WHERE id = ? AND frozen_points >= ?";
        String sql2 = "UPDATE user SET points = points + ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sql);
                 PreparedStatement ps2 = conn.prepareStatement(sql2)) {
                ps1.setInt(1, amount);
                ps1.setLong(2, fromUserId);
                ps1.setInt(3, amount);
                if (ps1.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
                ps2.setInt(1, amount);
                ps2.setLong(2, toUserId);
                if (ps2.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean transferPoints(Long fromUserId, Long toUserId, int amount) {
        String sql = "UPDATE user SET points = points - ? WHERE id = ? AND points >= ?";
        String sql2 = "UPDATE user SET points = points + ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sql);
                 PreparedStatement ps2 = conn.prepareStatement(sql2)) {
                ps1.setInt(1, amount);
                ps1.setLong(2, fromUserId);
                ps1.setInt(3, amount);
                if (ps1.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
                ps2.setInt(1, amount);
                ps2.setLong(2, toUserId);
                if (ps2.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM user WHERE status = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private User extractUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setNickname(rs.getString("nickname"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setContactType(rs.getString("contact_type"));
        u.setWorkNature(rs.getString("work_nature"));
        u.setWorkLocation(rs.getString("work_location"));
        u.setAvatar(rs.getString("avatar"));
        u.setPoints(rs.getInt("points"));
        u.setFrozenPoints(rs.getInt("frozen_points"));
        u.setRole(rs.getInt("role"));
        u.setStatus(rs.getInt("status"));
        u.setCreateTime(rs.getTimestamp("create_time"));
        u.setUpdateTime(rs.getTimestamp("update_time"));
        return u;
    }
}