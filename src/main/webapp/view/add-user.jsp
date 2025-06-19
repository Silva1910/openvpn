<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add User - Admin</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="./navbar.jsp"/>
    
    <div class="container mt-4">
        <h2>Add New User</h2>
        
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        
        <c:if test="${not empty tempPassword}">
            <div class="alert alert-success" style="white-space: pre-line">
                Usuário criado com sucesso!
                Senha temporária: ${tempPassword}
                Por favor, forneça esta senha ao usuário para o primeiro acesso.
            </div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/admin/add-user" method="post">
            <div class="mb-3">
                <label for="username" class="form-label">Username</label>
                <input type="text" class="form-control" id="username" name="username" required minlength="3" maxlength="30">
            </div>
            
            <div class="mb-3">
                <label for="fullName" class="form-label">Full Name</label>
                <input type="text" class="form-control" id="fullName" name="fullName" required>
            </div>
            
            <div class="mb-3">
                <label for="email" class="form-label">Email</label>
                <input type="email" class="form-control" id="email" name="email" required>
            </div>
            
            <div class="mb-3 form-check">
                <input type="checkbox" class="form-check-input" id="isAdmin" name="isAdmin">
                <label class="form-check-label" for="isAdmin">Administrator</label>
            </div>
            
            <button type="submit" class="btn btn-primary">Add User</button>
            <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">Cancel</a>
        </form>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</body>
</html>