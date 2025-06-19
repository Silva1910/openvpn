<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Configurações do Sistema</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="./navbar.jsp"/>
    
    <div class="container mt-4">
        <h2>Configurações do Sistema</h2>
        
        <form action="${pageContext.request.contextPath}/admin/settings/save" method="post" class="mt-4">
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0">Configuração SSH do Servidor VPN</h5>
                </div>
                <div class="card-body">
                    <div class="alert alert-info">
                        <i class="bi bi-info-circle"></i>
                        Estas configurações são necessárias para que o sistema possa se comunicar com o servidor VPN e gerar os certificados.
                    </div>
                    
                    <div class="mb-3">
                        <label for="sshHost" class="form-label">Endereço IP do Servidor</label>
                        <input type="text" class="form-control" id="sshHost" name="sshHost" 
                               value="${settings.sshHost}" required>
                        <div class="form-text">Endereço IP da máquina virtual que executa o servidor VPN</div>
                    </div>
                    <div class="mb-3">
                        <label for="sshPort" class="form-label">Porta SSH</label>
                        <input type="number" class="form-control" id="sshPort" name="sshPort" 
                               value="${settings.sshPort}" required>
                        <div class="form-text">Normalmente é a porta 22</div>
                    </div>
                    <div class="mb-3">
                        <label for="sshUsername" class="form-label">Usuário SSH</label>
                        <input type="text" class="form-control" id="sshUsername" name="sshUsername" 
                               value="${settings.sshUsername}" required>
                        <div class="form-text">Usuário com permissão para executar o script de geração de certificados</div>
                    </div>
                    <div class="mb-3">
                        <label for="sshPassword" class="form-label">Senha SSH</label>
                        <input type="password" class="form-control" id="sshPassword" name="sshPassword" 
                               value="${settings.sshPassword}" required>
                        <div class="form-text">Senha do usuário SSH</div>
                    </div>
                </div>
            </div>

            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                <button type="submit" class="btn btn-primary">Salvar Configurações</button>
            </div>
        </form>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</body>
</html> 
</body>
</html> 