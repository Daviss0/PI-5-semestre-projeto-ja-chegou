package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.Admin;
import org.springframework.stereotype.Service;

import java.util.Optional;


public interface AdminService {
    Admin register(Admin admin);

    Optional<Admin> login(String email, String password);

    Admin updateStatus(Long id, String status);
}
