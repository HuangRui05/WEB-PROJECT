package com.bbs.entity;

public class Section {
    private Long id;
    private String name;
    private String description;
    private String icon;
    private Integer sortOrder;
    private Integer topicCount;
    private Integer postCount;
    private Long moderatorId;
    private java.sql.Timestamp createTime;
    private java.sql.Timestamp updateTime;

    private User moderator; // 版主对象

    public Section() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getTopicCount() { return topicCount; }
    public void setTopicCount(Integer topicCount) { this.topicCount = topicCount; }
    public Integer getPostCount() { return postCount; }
    public void setPostCount(Integer postCount) { this.postCount = postCount; }
    public Long getModeratorId() { return moderatorId; }
    public void setModeratorId(Long moderatorId) { this.moderatorId = moderatorId; }
    public java.sql.Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(java.sql.Timestamp createTime) { this.createTime = createTime; }
    public java.sql.Timestamp getUpdateTime() { return updateTime; }
    public void setUpdateTime(java.sql.Timestamp updateTime) { this.updateTime = updateTime; }
    public User getModerator() { return moderator; }
    public void setModerator(User moderator) { this.moderator = moderator; }
}