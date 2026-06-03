package com.bbs.dao;

import com.bbs.entity.Reply;
import com.bbs.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReplyDao {

    public List<Reply> findByPost(Long postId) {
        String sql = "SELECT r.*, u.username, u.nickname FROM reply r JOIN user u ON r.user_id = u.id WHERE r.post_id = ? ORDER BY r.create_time";
        List<Reply> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, postId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractReply(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Reply reply) {
        String sql = "INSERT INTO reply (post_id, user_id, content) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, reply.getPostId());
            ps.setLong(2, reply.getUserId());
            ps.setString(3, reply.getContent());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    reply.setId(rs.getLong(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Reply reply) {
        String sql = "UPDATE reply SET content = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reply.getContent());
            ps.setLong(2, reply.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Reply findById(Long id) {
        String sql = "SELECT r.*, u.username, u.nickname FROM reply r JOIN user u ON r.user_id = u.id WHERE r.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractReply(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean accept(Long replyId, int points) {
        String sql = "UPDATE reply SET is_accept = 1, points_earned = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, points);
            ps.setLong(2, replyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Long replyId) {
        String sql = "DELETE FROM reply WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, replyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteByPost(Long postId) {
        String sql = "DELETE FROM reply WHERE post_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, postId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int deleteOrphanReplies() {
        String sql = "DELETE FROM reply WHERE post_id NOT IN (SELECT id FROM post)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Reply> findByUser(Long userId) {
        String sql = "SELECT r.*, u.username, u.nickname FROM reply r JOIN user u ON r.user_id = u.id WHERE r.user_id = ? ORDER BY r.create_time DESC";
        List<Reply> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractReply(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Reply> findAll() {
        String sql = "SELECT r.*, u.username, u.nickname FROM reply r JOIN user u ON r.user_id = u.id ORDER BY r.create_time DESC";
        List<Reply> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractReply(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Reply extractReply(ResultSet rs) throws SQLException {
        Reply r = new Reply();
        r.setId(rs.getLong("id"));
        r.setPostId(rs.getLong("post_id"));
        r.setUserId(rs.getLong("user_id"));
        r.setContent(rs.getString("content"));
        r.setIsAccept(rs.getInt("is_accept"));
        r.setPointsEarned(rs.getInt("points_earned"));
        r.setCreateTime(rs.getTimestamp("create_time"));
        r.setUpdateTime(rs.getTimestamp("update_time"));
        com.bbs.entity.User author = new com.bbs.entity.User();
        author.setId(rs.getLong("user_id"));
        author.setUsername(rs.getString("username"));
        author.setNickname(rs.getString("nickname"));
        r.setAuthor(author);
        return r;
    }
}