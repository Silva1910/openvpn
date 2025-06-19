package com.vpnpanel.controller;

import com.vpnpanel.model.User;
import com.vpnpanel.service.UserService;
import com.vpnpanel.util.DependencyFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        System.out.println("Initializing LoginController...");
        try {
            userService = DependencyFactory.getUserService();
            System.out.println("UserService initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing UserService:");
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Processing GET request to /login");
        
        // Se vier da página de reset de senha, invalida a sessão atual
        String fromReset = request.getParameter("fromReset");
        if ("true".equals(fromReset)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
        }
        
        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null && !"true".equals(fromReset)) {
            System.out.println("User already logged in, redirecting to dashboard");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        System.out.println("Forwarding to login.jsp");
        request.getRequestDispatcher("/view/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Processing POST request to /login");
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        System.out.println("Login attempt for username: " + username);
        
        // Validate inputs
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            System.out.println("Username or password is empty");
            request.setAttribute("error", "Por favor preencha todos os campos");
            request.getRequestDispatcher("/view/login.jsp").forward(request, response);
            return;
        }
        
        if (username.length() < 3 || username.length() > 30) {
            System.out.println("Username length invalid: " + username.length());
            request.setAttribute("error", "Username deve ter entre 3 e 30 caracteres");
            request.getRequestDispatcher("/view/login.jsp").forward(request, response);
            return;
        }
        
        try {
            // Authenticate user
            System.out.println("Attempting to authenticate user");
            boolean isAuthenticated = userService.authenticate(username, password);
            
            if (isAuthenticated) {
                System.out.println("Authentication successful for user: " + username);
                
                User user = userService.getUserByUsername(username);
                
                if (user.isFirstLogin()) {
                    System.out.println("First login detected, redirecting to password change");
                    // Redirect to password change page
                    HttpSession session = request.getSession();
                    session.setAttribute("user", user);
                    response.sendRedirect(request.getContextPath() + "/change-password");
                    return;
                }
                
                // Create session and redirect to dashboard
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                
                System.out.println("User is admin: " + user.isAdmin());
                
                if (user.isAdmin()) {
                    System.out.println("Admin user detected, redirecting to admin dashboard");
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                } else {
                    System.out.println("Regular user detected, redirecting to dashboard");
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                }
            } else {
                System.out.println("Authentication failed for user: " + username);
                System.out.println("Invalid credentials");
                request.setAttribute("error", "Usuário ou senha inválidos");
                request.getRequestDispatcher("/view/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.err.println("Error during authentication:");
            e.printStackTrace();
            request.setAttribute("error", "Erro interno do servidor. Por favor, tente novamente mais tarde.");
            request.getRequestDispatcher("/view/login.jsp").forward(request, response);
        }
    }
}