<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Nova VPN - VPN Panel</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="../navbar.jsp"/>
    
    <div class="container mt-4">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-body">
                        <h2 class="card-title text-center mb-4">Nova Configuração VPN</h2>
                        
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger">
                                ${error}
                            </div>
                        </c:if>
                        
                        <div class="alert alert-info">
                            <h5>Informações Importantes:</h5>
                            <ul class="mb-0">
                                <li>Cada usuário pode ter múltiplas configurações VPN ativas</li>
                                <li>O nome do cliente deve ser único e será usado para identificar sua VPN</li>
                                <li>A senha será usada para proteger sua configuração VPN</li>
                                <li>A configuração pode ser ativada ou desativada a qualquer momento</li>
                            </ul>
                        </div>

                        <form action="${pageContext.request.contextPath}/vpn-config/create" method="post">
                            <div class="mb-3">
                                <label for="password" class="form-label">Senha para o Certificado VPN</label>
                                <input type="password" class="form-control" id="password" name="password" required
                                       placeholder="Digite uma senha forte">
                                <div class="form-text">Esta senha será usada para proteger seu certificado VPN.</div>
                            </div>

                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary btn-lg">
                                    Criar Nova VPN
                                </button>
                                <a href="${pageContext.request.contextPath}/vpn-config/list" 
                                   class="btn btn-outline-secondary">
                                    Cancelar
                                </a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</body>
</html> 