<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login - VPN Panel</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .login-form {
            max-width: 400px;
            margin: 100px auto;
            padding: 20px;
            background-color: white;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
    </style>
</head>
<body class="bg-light">
    <div class="container">
        <div class="login-form">
            <h2 class="text-center mb-4">Login</h2>
            
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>
            
            <c:if test="${param.reset eq 'success'}">
                <div class="alert alert-success">
                    Sua senha foi redefinida com sucesso. Por favor, faça login com sua nova senha.
                </div>
            </c:if>
            
            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="mb-3">
                    <label for="username" class="form-label">Nome de Usuário</label>
                    <input type="text" class="form-control" id="username" name="username" required>
                </div>
                
                <div class="mb-3">
                    <label for="password" class="form-label">Senha</label>
                    <input type="password" class="form-control" id="password" name="password" required>
                </div>
                
                <div class="d-grid gap-2">
                    <button type="submit" class="btn btn-primary">Entrar</button>
                    <a href="${pageContext.request.contextPath}/reset-password" class="btn btn-link">Esqueci minha senha</a>
                </div>
            </form>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</body>
</html>