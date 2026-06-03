package com.bbs.dao;

import com.bbs.entity.PointLog;
import com.bbs.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PointLogDao {

    public boolean insert(PointLog log) {
        String sql = "INSERT INTO point_log (user_id, change_type, points, balance, relate_id, remark) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, log.getUserId());
            ps.setString(2, log.getChangeType());
            ps.setInt(3, log.getPoints());
            ps.setInt(4, log.getBalance());
            ps.setObject(5, log.getRelateId());
            ps.setString(6, log.getRemark());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<PointLog> findByUser(Long userId) {
        String sql = "SELECT * FROM point_log WHERE user_id = ? ORDER BY create_time DESC LIMIT 50";
        List<PointLog> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PointLog log = new PointLog();
                log.setId(rs.getLong("id"));
                log.setUserId(rs.getLong("user_id"));
                log.setChangeType(rs.getString("change_type"));
                log.setPoints(rs.getInt("points"));
                log.setBalance(rs.getInt("balance"));
                log.setRelateId(rs.getObject("relate_id", Long.class));
                log.setRemark(rs.getString("remark"));
                log.setCreateTime(rs.getTimestamp("create_time"));
                list.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}