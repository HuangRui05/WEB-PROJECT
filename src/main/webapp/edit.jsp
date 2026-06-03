<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>编辑帖子 - BBS论坛</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: "Microsoft YaHei", Arial, sans-serif; background: #f0f2f5; }
        .header { background: #2c3e50; color: white; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; }
        .header h1 { font-size: 24px; }
        .header a { color: white; text-decoration: none; }
        .container { max-width: 800px; margin: 20px auto; background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); padding: 30px; }
        h2 { margin-bottom: 20px; color: #2c3e50; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; color: #555; font-weight: bold; }
        .form-group input, .form-group select, .form-group textarea { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; font-size: 14px; }
        .form-group textarea { height: 200px; resize: vertical; }
        .form-group input[readonly] { background: #f5f5f5; }
        .btn { padding: 12px 30px; background: #3498db; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; margin-right: 10px; }
        .btn:hover { background: #2980b9; }
        .btn-secondary { background: #95a5a6; }
        .btn-secondary:hover { background: #7f8c8d; }
        .error { color: #e74c3c; margin-bottom: 15px; }
        .info { color: #7f8c8d; font-size: 13px; margin-top: 5px; }
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
    <h1><a href="index.jsp">BBS论坛</a></h1>
    <a href="index.jsp">返回首页</a>
</div>

<div class="container">
    <h2>编辑帖子</h2>
    <div id="errorMsg" class="error" style="display:none;"></div>

    <form id="editForm">
        <input type="hidden" name="id" id="postId">
        <div class="form-group">
            <label>板块</label>
            <input type="text" id="sectionName" readonly>
        </div>
        <div class="form-group">
            <label>作者</label>
            <input type="text" id="authorName" readonly>
        </div>
        <div class="form-group">
            <label>标题</label>
            <input type="text" name="title" id="title" required maxlength="200">
        </div>
        <div class="form-group">
            <label>内容</label>
            <textarea name="content" id="content" required></textarea>
        </div>
        <button type="submit" class="btn">保存</button>
        <a href="javascript:history.back()" class="btn btn-secondary">取消</a>
    </form>
</div>

<script>
var postId = <%= request.getParameter("id") != null ? request.getParameter("id") : "null" %>;

if (!postId) {
    document.getElementById('errorMsg').textContent = '帖子ID不存在';
    document.getElementById('errorMsg').style.display = 'block';
} else {
    document.getElementById('postId').value = postId;
    loadPost();
}

function loadPost() {
    fetch('post/' + postId)
        .then(r => r.json())
        .then(data => {
            if (!data.success) {
                document.getElementById('errorMsg').textContent = '帖子不存在';
                document.getElementById('errorMsg').style.display = 'block';
                return;
            }
            var p = data.post;
            document.getElementById('sectionName').value = p.sectionName || '';
            document.getElementById('authorName').value = p.authorName || '';
            document.getElementById('title').value = p.title || '';
            document.getElementById('content').value = p.content || '';
        });
}

document.getElementById('editForm').onsubmit = function(e) {
    e.preventDefault();
    var formData = new FormData(this);
    fetch('post/update', {
        method: 'POST',
        body: new URLSearchParams(formData)
    })
    .then(r => r.json())
    .then(data => {
        if (data.success) {
            alert('更新成功');
            location.href = 'post.jsp?id=' + postId;
        } else {
            document.getElementById('errorMsg').textContent = data.message || '更新失败';
            document.getElementById('errorMsg').style.display = 'block';
        }
    });
};
</script>
</body>
</html>
