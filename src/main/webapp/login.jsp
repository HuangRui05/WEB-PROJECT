<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>用户登录 - BBS论坛</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: "Microsoft YaHei", Arial, sans-serif;
            background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .login-box {
            background: white;
            border-radius: 12px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            padding: 40px;
            width: 380px;
        }
        .login-box h2 {
            text-align: center;
            color: #2c3e50;
            margin-bottom: 30px;
            font-size: 24px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #555;
            font-size: 14px;
        }
        .form-group input {
            width: 100%;
            padding: 12px 15px;
            border: 1px solid #ddd;
            border-radius: 6px;
            font-size: 14px;
            transition: border-color 0.2s;
        }
        .form-group input:focus {
            outline: none;
            border-color: #3498db;
        }
        .btn-login {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 16px;
            cursor: pointer;
            transition: transform 0.2s;
        }
        .btn-login:hover {
            transform: translateY(-2px);
        }
        .links {
            text-align: center;
            margin-top: 25px;
            font-size: 14px;
        }
        .links a {
            color: #3498db;
            text-decoration: none;
        }
        .links a:hover {
            text-decoration: underline;
        }
        .error {
            color: #e74c3c;
            text-align: center;
            margin-bottom: 15px;
            font-size: 14px;
            display: none;
        }
    </style>
</head>
<body>
<div class="login-box">
    <h2>用户登录</h2>
    <div id="errorMsg" class="error"></div>
    <form id="loginForm">
        <div class="form-group">
            <label>用户名</label>
            <input type="text" name="username" placeholder="请输入用户名" required>
        </div>
        <div class="form-group">
            <label>密码</label>
            <input type="password" name="password" placeholder="请输入密码" required>
        </div>
        <button type="submit" class="btn-login">登录</button>
    </form>
    <div class="links">
        <a href="register.jsp">还没有账号？立即注册</a><br>
        <a href="index.jsp">返回首页</a>
    </div>
</div>

<script>
document.getElementById('loginForm').onsubmit = function(e) {
    e.preventDefault();
    var formData = new FormData(this);
    fetch('user/login', {
        method: 'POST',
        body: new URLSearchParams(formData)
    })
    .then(r => r.json())
    .then(data => {
        if (data.success) {
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
