<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Editar Usuário - VPN Panel</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="./navbar.jsp"/>
    
    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Editar Usuário</h2>
            <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left"></i> Voltar
            </a>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>

        <div class="card">
            <div class="card-body">
                <h5 class="card-title mb-4">Informações do Usuário</h5>
                
                <div class="row mb-4">
                    <div class="col-md-6">
                        <p><strong>Username:</strong> ${user.username}</p>
                        <p><strong>Nome Completo:</strong> ${user.fullName}</p>
                        <p><strong>Email:</strong> ${user.email}</p>
                    </div>
                    <div class="col-md-6">
                        <p>
                            <strong>Status:</strong>
                            <span class="badge ${user.active ? 'bg-success' : 'bg-danger'}">
                                ${user.active ? 'Ativo' : 'Inativo'}
                            </span>
                        </p>
                        <p>
                            <strong>Tipo:</strong>
                            <span class="badge ${user.admin ? 'bg-primary' : 'bg-secondary'}">
                                ${user.admin ? 'Administrador' : 'Usuário'}
                            </span>
                        </p>
                    </div>
                </div>

                <hr>

                <h5 class="card-title mb-4">Ações Disponíveis</h5>
                
                <div class="d-grid gap-3">
                    <c:if test="${!user.admin}">
                        <!-- Opção para tornar administrador -->
                        <form action="${pageContext.request.contextPath}/admin/users/make-admin" method="post" class="d-inline">
                            <input type="hidden" name="userId" value="${user.id}">
                            <button type="submit" class="btn btn-primary w-100" onclick="return confirm('Deseja realmente transformar este usuário em administrador?')">
                                <i class="bi bi-shield-fill"></i> Transformar em Administrador
                            </button>
                        </form>
                    </c:if>
                    
                    <c:if test="${user.admin}">
                        <!-- Opção para revogar admin -->
                        <form action="${pageContext.request.contextPath}/admin/users/revoke-admin" method="post" class="d-inline">
                            <input type="hidden" name="userId" value="${user.id}">
                            <button type="submit" class="btn btn-warning w-100" onclick="return confirm('Deseja realmente revogar os privilégios de administrador deste usuário?')">
                                <i class="bi bi-shield-x"></i> Revogar Privilégios de Administrador
                            </button>
                        </form>
                    </c:if>

                    <c:if test="${user.active}">
                        <!-- Opção para revogar acesso -->
                        <form action="${pageContext.request.contextPath}/admin/users/revoke-access" method="post" class="d-inline">
                            <input type="hidden" name="userId" value="${user.id}">
                            <button type="submit" class="btn btn-danger w-100" onclick="return confirm('Deseja realmente revogar o acesso deste usuário? Ele não poderá mais fazer login.')">
                                <i class="bi bi-person-x-fill"></i> Revogar Acesso
                            </button>
                        </form>
                    </c:if>

                    <c:if test="${!user.active}">
                        <!-- Opção para ativar acesso -->
                        <form action="${pageContext.request.contextPath}/admin/users/activate" method="post" class="d-inline">
                            <input type="hidden" name="userId" value="${user.id}">
                            <button type="submit" class="btn btn-success w-100" onclick="return confirm('Deseja realmente reativar o acesso deste usuário?')">
                                <i class="bi bi-person-check-fill"></i> Ativar Acesso
                            </button>
                        </form>
                    </c:if>

                    <!-- Opção para remover usuário -->
                    <form action="${pageContext.request.contextPath}/admin/users/delete" method="post" class="d-inline">
                        <input type="hidden" name="userId" value="${user.id}">
                        <button type="submit" class="btn btn-outline-danger w-100" onclick="return confirm('ATENÇÃO: Esta ação é irreversível! Deseja realmente remover este usuário?')">
                            <i class="bi bi-trash-fill"></i> Remover Usuário
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</body>
</html> 