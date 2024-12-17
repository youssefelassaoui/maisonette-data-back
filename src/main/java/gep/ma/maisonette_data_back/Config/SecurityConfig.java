package gep.ma.maisonette_data_back.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Désactiver CSRF pour API REST
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/verify").permitAll() // Routes publiques
                        .requestMatchers("/api/campus/**").authenticated() // Sécuriser toutes les routes /api/campus/**
                        .anyRequest().authenticated()) // Toutes les autres routes nécessitent une authentification
                .httpBasic(withDefaults()) // Authentification basique (pour démo, remplacez par JWT en prod)
                .logout(logout -> logout.logoutUrl("/logout")); // Configuration pour logout

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
