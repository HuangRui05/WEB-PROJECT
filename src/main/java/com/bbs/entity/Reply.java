package com.bbs.entity;

public class Reply {
    private Long id;
    private Long postId;
    private Long userId;
    private String content;
    private Integer isAccept;
    private Integer pointsEarned;
    private java.sql.Timestamp createTime;
    private java.sql.Timestamp updateTime;

    private User author;

    public Reply() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getIsAccept() { return isAccept; }
    public void setIsAccept(Integer isAccept) { this.isAccept = isAccept; }
    public Integer getPointsEarned() { return pointsEarned; }
    public void setPointsEarned(Integer pointsEarned) { this.pointsEarned = pointsEarned; }
    public java.sql.Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(java.sql.Timestamp createTime) { this.createTime = createTime; }
    public java.sql.Timestamp getUpdateTime() { return updateTime; }
    public void setUpdateTime(java.sql.Timestamp updateTime) { this.updateTime = updateTime; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
}