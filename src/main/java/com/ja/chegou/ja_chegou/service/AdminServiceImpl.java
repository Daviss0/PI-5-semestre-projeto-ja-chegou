package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.Admin;
import com.ja.chegou.ja_chegou.enumerated.Role;
import com.ja.chegou.ja_chegou.enumerated.Status;
import com.ja.chegou.ja_chegou.repository.AdminRepository;
import com.ja.chegou.ja_chegou.utils.CpfUtils;
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
    public Admin register(Admin admin, String confPassword) {

        if (!admin.getPassword().equals(confPassword)) {
            throw new IllegalArgumentException("As senhas não conferem!");
        }

        if (!CpfUtils.isValidCPF(admin.getCpf())) {
            throw new IllegalArgumentException("CPF inválido!");
        }

        String cpfSemMascara = admin.getCpf().replaceAll("\\D", "");

        if (adminRepository.findByCpf(cpfSemMascara).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário com este CPF!");
        }

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setStatus(Status.ATIVO);
        admin.setRole(Role.ADMIN);
        admin.setCpf(cpfSemMascara);

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

    @Override
    public Admin searchById(Long id) {
      return adminRepository.findById(id)
              .orElseThrow(() -> new RuntimeException("Usuário inválido" + id));
    }
    @Override
    public Admin update(Long id, Admin updatedAdmin) {
        Admin admin = searchById(id);

        admin.setName(updatedAdmin.getName());
        admin.setEmail(updatedAdmin.getEmail());
        admin.setPassword(passwordEncoder.encode(updatedAdmin.getPassword()));

        return adminRepository.save(admin);
    }

    @Override
    public Admin updateBasicData(Long id, Admin updatedAdmin){
      Admin admin = searchById(id);

      admin.setName(updatedAdmin.getName());
      admin.setEmail(updatedAdmin.getEmail());
      admin.setStatus(updatedAdmin.getStatus());
      return adminRepository.save(admin);
    }

    @Override
    public void updatePassword(Long id, String currentPassword, String newPassword, String confPassword) {
        Admin admin = searchById(id);

        if(!passwordEncoder.matches(currentPassword, admin.getPassword())) {
            throw new RuntimeException("Senha atual incorreta");
        }
        if(!newPassword.equals(confPassword)) {
            throw new RuntimeException("A nova senha e a confirmação não coincidem");
        }
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);
    }
}
