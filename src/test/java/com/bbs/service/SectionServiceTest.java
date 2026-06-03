package com.bbs.service;

import com.bbs.dao.UserDao;
import com.bbs.entity.Section;
import com.bbs.entity.User;
import com.bbs.util.TestDBUtil;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SectionServiceTest {

    private static final SectionService sectionService = new SectionService();
    private static Section testSection;
    private static User testUser;

    @BeforeAll
    static void setUp() {
        TestDBUtil.initTestData();
        testUser = sectionService.getAll().get(0).getModerator();
        if (testUser == null) {
            testUser = new UserDao().findByUsername("testuser");
        }
    }

    @AfterAll
    static void tearDown() {
        TestDBUtil.cleanTestData();
    }

    @Test
    @Order(1)
    @DisplayName("获取所有板块")
    void testGetAll() {
        List<Section> sections = sectionService.getAll();
        assertNotNull(sections, "应该返回板块列表");
        assertTrue(sections.size() >= 1, "至少应该有1个板块");
    }

    @Test
    @Order(2)
    @DisplayName("根据ID获取板块")
    void testGetById() {
        List<Section> sections = sectionService.getAll();
        assertTrue(sections.size() >= 1, "应该有板块存在");

        Section first = sections.get(0);
        Section fetched = sectionService.getById(first.getId());
        assertNotNull(fetched, "应该能获取板块");
        assertEquals(first.getName(), fetched.getName());
    }

    @Test
    @Order(3)
    @DisplayName("创建新板块")
    void testCreate() {
        // 清理可能存在的测试板块
        Section existing = sectionService.findByName("单元测试板块");
        if (existing != null) {
            sectionService.delete(existing.getId());
        }

        Section section = new Section();
        section.setName("单元测试板块");
        section.setDescription("用于单元测试的板块");

        boolean result = sectionService.create(section);
        assertTrue(result, "创建板块应该成功");

        Section created = sectionService.findByName("单元测试板块");
        assertNotNull(created, "应该能查到刚创建的板块");
        assertEquals("单元测试板块", created.getName());
        assertEquals("用于单元测试的板块", created.getDescription());

        testSection = created;
    }

    @Test
    @Order(4)
    @DisplayName("根据名称查找板块")
    void testFindByName() {
        Section section = sectionService.findByName("测试板块");
        assertNotNull(section, "应该能找到测试板块");
        assertEquals("测试板块", section.getName());
    }

    @Test
    @Order(5)
    @DisplayName("更新板块")
    void testUpdate() {
        if (testSection == null) {
            testSection = sectionService.findByName("单元测试板块");
        }
        assertNotNull(testSection, "测试板块应该存在");

        testSection.setDescription("修改后的描述");
        testSection.setSortOrder(99);

        boolean result = sectionService.update(testSection);
        assertTrue(result, "更新板块应该成功");

        Section updated = sectionService.getById(testSection.getId());
        assertEquals("修改后的描述", updated.getDescription());
        assertEquals(99, updated.getSortOrder());
    }

    @Test
    @Order(6)
    @DisplayName("删除板块")
    void testDelete() {
        // 先创建一个待删除的板块
        Section section = new Section();
        section.setName("待删除板块");
        section.setDescription("用于删除测试");
        section.setSortOrder(100);
        sectionService.create(section);

        Section toDelete = sectionService.findByName("待删除板块");
        assertNotNull(toDelete, "待删除板块应该存在");

        boolean result = sectionService.delete(toDelete.getId());
        assertTrue(result, "删除板块应该成功");

        Section deleted = sectionService.getById(toDelete.getId());
        assertNull(deleted, "板块应该已被删除");
    }

    @Test
    @Order(7)
    @DisplayName("设置板块版主")
    void testSetModerator() {
        if (testSection == null) {
            testSection = sectionService.findByName("单元测试板块");
        }
        assertNotNull(testSection, "测试板块应该存在");
        assertNotNull(testUser, "测试用户应该存在");

        boolean result = sectionService.setModerator(testSection.getId(), testUser.getId());
        assertTrue(result, "设置版主应该成功");

        Section updated = sectionService.getById(testSection.getId());
        assertNotNull(updated.getModerator(), "版主应该已设置");
        assertEquals(testUser.getId(), updated.getModerator().getId());
    }

    @Test
    @Order(8)
    @DisplayName("创建板块-名称重复")
    void testCreateDuplicateName() {
        Section existing = sectionService.findByName("测试板块");
        assertNotNull(existing, "测试板块应该存在");

        Section newSection = new Section();
        newSection.setName("测试板块"); // 重复名称
        newSection.setDescription("重复描述");

        boolean result = sectionService.create(newSection);
        assertFalse(result, "名称重复应该创建失败");
    }

    @Test
    @Order(9)
    @DisplayName("获取板块统计信息")
    void testSectionCounts() {
        List<Section> sections = sectionService.getAll();
        assertTrue(sections.size() >= 1, "应该有板块存在");

        for (Section s : sections) {
            assertNotNull(s.getTopicCount(), "topicCount不应该为null");
            assertNotNull(s.getPostCount(), "postCount不应该为null");
        }
    }
}
