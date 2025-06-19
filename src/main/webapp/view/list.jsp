<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>My VPN Configurations</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .progress-bar {
            width: 100%;
            background-color: #e0e0e0;
            padding: 3px;
            border-radius: 3px;
            box-shadow: inset 0 1px 3px rgba(0, 0, 0, .2);
        }
        .progress-bar-fill {
            display: block;
            height: 20px;
            background-color: #007bff;
            border-radius: 3px;
            transition: width 500ms ease-in-out;
            animation: progressAnimation 2s infinite;
        }
        @keyframes progressAnimation {
            0% { width: 30%; }
            50% { width: 70%; }
            100% { width: 30%; }
        }
    </style>
</head>
<body>
    <jsp:include page="./navbar.jsp"/>
    
    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>My VPN Configurations</h2>
            <form action="${pageContext.request.contextPath}/vpn-config/create" method="post">
                <button type="submit" class="btn btn-primary">New Configuration</button>
            </form>
        </div>
        
        <c:if test="${not empty creatingConfig}">
            <div class="alert alert-info">
                <p>Creating VPN configuration, please wait...</p>
                <div class="progress-bar">
                    <span class="progress-bar-fill"></span>
                </div>
            </div>
        </c:if>
        
        <table class="table table-striped table-hover">
            <thead>
                <tr>
                    <th>Configuration ID</th>
                    <th>Created Date</th>
                    <th>Expiration Date</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${accesses}" var="access">
                    <tr>
                        <td>${access.id}</td>
                        <td>${access.creationDate}</td>
                        <td>${access.expirationDate}</td>
                        <td>
                            <span class="badge ${access.expirationDate.after(now) ? 'bg-success' : 'bg-warning'}">
                                ${access.expirationDate.after(now) ? 'Active' : 'Expired'}
                            </span>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/vpn-config/download?id=${access.id}" 
                               class="btn btn-sm btn-outline-primary">Download</a>
                            <form action="${pageContext.request.contextPath}/vpn-config/delete" method="post" class="d-inline">
                                <input type="hidden" name="id" value="${access.id}">
                                <button type="submit" class="btn btn-sm btn-outline-danger">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
    <script>
        <c:if test="${not empty creatingConfig}">
            setTimeout(function() {
                window.location.reload();
            }, 3000);
        </c:if>
    </script>
</body>
</html>