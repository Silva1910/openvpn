package com.vpnpanel.controller;

import com.vpnpanel.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/dashboard", "/admin/dashboard"})
public class DashboardController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Check if user is logged in
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        // Check if it's an admin accessing admin dashboard
        if (request.getRequestURI().contains("/admin/dashboard")) {
            if (!user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/403.jsp");
                return;
            }
            request.getRequestDispatcher("/view/admin-dashboard.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/view/dashboard.jsp").forward(request, response);
        }
    }
} 