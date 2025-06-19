<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>First Login - Set Password</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .password-container {
            max-width: 500px;
            margin: 50px auto;
            padding: 20px;
            background-color: white;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="password-container">
            <h2 class="text-center mb-4">First Login - Set Your Password</h2>
            
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>
            
            <form action="${pageContext.request.contextPath}/first-login" method="post">
                <div class="mb-3">
                    <label for="newPassword" class="form-label">New Password</label>
                    <input type="password" class="form-control" id="newPassword" name="newPassword" required>
                </div>
                
                <div class="mb-3">
                    <label for="confirmPassword" class="form-label">Confirm Password</label>
                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                </div>
                
                <div class="d-grid gap-2">
                    <button type="submit" class="btn btn-primary">Set Password</button>
                </div>
            </form>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</body>
</html>