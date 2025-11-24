package com.ja.chegou.ja_chegou.repository;

import com.ja.chegou.ja_chegou.entity.Client;
import com.ja.chegou.ja_chegou.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long>, ClientRepositoryCustom {
    Optional<Client> findByEmail(String email);
}
