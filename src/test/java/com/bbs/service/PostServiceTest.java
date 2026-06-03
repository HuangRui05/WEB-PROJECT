package com.bbs.service;

import com.bbs.dao.PostDao;
import com.bbs.dao.SectionDao;
import com.bbs.dao.UserDao;
import com.bbs.entity.Post;
import com.bbs.entity.Section;
import com.bbs.entity.User;
import com.bbs.util.TestDBUtil;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostServiceTest {

    private static final PostService postService = new PostService();
    private static final UserDao userDao = new UserDao();
    private static final PostDao postDao = new PostDao();
    private static final SectionDao sectionDao = new SectionDao();

    private static User testUser;
    private static User testUser2;
    private static Section testSection;
    private static Post testPost;

    @BeforeAll
    static void setUp() {
        TestDBUtil.initTestData();

        testUser = userDao.findByUsername("testuser");
        testUser2 = userDao.findByUsername("testuser2");

        testSection = sectionDao.findByName("测试板块");
        if (testSection == null) {
            testSection = new Section();
            testSection.setName("测试板块");
            testSection.setDescription("用于单元测试的板块");
            testSection.setSortOrder(1);
            sectionDao.insert(testSection);
            testSection = sectionDao.findByName("测试板块");
        }
    }

    @AfterAll
    static void tearDown() {
        TestDBUtil.cleanTestData();
    }

    @Test
    @Order(1)
    @DisplayName("发布普通帖子")
    void testPublishNormalPost() {
        assertNotNull(testUser, "测试用户应该存在");
        assertNotNull(testSection, "测试板块应该存在");

        Post post = new Post();
        post.setSectionId(testSection.getId());
        post.setUserId(testUser.getId());
        post.setTitle("测试帖子标题");
        post.setContent("这是测试帖子的内容");
        post.setIsDemand(0);
        post.setPointsReward(0);

        Post result = postService.publish(post);
        assertNotNull(result, "发布普通帖子应该成功");
        assertNotNull(result.getId(), "帖子应该有ID");

        testPost = result;
        System.out.println("发布的帖子ID: " + result.getId());
    }

    @Test
    @Order(2)
    @DisplayName("发布需求帖-积分不足")
    void testPublishDemandPostFailInsufficientPoints() {
        assertNotNull(testUser, "测试用户应该存在");
        assertNotNull(testSection, "测试板块应该存在");

        Post post = new Post();
        post.setSectionId(testSection.getId());
        post.setUserId(testUser.getId());
        post.setTitle("需求帖");
        post.setContent("内容");
        post.setIsDemand(1);
        post.setPointsReward(999999);

        Post result = postService.publish(post);
        assertNull(result, "积分不足应该返回null");
    }

    @Test
    @Order(3)
    @DisplayName("发布需求帖-成功冻结积分")
    void testPublishDemandPostSuccess() {
        assertNotNull(testUser, "测试用户应该存在");
        assertNotNull(testSection, "测试板块应该存在");

        int beforePoints = testUser.getPoints() != null ? testUser.getPoints() : 0;
        int rewardPoints = 10;

        Post post = new Post();
        post.setSectionId(testSection.getId());
        post.setUserId(testUser.getId());
        post.setTitle("悬赏10积分的需求帖");
        post.setContent("内容");
        post.setIsDemand(1);
        post.setPointsReward(rewardPoints);

        Post result = postService.publish(post);
        assertNotNull(result, "积分足够时发布需求帖应该成功");

        User updatedUser = userDao.findById(testUser.getId());
        int afterPoints = updatedUser.getPoints() != null ? updatedUser.getPoints() : 0;
        assertEquals(beforePoints - rewardPoints, afterPoints, "积分应该被冻结");

        if (result != null) {
            postDao.delete(result.getId());
            userDao.updatePoints(testUser.getId(), rewardPoints);
        }
    }

    @Test
    @Order(4)
    @DisplayName("获取帖子详情")
    void testGetById() {
        assertNotNull(testPost, "应该有已发布的测试帖子");

        Post post = postService.getById(testPost.getId());
        assertNotNull(post, "应该能获取帖子");
        assertEquals(testPost.getTitle(), post.getTitle());
    }

    @Test
    @Order(5)
    @DisplayName("根据板块获取帖子列表")
    void testGetBySection() {
        assertNotNull(testSection, "测试板块应该存在");

        List<Post> posts = postService.getBySection(testSection.getId(), 1, 10);
        assertNotNull(posts, "应该返回帖子列表");
        assertTrue(posts.size() >= 1, "至少有1个帖子");

        Post post = posts.get(0);
        assertNotNull(post.getSection(), "帖子应该有关联的板块信息");
        assertEquals(testSection.getId(), post.getSection().getId());
    }

    @Test
    @Order(6)
    @DisplayName("搜索帖子")
    void testSearch() {
        List<Post> posts = postService.search("测试", 1, 10);
        assertNotNull(posts, "应该返回搜索结果");
    }

    @Test
    @Order(7)
    @DisplayName("获取用户的所有帖子")
    void testGetMyPosts() {
        assertNotNull(testUser, "测试用户应该存在");

        List<Post> posts = postService.getMyPosts(testUser.getId());
        assertNotNull(posts, "应该返回帖子列表");

        for (Post p : posts) {
            assertEquals(testUser.getId(), p.getUserId(), "帖子应该属于测试用户");
        }
    }

    @Test
    @Order(8)
    @DisplayName("设置帖子置顶")
    void testSetTop() {
        assertNotNull(testPost, "测试帖子应该存在");

        boolean result = postService.setTop(testPost.getId(), true);
        assertTrue(result, "设置置顶应该成功");

        Post updated = postService.getById(testPost.getId());
        assertEquals(1, updated.getIsTop(), "帖子应该已置顶");

        result = postService.setTop(testPost.getId(), false);
        assertTrue(result, "取消置顶应该成功");

        updated = postService.getById(testPost.getId());
        assertEquals(0, updated.getIsTop(), "帖子应该已取消置顶");
    }

    @Test
    @Order(9)
    @DisplayName("设置帖子加精")
    void testSetGood() {
        assertNotNull(testPost, "测试帖子应该存在");

        boolean result = postService.setGood(testPost.getId(), true);
        assertTrue(result, "设置加精应该成功");

        Post updated = postService.getById(testPost.getId());
        assertEquals(1, updated.getIsGood(), "帖子应该已加精");

        result = postService.setGood(testPost.getId(), false);
        assertTrue(result, "取消加精应该成功");

        updated = postService.getById(testPost.getId());
        assertEquals(0, updated.getIsGood(), "帖子应该已取消加精");
    }

    @Test
    @Order(10)
    @DisplayName("删除帖子-作者自己删除")
    void testDeleteMy() {
        assertNotNull(testUser, "测试用户应该存在");

        Post post = new Post();
        post.setSectionId(testSection.getId());
        post.setUserId(testUser.getId());
        post.setTitle("待删除的帖子");
        post.setContent("内容");
        post.setIsDemand(0);
        Post created = postService.publish(post);
        assertNotNull(created);

        boolean result = postService.deleteMy(created.getId(), testUser.getId());
        assertTrue(result, "作者删除自己的帖子应该成功");
    }

    @Test
    @Order(11)
    @DisplayName("获取帖子总数")
    void testGetPostCount() {
        int count = postService.getPostCount();
        assertTrue(count >= 0, "帖子数量应该 >= 0");
    }

    @Test
    @Order(12)
    @DisplayName("用户无权删除他人的帖子")
    void testDeleteNotOwner() {
        assertNotNull(testUser, "测试用户应该存在");
        assertNotNull(testUser2, "第二个测试用户应该存在");

        if (testPost != null) {
            boolean result = postService.deleteMy(testPost.getId(), testUser2.getId());
            assertFalse(result, "用户不能删除他人的帖子");
        }
    }
}
