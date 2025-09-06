package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.Admin;
import com.ja.chegou.ja_chegou.enumerated.Role;
import com.ja.chegou.ja_chegou.enumerated.Status;
import com.ja.chegou.ja_chegou.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Admin register(Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setStatus(Status.ATIVO);
        admin.setRole(Role.ADMIN);
        return adminRepository.save(admin);
    }

    @Override
    public Optional<Admin> login(String email, String password) {
        return adminRepository.findByEmail(email)
                .filter(admin -> passwordEncoder.matches(password, admin.getPassword()));
    }

    @Override
    public Admin updateStatus(Long id, String status) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        admin.setStatus(Status.valueOf(status.toUpperCase()));
        return adminRepository.save(admin);
    }

}
