<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Nova Senha - VPN Panel</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .reset-form {
            max-width: 400px;
            margin: 100px auto;
            padding: 20px;
            background-color: white;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        .password-requirements {
            font-size: 0.875rem;
            color: #6c757d;
        }
    </style>
</head>
<body class="bg-light">
    <div class="container">
        <div class="reset-form">
            <h2 class="text-center mb-4">Criar Nova Senha</h2>
            
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>
            
            <form action="${pageContext.request.contextPath}/reset-password" method="post">
                <input type="hidden" name="token" value="${token}">
                <input type="hidden" name="action" value="reset">
                
                <div class="mb-3">
                    <label for="password" class="form-label">Nova Senha</label>
                    <input type="password" class="form-control" id="password" name="password" required>
                    <div class="password-requirements mt-2">
                        A senha deve conter:
                        <ul>
                            <li>Mínimo de 8 caracteres</li>
                            <li>Pelo menos uma letra maiúscula</li>
                            <li>Pelo menos um número</li>
                            <li>Pelo menos um caractere especial</li>
                        </ul>
                    </div>
                </div>
                
                <div class="mb-3">
                    <label for="confirmPassword" class="form-label">Confirmar Nova Senha</label>
                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                </div>
                
                <div class="d-grid">
                    <button type="submit" class="btn btn-primary">Salvar Nova Senha</button>
                </div>
            </form>
        </div>
    </div>
    
    <script>
        document.querySelector('form').addEventListener('submit', function(e) {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            if (password !== confirmPassword) {
                e.preventDefault();
                alert('As senhas não coincidem!');
                return;
            }
            
            const passwordRegex = /^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{8,}$/;
            if (!passwordRegex.test(password)) {
                e.preventDefault();
                alert('A senha não atende aos requisitos mínimos!');
            }
        });
    </script>
</body>
</html>