package com.bbs.dao;

import com.bbs.entity.Post;
import com.bbs.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDao {

    public List<Post> findBySection(Long sectionId, int page, int size) {
        String sql = "SELECT p.*, u.username, u.nickname, s.name as section_name FROM post p " +
                     "JOIN user u ON p.user_id = u.id JOIN section s ON p.section_id = s.id " +
                     "WHERE p.section_id = ? AND p.status = 1 ORDER BY p.is_top DESC, p.create_time DESC LIMIT ?, ?";
        List<Post> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sectionId);
            ps.setInt(2, (page - 1) * size);
            ps.setInt(3, size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Post> findAll(int page, int size, String keyword) {
        String sql = "SELECT p.*, u.username, u.nickname, s.name as section_name FROM post p " +
                     "JOIN user u ON p.user_id = u.id JOIN section s ON p.section_id = s.id " +
                     "WHERE p.status = 1 AND (p.title LIKE ? OR p.content LIKE ?) " +
                     "ORDER BY p.is_top DESC, p.create_time DESC LIMIT ?, ?";
        List<Post> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setInt(3, (page - 1) * size);
            ps.setInt(4, size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Post findById(Long id) {
        String sql = "SELECT p.*, u.username, u.nickname, s.name as section_name FROM post p " +
                     "JOIN user u ON p.user_id = u.id JOIN section s ON p.section_id = s.id WHERE p.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractPost(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Post post) {
        String sql = "INSERT INTO post (section_id, user_id, title, content, points_reward, frozen_points, is_demand, is_solved, solve_time, expire_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, post.getSectionId());
            ps.setLong(2, post.getUserId());
            ps.setString(3, post.getTitle());
            ps.setString(4, post.getContent());
            ps.setInt(5, post.getPointsReward() != null ? post.getPointsReward() : 0);
            ps.setInt(6, post.getFrozenPoints() != null ? post.getFrozenPoints() : 0);
            ps.setInt(7, post.getIsDemand() != null ? post.getIsDemand() : 0);
            ps.setInt(8, post.getIsSolved() != null ? post.getIsSolved() : 0);
            ps.setTimestamp(9, post.getSolveTime());
            ps.setTimestamp(10, post.getExpireTime());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    post.setId(rs.getLong(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Post post) {
        String sql = "UPDATE post SET title=?, content=?, points_reward=?, frozen_points=?, is_demand=?, is_solved=?, is_top=?, is_good=?, solve_time=?, expire_time=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getContent());
            ps.setInt(3, post.getPointsReward() != null ? post.getPointsReward() : 0);
            ps.setInt(4, post.getFrozenPoints() != null ? post.getFrozenPoints() : 0);
            ps.setInt(5, post.getIsDemand() != null ? post.getIsDemand() : 0);
            ps.setInt(6, post.getIsSolved() != null ? post.getIsSolved() : 0);
            ps.setInt(7, post.getIsTop() != null ? post.getIsTop() : 0);
            ps.setInt(8, post.getIsGood() != null ? post.getIsGood() : 0);
            ps.setTimestamp(9, post.getSolveTime());
            ps.setTimestamp(10, post.getExpireTime());
            ps.setLong(11, post.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateReplyCount(Long postId) {
        String sql = "UPDATE post SET reply_count = (SELECT COUNT(*) FROM reply WHERE post_id = ?) WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, postId);
            ps.setLong(2, postId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Long id) {
        String sql = "UPDATE post SET status = 0 WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Post> findByUser(Long userId) {
        String sql = "SELECT p.*, u.username, u.nickname, s.name as section_name FROM post p " +
                     "JOIN user u ON p.user_id = u.id JOIN section s ON p.section_id = s.id " +
                     "WHERE p.user_id = ? AND p.status = 1 ORDER BY p.create_time DESC";
        List<Post> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Post> findAllPosts() {
        String sql = "SELECT p.*, u.username, u.nickname, s.name as section_name FROM post p " +
                     "JOIN user u ON p.user_id = u.id JOIN section s ON p.section_id = s.id " +
                     "WHERE p.status = 1 ORDER BY p.create_time DESC";
        List<Post> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM post WHERE status = 1";
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

    private Post extractPost(ResultSet rs) throws SQLException {
        Post p = new Post();
        p.setId(rs.getLong("id"));
        p.setSectionId(rs.getLong("section_id"));
        p.setUserId(rs.getLong("user_id"));
        p.setTitle(rs.getString("title"));
        p.setContent(rs.getString("content"));
        p.setPointsReward(rs.getInt("points_reward"));
        p.setFrozenPoints(rs.getInt("frozen_points"));
        p.setIsDemand(rs.getInt("is_demand"));
        p.setIsSolved(rs.getInt("is_solved"));
        p.setIsTop(rs.getInt("is_top"));
        p.setIsGood(rs.getInt("is_good"));
        p.setViewCount(rs.getInt("view_count"));
        p.setReplyCount(rs.getInt("reply_count"));
        p.setStatus(rs.getInt("status"));
        p.setSolveTime(rs.getTimestamp("solve_time"));
        p.setExpireTime(rs.getTimestamp("expire_time"));
        p.setCreateTime(rs.getTimestamp("create_time"));
        p.setUpdateTime(rs.getTimestamp("update_time"));
        // 虚拟属性
        com.bbs.entity.User author = new com.bbs.entity.User();
        author.setId(rs.getLong("user_id"));
        author.setUsername(rs.getString("username"));
        author.setNickname(rs.getString("nickname"));
        p.setAuthor(author);
        com.bbs.entity.Section section = new com.bbs.entity.Section();
        section.setId(rs.getLong("section_id"));
        section.setName(rs.getString("section_name"));
        p.setSection(section);
        return p;
    }
}