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

@WebServlet("/change-password")
public class ChangePasswordController extends HttpServlet {
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        userService = DependencyFactory.getUserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!user.isFirstLogin()) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        request.getRequestDispatcher("/view/change-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (newPassword == null || newPassword.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
            request.setAttribute("error", "Por favor, preencha todos os campos");
            request.getRequestDispatcher("/view/change-password.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "As senhas não coincidem");
            request.getRequestDispatcher("/view/change-password.jsp").forward(request, response);
            return;
        }

        // Validar requisitos da senha
        if (!isPasswordValid(newPassword)) {
            request.setAttribute("error", "A senha deve ter no mínimo 8 caracteres, incluindo letras maiúsculas, números e caracteres especiais");
            request.getRequestDispatcher("/view/change-password.jsp").forward(request, response);
            return;
        }

        // Atualizar a senha
        user.setPassword(newPassword);
        user.setFirstLogin(false);
        userService.saveUser(user);

        // Redirecionar para o dashboard apropriado
        if (user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    private boolean isPasswordValid(String password) {
        // Mínimo de 8 caracteres
        if (password.length() < 8) return false;

        // Pelo menos uma letra maiúscula
        if (!password.matches(".*[A-Z].*")) return false;

        // Pelo menos um número
        if (!password.matches(".*\\d.*")) return false;

        // Pelo menos um caractere especial
        if (!password.matches(".*[!@#$%&*\\-_+=].*")) return false;

        return true;
    }
} 