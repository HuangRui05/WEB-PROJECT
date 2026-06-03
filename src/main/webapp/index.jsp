<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>BBS论坛 - 首页</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: "Microsoft YaHei", Arial, sans-serif; background: #f5f6f7; }

        :root {
            --primary: #3498db;
            --success: #27ae60;
            --danger: #e74c3c;
            --dark: #2c3e50;
            --light: #ecf0f1;
            --border: #dce4ec;
        }

        .header {
            background: var(--dark);
            color: white;
            padding: 0 30px;
            height: 56px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            position: sticky;
            top: 0;
            z-index: 100;
        }
        .header .logo { font-size: 20px; font-weight: bold; color: #fff; text-decoration: none; }
        .header .nav { display: flex; gap: 20px; align-items: center; }
        .header .nav a { color: #bdc3c7; text-decoration: none; padding: 8px 12px; border-radius: 4px; transition: all 0.2s; }
        .header .nav a:hover { background: rgba(255,255,255,0.1); color: #fff; }
        .header .user-info { display: flex; align-items: center; gap: 10px; }
        .header .user-info span { color: #bdc3c7; }
        .header .user-info a { color: #3498db; text-decoration: none; }
        .header .user-info a:hover { color: #2980b9; }

        .container { max-width: 1100px; margin: 20px auto; padding: 0 20px; }

        .section-card {
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.08);
            margin-bottom: 15px;
            overflow: hidden;
            transition: box-shadow 0.2s;
        }
        .section-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.12); }

        .section-header {
            padding: 20px 25px;
            cursor: pointer;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 1px solid var(--border);
        }
        .section-header:hover { background: #f8f9fa; }
        .section-title {
            display: flex;
            align-items: center;
            gap: 15px;
        }
        .section-icon {
            width: 48px;
            height: 48px;
            background: linear-gradient(135deg, var(--primary), #2980b9);
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 20px;
        }
        .section-info h3 { font-size: 17px; color: #2c3e50; margin-bottom: 4px; }
        .section-info p { color: #95a5a6; font-size: 13px; }
        .section-stats {
            display: flex;
            gap: 25px;
            color: #7f8c8d;
            font-size: 13px;
        }
        .section-stats span { display: flex; align-items: center; gap: 5px; }
        .section-toggle {
            width: 24px;
            height: 24px;
            border-radius: 50%;
            background: var(--light);
            display: flex;
            align-items: center;
            justify-content: center;
            transition: transform 0.3s;
        }
        .section-toggle.expanded { transform: rotate(180deg); }

        .section-posts { display: none; padding: 0; }
        .section-posts.show { display: block; }

        .post-item {
            display: flex;
            align-items: center;
            padding: 14px 25px;
            border-bottom: 1px solid var(--border);
            transition: background 0.2s;
        }
        .post-item:last-child { border-bottom: none; }
        .post-item:hover { background: #f8f9fa; }
        .post-item .post-title {
            flex: 1;
            color: #2c3e50;
            text-decoration: none;
            font-size: 14px;
        }
        .post-item .post-title:hover { color: var(--primary); }
        .post-badges { display: flex; gap: 5px; margin-right: 8px; }
        .badge { padding: 2px 6px; border-radius: 3px; font-size: 11px; font-weight: bold; }
        .badge-top { background: #e74c3c; color: white; }
        .badge-good { background: #f39c12; color: white; }
        .post-item .post-meta {
            display: flex;
            gap: 20px;
            color: #95a5a6;
            font-size: 12px;
        }
        .post-item .post-meta span { display: flex; align-items: center; gap: 5px; }

        .empty-tip {
            padding: 30px;
            text-align: center;
            color: #95a5a6;
            font-size: 14px;
        }

        .loading-tip {
            padding: 30px;
            text-align: center;
            color: #95a5a6;
        }

        .btn { padding: 8px 16px; background: var(--primary); color: white; text-decoration: none; border-radius: 4px; border: none; cursor: pointer; font-size: 14px; transition: background 0.2s; }
        .btn:hover { background: #2980b9; }
    </style>
</head>
<body>
<div class="header">
    <a href="index.jsp" class="logo">BBS论坛</a>
    <div class="nav">
        <a href="index.jsp">首页</a>
        <% if (session.getAttribute("user") != null) { %>
            <a href="publish.jsp">发帖</a>
            <a href="profile.jsp">个人中心</a>
            <% if (((com.bbs.entity.User)session.getAttribute("user")).isAdmin()) { %>
            <a href="admin.jsp">后台管理</a>
            <% } %>
        <% } %>
    </div>
    <div class="user-info">
        <% if (session.getAttribute("user") != null) { %>
            <span>欢迎，<%= ((com.bbs.entity.User)session.getAttribute("user")).getNickname() %></span>
            <a href="user/logout">退出</a>
        <% } else { %>
            <a href="login.jsp">登录</a>
            <a href="register.jsp">注册</a>
        <% } %>
    </div>
</div>

<div class="container">
    <% if (session.getAttribute("user") == null) { %>
    <div style="background: #fff3cd; padding: 12px 20px; border-radius: 8px; margin-bottom: 20px; color: #856404; font-size: 14px;">
        您当前未登录，浏览帖子、发表评论均需 <a href="login.jsp" style="color: #007bff;">登录</a>
    </div>
    <% } %>

    <div id="sectionList">
        <div class="loading-tip">加载中...</div>
    </div>
</div>

<script>
var isLoggedIn = <%= session.getAttribute("user") != null %>;
var sectionData = [];  // 保存板块数据，用于动态加载

function loadSections() {
    fetch('section/list')
        .then(r => r.json())
        .then(data => {
            if (data.success && data.data.length > 0) {
                sectionData = data.data;
                let html = '';
                data.data.forEach(s => {
                    html += '<div class="section-card">' +
                        '<div class="section-header" onclick="toggleSection(' + s.id + ')">' +
                        '<div class="section-title">' +
                        '<div class="section-icon">' + getSectionIcon(s.name) + '</div>' +
                        '<div class="section-info">' +
                        '<h3>' + s.name + '</h3>' +
                        '<p>' + (s.description || '暂无描述') + '</p>' +
                        '</div>' +
                        '</div>' +
                        '<div class="section-stats">' +
                        '<span>帖子: ' + (s.topicCount || 0) + '</span>' +
                        '<span>回复: ' + (s.postCount || 0) + '</span>' +
                        '</div>' +
                        '<div class="section-toggle" id="toggle-' + s.id + '">▼</div>' +
                        '</div>' +
                        '<div class="section-posts" id="posts-' + s.id + '">' +
                        '<div class="loading-tip">加载中...</div>' +
                        '</div>' +
                        '</div>';
                });
                document.getElementById('sectionList').innerHTML = html;
            } else {
                document.getElementById('sectionList').innerHTML = '<div class="empty-tip">暂无板块</div>';
            }
        });
}

function getSectionIcon(name) {
    if (name.indexOf('新') != -1 || name.indexOf('入门') != -1) return '📚';
    if (name.indexOf('技术') != -1 || name.indexOf('编程') != -1) return '💻';
    if (name.indexOf('生活') != -1 || name.indexOf('休闲') != -1) return '🎮';
    if (name.indexOf('校园') != -1) return '🏫';
    if (name.indexOf('学习') != -1) return '📖';
    return '📋';
}

function toggleSection(sectionId) {
    var postsDiv = document.getElementById('posts-' + sectionId);
    var toggleDiv = document.getElementById('toggle-' + sectionId);

    if (postsDiv.classList.contains('show')) {
        // 收起
        postsDiv.classList.remove('show');
        toggleDiv.classList.remove('expanded');
    } else {
        // 展开
        postsDiv.classList.add('show');
        toggleDiv.classList.add('expanded');
        // 如果还没有加载过帖子，则加载
        if (postsDiv.innerHTML.indexOf('加载中') !== -1) {
            loadPostsBySection(sectionId);
        }
    }
}

function loadPostsBySection(sectionId) {
    fetch('post/list?sectionId=' + sectionId)
        .then(r => r.json())
        .then(data => {
            let container = document.getElementById('posts-' + sectionId);
            if (!container) return;

            if (data.success && data.data.length > 0) {
                let html = '';
                data.data.forEach(p => {
                    let badges = '';
                    if (p.isTop == 1) badges += '<span class="badge badge-top">置顶</span>';
                    if (p.isGood == 1) badges += '<span class="badge badge-good">精</span>';
                    if (p.isDemand == 1) badges += '<span class="badge" style="background:#9b59b6;color:white;">赏</span>';
                    html += '<div class="post-item">' +
                        '<div class="post-badges">' + badges + '</div>' +
                        '<a class="post-title" href="post.jsp?id=' + p.id + '">' + p.title + '</a>' +
                        '<div class="post-meta">' +
                        '<span>' + p.authorName + '</span>' +
                        '<span>' + p.replyCount + ' 回复</span>' +
                        '<span>' + formatTime(p.createTime) + '</span>' +
                        '</div>' +
                        '</div>';
                });
                container.innerHTML = html;
            } else {
                container.innerHTML = '<div class="empty-tip">该板块暂无帖子</div>';
            }
        });
}

function formatTime(timeStr) {
    if (!timeStr) return '';
    var d = new Date(timeStr);
    var month = d.getMonth() + 1;
    var day = d.getDate();
    return month + '-' + day;
}

loadSections();
</script>
</body>
</html>
