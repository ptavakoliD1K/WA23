package com.welfenhub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
public class SecurityConfig {

    private static final String REMEMBER_ME_KEY = "uniqueAndSecretKey"; // Key für "Remember Me"

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                /* ===========================
                 *  CSRF-Schutz: Bestimmte Endpunkte ignorieren
                 * =========================== */
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher("/chat/group"), // ✅ Fix für CSRF
                                new AntPathRequestMatcher("/chat/{chatRoomId}/addUsers"), // ✅ Fix für CSRF
                                new AntPathRequestMatcher("/chat/{chatRoomId}/markAsRead"),
                                new AntPathRequestMatcher("/posts/comment") // ✅ CSRF für Kommentare ignorieren
                        )
                )

                /* ===========================
                 *  Autorisierungsregeln
                 * =========================== */
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/register"),
                                new AntPathRequestMatcher("/login"),
                                new AntPathRequestMatcher("/css/**"),
                                new AntPathRequestMatcher("/images/**"),
                                new AntPathRequestMatcher("/static/**"),
                                new AntPathRequestMatcher("/templates/**"),
                                new AntPathRequestMatcher("/passwordreset"),
                                new AntPathRequestMatcher("/upload"),
                                new AntPathRequestMatcher("/js/**"),
                                new AntPathRequestMatcher("/passwordResetProcess"),
                                new AntPathRequestMatcher("/reset-password**"),
                                new AntPathRequestMatcher("/setNewPassword**"),
                                new AntPathRequestMatcher("/posts/**"),
                                new AntPathRequestMatcher("/event/get-event"),
                                new AntPathRequestMatcher("/event/get-event-count"),
                                new AntPathRequestMatcher("/event/show**")
                        ).permitAll() // Diese Endpunkte sind für alle zugänglich

                        .requestMatchers(
                                new AntPathRequestMatcher("/event/**"),
                                new AntPathRequestMatcher("/job/**"))
                        .hasAuthority("ROLE_MODERATOR")

                        .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/moderator/**"), new AntPathRequestMatcher("/event/**"))
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_MODERATOR")

                        .anyRequest().authenticated() // Alle anderen Endpunkte nur für authentifizierte Nutzer
                )

                /* ===========================
                 *  Login-Konfiguration
                 * =========================== */
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(authenticationSuccessHandler()) // Weiterleitung nach Login
                        .permitAll()
                )

                /* ===========================
                 *  "Remember Me"-Funktion
                 * =========================== */
                .rememberMe(rememberMe -> rememberMe
                        .key(REMEMBER_ME_KEY)
                        .tokenValiditySeconds(7 * 24 * 60 * 60) // 7 Tage gültig
                        .rememberMeParameter("remember-me") // HTML-Checkbox "remember-me"
                )

                /* ===========================
                 *  Logout-Konfiguration
                 * =========================== */
                .logout(logout -> logout
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )

                /* ===========================
                 *  Fehlerhandling
                 * =========================== */
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/access-denied") // Eigene Fehlerseite für 403
                );

        return http.build();
    }

    /**
     * Erfolgreicher Login: Weiterleitung je nach Rolle
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            var userDetails = (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
            String redirectUrl = userDetails.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))
                    ? "/admin/AdminDashboard"
                    : "/";
            response.sendRedirect(redirectUrl);
        };
    }
}