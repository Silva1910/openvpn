<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Gerenciamento de Acessos VPN</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="./navbar.jsp"/>
    
    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Gerenciamento de Acessos VPN</h2>
            <a href="${pageContext.request.contextPath}/admin/vpn-access/add" class="btn btn-primary">Add VPN Access</a>
        </div>
        
        <div class="table-responsive">
            <table class="table table-striped table-hover">
                <thead>
                    <tr>
                        <th>Usuário</th>
                        <th>Identificador</th>
                        <th>Data de Criação</th>
                        <th>Status</th>
                        <th>Ações</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${vpnAccesses}" var="access">
                        <tr>
                            <td>${access.user.username}</td>
                            <td>${access.identifier}</td>
                            <td><fmt:formatDate value="${access.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                            <td>
                                <span class="badge bg-${access.active ? 'success' : 'danger'}">
                                    ${access.active ? 'Ativo' : 'Inativo'}
                                </span>
                            </td>
                            <td>
                                <button type="button" 
                                        onclick="toggleVPNStatus('${access.identifier}')"
                                        class="btn btn-sm btn-outline-${access.active ? 'danger' : 'success'}">
                                    ${access.active ? 'Desativar' : 'Ativar'}
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
    <script>
        function toggleVPNStatus(identifier) {
            if (confirm('Deseja realmente alterar o status deste acesso VPN?')) {
                window.location.href = '${pageContext.request.contextPath}/admin/vpn-access/toggle?identifier=' + identifier;
            }
        }
    </script>
</body>
</html> 