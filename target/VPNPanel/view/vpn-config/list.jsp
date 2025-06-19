<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Minhas VPNs - VPN Panel</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        .dropdown-toggle::after {
            display: none;
        }
        .action-menu {
            cursor: pointer;
        }
    </style>
</head>
<body>
    <jsp:include page="../navbar.jsp"/>
    
    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Minhas Configurações VPN</h2>
            <a href="${pageContext.request.contextPath}/vpn-config/create" class="btn btn-primary">
                Nova Configuração VPN
            </a>
        </div>

        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${sessionScope.successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <% session.removeAttribute("successMessage"); %>
        </c:if>

        <c:if test="${empty vpnAccesses}">
            <div class="alert alert-info">
                Você ainda não tem configurações VPN. Clique no botão acima para criar uma nova.
            </div>
        </c:if>

        <c:if test="${not empty vpnAccesses}">
            <div class="mb-3">
                <form action="${pageContext.request.contextPath}/vpn-config/list" method="get" class="row g-3">
                    <div class="col-auto">
                        <input type="text" name="filter" class="form-control" placeholder="Filtrar por identificador..." value="${param.filter}">
                    </div>
                    <div class="col-auto">
                        <button type="submit" class="btn btn-secondary">Filtrar</button>
                    </div>
                </form>
            </div>

            <div class="table-responsive">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Identificador</th>
                            <th>Data de Criação</th>
                            <th>Status</th>
                            <th>Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="vpn" items="${vpnAccesses}">
                            <tr>
                                <td>${vpn.identifier}</td>
                                <td>${vpn.createdAt}</td>
                                <td>
                                    <span class="badge bg-${vpn.active ? 'success' : 'danger'}">
                                        ${vpn.active ? 'Ativa' : 'Inativa'}
                                    </span>
                                </td>
                                <td>
                                    <c:if test="${vpn.active}">
                                        <div class="dropdown">
                                            <button class="btn btn-link action-menu p-0" type="button" data-bs-toggle="dropdown">
                                                <i class="bi bi-three-dots-vertical"></i>
                                            </button>
                                            <ul class="dropdown-menu">
                                                <li>
                                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/vpn-config/download?identifier=${vpn.identifier}">
                                                        <i class="bi bi-download me-2"></i>Baixar
                                                    </a>
                                                </li>
                                                <li>
                                                    <a class="dropdown-item text-danger" href="#" onclick="toggleVPNStatus('${vpn.identifier}', ${vpn.active}); return false;">
                                                        <i class="bi bi-x-circle me-2"></i>Desativar
                                                    </a>
                                                </li>
                                            </ul>
                                        </div>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
    </div>

    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
    <script>
        function toggleVPNStatus(identifier, currentStatus) {
            if (confirm('Deseja realmente desativar esta VPN?')) {
                window.location.href = '${pageContext.request.contextPath}/vpn-config/toggle?identifier=' + identifier;
            }
        }
    </script>
</body>
</html> 