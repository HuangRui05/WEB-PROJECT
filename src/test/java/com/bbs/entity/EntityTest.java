package com.bbs.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EntityTest {

    @Test
    public void testUserIsAdmin() {
        User user = new User();
        user.setRole(2);
        assertTrue(user.isAdmin());
        assertTrue(user.isModerator());

        user.setRole(1);
        assertFalse(user.isAdmin());
        assertTrue(user.isModerator());

        user.setRole(0);
        assertFalse(user.isAdmin());
        assertFalse(user.isModerator());
    }

    @Test
    public void testUserGettersAndSetters() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("hashedpassword");
        user.setNickname("测试昵称");
        user.setEmail("test@example.com");
        user.setPhone("13800138000");
        user.setPoints(100);
        user.setFrozenPoints(10);
        user.setRole(0);

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("hashedpassword", user.getPassword());
        assertEquals("测试昵称", user.getNickname());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("13800138000", user.getPhone());
        assertEquals(100, user.getPoints());
        assertEquals(10, user.getFrozenPoints());
        assertEquals(0, user.getRole());
    }

    @Test
    public void testPostEntity() {
        Post post = new Post();
        post.setId(1L);
        post.setSectionId(1L);
        post.setUserId(1L);
        post.setTitle("测试标题");
        post.setContent("测试内容");
        post.setPointsReward(10);
        post.setFrozenPoints(10);
        post.setIsDemand(1);
        post.setIsSolved(0);
        post.setIsTop(0);
        post.setIsGood(0);
        post.setViewCount(0);
        post.setReplyCount(0);

        assertEquals(1L, post.getId());
        assertEquals(1L, post.getSectionId());
        assertEquals(1L, post.getUserId());
        assertEquals("测试标题", post.getTitle());
        assertEquals("测试内容", post.getContent());
        assertEquals(10, post.getPointsReward());
        assertEquals(10, post.getFrozenPoints());
        assertEquals(1, post.getIsDemand());
        assertEquals(0, post.getIsSolved());
    }

    @Test
    public void testSectionEntity() {
        Section section = new Section();
        section.setId(1L);
        section.setName("技术讨论");
        section.setDescription("技术相关讨论板块");
        section.setSortOrder(1);
        section.setTopicCount(10);
        section.setPostCount(100);

        assertEquals(1L, section.getId());
        assertEquals("技术讨论", section.getName());
        assertEquals("技术相关讨论板块", section.getDescription());
        assertEquals(1, section.getSortOrder());
        assertEquals(10, section.getTopicCount());
        assertEquals(100, section.getPostCount());
    }

    @Test
    public void testReplyEntity() {
        Reply reply = new Reply();
        reply.setId(1L);
        reply.setPostId(1L);
        reply.setUserId(1L);
        reply.setContent("这是回复内容");
        reply.setIsAccept(0);
        reply.setPointsEarned(0);

        assertEquals(1L, reply.getId());
        assertEquals(1L, reply.getPostId());
        assertEquals(1L, reply.getUserId());
        assertEquals("这是回复内容", reply.getContent());
        assertEquals(0, reply.getIsAccept());
    }

    @Test
    public void testPointLogEntity() {
        PointLog log = new PointLog();
        log.setId(1L);
        log.setUserId(1L);
        log.setChangeType("earn");
        log.setPoints(50);
        log.setBalance(150);
        log.setRelateId(1L);
        log.setRemark("测试备注");

        assertEquals(1L, log.getId());
        assertEquals(1L, log.getUserId());
        assertEquals("earn", log.getChangeType());
        assertEquals(50, log.getPoints());
        assertEquals(150, log.getBalance());
        assertEquals(1L, log.getRelateId());
        assertEquals("测试备注", log.getRemark());
    }
}
