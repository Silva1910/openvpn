package com.vpnpanel.controller;

import com.vpnpanel.model.User;
import com.vpnpanel.model.VPNAccess;
import com.vpnpanel.service.VPNAccessService;
import com.vpnpanel.util.SSHService;
import com.vpnpanel.util.DependencyFactory;
import com.vpnpanel.service.SettingsService;
import com.vpnpanel.model.Settings;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

@WebServlet("/vpn-config/*")
public class VPNConfigController extends HttpServlet {
    private VPNAccessService vpnAccessService;
    private SettingsService settingsService;

    @Override
    public void init() throws ServletException {
        super.init();
        vpnAccessService = DependencyFactory.getVPNAccessService();
        settingsService = DependencyFactory.getSettingsService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        String action = request.getPathInfo();
        
        if (action == null || action.equals("/") || action.equals("/list")) {
            // Listar configurações VPN do usuário
            String filter = request.getParameter("filter");
            List<VPNAccess> accesses;
            
            // Se for admin, pode ver todas as VPNs, senão só as próprias
            if (currentUser.isAdmin()) {
                if (filter != null && !filter.isEmpty()) {
                    accesses = vpnAccessService.getAllVPNAccessWithFilter(filter);
                } else {
                    accesses = vpnAccessService.getAllVPNAccess();
                }
            } else {
                if (filter != null && !filter.isEmpty()) {
                    accesses = vpnAccessService.getUserAccessesWithFilter(currentUser, filter);
                } else {
                    accesses = vpnAccessService.getUserAccesses(currentUser);
                }
            }
            
            request.setAttribute("vpnAccesses", accesses);
            request.getRequestDispatcher("/view/vpn-config/list.jsp").forward(request, response);
        } else if (action.equals("/create")) {
            // Mostrar formulário de criação de VPN
            request.getRequestDispatcher("/view/vpn-config/create.jsp").forward(request, response);
        } else if (action.equals("/toggle")) {
            // Toggle VPN status
            String identifier = request.getParameter("identifier");
            if (identifier == null || identifier.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identificador não fornecido");
                return;
            }

            try {
                // Verificar se o usuário tem permissão para alterar esta VPN
                VPNAccess vpnAccess = vpnAccessService.getVPNAccessByIdentifier(identifier);
                if (vpnAccess == null || (!currentUser.isAdmin() && !vpnAccess.getUser().getId().equals(currentUser.getId()))) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "VPN não encontrada ou não autorizada");
                    return;
                }

                vpnAccessService.toggleVPNStatus(identifier, currentUser);
                request.getSession().setAttribute("successMessage", "Status da VPN alterado com sucesso!");
            } catch (IllegalArgumentException e) {
                request.getSession().setAttribute("errorMessage", "VPN não encontrada ou não autorizada");
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("errorMessage", "Erro ao alterar status da VPN: " + e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/vpn-config/list");
        } else if (action.equals("/download")) {
            // Download VPN configuration
            String identifier = request.getParameter("identifier");
            if (identifier == null || identifier.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identificador não fornecido");
                return;
            }

            try {
                // Verificar se o usuário tem permissão para baixar esta VPN
                VPNAccess vpnAccess = vpnAccessService.getVPNAccessByIdentifier(identifier);
                if (vpnAccess == null || (!currentUser.isAdmin() && !vpnAccess.getUser().getId().equals(currentUser.getId()))) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado");
                    return;
                }

                if (!vpnAccess.isActive()) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Esta VPN está revogada");
                    return;
                }

                // Criar instância do SSHService
                SSHService sshService = SSHService.getInstance(
                    settingsService.getSettings().getSshHost(),
                    settingsService.getSettings().getSshPort(),
                    settingsService.getSettings().getSshUsername(),
                    settingsService.getSettings().getSshPassword()
                );
                
                // Baixar arquivo ZIP do servidor remoto
                String remoteZipPath = "/home/usuario/" + identifier + ".zip";
                byte[] zipContent = sshService.downloadFileViaSFTP(remoteZipPath);
                
                // Enviar arquivo para o cliente
                response.setContentType("application/zip");
                response.setHeader("Content-Disposition", 
                                 "attachment; filename=\"" + identifier + ".zip\"");
                response.getOutputStream().write(zipContent);
                
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                                 "Erro ao baixar configuração VPN: " + e.getMessage());
            }
        } else if (action.equals("/delete")) {
            // Delete VPN configuration
            String identifier = request.getParameter("identifier");
            if (identifier == null || identifier.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identificador não fornecido");
                return;
            }

            try {
                // Get the VPN access first to check ownership
                VPNAccess vpnAccess = vpnAccessService.getVPNAccessByIdentifier(identifier);
                if (vpnAccess == null || !vpnAccess.getUser().getId().equals(currentUser.getId())) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "VPN não encontrada ou não autorizada");
                    return;
                }

                vpnAccessService.deleteVPNAccess(identifier, currentUser);
                request.getSession().setAttribute("successMessage", "Configuração VPN removida com sucesso!");
            } catch (IllegalArgumentException e) {
                request.getSession().setAttribute("errorMessage", "Configuração VPN não encontrada ou não autorizada");
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("errorMessage", "Erro ao remover configuração VPN: " + e.getMessage());
            }
            
            response.sendRedirect(request.getContextPath() + "/vpn-config/list");
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String password = request.getParameter("password");
            if (password == null || password.isEmpty()) {
                request.setAttribute("error", "A senha é obrigatória");
                request.getRequestDispatcher("/view/vpn-config/create.jsp").forward(request, response);
                return;
            }

            // DEBUG: Imprimir configurações SSH
            Settings settings = settingsService.getSettings();
            System.out.println("DEBUG - Configurações SSH:");
            System.out.println("Host: " + settings.getSshHost());
            System.out.println("Port: " + settings.getSshPort());
            System.out.println("Username: " + settings.getSshUsername());
            System.out.println("Password length: " + (settings.getSshPassword() != null ? settings.getSshPassword().length() : 0));

            // Gerar configuração VPN
            VPNAccess vpnAccess = vpnAccessService.generateVPNConfig(user, password);
            
            // Adicionar mensagem de sucesso
            request.getSession().setAttribute("successMessage", "Configuração VPN criada com sucesso!");
            
            // Redirecionar para o dashboard
            response.sendRedirect(request.getContextPath() + "/vpn-config/list");
            
        } catch (Exception e) {
            e.printStackTrace(); // Adicionar stack trace completo para debug
            request.setAttribute("error", "Erro ao gerar configuração VPN: " + e.getMessage());
            request.getRequestDispatcher("/view/vpn-config/create.jsp").forward(request, response);
        }
    }
}