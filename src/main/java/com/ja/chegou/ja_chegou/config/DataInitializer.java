

package com.ja.chegou.ja_chegou.config;

import com.ja.chegou.ja_chegou.entity.Admin;
import com.ja.chegou.ja_chegou.enumerated.Role;
import com.ja.chegou.ja_chegou.enumerated.Status;
import com.ja.chegou.ja_chegou.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdmin(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (adminRepository.findAll().isEmpty()) {
                Admin admin = new Admin();
                admin.setCpf("39053344705");
                admin.setName("Administrador Padrão");
                admin.setEmail("admin@admin.com");
                admin.setPassword(passwordEncoder.encode("123456")); // senha em BCrypt
                admin.setRole(Role.ADMIN);
                admin.setStatus(Status.ATIVO);

                adminRepository.save(admin);
                System.out.println(">>> Admin padrão criado: email=admin@admin.com | senha=123456");
            }
        };
    }
}
