<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>发布帖子 - BBS论坛</title>
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

        .container { max-width: 800px; margin: 30px auto; background: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.08); padding: 30px; }
        h2 { margin-bottom: 25px; color: #2c3e50; font-size: 20px; border-bottom: 2px solid #3498db; padding-bottom: 10px; }

        .form-group { margin-bottom: 20px; }
        .form-group label { display: block; margin-bottom: 8px; color: #555; font-weight: 500; font-size: 14px; }
        .form-group input, .form-group select, .form-group textarea {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 6px;
            font-size: 14px;
            font-family: inherit;
        }
        .form-group input:focus, .form-group select:focus, .form-group textarea:focus {
            outline: none;
            border-color: #3498db;
        }
        .form-group textarea { height: 250px; resize: vertical; line-height: 1.6; }

        .demand-group {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 6px;
            margin-bottom: 20px;
            border: 1px solid #dce4ec;
        }
        .demand-group label { display: flex; align-items: center; cursor: pointer; }
        .demand-group input[type="checkbox"] { width: auto; margin-right: 8px; }
        .demand-group .points-input { display: none; margin-top: 10px; }
        .demand-group .points-input input { width: 120px; }

        .btn-submit {
            padding: 14px 40px;
            background: #3498db;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 16px;
            transition: background 0.2s;
        }
        .btn-submit:hover { background: #2980b9; }
        .btn-back { padding: 14px 30px; background: #95a5a6; color: white; border: none; border-radius: 6px; cursor: pointer; font-size: 16px; text-decoration: none; margin-right: 10px; }
        .btn-back:hover { background: #7f8c8d; }

        .error { color: #e74c3c; margin-bottom: 15px; font-size: 14px; display: none; }
        .info { color: #7f8c8d; font-size: 12px; margin-top: 5px; }
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
        <a href="index.jsp">返回首页</a>
    </div>
</div>

<div class="container">
    <h2>发布帖子</h2>
    <div id="errorMsg" class="error"></div>
    <form id="postForm">
        <div class="form-group">
            <label>选择板块</label>
            <select name="sectionId" id="sectionId" required>
                <option value="">加载中...</option>
            </select>
        </div>
        <div class="form-group">
            <label>帖子标题</label>
            <input type="text" name="title" placeholder="请输入帖子标题" required maxlength="200">
        </div>
        <div class="form-group">
            <label>帖子内容</label>
            <textarea name="content" placeholder="请输入帖子内容..." required></textarea>
        </div>

        <div class="demand-group">
            <label>
                <input type="checkbox" id="isDemand" name="isDemand" value="1">
                <span>发布为需求帖（设置积分悬赏）</span>
            </label>
            <div class="points-input" id="pointsGroup">
                <label>悬赏积分：</label>
                <input type="number" name="pointsReward" id="pointsReward" min="1" value="10" placeholder="输入积分">
                <p class="info">设置悬赏积分，回复被采纳后奖励给回答者</p>
            </div>
        </div>

        <div style="margin-top: 25px;">
            <a href="index.jsp" class="btn-back">取消</a>
            <button type="submit" class="btn-submit">发布帖子</button>
        </div>
    </form>
</div>

<script>
document.getElementById('isDemand').onchange = function() {
    document.getElementById('pointsGroup').style.display = this.checked ? 'block' : 'none';
    if (!this.checked) {
        document.getElementById('pointsReward').value = '0';
    }
};

fetch('section/list')
    .then(r => r.json())
    .then(data => {
        if (data.success && data.data.length > 0) {
            let html = '<option value="">请选择板块</option>';
            data.data.forEach(s => {
                html += '<option value="' + s.id + '">' + s.name + '</option>';
            });
            document.getElementById('sectionId').innerHTML = html;
        }
    });

document.getElementById('postForm').onsubmit = function(e) {
    e.preventDefault();
    var formData = new FormData(this);
    var isDemand = document.getElementById('isDemand').checked;
    if (!isDemand) {
        formData.set('isDemand', '0');
        formData.set('pointsReward', '0');
    }
    fetch('post/publish', {
        method: 'POST',
        body: new URLSearchParams(formData)
    })
    .then(r => r.json())
    .then(data => {
        if (data.success) {
            alert('发布成功');
            location.href = 'index.jsp';
        } else {
            document.getElementById('errorMsg').textContent = data.message;
            document.getElementById('errorMsg').style.display = 'block';
        }
    });
};
</script>
</body>
</html>
