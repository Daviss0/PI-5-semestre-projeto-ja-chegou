package com.ja.chegou.ja_chegou.security;

import com.ja.chegou.ja_chegou.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
        this.customUserDetailsService = userDetailsService;
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth
                        // 🔓 Páginas públicas (sem necessidade de login)
                        .requestMatchers(
                                "/", "/mainPage",
                                "/manifest.webmanifest",
                                "/sw.js",
                                "/icons/**",
                                "/css/**", "/js/**", "/images/**", "/webjars/**",
                                "/login", "/cadastro",
                                "/api/routes/public",
                                "/api/trucks/public/**",
                                "/api/osrm/**"
                        ).permitAll()

                        // 🔐 Rotas administrativas (exigem login de ADMIN)
                        .requestMatchers(
                                "/admin/login_adm", "/admin/register", "/h2-console/**"
                        ).permitAll()
                        .requestMatchers("/admin/**", "/driver/**", "/user/**").hasRole("ADMIN")

                        // Qualquer outra rota precisa de autenticação
                        .anyRequest().authenticated()
                )
                // ⚙️ Configuração de login administrativo
                .formLogin(form -> form
                        .loginPage("/admin/login_adm")
                        .loginProcessingUrl("/admin/login")
                        .defaultSuccessUrl("/admin/home", true)
                        .failureHandler(customAuthenticationFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout.permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
