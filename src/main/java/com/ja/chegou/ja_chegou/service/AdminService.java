package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.Admin;
import org.springframework.stereotype.Service;

import java.util.Optional;


public interface AdminService {
    Admin register(Admin admin, String confPassword);

    Optional<Admin> login(String email, String password);

    Admin updateStatus(Long id, String status);

    Admin searchById(Long id);

    Admin update(Long id, Admin updatedAdmin);

    Admin updateBasicData(Long id, Admin updatedAdmin);

    void updatePassword(Long id, String currentPassword, String newPassword, String confPassword);
}
