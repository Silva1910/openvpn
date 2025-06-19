package com.vpnpanel.controller;

import com.vpnpanel.model.User;
import com.vpnpanel.model.VPNAccess;
import com.vpnpanel.model.Settings;
import com.vpnpanel.service.UserService;
import com.vpnpanel.service.VPNAccessService;
import com.vpnpanel.service.SettingsService;
import com.vpnpanel.util.DependencyFactory;
import com.vpnpanel.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/*")
public class AdminController extends HttpServlet {
    private UserService userService;
    private VPNAccessService vpnAccessService;
    private SettingsService settingsService;

    @Override
    public void init() throws ServletException {
        super.init();
        userService = DependencyFactory.getUserService();
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
        if (!currentUser.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado");
            return;
        }

        String action = request.getPathInfo();
        System.out.println("Admin action requested: " + action);
        
        if (action == null || action.equals("/") || action.equals("/dashboard")) {
            // Admin dashboard
            System.out.println("Forwarding to admin dashboard");
            request.getRequestDispatcher("/view/admin-dashboard.jsp").forward(request, response);
        } else if (action.equals("/users")) {
            // List users
            System.out.println("Listing users");
            String filter = request.getParameter("filter");
            List<User> users;
            
            if (filter != null && !filter.isEmpty()) {
                users = userService.getUsersByFilter(filter);
            } else {
                users = userService.getAllUsers();
            }
            
            request.setAttribute("users", users);
            request.getRequestDispatcher("/view/user-list.jsp").forward(request, response);
        } else if (action.equals("/add-user")) {
            // Show add user form
            System.out.println("Showing add user form");
            request.getRequestDispatcher("/view/add-user.jsp").forward(request, response);
        } else if (action.equals("/vpn-access")) {
            // List VPN access
            System.out.println("Listing VPN access");
            List<VPNAccess> vpnAccesses = vpnAccessService.getAllVPNAccess();
            request.setAttribute("vpnAccesses", vpnAccesses);
            request.getRequestDispatcher("/view/vpn-access-list.jsp").forward(request, response);
        } else if (action.equals("/vpn-access/toggle")) {
            // Toggle VPN access status
            String identifier = request.getParameter("identifier");
            if (identifier != null && !identifier.isEmpty()) {
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    Transaction tx = hibernateSession.beginTransaction();
                    try {
                        VPNAccess vpnAccess = hibernateSession.createQuery(
                            "FROM VPNAccess WHERE identifier = :identifier", 
                            VPNAccess.class
                        )
                        .setParameter("identifier", identifier)
                        .uniqueResult();

                        if (vpnAccess != null) {
                            vpnAccess.setActive(!vpnAccess.isActive());
                            hibernateSession.update(vpnAccess);
                        }
                        tx.commit();
                    } catch (Exception e) {
                        tx.rollback();
                        throw e;
                    }
                }
            }
            response.sendRedirect(request.getContextPath() + "/admin/vpn-access");
        } else if (action.equals("/settings")) {
            // Show system settings
            System.out.println("Showing system settings");
            Settings settings = settingsService.getSettings();
            request.setAttribute("settings", settings);
            request.getRequestDispatcher("/view/system-settings.jsp").forward(request, response);
        } else if (action.equals("/users/edit")) {
            // Show edit user form
            String userId = request.getParameter("id");
            if (userId != null && !userId.isEmpty()) {
                User user = userService.getUserById(Integer.parseInt(userId));
                if (user != null) {
                    request.setAttribute("user", user);
                    request.getRequestDispatcher("/view/edit-user.jsp").forward(request, response);
                    return;
                }
            }
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } else {
            System.out.println("Invalid admin action: " + action);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        if (!currentUser.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado");
            return;
        }

        String action = request.getPathInfo();
        System.out.println("Admin POST action: " + action);
        
        // Get userId for user management actions
        String userIdStr = request.getParameter("userId");
        Integer userId = null;
        User targetUser = null;
        
        if (userIdStr != null && !userIdStr.isEmpty()) {
            try {
                userId = Integer.parseInt(userIdStr);
                targetUser = userService.getUserById(userId);
                
                // Verificar se o usuário existe
                if (targetUser == null) {
                    request.getSession().setAttribute("error", "Usuário não encontrado");
                    response.sendRedirect(request.getContextPath() + "/admin/users");
                    return;
                }
                
                // Não permitir que um admin modifique a si mesmo
                if (targetUser.getId().equals(currentUser.getId())) {
                    request.getSession().setAttribute("error", "Você não pode modificar seu próprio usuário");
                    response.sendRedirect(request.getContextPath() + "/admin/users");
                    return;
                }
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("error", "ID de usuário inválido");
                response.sendRedirect(request.getContextPath() + "/admin/users");
                return;
            }
        }

        if (action.equals("/users/make-admin")) {
            // Transformar em administrador
            targetUser.setAdmin(true);
            userService.saveUser(targetUser);
            request.getSession().setAttribute("successMessage", "Usuário promovido a administrador com sucesso!");
            
        } else if (action.equals("/users/revoke-admin")) {
            // Revogar privilégios de administrador
            targetUser.setAdmin(false);
            userService.saveUser(targetUser);
            request.getSession().setAttribute("successMessage", "Privilégios de administrador revogados com sucesso!");
            
        } else if (action.equals("/users/revoke-access")) {
            // Revogar acesso (desativar usuário)
            targetUser.setActive(false);
            userService.saveUser(targetUser);
            request.getSession().setAttribute("successMessage", "Acesso do usuário revogado com sucesso!");
            
        } else if (action.equals("/users/activate")) {
            // Ativar usuário
            targetUser.setActive(true);
            userService.saveUser(targetUser);
            request.getSession().setAttribute("successMessage", "Acesso do usuário ativado com sucesso!");
            
        } else if (action.equals("/users/delete")) {
            // Remover usuário
            userService.deleteUser(targetUser);
            request.getSession().setAttribute("successMessage", "Usuário removido com sucesso!");
            
        } else if (action.equals("/add-user")) {
            // Process add user form
            String username = request.getParameter("username");
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            boolean isAdmin = "on".equals(request.getParameter("isAdmin"));
            
            System.out.println("Processing add user request:");
            System.out.println("Username: " + username);
            System.out.println("Full Name: " + fullName);
            System.out.println("Email: " + email);
            System.out.println("Is Admin: " + isAdmin);
            
            // Validate inputs
            if (username == null || username.isEmpty() || 
                fullName == null || fullName.isEmpty() || 
                email == null || email.isEmpty()) {
                
                request.setAttribute("error", "Por favor preencha todos os campos");
                request.getRequestDispatcher("/view/add-user.jsp").forward(request, response);
                return;
            }
            
            if (username.length() < 3 || username.length() > 30) {
                request.setAttribute("error", "Username deve ter entre 3 e 30 caracteres");
                request.getRequestDispatcher("/view/add-user.jsp").forward(request, response);
                return;
            }
            
            // Check if username already exists
            if (userService.getUserByUsername(username) != null) {
                request.setAttribute("error", "Username já está em uso");
                request.getRequestDispatcher("/view/add-user.jsp").forward(request, response);
                return;
            }
            
            // Create new user with temporary password
            User newUser = new User();
            newUser.setUsername(username);
            // Set a temporary password that will be changed on first login
            String tempPassword = "temp" + System.currentTimeMillis();
            newUser.setPassword(tempPassword);
            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setAdmin(isAdmin);
            newUser.setFirstLogin(true);
            newUser.setActive(true);
            newUser.setFailedLoginAttempts(0);
            
            System.out.println("Saving new user: " + newUser);
            userService.saveUser(newUser);
            System.out.println("User saved successfully");
            
            // Mostrar a senha temporária na mesma página
            request.setAttribute("tempPassword", tempPassword);
            request.getRequestDispatcher("/view/add-user.jsp").forward(request, response);
            return;
        } else if (action.equals("/settings/save")) {
            // Process system settings form
            Settings settings = settingsService.getSettings();
            
            // Save SSH settings
            settings.setSshHost(request.getParameter("sshHost"));
            settings.setSshPort(Integer.parseInt(request.getParameter("sshPort")));
            settings.setSshUsername(request.getParameter("sshUsername"));
            settings.setSshPassword(request.getParameter("sshPassword"));
            
            settingsService.saveSettings(settings);
            
            response.sendRedirect(request.getContextPath() + "/admin/settings");
        } else {
            System.out.println("Invalid admin POST action: " + action);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Redirect back to users list after any user management action
        if (action.startsWith("/users/")) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }
}