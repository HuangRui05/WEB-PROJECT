<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>帖子详情 - BBS论坛</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: "Microsoft YaHei", Arial, sans-serif; background: #f5f6f7; }

        .header {
            background: #2c3e50;
            color: white;
            padding: 0 30px;
            height: 56px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .header .logo { font-size: 20px; font-weight: bold; color: #fff; text-decoration: none; }
        .header a { color: #bdc3c7; text-decoration: none; padding: 8px 12px; }
        .header a:hover { color: #fff; }

        .container { max-width: 900px; margin: 20px auto; padding: 0 20px; }

        .post-detail {
            background: white;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.08);
            padding: 25px;
            margin-bottom: 20px;
        }
        .post-title { font-size: 22px; color: #2c3e50; margin-bottom: 15px; font-weight: 500; }
        .post-meta {
            font-size: 13px;
            color: #7f8c8d;
            padding-bottom: 15px;
            border-bottom: 1px solid #eee;
            margin-bottom: 15px;
            display: flex;
            gap: 20px;
            align-items: center;
        }
        .post-meta span { display: flex; align-items: center; gap: 5px; }
        .post-content {
            line-height: 1.8;
            color: #333;
            font-size: 15px;
            white-space: pre-wrap;
            min-height: 100px;
        }

        .reply-section {
            background: white;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.08);
            padding: 20px;
        }
        .reply-section h3 {
            margin-bottom: 15px;
            color: #2c3e50;
            font-size: 16px;
            padding-bottom: 10px;
            border-bottom: 2px solid #3498db;
        }

        .reply-item {
            padding: 15px 0;
            border-bottom: 1px solid #eee;
        }
        .reply-item:last-child { border-bottom: none; }
        .reply-meta {
            font-size: 12px;
            color: #95a5a6;
            margin-bottom: 8px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .reply-content { color: #333; line-height: 1.6; }

        .reply-form {
            margin-top: 20px;
            padding-top: 20px;
            border-top: 1px solid #eee;
        }
        .reply-form textarea {
            width: 100%;
            height: 100px;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 6px;
            resize: vertical;
            font-family: inherit;
            font-size: 14px;
        }
        .reply-form textarea:focus { outline: none; border-color: #3498db; }

        .btn {
            padding: 8px 16px;
            background: #3498db;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
        }
        .btn:hover { background: #2980b9; }
        .btn-small { padding: 4px 10px; font-size: 12px; }
        .btn-danger { background: #e74c3c; }
        .btn-danger:hover { background: #c0392b; }

        .back-link {
            display: inline-block;
            margin-bottom: 15px;
            color: #3498db;
            text-decoration: none;
            font-size: 14px;
        }
        .back-link:hover { text-decoration: underline; }

        .login-tip {
            background: #fff3cd;
            padding: 12px 20px;
            border-radius: 6px;
            color: #856404;
            font-size: 14px;
            text-align: center;
        }
        .login-tip a { color: #007bff; }
        .post-badges { display: inline-flex; gap: 5px; margin-right: 8px; vertical-align: middle; }
        .badge { padding: 2px 6px; border-radius: 3px; font-size: 11px; font-weight: bold; }
        .badge-top { background: #e74c3c; color: white; }
        .badge-good { background: #f39c12; color: white; }
        .badge-demand { background: #9b59b6; color: white; }
        .badge-solved { background: #27ae60; color: white; }
        .badge-pending { background: #e67e22; color: white; }
    </style>
</head>
<body>
<div class="header">
    <a href="index.jsp" class="logo">BBS论坛</a>
    <div>
        <a href="index.jsp">首页</a>
    </div>
</div>

<div class="container">
    <a class="back-link" href="index.jsp">&lt; 返回首页</a>

    <div class="post-detail" id="postDetail">
        <div class="post-title" id="postTitle">加载中...</div>
        <div class="post-meta" id="postMeta"></div>
        <div class="post-content" id="postContent"></div>
    </div>

    <div class="reply-section">
        <h3>评论列表</h3>
        <div id="replyList">
            <p>暂无评论</p>
        </div>

        <div class="reply-form">
            <% if (session.getAttribute("user") != null) { %>
                <textarea id="replyContent" placeholder="请输入评论内容..."></textarea>
                <br><br>
                <button class="btn" onclick="submitReply()">发表评论</button>
            <% } else { %>
                <div class="login-tip">
                    您尚未登录，<a href="login.jsp">登录</a> 后可以发表评论
                </div>
            <% } %>
        </div>
    </div>
</div>

<script>
var postId = <%= request.getParameter("id") != null ? request.getParameter("id") : "null" %>;
var currentUserId = <%= session.getAttribute("user") != null ? ((com.bbs.entity.User)session.getAttribute("user")).getId() : "null" %>;
var currentUserRole = <%= session.getAttribute("user") != null ? ((com.bbs.entity.User)session.getAttribute("user")).getRole() : "null" %>;
var postUserId = null;
var postIsTop = 0;
var postIsGood = 0;
var isDemand = 0;
var isSolved = 0;

function loadPost() {
    if (!postId) {
        document.getElementById('postTitle').textContent = '帖子不存在';
        return;
    }
    fetch('post/' + postId)
        .then(r => r.json())
        .then(data => {
            if (!data.success) {
                document.getElementById('postTitle').textContent = '帖子不存在';
                return;
            }
            var p = data.post;
            postUserId = p.userId;
            postIsTop = p.isTop || 0;
            postIsGood = p.isGood || 0;
            isDemand = p.isDemand || 0;
            isSolved = p.isSolved || 0;

            var badges = '';
            if (p.isTop == 1) badges += '<span class="badge badge-top">置顶</span>';
            if (p.isGood == 1) badges += '<span class="badge badge-good">精</span>';
            if (isDemand == 1) {
                badges += '<span class="badge" style="background:#9b59b6;color:white;">悬赏 ' + (p.pointsReward || 0) + ' 积分</span>';
                if (isSolved == 1) {
                    badges += '<span class="badge" style="background:#27ae60;color:white;">已解决</span>';
                } else {
                    badges += '<span class="badge" style="background:#e67e22;color:white;">待采纳</span>';
                }
            }
            document.getElementById('postTitle').innerHTML = '<span class="post-badges">' + badges + '</span>' + p.title;
            var metaHtml = '<span>作者: ' + p.authorName + '</span>' +
                '<span>板块: ' + (p.sectionName || '') + '</span>' +
                '<span>浏览: ' + p.viewCount + '</span>' +
                '<span>回复: ' + p.replyCount + '</span>' +
                '<span>' + p.createTime + '</span>';

            // 只有作者或管理员可以删除帖子
            if (currentUserId !== null && (currentUserId == p.userId || currentUserRole == 2)) {
                metaHtml += ' <button class="btn btn-danger btn-small" onclick="deletePost()" style="margin-left:10px;">删除帖子</button>';
            }
            // 管理员专属：置顶、加精按钮
            if (currentUserRole == 2) {
                var isTop = p.isTop == 1 ? '取消置顶' : '置顶';
                var isGood = p.isGood == 1 ? '取消加精' : '加精';
                metaHtml += ' <button class="btn btn-small" onclick="setTop()" style="margin-left:5px;">' + isTop + '</button>';
                metaHtml += ' <button class="btn btn-small" onclick="setGood()" style="margin-left:5px;">' + isGood + '</button>';
            }
            document.getElementById('postMeta').innerHTML = metaHtml;
            document.getElementById('postContent').textContent = p.content;
            loadReplies();
        });
}

function loadReplies() {
    fetch('reply/' + postId)
        .then(r => r.json())
        .then(data => {
            if (data.success && data.data.length > 0) {
                var html = '';
                data.data.forEach(function(r) {
                    var acceptBtn = '';
                    // 需求帖且未解决时，显示采纳按钮（仅对帖子作者）
                    if (isDemand == 1 && isSolved == 0 && currentUserId !== null && currentUserId == postUserId && r.isAccept != 1) {
                        acceptBtn = ' <button class="btn btn-small" onclick="acceptAnswer(' + r.id + ')" style="background:#27ae60;margin-left:5px;">采纳</button>';
                    }
                    if (r.isAccept == 1) {
                        acceptBtn = ' <span class="badge" style="background:#27ae60;color:white;margin-left:5px;">已采纳</span>';
                    }
                    var deleteBtn = '';
                    if (currentUserId !== null && (currentUserId == r.userId || currentUserRole == 2)) {
                        deleteBtn = ' <button class="btn btn-danger btn-small" onclick="deleteReply(' + r.id + ')">删除</button>';
                    }
                    html += '<div class="reply-item">' +
                        '<div class="reply-meta">' +
                        '<span>' + r.authorName + ' | ' + r.createTime + '</span>' +
                        acceptBtn + deleteBtn +
                        '</div>' +
                        '<div class="reply-content">' + r.content + '</div>' +
                        '</div>';
                });
                document.getElementById('replyList').innerHTML = html;
            }
        });
}

function acceptAnswer(replyId) {
    if (!confirm('确定要采纳这条回答吗？采纳后悬赏积分将自动转给回答者。')) return;
    fetch('post/acceptAnswer', {
        method: 'POST',
        body: new URLSearchParams({ replyId: replyId })
    })
    .then(r => r.json())
    .then(data => {
        if (data.success) {
            alert('采纳成功');
            location.reload();
        } else {
            alert(data.message || '采纳失败');
        }
    });
}

function submitReply() {
    var content = document.getElementById('replyContent').value.trim();
    if (!content) {
        alert('请输入评论内容');
        return;
    }
    var formData = new FormData();
    formData.append('postId', postId);
    formData.append('content', content);
    fetch('post/reply', {
        method: 'POST',
        body: new URLSearchParams(formData)
    })
    .then(r => r.json())
    .then(data => {
        if (data.success) {
            alert('评论成功');
            location.reload();
        } else {
            alert(data.message || '评论失败');
        }
    });
}

function deletePost() {
    if (!confirm('确定要删除这个帖子吗？')) return;
    fetch('post/deleteMy?id=' + postId, { method: 'POST' })
        .then(r => r.json())
        .then(data => {
            if (data.success) {
                alert('删除成功');
                location.href = 'index.jsp';
            } else {
                alert(data.message || '删除失败');
            }
        });
}

function deleteReply(replyId) {
    if (!confirm('确定要删除这条评论吗？')) return;
    fetch('reply/delete?id=' + replyId, { method: 'POST' })
        .then(r => r.json())
        .then(data => {
            if (data.success) {
                alert('删除成功');
                location.reload();
            } else {
                alert(data.message || '删除失败');
            }
        });
}

function setTop() {
    var newTop = postIsTop == 1 ? 0 : 1;
    fetch('post/setTop?id=' + postId + '&top=' + newTop, { method: 'POST' })
        .then(r => r.json())
        .then(data => {
            if (data.success) {
                location.reload();
            } else {
                alert(data.message || '操作失败');
            }
        });
}

function setGood() {
    var newGood = postIsGood == 1 ? 0 : 1;
    fetch('post/setGood?id=' + postId + '&good=' + newGood, { method: 'POST' })
        .then(r => r.json())
        .then(data => {
            if (data.success) {
                location.reload();
            } else {
                alert(data.message || '操作失败');
            }
        });
}

loadPost();
</script>
</body>
</html>
