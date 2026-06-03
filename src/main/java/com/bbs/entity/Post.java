package com.bbs.entity;

public class Post {
    private Long id;
    private Long sectionId;
    private Long userId;
    private String title;
    private String content;
    private Integer pointsReward;
    private Integer frozenPoints;
    private Integer isDemand;
    private Integer isSolved;
    private Integer isTop;
    private Integer isGood;
    private Integer viewCount;
    private Integer replyCount;
    private Integer status;
    private java.sql.Timestamp solveTime;
    private java.sql.Timestamp expireTime;
    private java.sql.Timestamp createTime;
    private java.sql.Timestamp updateTime;

    private User author;
    private Section section;

    public Post() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSectionId() { return sectionId; }
    public void setSectionId(Long sectionId) { this.sectionId = sectionId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getPointsReward() { return pointsReward; }
    public void setPointsReward(Integer pointsReward) { this.pointsReward = pointsReward; }
    public Integer getFrozenPoints() { return frozenPoints; }
    public void setFrozenPoints(Integer frozenPoints) { this.frozenPoints = frozenPoints; }
    public Integer getIsDemand() { return isDemand; }
    public void setIsDemand(Integer isDemand) { this.isDemand = isDemand; }
    public Integer getIsTop() { return isTop; }
    public void setIsTop(Integer isTop) { this.isTop = isTop; }
    public Integer getIsGood() { return isGood; }
    public void setIsGood(Integer isGood) { this.isGood = isGood; }
    public Integer getIsSolved() { return isSolved; }
    public void setIsSolved(Integer isSolved) { this.isSolved = isSolved; }
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public Integer getReplyCount() { return replyCount; }
    public void setReplyCount(Integer replyCount) { this.replyCount = replyCount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public java.sql.Timestamp getSolveTime() { return solveTime; }
    public void setSolveTime(java.sql.Timestamp solveTime) { this.solveTime = solveTime; }
    public java.sql.Timestamp getExpireTime() { return expireTime; }
    public void setExpireTime(java.sql.Timestamp expireTime) { this.expireTime = expireTime; }
    public java.sql.Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(java.sql.Timestamp createTime) { this.createTime = createTime; }
    public java.sql.Timestamp getUpdateTime() { return updateTime; }
    public void setUpdateTime(java.sql.Timestamp updateTime) { this.updateTime = updateTime; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public Section getSection() { return section; }
    public void setSection(Section section) { this.section = section; }
}