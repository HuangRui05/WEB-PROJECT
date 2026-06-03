package com.bbs.dao;

import com.bbs.entity.Section;
import com.bbs.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionDao {

    public List<Section> findAll() {
        String sql = "SELECT s.*, u.nickname as moderator_name FROM section s LEFT JOIN user u ON s.moderator_id = u.id ORDER BY sort_order";
        List<Section> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractSection(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Section findById(Long id) {
        String sql = "SELECT * FROM section WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractSection(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Section section) {
        String sql = "INSERT INTO section (name, description, icon, sort_order) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, section.getName());
            ps.setString(2, section.getDescription());
            ps.setString(3, section.getIcon());
            ps.setInt(4, section.getSortOrder() != null ? section.getSortOrder() : 0);
            int rows = ps.executeUpdate();
            System.out.println("SectionDao.insert: rows affected = " + rows);
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    section.setId(rs.getLong(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("SectionDao.insert SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            e.printStackTrace();
            return false;
        }
    }

    public Section findByName(String name) {
        String sql = "SELECT * FROM section WHERE name = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractSection(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(Section section) {
        String sql = "UPDATE section SET name=?, description=?, icon=?, sort_order=?, moderator_id=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, section.getName());
            ps.setString(2, section.getDescription());
            ps.setString(3, section.getIcon());
            ps.setInt(4, section.getSortOrder());
            ps.setObject(5, section.getModeratorId());
            ps.setLong(6, section.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCounts(Long sectionId) {
        String sql = "UPDATE section SET topic_count=(SELECT COUNT(*) FROM post WHERE section_id=? AND status=1), post_count=(SELECT COUNT(*) FROM reply r JOIN post p ON r.post_id=p.id WHERE p.section_id=? AND p.status=1) WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sectionId);
            ps.setLong(2, sectionId);
            ps.setLong(3, sectionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAllCounts() {
        String sql = "UPDATE section s SET topic_count=(SELECT COUNT(*) FROM post WHERE section_id=s.id AND status=1), post_count=(SELECT COUNT(*) FROM reply r JOIN post p ON r.post_id=p.id WHERE p.section_id=s.id AND p.status=1)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM section WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Section extractSection(ResultSet rs) throws SQLException {
        Section s = new Section();
        s.setId(rs.getLong("id"));
        s.setName(rs.getString("name"));
        s.setDescription(rs.getString("description"));
        s.setIcon(rs.getString("icon"));
        s.setSortOrder(rs.getInt("sort_order"));
        s.setTopicCount(rs.getInt("topic_count"));
        s.setPostCount(rs.getInt("post_count"));
        s.setModeratorId(rs.getObject("moderator_id", Long.class));
        s.setCreateTime(rs.getTimestamp("create_time"));
        s.setUpdateTime(rs.getTimestamp("update_time"));
        return s;
    }
}