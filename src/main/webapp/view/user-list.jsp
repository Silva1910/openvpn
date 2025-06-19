<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>User Management</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .filter-box { max-width: 300px; }
    </style>
</head>
<body>
    <jsp:include page="./navbar.jsp"/>
    
    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>User Management</h2>
            <div>
                <input type="text" id="filterInput" class="form-control filter-box" 
                       placeholder="Filter by name..." value="${param.filter}">
            </div>
            <a href="${pageContext.request.contextPath}/admin/add-user" class="btn btn-primary">Add User</a>
        </div>
        
        <table class="table table-striped table-hover">
            <thead>
                <tr>
                    <th>Username</th>
                    <th>Full Name</th>
                    <th>Email</th>
                    <th>Status</th>
                    <th>Role</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${users}" var="user">
                    <tr>
                        <td>${user.username}</td>
                        <td>${user.fullName}</td>
                        <td>${user.email}</td>
                        <td>
                            <span class="badge ${user.active ? 'bg-success' : 'bg-danger'}">
                                ${user.active ? 'Active' : 'Inactive'}
                            </span>
                        </td>
                        <td>
                            <span class="badge ${user.admin ? 'bg-primary' : 'bg-secondary'}">
                                ${user.admin ? 'Admin' : 'User'}
                            </span>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/users/edit?id=${user.id}" 
                               class="btn btn-sm btn-outline-primary">Edit</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
    <script>
        document.getElementById('filterInput').addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                var filterValue = this.value;
                var baseUrl = '${pageContext.request.contextPath}/admin/users';
                window.location.href = baseUrl + '?filter=' + encodeURIComponent(filterValue);
            }
        });
    </script>
</body>
</html>