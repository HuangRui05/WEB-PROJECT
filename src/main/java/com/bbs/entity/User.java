package com.bbs.entity;

public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private String contactType;
    private String workNature;
    private String workLocation;
    private String avatar;
    private Integer points;
    private Integer frozenPoints;
    private Integer role; // 0-普通用户, 1-版主, 2-管理员
    private Integer status;
    private java.sql.Timestamp createTime;
    private java.sql.Timestamp updateTime;

    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getContactType() { return contactType; }
    public void setContactType(String contactType) { this.contactType = contactType; }
    public String getWorkNature() { return workNature; }
    public void setWorkNature(String workNature) { this.workNature = workNature; }
    public String getWorkLocation() { return workLocation; }
    public void setWorkLocation(String workLocation) { this.workLocation = workLocation; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    public Integer getFrozenPoints() { return frozenPoints; }
    public void setFrozenPoints(Integer frozenPoints) { this.frozenPoints = frozenPoints; }
    public Integer getRole() { return role; }
    public void setRole(Integer role) { this.role = role; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public java.sql.Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(java.sql.Timestamp createTime) { this.createTime = createTime; }
    public java.sql.Timestamp getUpdateTime() { return updateTime; }
    public void setUpdateTime(java.sql.Timestamp updateTime) { this.updateTime = updateTime; }

    public boolean isAdmin() { return role != null && role == 2; }
    public boolean isModerator() { return role != null && (role == 1 || role == 2); }
}