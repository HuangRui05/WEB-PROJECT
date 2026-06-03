package com.bbs.service;

import com.bbs.entity.User;
import com.bbs.util.TestDBUtil;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

    private static final UserService userService = new UserService();
    private static Long testUserId;

    @BeforeAll
    static void setUp() {
        TestDBUtil.initTestData();
    }

    @AfterAll
    static void tearDown() {
        TestDBUtil.cleanTestData();
    }

    @Test
    @Order(1)
    @DisplayName("用户注册成功")
    void testRegisterSuccess() {
        // 清理可能存在的测试用户
        User existing = userService.login("newuser", "123456");
        if (existing != null) {
            userService.updateProfile(existing);
        }

        User user = userService.register("newuser", "123456", "新用户", "new@test.com");
        assertNotNull(user, "注册应该成功");
        assertEquals("newuser", user.getUsername());
        assertEquals("新用户", user.getNickname());
        assertEquals(100, user.getPoints(), "新用户应该有100积分");
        testUserId = user.getId();
    }

    @Test
    @Order(2)
    @DisplayName("用户注册失败-用户名已存在")
    void testRegisterFailDuplicate() {
        User user = userService.register("newuser", "123456", "重复用户", null);
        assertNull(user, "用户名已存在应该返回null");
    }

    @Test
    @Order(3)
    @DisplayName("用户登录成功")
    void testLoginSuccess() {
        User user = userService.login("testuser", "123456");
        assertNotNull(user, "登录应该成功");
        assertEquals("testuser", user.getUsername());
        assertEquals(100, user.getPoints(), "测试用户应该有100积分");
    }

    @Test
    @Order(4)
    @DisplayName("用户登录失败-密码错误")
    void testLoginFailWrongPassword() {
        User user = userService.login("testuser", "wrongpassword");
        assertNull(user, "密码错误应该返回null");
    }

    @Test
    @Order(5)
    @DisplayName("用户登录失败-用户不存在")
    void testLoginFailUserNotExist() {
        User user = userService.login("nonexistent", "123456");
        assertNull(user, "用户不存在应该返回null");
    }

    @Test
    @Order(6)
    @DisplayName("获取用户信息")
    void testGetById() {
        User user = userService.login("testuser", "123456");
        assertNotNull(user);

        User fetched = userService.getById(user.getId());
        assertNotNull(fetched);
        assertEquals("testuser", fetched.getUsername());
    }

    @Test
    @Order(7)
    @DisplayName("更新用户资料")
    void testUpdateProfile() {
        User user = userService.login("testuser", "123456");
        assertNotNull(user);

        user.setNickname("修改后的昵称");
        user.setEmail("updated@test.com");
        user.setPhone("13800138000");
        user.setWorkNature("学生");
        user.setWorkLocation("北京");

        boolean result = userService.updateProfile(user);
        assertTrue(result, "更新应该成功");

        User updated = userService.getById(user.getId());
        assertEquals("修改后的昵称", updated.getNickname());
        assertEquals("updated@test.com", updated.getEmail());
        assertEquals("13800138000", updated.getPhone());
        assertEquals("学生", updated.getWorkNature());
        assertEquals("北京", updated.getWorkLocation());
    }

    @Test
    @Order(8)
    @DisplayName("获取用户总数")
    void testGetUserCount() {
        int count = userService.getUserCount();
        assertTrue(count >= 3, "至少有3个测试用户");
    }

    @Test
    @Order(9)
    @DisplayName("用户角色判断")
    void testUserRole() {
        User normalUser = userService.login("testuser", "123456");
        assertNotNull(normalUser);
        assertFalse(normalUser.isAdmin(), "普通用户不是管理员");
        assertFalse(normalUser.isModerator(), "普通用户不是版主");

        User adminUser = userService.login("admin", "admin123");
        assertNotNull(adminUser);
        assertTrue(adminUser.isAdmin(), "admin是管理员");
        assertTrue(adminUser.isModerator(), "admin是版主");
    }

    @Test
    @Order(10)
    @DisplayName("用户积分变动")
    void testUpdatePoints() {
        User user = userService.login("testuser", "123456");
        assertNotNull(user);
        int originalPoints = user.getPoints() != null ? user.getPoints() : 0;

        // 增加积分
        boolean result = userService.updatePoints(user.getId(), 50);
        assertTrue(result, "增加积分应该成功");

        User updated = userService.getById(user.getId());
        assertEquals(originalPoints + 50, updated.getPoints(), "积分应该增加50");

        // 减少积分
        result = userService.updatePoints(user.getId(), -30);
        assertTrue(result, "减少积分应该成功");

        updated = userService.getById(user.getId());
        assertEquals(originalPoints + 20, updated.getPoints(), "积分应该剩余original + 20");
    }
}
