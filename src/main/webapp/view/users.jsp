<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Users - Admin</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="./navbar.jsp"/>
    
    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Usuários</h2>
            <a href="${pageContext.request.contextPath}/admin/add-user" class="btn btn-primary">Adicionar Usuário</a>
        </div>

        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success" style="white-space: pre-line">
                ${sessionScope.successMessage}
            </div>
            <% session.removeAttribute("successMessage"); %>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <div class="table-responsive">
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Username</th>
                        <th>Nome Completo</th>
                        <th>Email</th>
                        <th>Admin</th>
                        <th>Status</th>
                        <th>Ações</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="user" items="${users}">
                        <tr>
                            <td>${user.username}</td>
                            <td>${user.fullName}</td>
                            <td>${user.email}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${user.admin}">
                                        <span class="badge bg-primary">Sim</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary">Não</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${user.active}">
                                        <span class="badge bg-success">Ativo</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-danger">Inativo</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <div class="btn-group" role="group">
                                    <a href="${pageContext.request.contextPath}/admin/edit-user?id=${user.id}" 
                                       class="btn btn-sm btn-outline-primary">Editar</a>
                                    <button onclick="toggleUserStatus(${user.id}, ${user.active})" 
                                            class="btn btn-sm btn-outline-${user.active ? 'danger' : 'success'}">
                                        ${user.active ? 'Desativar' : 'Ativar'}
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
    <script>
        function toggleUserStatus(userId, currentStatus) {
            if (confirm('Deseja realmente ' + (currentStatus ? 'desativar' : 'ativar') + ' este usuário?')) {
                window.location.href = '${pageContext.request.contextPath}/admin/toggle-user-status?id=' + userId;
            }
        }
    </script>
</body>
</html> 