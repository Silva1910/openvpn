<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard - VPN Panel</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="navbar.jsp" />
    
    <div class="container mt-4">
        <div class="row">
            <div class="col-12">
                <h2>Admin Dashboard</h2>
                <p>Welcome, ${sessionScope.user.fullName}!</p>
            </div>
        </div>
        
        <div class="row mt-4">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">Users</h5>
                        <p class="card-text">Manage system users</p>
                        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-primary">Manage Users</a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-6">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">VPN Access</h5>
                        <p class="card-text">Manage VPN access configurations</p>
                        <a href="${pageContext.request.contextPath}/admin/vpn-access" class="btn btn-primary">Manage VPN Access</a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</body>
</html> 