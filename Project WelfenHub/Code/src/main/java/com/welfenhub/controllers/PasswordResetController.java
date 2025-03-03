package com.welfenhub.controllers;


import com.welfenhub.repositories.PasswordResetTokensRepository;
import com.welfenhub.repositories.UserRepository;
import com.welfenhub.services.PasswordResetLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * directs link to correct page
 */

@RestController
public class PasswordResetController {

    @Autowired
    PasswordResetTokensRepository passwordResetTokensRepository;

    @Autowired
    UserRepository userRepository;

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
    public ModelAndView showResetPasswordPage(@RequestParam("token") String token) {

        int isPasswordChanged = passwordResetTokensRepository.selectIsPasswordChanged(token).get(0);

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
     *
     * @param newPassword
     * @param token
     * @return response if success or not
     * @throws SQLException
     */


    @PostMapping("setNewPassword")
    public Map<String, String> setNewPassword(
            @RequestParam("newPassword") String newPassword,
            @RequestParam("token") String token) {
        Map<String, String> response = new HashMap<>();

        boolean isValid = PasswordResetLinkService.validateToken(token);

        if (!isValid) {
            response.put("status", "error");
            response.put("message", "Ungültiger oder abgelaufener Token.");
            return response;
        }

        userRepository.updateUserPassword(token, passwordEncoder.encode(newPassword));

        passwordResetTokensRepository.updateIsPasswordChanged(token);

        response.put("status", "success");
        response.put("message", "Das Passwort wurde erfolgreich geändert");
        response.put("redirectUrl", "/login");
        return response;
    }
}

