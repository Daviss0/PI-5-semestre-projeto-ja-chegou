package com.ja.chegou.ja_chegou.repository;

import com.ja.chegou.ja_chegou.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
    Optional<Admin> findByCpf(String cpf);
}
