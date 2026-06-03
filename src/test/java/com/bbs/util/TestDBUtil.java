package com.bbs.util;

import java.sql.*;

public class TestDBUtil {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/bbs_db?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8";
    private static final String USER = "root";
    private static final String PASS = "123456";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }

    public static void initTestData() {
        Connection conn = null;
        try {
            conn = getConnection();
            Statement stmt = conn.createStatement();

            // 创建测试用户（如果不存在）
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM user WHERE username = 'testuser'");
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.executeUpdate("INSERT INTO user (username, password, nickname, points, role, status, create_time) " +
                    "VALUES ('testuser', '" + MD5Util.encode("123456") + "', '测试用户', 100, 0, 1, NOW())");
            }

            rs = stmt.executeQuery("SELECT COUNT(*) FROM user WHERE username = 'testuser2'");
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.executeUpdate("INSERT INTO user (username, password, nickname, points, role, status, create_time) " +
                    "VALUES ('testuser2', '" + MD5Util.encode("123456") + "', '测试用户2', 100, 0, 1, NOW())");
            }

            rs = stmt.executeQuery("SELECT COUNT(*) FROM user WHERE username = 'admin'");
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.executeUpdate("INSERT INTO user (username, password, nickname, points, role, status, create_time) " +
                    "VALUES ('admin', '" + MD5Util.encode("admin123") + "', '管理员', 100, 2, 1, NOW())");
            } else {
                // 重置admin积分以便测试
                stmt.executeUpdate("UPDATE user SET points = 100 WHERE username = 'admin'");
            }

            // 重置测试用户积分
            stmt.executeUpdate("UPDATE user SET points = 100 WHERE username = 'testuser'");
            stmt.executeUpdate("UPDATE user SET points = 100 WHERE username = 'testuser2'");

            // 创建测试板块（如果不存在）
            rs = stmt.executeQuery("SELECT COUNT(*) FROM section WHERE name = '测试板块'");
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.executeUpdate("INSERT INTO section (name, description, sort_order, create_time) " +
                    "VALUES ('测试板块', '用于单元测试的板块', 1, NOW())");
            }

            System.out.println("[TestDBUtil] 测试数据初始化完成");
        } catch (SQLException e) {
            System.err.println("[TestDBUtil] 初始化测试数据失败: " + e.getMessage());
        } finally {
            close(conn, null, null);
        }
    }

    public static void cleanTestData() {
        Connection conn = null;
        try {
            conn = getConnection();
            Statement stmt = conn.createStatement();

            // 删除测试帖子（先删除回复）
            stmt.executeUpdate("DELETE FROM reply WHERE post_id IN " +
                "(SELECT id FROM (SELECT id FROM post WHERE user_id IN " +
                "(SELECT id FROM user WHERE username IN ('testuser', 'testuser2'))) AS temp)");

            // 删除测试帖子
            stmt.executeUpdate("DELETE FROM post WHERE user_id IN " +
                "(SELECT id FROM user WHERE username IN ('testuser', 'testuser2'))");

            // 删除测试板块
            stmt.executeUpdate("DELETE FROM section WHERE name IN ('测试板块', '单元测试板块', '待删除板块')");

            // 重置测试用户积分
            stmt.executeUpdate("UPDATE user SET points = 100, frozen_points = 0 WHERE username IN ('testuser', 'testuser2')");

            System.out.println("[TestDBUtil] 测试数据清理完成");
        } catch (SQLException e) {
            System.err.println("[TestDBUtil] 清理测试数据失败: " + e.getMessage());
        } finally {
            close(conn, null, null);
        }
    }
}
