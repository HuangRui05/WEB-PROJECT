package com.bbs.service;

import com.bbs.dao.PointLogDao;
import com.bbs.dao.PostDao;
import com.bbs.dao.ReplyDao;
import com.bbs.dao.SectionDao;
import com.bbs.dao.UserDao;
import com.bbs.entity.Post;
import com.bbs.entity.PointLog;
import com.bbs.entity.Reply;
import com.bbs.entity.User;

import java.util.List;

public class PostService {
    private final PostDao postDao = new PostDao();
    private final ReplyDao replyDao = new ReplyDao();
    private final SectionDao sectionDao = new SectionDao();
    private final UserDao userDao = new UserDao();
    private final PointLogDao pointLogDao = new PointLogDao();

    public Post publish(Post post) {
        // 需求帖检查积分并冻结
        if (post.getIsDemand() != null && post.getIsDemand() == 1 && post.getPointsReward() != null && post.getPointsReward() > 0) {
            User user = userDao.findById(post.getUserId());
            if (user == null || user.getPoints() < post.getPointsReward()) {
                return null; // 积分不足
            }
            // 冻结积分
            if (!userDao.freezePoints(post.getUserId(), post.getPointsReward())) {
                return null; // 冻结失败
            }
            post.setFrozenPoints(post.getPointsReward());
            // 记录积分冻结变动
            PointLog log = new PointLog();
            log.setUserId(post.getUserId());
            log.setChangeType("frozen");
            log.setPoints(-post.getPointsReward());
            User updatedUser = userDao.findById(post.getUserId());
            log.setBalance(updatedUser.getPoints());
            log.setRelateId(post.getId());
            log.setRemark("发布需求帖冻结悬赏积分");
            pointLogDao.insert(log);
        }
        if (postDao.insert(post)) {
            sectionDao.updateCounts(post.getSectionId());
            return post;
        }
        return null;
    }

    public Post getById(Long id) {
        Post post = postDao.findById(id);
        if (post != null) {
            // 增加浏览量
            post.setViewCount(post.getViewCount() + 1);
        }
        return post;
    }

    public List<Post> getBySection(Long sectionId, int page, int size) {
        return postDao.findBySection(sectionId, page, size);
    }

    public List<Post> search(String keyword, int page, int size) {
        return postDao.findAll(page, size, keyword);
    }

    public boolean update(Post post, boolean isAdminOrAuthor) {
        if (!isAdminOrAuthor) return false;
        return postDao.update(post);
    }

    public boolean delete(Long id, boolean isAdmin) {
        if (!isAdmin) return false;
        Post post = postDao.findById(id);
        if (post == null) return false;
        // 删除帖子关联的评论
        replyDao.deleteByPost(id);
        if (postDao.delete(id)) {
            sectionDao.updateCounts(post.getSectionId());
            return true;
        }
        return false;
    }

    public boolean deleteMy(Long postId, Long userId) {
        Post post = postDao.findById(postId);
        if (post == null) return false;
        if (!post.getUserId().equals(userId)) return false;
        // 删除帖子关联的评论
        replyDao.deleteByPost(postId);
        if (postDao.delete(postId)) {
            sectionDao.updateCounts(post.getSectionId());
            return true;
        }
        return false;
    }

    public List<Post> getMyPosts(Long userId) {
        return postDao.findByUser(userId);
    }

    public List<Post> getAllPosts() {
        return postDao.findAllPosts();
    }

    public boolean setTop(Long postId, boolean top) {
        Post post = postDao.findById(postId);
        if (post == null) return false;
        post.setIsTop(top ? 1 : 0);
        return postDao.update(post);
    }

    public boolean setGood(Long postId, boolean good) {
        Post post = postDao.findById(postId);
        if (post == null) return false;
        post.setIsGood(good ? 1 : 0);
        return postDao.update(post);
    }

    public Reply reply(Reply reply) {
        if (replyDao.insert(reply)) {
            postDao.updateReplyCount(reply.getPostId());
            return reply;
        }
        return null;
    }

    public List<Reply> getReplies(Long postId) {
        return replyDao.findByPost(postId);
    }

    public boolean acceptAnswer(Long replyId, Long postAuthorId) {
        Reply reply = replyDao.findById(replyId);
        if (reply == null) {
            System.out.println("acceptAnswer: reply not found, replyId=" + replyId);
            return false;
        }

        Post post = postDao.findById(reply.getPostId());
        if (post == null) {
            System.out.println("acceptAnswer: post not found, postId=" + reply.getPostId());
            return false;
        }
        System.out.println("acceptAnswer: postAuthorId=" + postAuthorId + ", post.userId=" + post.getUserId() + ", equals=" + post.getUserId().equals(postAuthorId));
        if (!post.getUserId().equals(postAuthorId)) {
            System.out.println("acceptAnswer: not the post author");
            return false;
        }
        if (post.getIsSolved() != null && post.getIsSolved() == 1) return false; // 已解决无法再次采纳

        int frozenPoints = post.getFrozenPoints() != null ? post.getFrozenPoints() : 0;
        if (frozenPoints > 0) {
            // 解冻并转给回答者，同时从发帖者常规积分中扣除
            if (!userDao.unfreezeAndTransfer(post.getUserId(), reply.getUserId(), frozenPoints)) {
                return false;
            }
            // 从发帖者常规积分中扣除（因为unfreezeAndTransfer只处理冻结积分转出）
            userDao.updatePoints(post.getUserId(), -frozenPoints);
            // 更新回复为已采纳
            replyDao.accept(replyId, frozenPoints);

            // 记录积分转出日志（给回答者）
            User earnUser = userDao.findById(reply.getUserId());
            PointLog earnLog = new PointLog();
            earnLog.setUserId(reply.getUserId());
            earnLog.setChangeType("unfreeze_earn");
            earnLog.setPoints(frozenPoints);
            earnLog.setBalance(earnUser.getPoints());
            earnLog.setRelateId(reply.getId());
            earnLog.setRemark("回答被采纳获得悬赏积分");
            pointLogDao.insert(earnLog);

            // 记录积分扣除日志（给发帖者）
            User spendUser = userDao.findById(post.getUserId());
            PointLog spendLog = new PointLog();
            spendLog.setUserId(post.getUserId());
            spendLog.setChangeType("spend");
            spendLog.setPoints(-frozenPoints);
            spendLog.setBalance(spendUser.getPoints());
            spendLog.setRelateId(reply.getId());
            spendLog.setRemark("悬赏帖被采纳，扣除悬赏积分");
            pointLogDao.insert(spendLog);

            // 更新帖子状态
            post.setFrozenPoints(0);
            post.setIsSolved(1);
            post.setSolveTime(new java.sql.Timestamp(System.currentTimeMillis()));
            postDao.update(post);
        } else {
            // 无冻结积分时，直接发放（兼容旧数据）
            int rewardPoints = post.getPointsReward() != null ? post.getPointsReward() : 0;
            if (replyDao.accept(replyId, rewardPoints)) {
                if (rewardPoints > 0) {
                    // 扣除发帖者积分
                    userDao.updatePoints(reply.getUserId(), rewardPoints);
                    userDao.updatePoints(post.getUserId(), -rewardPoints);
                    User earnUser = userDao.findById(reply.getUserId());
                    PointLog earnLog = new PointLog();
                    earnLog.setUserId(reply.getUserId());
                    earnLog.setChangeType("earn");
                    earnLog.setPoints(rewardPoints);
                    earnLog.setBalance(earnUser.getPoints());
                    earnLog.setRelateId(reply.getId());
                    earnLog.setRemark("回答被采纳获得悬赏积分");
                    pointLogDao.insert(earnLog);

                    // 记录发帖者扣除积分日志
                    User spendUser = userDao.findById(post.getUserId());
                    PointLog spendLog = new PointLog();
                    spendLog.setUserId(post.getUserId());
                    spendLog.setChangeType("spend");
                    spendLog.setPoints(-rewardPoints);
                    spendLog.setBalance(spendUser.getPoints());
                    spendLog.setRelateId(reply.getId());
                    spendLog.setRemark("悬赏帖被采纳，扣除悬赏积分");
                    pointLogDao.insert(spendLog);
                }
                post.setIsSolved(1);
                post.setSolveTime(new java.sql.Timestamp(System.currentTimeMillis()));
                postDao.update(post);
            }
        }
        return true;
    }

    public boolean cancelDemand(Long postId, Long userId) {
        Post post = postDao.findById(postId);
        if (post == null || !post.getUserId().equals(userId)) return false;
        if (post.getIsSolved() != null && post.getIsSolved() == 1) return false; // 已解决无法取消

        int frozenPoints = post.getFrozenPoints() != null ? post.getFrozenPoints() : 0;
        if (frozenPoints > 0) {
            // 退还冻结积分
            if (!userDao.unfreezePoints(post.getUserId(), frozenPoints)) {
                return false;
            }
            // 记录退还日志
            User user = userDao.findById(post.getUserId());
            PointLog log = new PointLog();
            log.setUserId(post.getUserId());
            log.setChangeType("unfreeze_return");
            log.setPoints(frozenPoints);
            log.setBalance(user.getPoints());
            log.setRelateId(post.getId());
            log.setRemark("需求帖取消退还冻结积分");
            pointLogDao.insert(log);
        }

        // 更新帖子状态为已关闭
        post.setIsSolved(1);
        post.setSolveTime(new java.sql.Timestamp(System.currentTimeMillis()));
        return postDao.update(post);
    }

    public int getPostCount() {
        return postDao.count();
    }

    public int cleanupOrphanReplies() {
        return replyDao.deleteOrphanReplies();
    }
}