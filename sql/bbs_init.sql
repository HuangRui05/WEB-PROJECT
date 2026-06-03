-- BBS论坛数据库建表脚本
-- 数据库: bbs_db
-- 连接信息: 127.0.0.1:3306, root/123456

DROP DATABASE IF EXISTS bbs_db;
CREATE DATABASE bbs_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bbs_db;

-- ----------------------------
-- 1. 用户表 (user)
-- ----------------------------
CREATE TABLE user (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username        VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password        VARCHAR(128) NOT NULL COMMENT '密码(MD5加密)',
    nickname        VARCHAR(50) COMMENT '昵称',
    email           VARCHAR(100) COMMENT '邮箱',
    phone           VARCHAR(20) COMMENT '联系电话',
    contact_type    VARCHAR(50) COMMENT '联系方式类型(如:微信、QQ)',
    work_nature     VARCHAR(50) COMMENT '工作性质(如:IT、教育、自由职业)',
    work_location   VARCHAR(100) COMMENT '工作地点',
    avatar          VARCHAR(255) DEFAULT '/static/img/default-avatar.png' COMMENT '头像路径',
    points          INT DEFAULT 0 COMMENT '积分余额',
    frozen_points   INT DEFAULT 0 COMMENT '冻结积分',
    role            TINYINT DEFAULT 0 COMMENT '角色: 0-普通用户, 1-版主, 2-管理员',
    status          TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 2. 板块表 (section)
-- ----------------------------
CREATE TABLE section (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '板块ID',
    name            VARCHAR(100) NOT NULL COMMENT '板块名称',
    description     VARCHAR(500) COMMENT '板块描述',
    icon            VARCHAR(255) COMMENT '板块图标',
    sort_order      INT DEFAULT 0 COMMENT '排序(数字越小越靠前)',
    topic_count     INT DEFAULT 0 COMMENT '帖子数量',
    post_count      INT DEFAULT 0 COMMENT '回复数量',
    moderator_id    BIGINT COMMENT '版主ID',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (moderator_id) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='板块表';

-- ----------------------------
-- 3. 帖子表 (post)
-- ----------------------------
CREATE TABLE post (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '帖子ID',
    section_id      BIGINT NOT NULL COMMENT '所属板块ID',
    user_id         BIGINT NOT NULL COMMENT '作者ID',
    title           VARCHAR(200) NOT NULL COMMENT '标题',
    content         TEXT NOT NULL COMMENT '内容',
    points_reward   INT DEFAULT 0 COMMENT '悬赏积分(需求帖专用)',
    frozen_points   INT DEFAULT 0 COMMENT '冻结积分(需求帖专用)',
    is_demand       TINYINT DEFAULT 0 COMMENT '是否需求帖: 0-普通帖, 1-需求帖',
    is_solved       TINYINT DEFAULT 0 COMMENT '是否已解决: 0-未解决, 1-已解决/已关闭',
    is_top          TINYINT DEFAULT 0 COMMENT '是否置顶: 0-否, 1-是',
    is_good         TINYINT DEFAULT 0 COMMENT '是否加精: 0-否, 1-是',
    view_count      INT DEFAULT 0 COMMENT '浏览量',
    reply_count     INT DEFAULT 0 COMMENT '回复数',
    status          TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 0-删除',
    solve_time      DATETIME COMMENT '解决/关闭时间',
    expire_time     DATETIME COMMENT '过期时间',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (section_id) REFERENCES section(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';

-- ----------------------------
-- 4. 回复表 (reply)
-- ----------------------------
CREATE TABLE reply (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '回复ID',
    post_id         BIGINT NOT NULL COMMENT '所属帖子ID',
    user_id         BIGINT NOT NULL COMMENT '回复者ID',
    content         TEXT NOT NULL COMMENT '回复内容',
    is_accept       TINYINT DEFAULT 0 COMMENT '是否被采纳: 0-否, 1-是',
    points_earned   INT DEFAULT 0 COMMENT '获得的积分',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '回复时间',
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回复表';

-- ----------------------------
-- 5. 积分变动表 (point_log)
-- ----------------------------
CREATE TABLE point_log (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id         BIGINT NOT NULL COMMENT '用户ID',
    change_type     VARCHAR(30) NOT NULL COMMENT '变动类型: frozen-冻结积分, unfreeze_earn-解冻转出, unfreeze_return-解冻退还, reward-悬赏支出, earn-获得悬赏, register-注册奖励, daily-每日签到',
    points          INT NOT NULL COMMENT '变动积分数量(正数增加,负数减少)',
    balance         INT NOT NULL COMMENT '变动后余额',
    relate_id       BIGINT COMMENT '关联ID(帖子ID或回复ID)',
    remark          VARCHAR(200) COMMENT '备注',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分变动表';

-- ----------------------------
-- 初始化管理员账号 (密码: admin123)
-- ----------------------------
INSERT INTO user (username, password, nickname, role, points) VALUES
('admin', '0192023a7bb1a7522d17e2e72b5e4b1a', '管理员', 2, 100);
-- 0192023a7bb1a7522d17e2e72b5e4b1a 是 admin123 的MD5值

-- ----------------------------
-- 初始化板块数据
-- ----------------------------
INSERT INTO section (name, description, sort_order) VALUES
('新手入门', '新手报到、规则说明、问题咨询', 1),
('技术交流', '编程技术、软件开发、系统架构', 2),
('生活休闲', '闲聊灌水、生活分享、兴趣爱好', 3);