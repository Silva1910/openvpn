<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Alterar Senha - Primeiro Acesso</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .password-container {
            max-width: 500px;
            margin: 50px auto;
            padding: 20px;
            background-color: white;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        .password-rules {
            background-color: #f8f9fa;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="password-container">
            <h2 class="text-center mb-4">Alterar Senha - Primeiro Acesso</h2>
            
            <div class="alert alert-info">
                Por favor, altere sua senha para continuar. Esta é uma medida de segurança necessária no primeiro acesso.
            </div>
            
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>
            
            <div class="password-rules">
                <h5>Requisitos da Senha:</h5>
                <ul>
                    <li>Mínimo de 8 caracteres</li>
                    <li>Pelo menos 1 letra maiúscula (A-Z)</li>
                    <li>Pelo menos 1 número (0-9)</li>
                    <li>Pelo menos 1 caractere especial (!@#$%&*-_+=)</li>
                </ul>
            </div>
            
            <form action="${pageContext.request.contextPath}/change-password" method="post">
                <div class="mb-3">
                    <label for="newPassword" class="form-label">Nova Senha</label>
                    <input type="password" class="form-control" id="newPassword" name="newPassword" required>
                </div>
                
                <div class="mb-3">
                    <label for="confirmPassword" class="form-label">Confirmar Nova Senha</label>
                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                </div>
                
                <div class="d-grid gap-2">
                    <button type="submit" class="btn btn-primary">Alterar Senha</button>
                </div>
            </form>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</body>
</html> 