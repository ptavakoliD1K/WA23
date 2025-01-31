package com.welfenhub.services;

import com.welfenhub.models.User;
import com.welfenhub.repositories.PasswordResetTokensRepository;
import com.welfenhub.repositories.UserRepository;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static PasswordResetTokensRepository passwordResetTokensRepository;

    @Autowired
    public void setPasswordResetTokensRepository(PasswordResetTokensRepository repository) {
        passwordResetTokensRepository = repository;
    }

    private static UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository repository) {
        userRepository = repository;
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String token = randomToken();
        LocalDateTime dateTime = LocalDateTime.now();

        Long userId = (userRepository.findByEmail(receiver)).getId();

        passwordResetTokensRepository.insertToken(token, userId, dateTime.plusMinutes(150).format(formatter), 0);

        return "http://localhost:8080/reset-password?token=" + token;
    }

    /**
     * validates token of the password reset link which is sent by mail
     * @param token token of the password reset link
     * @return true when token is valid, false when not valid
     * @throws SQLException
     */

    public static boolean validateToken(String token){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime dateTime = LocalDateTime.now();

        String expireTime = (passwordResetTokensRepository.selectExpiresAt(token)).get(0);

        LocalDateTime dbExpireTime = LocalDateTime.parse(expireTime, formatter);

        if (dbExpireTime.isBefore(dateTime)) {
            return false;
        }

        return true;
    }
}
