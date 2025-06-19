<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Reset Password Error</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .error-container {
            max-width: 500px;
            margin: 100px auto;
            padding: 20px;
            background-color: white;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="error-container">
            <h2 class="text-center mb-4">Reset Password Error</h2>
            
            <div class="alert alert-danger">
                <c:choose>
                    <c:when test="${not empty error}">
                        ${error}
                    </c:when>
                    <c:otherwise>
                        An error occurred during the password reset process. The link may be invalid or expired.
                    </c:otherwise>
                </c:choose>
            </div>
            
            <div class="text-center mt-4">
                <a href="${pageContext.request.contextPath}/reset-password" class="btn btn-primary">Request New Reset Link</a>
                <div class="mt-3">
                    <a href="${pageContext.request.contextPath}/login">Back to Login</a>
                </div>
            </div>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</body>
</html> 