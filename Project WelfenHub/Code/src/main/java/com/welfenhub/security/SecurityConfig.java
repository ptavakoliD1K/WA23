package com.WelfenHub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    private final String REMEMBER_ME_KEY = "uniqueAndSecretKey"; // Key for Remember-Me functionality

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF teilweise deaktivieren
                .csrf(csrf -> csrf
                        .ignoringAntMatchers("/posts/comment")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )


                // Authorization Rules
                .authorizeRequests(auth -> auth
                        .antMatchers("/register", "/login", "/css/**", "/images/**", "/static/**", "/templates/**", "/passwordreset", "/upload", "/js/**", "/passwordResetProcess", "/reset-password**", "/setNewPassword**","/posts/**").permitAll()
                        .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        .antMatchers("/moderator/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MODERATOR")
                        .anyRequest().authenticated()
                )

                // Login Configuration
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(authenticationSuccessHandler()) // Success handler for redirecting
                        .permitAll()
                )

                // Remember Me Configuration
                .rememberMe(rememberMe -> rememberMe
                        .key(REMEMBER_ME_KEY) // Secret key for Remember-Me tokens
                        .tokenValiditySeconds(7 * 24 * 60 * 60) // 7 days validity
                        .rememberMeParameter("remember-me") // Matches the checkbox name
                );

        // Logout Configuration
        http
                .logout(logout -> logout
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                );

        // Exception Handling
        http
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/access-denied")
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            var userDetails = (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
            String redirectUrl = userDetails.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN")) ?
                    "/admin/AdminDashboard" : "/";
            response.sendRedirect(redirectUrl);
        };
    }
}