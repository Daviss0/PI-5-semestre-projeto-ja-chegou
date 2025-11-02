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
                        // 🔓 Endpoints públicos da API (liberados para o app Expo)
                        .requestMatchers("/api/collections/**").permitAll()
                        .requestMatchers("/api/osrm/**").permitAll()
                        .requestMatchers("/api/routes/public").permitAll()
                        .requestMatchers("/api/trucks/public/**").permitAll()

                        // 🔓 Recursos estáticos e páginas públicas
                        .requestMatchers(
                                "/", "/mainPage",
                                "/manifest.webmanifest",
                                "/sw.js",
                                "/icons/**",
                                "/css/**", "/js/**", "/images/**", "/webjars/**",
                                "/login", "/cadastro"
                        ).permitAll()

                        // 🔐 Login administrativo e console
                        .requestMatchers("/admin/login_adm", "/admin/register", "/h2-console/**").permitAll()
                        .requestMatchers("/admin/**", "/driver/**", "/user/**").hasRole("ADMIN")

                        // 🚫 Tudo o resto precisa de login
                        .anyRequest().authenticated()
                )
                // ⚙️ Configuração de login administrativo (mantida)
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
