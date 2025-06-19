<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>403 Forbidden</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-6 text-center">
                <h1 class="display-1">403</h1>
                <p class="lead">Access Denied</p>
                <p>You don't have permission to access this page.</p>
                <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">Go to Dashboard</a>
            </div>
        </div>
    </div>
</body>
</html>