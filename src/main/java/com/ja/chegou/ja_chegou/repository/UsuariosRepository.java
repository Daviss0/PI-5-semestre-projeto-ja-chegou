package com.ja.chegou.ja_chegou.repository;

import com.ja.chegou.ja_chegou.entity.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuarios, Integer> {
   Optional<Usuarios> findByEmail(String email);
   Optional<Usuarios> findByCpf(String cpf);
}
