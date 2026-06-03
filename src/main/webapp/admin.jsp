<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>后台管理 - BBS论坛</title>
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

        .container { max-width: 1200px; margin: 20px auto; padding: 0 20px; }

        .layout { display: flex; gap: 20px; }
        .sidebar {
            width: 200px;
            background: white;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.08);
            padding: 15px 0;
        }
        .sidebar-menu {
            padding: 10px 20px;
            cursor: pointer;
            color: #555;
            font-size: 14px;
            border-left: 3px solid transparent;
            transition: all 0.2s;
        }
        .sidebar-menu:hover { background: #f8f9fa; color: #2c3e50; }
        .sidebar-menu.active {
            background: #e8f4fc;
            color: #3498db;
            border-left-color: #3498db;
        }

        .main-content {
            flex: 1;
            background: white;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.08);
            padding: 20px;
            min-height: 500px;
        }

        .page-title {
            font-size: 18px;
            color: #2c3e50;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 2px solid #3498db;
        }

        .data-table { width: 100%; border-collapse: collapse; }
        .data-table th, .data-table td {
            padding: 12px 10px;
            text-align: left;
            border-bottom: 1px solid #eee;
            font-size: 13px;
        }
        .data-table th { background: #f8f9fa; color: #7f8c8d; font-weight: 500; }
        .data-table td a { color: #3498db; text-decoration: none; }
        .data-table td a:hover { text-decoration: underline; }

        .btn { padding: 6px 12px; background: #3498db; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 12px; }
        .btn:hover { background: #2980b9; }
        .btn-sm { padding: 4px 8px; font-size: 11px; }
        .btn-danger { background: #e74c3c; }
        .btn-danger:hover { background: #c0392b; }
        .btn-success { background: #27ae60; }
        .btn-success:hover { background: #219a52; }

        .tag { padding: 2px 6px; border-radius: 3px; font-size: 10px; color: white; }
        .tag-top { background: #e67e22; }
        .tag-good { background: #9b59b6; }

        .empty-tip { text-align: center; padding: 40px; color: #95a5a6; }

        .form-inline { display: flex; gap: 10px; align-items: center; margin-bottom: 15px; }
        .form-inline input, .form-inline select {
            padding: 8px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 13px;
        }
        .form-inline input { flex: 1; }

        .section-form {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 6px;
            margin-bottom: 20px;
        }
        .section-form h4 { margin-bottom: 15px; color: #2c3e50; }
        .section-form .form-group { margin-bottom: 12px; }
        .section-form label { display: block; margin-bottom: 5px; color: #555; font-size: 13px; }
        .section-form input, .section-form textarea {
            width: 100%;
            padding: 8px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 13px;
        }
    </style>
</head>
<body>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    com.bbs.entity.User user = (com.bbs.entity.User) session.getAttribute("user");
    if (!user.isAdmin()) {
        response.sendRedirect("index.jsp");
        return;
    }
%>

<div class="header">
    <a href="index.jsp" class="logo">BBS论坛 - 管理后台</a>
    <div>
        <a href="index.jsp">返回首页</a>
        <a href="user/logout">退出</a>
    </div>
</div>

<div class="container">
    <div class="layout">
        <div class="sidebar">
            <div class="sidebar-menu active" onclick="switchPage('posts')">帖子管理</div>
            <div class="sidebar-menu" onclick="switchPage('replies')">评论管理</div>
            <div class="sidebar-menu" onclick="switchPage('sections')">板块管理</div>
        </div>

        <div class="main-content">
            <div id="page-posts" class="admin-page">
                <h2 class="page-title">帖子管理</h2>
                <div id="postsList"><p class="empty-tip">加载中...</p></div>
            </div>

            <div id="page-replies" class="admin-page" style="display:none;">
                <h2 class="page-title">评论管理</h2>
                <div style="margin-bottom:15px;padding:10px;background:#fff3cd;border-radius:4px;">
                    <button class="btn btn-danger" onclick="cleanupOrphanReplies()">清理孤立评论</button>
                    <span style="margin-left:10px;color:#856404;font-size:12px;">清理已被删除帖子的残留评论</span>
                </div>
                <div id="repliesList"><p class="empty-tip">加载中...</p></div>
            </div>

            <div id="page-sections" class="admin-page" style="display:none;">
                <h2 class="page-title">板块管理</h2>

                <div class="section-form">
                    <h4>添加/编辑板块</h4>
                    <input type="hidden" id="editSectionId">
                    <div class="form-group">
                        <label>板块名称</label>
                        <input type="text" id="sectionName" placeholder="请输入板块名称">
                    </div>
                    <div class="form-group">
                        <label>板块描述</label>
                        <input type="text" id="sectionDesc" placeholder="请输入板块描述">
                    </div>
                    <button class="btn btn-success" onclick="saveSection()">保存</button>
                    <button class="btn" onclick="clearSectionForm()" style="margin-left:10px;">取消</button>
                </div>

                <div id="sectionsList"><p class="empty-tip">加载中...</p></div>
            </div>
        </div>
    </div>
</div>

<script>
function switchPage(page) {
    document.querySelectorAll('.sidebar-menu').forEach(function(m) { m.classList.remove('active'); });
    document.querySelectorAll('.admin-page').forEach(function(p) { p.style.display = 'none'; });

    if (page === 'posts') {
        document.querySelector('.sidebar-menu:nth-child(1)').classList.add('active');
        document.getElementById('page-posts').style.display = 'block';
        loadAllPosts();
    } else if (page === 'replies') {
        document.querySelector('.sidebar-menu:nth-child(2)').classList.add('active');
        document.getElementById('page-replies').style.display = 'block';
        loadAllReplies();
    } else if (page === 'sections') {
        document.querySelector('.sidebar-menu:nth-child(3)').classList.add('active');
        document.getElementById('page-sections').style.display = 'block';
        loadSections();
    }
}

function loadAllPosts() {
    fetch('post/allPosts')
        .then(r => r.json())
        .then(data => {
            if (data.success && data.data.length > 0) {
                var html = '<table class="data-table"><thead><tr><th>ID</th><th>标题</th><th>板块</th><th>作者</th><th>回复</th><th>发布时间</th><th>状态</th><th>操作</th></tr></thead><tbody>';
                data.data.forEach(function(p) {
                    var status = '';
                    if (p.isTop) status += '<span class="tag tag-top">置顶</span> ';
                    if (p.isGood) status += '<span class="tag tag-good">精华</span>';
                    if (p.isDemand == 1) status += '<span class="tag" style="background:#9b59b6;">赏' + (p.pointsReward || 0) + '</span>';
                    var badges = '';
                    if (p.isTop) badges += '<span class="tag tag-top">顶</span> ';
                    if (p.isGood) badges += '<span class="tag tag-good">精</span>';
                    if (p.isDemand == 1) badges += '<span class="tag" style="background:#9b59b6;">赏</span>';
                    html += '<tr>' +
                        '<td>' + p.id + '</td>' +
                        '<td><span style="margin-right:5px;">' + badges + '</span><a href="post.jsp?id=' + p.id + '">' + p.title + '</a></td>' +
                        '<td>' + p.sectionName + '</td>' +
                        '<td>' + p.authorName + '</td>' +
                        '<td>' + p.replyCount + '</td>' +
                        '<td>' + p.createTime + '</td>' +
                        '<td>' + status + '</td>' +
                        '<td>' +
                        '<button class="btn btn-sm" onclick="toggleTop(' + p.id + ',' + !p.isTop + ')">' + (p.isTop ? '取消置顶' : '置顶') + '</button> ' +
                        '<button class="btn btn-sm" onclick="toggleGood(' + p.id + ',' + !p.isGood + ')">' + (p.isGood ? '取消精华' : '精华') + '</button> ' +
                        '<button class="btn btn-danger btn-sm" onclick="deletePost(' + p.id + ')">删除</button></td>' +
                        '</tr>';
                });
                html += '</tbody></table>';
                document.getElementById('postsList').innerHTML = html;
            } else {
                document.getElementById('postsList').innerHTML = '<p class="empty-tip">暂无帖子</p>';
            }
        });
}

function loadAllReplies() {
    fetch('reply/listAll')
        .then(r => r.json())
        .then(data => {
            if (data.success && data.data.length > 0) {
                var html = '<table class="data-table"><thead><tr><th>ID</th><th>评论内容</th><th>用户</th><th>评论时间</th><th>操作</th></tr></thead><tbody>';
                data.data.forEach(function(r) {
                    html += '<tr>' +
                        '<td>' + r.id + '</td>' +
                        '<td style="max-width:400px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">' + r.content + '</td>' +
                        '<td>' + r.authorName + '</td>' +
                        '<td>' + r.createTime + '</td>' +
                        '<td><button class="btn btn-danger" onclick="deleteReply(' + r.id + ')">删除</button></td>' +
                        '</tr>';
                });
                html += '</tbody></table>';
                document.getElementById('repliesList').innerHTML = html;
            } else {
                document.getElementById('repliesList').innerHTML = '<p class="empty-tip">暂无评论</p>';
            }
        });
}

function loadSections() {
    fetch('section/list')
        .then(r => r.json())
        .then(data => {
            if (data.success && data.data.length > 0) {
                var html = '<table class="data-table"><thead><tr><th>ID</th><th>名称</th><th>描述</th><th>帖子数</th><th>操作</th></tr></thead><tbody>';
                data.data.forEach(function(s) {
                    html += '<tr>' +
                        '<td>' + s.id + '</td>' +
                        '<td>' + s.name + '</td>' +
                        '<td>' + (s.description || '-') + '</td>' +
                        '<td>' + (s.topicCount || 0) + '</td>' +
                        '<td><button class="btn" onclick="editSection(' + s.id + ',\'' + s.name + '\',\'' + (s.description || '') + '\')">编辑</button> <button class="btn btn-danger btn-sm" onclick="deleteSection(' + s.id + ')">删除</button></td>' +
                        '</tr>';
                });
                html += '</tbody></table>';
                document.getElementById('sectionsList').innerHTML = html;
            } else {
                document.getElementById('sectionsList').innerHTML = '<p class="empty-tip">暂无板块</p>';
            }
        });
}

function deletePost(postId) {
    if (!confirm('确定要删除这个帖子吗？')) return;
    fetch('post/delete?id=' + postId, { method: 'POST' })
        .then(r => r.json())
        .then(data => {
            if (data.success) {
                alert('删除成功');
                loadAllPosts();
            } else {
                alert(data.message || '删除失败');
            }
        });
}

function toggleTop(postId, top) {
    fetch('post/setTop?id=' + postId + '&top=' + (top ? 1 : 0), { method: 'POST' })
        .then(r => r.json())
        .then(data => {
            if (data.success) {
                loadAllPosts();
            } else {
                alert(data.message || '操作失败');
            }
        });
}

function toggleGood(postId, good) {
    fetch('post/setGood?id=' + postId + '&good=' + (good ? 1 : 0), { method: 'POST' })
        .then(r => r.json())
        .then(data => {
            if (data.success) {
                loadAllPosts();
            } else {
                alert(data.message || '操作失败');
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
                loadAllReplies();
            } else {
                alert(data.message || '删除失败');
            }
        });
}

function cleanupOrphanReplies() {
    if (!confirm('确定要清理孤立评论吗？这将删除所有指向已不存在帖子的评论。')) return;
    fetch('post/cleanupOrphanReplies', { method: 'POST' })
        .then(r => r.json())
        .then(data => {
            if (data.success) {
                alert('清理成功，删除了 ' + (data.count || 0) + ' 条孤立评论');
                loadAllReplies();
            } else {
                alert(data.message || '清理失败');
            }
        });
}

function saveSection() {
    var id = document.getElementById('editSectionId').value;
    var name = document.getElementById('sectionName').value.trim();
    var desc = document.getElementById('sectionDesc').value.trim();

    if (!name) {
        alert('请输入板块名称');
        return;
    }

    var formData = new URLSearchParams();
    formData.append('name', name);
    formData.append('description', desc);
    if (id) formData.append('id', id);

    console.log('发送创建板块请求: name=' + name + ', desc=' + desc);

    var url = 'section?action=' + (id ? 'update' : 'add');
    if (id) formData.append('id', id);
    fetch(url, {
        method: 'POST',
        body: formData,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
        .then(r => r.json())
        .then(data => {
            if (data.success) {
                alert('保存成功');
                clearSectionForm();
                loadSections();
            } else {
                alert(data.message || '保存失败');
            }
        });
}

function deleteSection(sectionId) {
    if (!confirm('确定要删除这个板块吗？')) return;
    fetch('section?action=delete&id=' + sectionId, {
        method: 'POST'
    })
        .then(r => r.json())
        .then(data => {
            if (data.success) {
                alert('删除成功');
                loadSections();
            } else {
                alert(data.message || '删除失败');
            }
        });
}

function editSection(id, name, desc) {
    document.getElementById('editSectionId').value = id;
    document.getElementById('sectionName').value = name;
    document.getElementById('sectionDesc').value = desc;
}

function clearSectionForm() {
    document.getElementById('editSectionId').value = '';
    document.getElementById('sectionName').value = '';
    document.getElementById('sectionDesc').value = '';
}

// Load initial data
loadAllPosts();
</script>
</body>
</html>
