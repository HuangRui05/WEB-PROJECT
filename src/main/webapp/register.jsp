<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>用户注册 - BBS论坛</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: "Microsoft YaHei", Arial, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .register-box {
            background: white;
            border-radius: 12px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            padding: 40px;
            width: 380px;
        }
        .register-box h2 {
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
            border-color: #667eea;
        }
        .btn-register {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 16px;
            cursor: pointer;
            transition: transform 0.2s;
        }
        .btn-register:hover {
            transform: translateY(-2px);
        }
        .links {
            text-align: center;
            margin-top: 25px;
            font-size: 14px;
        }
        .links a {
            color: #667eea;
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
        .success {
            color: #27ae60;
            text-align: center;
            margin-bottom: 15px;
            font-size: 14px;
            display: none;
        }
    </style>
</head>
<body>
<div class="register-box">
    <h2>用户注册</h2>
    <div id="message" class="error"></div>
    <form id="registerForm">
        <div class="form-group">
            <label>用户名</label>
            <input type="text" name="username" placeholder="请输入用户名" required>
        </div>
        <div class="form-group">
            <label>密码</label>
            <input type="password" name="password" placeholder="请输入密码" required>
        </div>
        <div class="form-group">
            <label>确认密码</label>
            <input type="password" name="confirmPassword" placeholder="请再次输入密码" required>
        </div>
        <button type="submit" class="btn-register">注册</button>
    </form>
    <div class="links">
        <a href="login.jsp">已有账号？立即登录</a><br>
        <a href="index.jsp">返回首页</a>
    </div>
</div>

<script>
document.getElementById('registerForm').onsubmit = function(e) {
    e.preventDefault();
    var formData = new FormData(this);
    var password = formData.get('password');
    var confirmPassword = formData.get('confirmPassword');

    if (password !== confirmPassword) {
        document.getElementById('message').textContent = '两次输入的密码不一致';
        document.getElementById('message').style.display = 'block';
        document.getElementById('message').className = 'error';
        return;
    }

    fetch('user/register', {
        method: 'POST',
        body: new URLSearchParams(formData)
    })
    .then(r => r.json())
    .then(data => {
        if (data.success) {
            document.getElementById('message').textContent = '注册成功，即将跳转到登录页...';
            document.getElementById('message').style.display = 'block';
            document.getElementById('message').className = 'success';
            setTimeout(function() { location.href = 'login.jsp'; }, 1500);
        } else {
            document.getElementById('message').textContent = data.message;
            document.getElementById('message').style.display = 'block';
            document.getElementById('message').className = 'error';
        }
    });
};
</script>
</body>
</html>
