package com.bbs.entity;

public class PointLog {
    private Long id;
    private Long userId;
    private String changeType;
    private Integer points;
    private Integer balance;
    private Long relateId;
    private String remark;
    private java.sql.Timestamp createTime;

    public PointLog() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    public Integer getBalance() { return balance; }
    public void setBalance(Integer balance) { this.balance = balance; }
    public Long getRelateId() { return relateId; }
    public void setRelateId(Long relateId) { this.relateId = relateId; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public java.sql.Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(java.sql.Timestamp createTime) { this.createTime = createTime; }
}