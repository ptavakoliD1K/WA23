package com.welfenhub.controllers;


import com.welfenhub.services.PasswordResetLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * directs link to correct page
 */

@RestController
public class PasswordResetController {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    private static String staticDatabaseUrl;

    /**
     * sets staticDatabaseUrl to databaseUrl
     */
    @PostConstruct
    private void initStaticFields() {
        staticDatabaseUrl = databaseUrl;
    }

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * directs link either to password reset page or error page
     *
     * @param token password reset token of requested password reset
     * @return error page or password reset page
     * @throws SQLException
     */

    @GetMapping("reset-password")
    public ModelAndView showResetPasswordPage(@RequestParam("token") String token) throws SQLException {

        int isPasswordChanged;

        try (Connection conn = DriverManager.getConnection(staticDatabaseUrl)) {
            String selectSQL = "SELECT isPasswordChanged FROM password_reset_tokens WHERE token = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
                pstmt.setString(1, token);
                try (ResultSet rs = pstmt.executeQuery()) {
                    isPasswordChanged = rs.getInt(1);
                }
            }
        }

        boolean isValid = PasswordResetLinkService.validateToken(token);
        if (!isValid || isPasswordChanged == 1) {
            return new ModelAndView("errorPasswordReset")
                    .addObject("message", "Ungültiger oder abgelaufener Token.");
        }
        return new ModelAndView("resetPassword")
                .addObject("token", token);
    }

    /**
     * sets new password for user
     * @param newPassword
     * @param token
     * @return response if success or not
     * @throws SQLException
     */


    @PostMapping("setNewPassword")
    public Map<String, String> setNewPassword(
            @RequestParam("newPassword") String newPassword,
            @RequestParam("token") String token
    ) throws SQLException {
        Map<String, String> response = new HashMap<>();

        boolean isValid = PasswordResetLinkService.validateToken(token);

        if (!isValid) {
            response.put("status", "error");
            response.put("message", "Ungültiger oder abgelaufener Token.");
            return response;
        }


        try (Connection conn = DriverManager.getConnection(staticDatabaseUrl)) {
            String updateSQL = "UPDATE user SET password = ? WHERE id = (SELECT UserID FROM password_reset_tokens WHERE token = ?)";
            String updateSQLChanged = "UPDATE password_reset_tokens SET isPasswordChanged = 1 WHERE token = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
                pstmt.setString(1, passwordEncoder.encode(newPassword));
                pstmt.setString(2, token);
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement(updateSQLChanged)) {
                pstmt.setString(1, token);
                pstmt.executeUpdate();
            }
        }

                response.put("status", "success");
                response.put("message", "Das Passwort wurde erfolgreich geändert");
                response.put("redirectUrl", "/login");
                return response;
            }
        }

