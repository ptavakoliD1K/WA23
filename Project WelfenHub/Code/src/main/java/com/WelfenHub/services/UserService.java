package com.WelfenHub.services;

import com.WelfenHub.models.User;
import com.WelfenHub.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void save(User user) throws Exception {
        logger.info("Saving user: {}", user.getUsername());
        if (userRepository.findByUsername(user.getUsername()) != null) {
            logger.error("Username already exists: {}", user.getUsername());
            throw new Exception("Username already exists");
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            logger.error("Email already exists: {}", user.getEmail());
            throw new Exception("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            logger.info("Encoded password for user: {}", user.getUsername());
            userRepository.save(user);
            logger.info("User saved successfully: {}", user.getUsername());
        } catch (Exception ex) {
            logger.error("Error saving user: {}", user.getUsername(), ex);
            throw ex;
        }
    }

    public boolean authenticate(User user) {
        logger.info("Authenticating user: {}", user.getUsername());
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser != null) {
            boolean matches = passwordEncoder.matches(user.getPassword(), existingUser.getPassword());
            logger.info("Authentication {}", matches ? "successful" : "failed");
            return matches;
        }
        logger.info("Authentication failed: user not found");
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Loading user by username: {}", username);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.error("User not found: {}", username);
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.emptyList());
    }

    public void deleteUserByUsername(String username) {
        logger.info("Deleting user: {}", username);
        userRepository.deleteByUsername(username);
        logger.info("User deleted successfully: {}", username);
    }
}
