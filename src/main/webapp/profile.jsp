<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>个人中心 - BBS论坛</title>
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

        .panel {
            background: white;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .tabs {
            display: flex;
            background: #ecf0f1;
            border-bottom: 1px solid #dce4ec;
        }
        .tab {
            padding: 15px 25px;
            cursor: pointer;
            color: #7f8c8d;
            font-size: 14px;
            border-bottom: 2px solid transparent;
            transition: all 0.2s;
        }
        .tab:hover { color: #2c3e50; }
        .tab.active {
            color: #3498db;
            border-bottom-color: #3498db;
            background: white;
        }

        .tab-content { padding: 25px; display: none; }
        .tab-content.active { display: block; }

        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; color: #555; font-size: 14px; }
        .form-group input {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }
        .form-group input:focus { outline: none; border-color: #3498db; }
        .form-group input[readonly] { background: #f5f5f5; }

        .btn { padding: 10px 25px; background: #3498db; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 14px; }
        .btn:hover { background: #2980b9; }
        .btn-danger { background: #e74c3c; }
        .btn-danger:hover { background: #c0392b; }
        .btn-small { padding: 4px 10px; font-size: 12px; }

        .info-item {
            padding: 12px 0;
            border-bottom: 1px solid #eee;
            display: flex;
            align-items: center;
        }
        .info-item:last-child { border-bottom: none; }
        .info-item .label { width: 100px; color: #7f8c8d; font-size: 13px; }
        .info-item .value { color: #2c3e50; font-size: 14px; }

        .post-table { width: 100%; border-collapse: collapse; }
        .post-table th, .post-table td {
            padding: 12px 10px;
            text-align: left;
            border-bottom: 1px solid #eee;
            font-size: 13px;
        }
        .post-table th { background: #f8f9fa; color: #7f8c8d; font-weight: 500; }
        .post-table td a { color: #3498db; text-decoration: none; }
        .post-table td a:hover { text-decoration: underline; }

        .empty-tip { text-align: center; padding: 40px; color: #95a5a6; }
    </style>
</head>
<body>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<div class="header">
    <a href="index.jsp" class="logo">BBS论坛</a>
    <div>
        <a href="index.jsp">首页</a>
        <a href="user/logout">退出</a>
    </div>
</div>

<div class="container">
    <div class="panel">
        <div class="tabs">
            <div class="tab active" onclick="switchTab('profile')">个人资料</div>
            <div class="tab" onclick="switchTab('posts')">我的帖子</div>
            <div class="tab" onclick="switchTab('replies')">我的评论</div>
        </div>

        <div id="tab-profile" class="tab-content active">
            <form id="profileForm">
                <div class="info-item">
                    <span class="label">用户名</span>
                    <span class="value" id="username"></span>
                </div>
                <div class="info-item">
                    <span class="label">积分余额</span>
                    <span class="value" id="points"></span>
                </div>
                <div class="info-item">
                    <span class="label">冻结积分</span>
                    <span class="value" id="frozenPoints"></span>
                </div>
                <div style="margin-top: 20px; padding-top: 20px; border-top: 1px solid #eee;">
                    <h4 style="margin-bottom: 15px; color: #2c3e50;">修改个人资料</h4>
                    <div class="form-group">
                        <label>昵称</label>
                        <input type="text" name="nickname" id="nickname" maxlength="50">
                    </div>
                    <div class="form-group">
                        <label>邮箱</label>
                        <input type="email" name="email" id="email" maxlength="100">
                    </div>
                    <div class="form-group">
                        <label>手机号</label>
                        <input type="text" name="phone" id="phone" maxlength="20">
                    </div>
                    <div class="form-group">
                        <label>工作性质</label>
                        <select name="workNature" id="workNature">
                            <option value="">请选择</option>
                            <option value="全职">全职</option>
                            <option value="兼职">兼职</option>
                            <option value="自由职业">自由职业</option>
                            <option value="学生">学生</option>
                            <option value="待业">待业</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>工作地点</label>
                        <input type="text" name="workLocation" id="workLocation" maxlength="100" placeholder="如：北京、上海、广州">
                    </div>
                    <button type="submit" class="btn">保存修改</button>
                </div>
            </form>
        </div>

        <div id="tab-posts" class="tab-content">
            <div id="myPostsList">
                <p class="empty-tip">加载中...</p>
            </div>
        </div>

        <div id="tab-replies" class="tab-content">
            <div id="myRepliesList">
                <p class="empty-tip">加载中...</p>
            </div>
        </div>
    </div>
</div>

<script>
var currentUser = null;

function switchTab(tab) {
    document.querySelectorAll('.tab').forEach(function(t) { t.classList.remove('active'); });
    document.querySelectorAll('.tab-content').forEach(function(c) { c.classList.remove('active'); });
    document.querySelector('.tab:nth-child(' + (tab === 'profile' ? 1 : tab === 'posts' ? 2 : 3) + ')').classList.add('active');
    document.getElementById('tab-' + tab).classList.add('active');

    if (tab === 'posts') loadMyPosts();
    if (tab === 'replies') loadMyReplies();
}

fetch('user/info')
    .then(r => r.json())
    .then(data => {
        if (data.success) {
            currentUser = data.user;
            document.getElementById('username').textContent = currentUser.username;
            document.getElementById('points').textContent = currentUser.points || 0;
            document.getElementById('frozenPoints').textContent = currentUser.frozenPoints || 0;
            document.getElementById('nickname').value = currentUser.nickname || '';
            document.getElementById('email').value = currentUser.email || '';
            document.getElementById('phone').value = currentUser.phone || '';
            document.getElementById('contactType').value = currentUser.contactType || '';
            document.getElementById('workNature').value = currentUser.workNature || '';
            document.getElementById('workLocation').value = currentUser.workLocation || '';
            }
    });

document.getElementById('profileForm').onsubmit = function(e) {
    e.preventDefault();
    var formData = new FormData(this);
    fetch('user/update', {
        method: 'POST',
        body: new URLSearchParams(formData)
    })
    .then(r => r.json())
    .then(data => {
        if (data.success) {
            alert('保存成功');
            location.reload();
        } else {
            alert(data.message || '保存失败');
        }
    });
};

function loadMyPosts() {
    fetch('post/myPosts')
        .then(r => r.json())
        .then(data => {
            if (data.success && data.data.length > 0) {
                var html = '<table class="post-table"><thead><tr><th>标题</th><th>板块</th><th>回复</th><th>发布时间</th><th>状态</th><th>操作</th></tr></thead><tbody>';
                data.data.forEach(function(p) {
                    var statusHtml = '';
                    if (p.isDemand == 1) {
                        statusHtml = '<span style="color:#9b59b6;">悬赏 ' + (p.pointsReward || 0) + ' 积分</span>';
                        if (p.isSolved == 1) {
                            statusHtml += ' <span style="color:#27ae60;">已解决</span>';
                        } else {
                            statusHtml += ' <span style="color:#e67e22;">待采纳</span>';
                        }
                    } else {
                        statusHtml = '普通帖';
                    }
                    var actionHtml = '';
                    if (p.isDemand == 1 && p.isSolved != 1) {
                        actionHtml = '<button class="btn btn-danger btn-small" onclick="cancelDemand(' + p.id + ')">取消悬赏</button> ';
                    }
                    actionHtml += '<button class="btn btn-danger btn-small" onclick="deletePost(' + p.id + ')">删除</button>';
                    html += '<tr>' +
                        '<td><a href="post.jsp?id=' + p.id + '">' + p.title + '</a></td>' +
                        '<td>' + p.sectionName + '</td>' +
                        '<td>' + p.replyCount + '</td>' +
                        '<td>' + p.createTime + '</td>' +
                        '<td>' + statusHtml + '</td>' +
                        '<td>' + actionHtml + '</td>' +
                        '</tr>';
                });
                html += '</tbody></table>';
                document.getElementById('myPostsList').innerHTML = html;
            } else {
                document.getElementById('myPostsList').innerHTML = '<p class="empty-tip">您还没有发布过帖子</p>';
            }
        });
}

function loadMyReplies() {
    fetch('reply/listByUser')
        .then(r => r.json())
        .then(data => {
            if (data.success && data.data.length > 0) {
                var html = '<table class="post-table"><thead><tr><th>评论内容</th><th>评论时间</th><th>操作</th></tr></thead><tbody>';
                data.data.forEach(function(r) {
                    html += '<tr>' +
                        '<td style="max-width:400px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">' + r.content + '</td>' +
                        '<td>' + r.createTime + '</td>' +
                        '<td><button class="btn btn-danger btn-small" onclick="deleteReply(' + r.id + ')">删除</button></td>' +
                        '</tr>';
                });
                html += '</tbody></table>';
                document.getElementById('myRepliesList').innerHTML = html;
            } else {
                document.getElementById('myRepliesList').innerHTML = '<p class="empty-tip">您还没有发表过评论</p>';
            }
        });
}

function deletePost(postId) {
    if (!confirm('确定要删除这个帖子吗？')) return;
    fetch('post/deleteMy?id=' + postId, { method: 'POST' })
        .then(r => r.json())
        .then(data => {
            if (data.success) {
                alert('删除成功');
                loadMyPosts();
            } else {
                alert(data.message || '删除失败');
            }
        });
}

function cancelDemand(postId) {
    if (!confirm('确定要取消这个悬赏吗？取消后冻结的积分将退还到您的账户。')) return;
    fetch('post/cancelDemand?id=' + postId, { method: 'POST' })
        .then(r => r.json())
        .then(data => {
            if (data.success) {
                alert('取消成功，积分已退还');
                loadMyPosts();
            } else {
                alert(data.message || '取消失败');
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
                loadMyReplies();
            } else {
                alert(data.message || '删除失败');
            }
        });
}
</script>
</body>
</html>
