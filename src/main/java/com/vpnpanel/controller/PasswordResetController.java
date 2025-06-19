package com.vpnpanel.controller;

import com.vpnpanel.model.User;
import com.vpnpanel.service.UserService;
import com.vpnpanel.util.DependencyFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Logger;

@WebServlet("/reset-password")
public class PasswordResetController extends HttpServlet {
    private static final Logger logger = Logger.getLogger(PasswordResetController.class.getName());
    private final UserService userService;

    public PasswordResetController() {
        this.userService = DependencyFactory.getUserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/view/request-reset.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        logger.info("Solicitação de reset de senha para usuário: " + username);

        try {
            // Executa o script Python
            String scriptPath = request.getServletContext().getRealPath("/WEB-INF/classes/python/password_reset.py");
            logger.info("Executando script Python: " + scriptPath);
            
            ProcessBuilder pb = new ProcessBuilder("python", scriptPath, username);
            pb.redirectErrorStream(true); // Combina stdout e stderr
            
            Process process = pb.start();
            
            // Lê toda a saída do script (stdout + stderr combinados)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                logger.info("Script output: " + line);
            }
            
            // Aguarda o processo terminar
            int exitCode = process.waitFor();
            logger.info("Script exit code: " + exitCode);
            logger.info("Script output completo:\n" + output.toString());
            
            if (exitCode == 0) {
                request.setAttribute("message", 
                    "Se o usuário existir e estiver ativo, um email será enviado com a senha temporária.");
            } else {
                logger.severe("Erro no script Python. Exit code: " + exitCode + "\nOutput: " + output.toString());
                request.setAttribute("error", "Ocorreu um erro ao processar sua solicitação. Por favor, contate o administrador.");
            }
        } catch (Exception e) {
            logger.severe("Erro ao executar script de reset: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Ocorreu um erro ao processar sua solicitação. Por favor, contate o administrador.");
        }
        
        request.getRequestDispatcher("/view/request-reset.jsp").forward(request, response);
    }
}