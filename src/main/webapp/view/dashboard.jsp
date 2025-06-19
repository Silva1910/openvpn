<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard - VPN Panel</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="./navbar.jsp"/>
    
    <div class="container mt-4">
        <h2>Bem-vindo, ${sessionScope.user.fullName}!</h2>
        
        <div class="row mt-4">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">Minhas VPNs</h5>
                        <p class="card-text">Gerencie suas configurações VPN</p>
                        <div class="d-grid gap-2">
                            <a href="${pageContext.request.contextPath}/vpn-config/list" class="btn btn-primary">Ver Minhas VPNs</a>
                            <a href="${pageContext.request.contextPath}/vpn-config/create" class="btn btn-success">Solicitar Nova VPN</a>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="col-md-6">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">Status da VPN</h5>
                        <p class="card-text">Você tem ${activeVpnCount} VPN(s) ativa(s)</p>
                        <a href="${pageContext.request.contextPath}/vpn-config/list" class="btn btn-info">Gerenciar VPNs</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</body>
</html>