package com.welfenhub.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * creates random token for link to password reset page
 * creates link with random token and saves token with expire time and userId in database
 * validates token of the link
 */

@Service
public class PasswordResetLinkService {

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

    /**
     * Private constructor -> no instance can be created
     */
    PasswordResetLinkService(){}


    /**
     * Creates a random String
     * @return random token as a String
     */
    private static String randomToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Creates a random link for password reset and saves the token in the database with userId and expire time of token (15 minutes)
     * @param receiver email of person who requests password reset
     * @return random link with random token
     */
    public static String linkGeneratorAndSaver(String receiver) throws SQLException {
        int userId;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String token = randomToken();
        LocalDateTime dateTime = LocalDateTime.now();

        try (Connection conn = DriverManager.getConnection(staticDatabaseUrl)) {
            String insertSQL = "INSERT INTO password_reset_tokens(token, UserID, expires_at) VALUES (?, ?, ?)";
            String selectSQL = "SELECT id FROM user WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
                pstmt.setString(1, receiver);
                try(ResultSet rs = pstmt.executeQuery()) {
                    userId = rs.getInt(1);
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setString(1, token);
                pstmt.setInt(2, userId);
                pstmt.setString(3, (dateTime.plusMinutes(15)).format(formatter));
                pstmt.executeUpdate();
            }
        }

        return "http://localhost:8080/reset-password?token=" + token;
    }

    /**
     * validates token of the password reset link which is sent by mail
     * @param token token of the password reset link
     * @return true when token is valid, false when not valid
     * @throws SQLException
     */

    public static boolean validateToken(String token) throws SQLException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime dateTime = LocalDateTime.now();

        try (Connection conn = DriverManager.getConnection(staticDatabaseUrl)) {
            String selectSQL = "SELECT expires_at FROM password_reset_tokens WHERE token = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
                pstmt.setString(1, token);
                try (ResultSet rs = pstmt.executeQuery()) {
                    String expireDateStr = rs.getString(1);
                    LocalDateTime dbExpireTime = LocalDateTime.parse(expireDateStr, formatter);
                    if (dbExpireTime.isBefore(dateTime)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
