package com.ja.chegou.ja_chegou.repository;

import com.ja.chegou.ja_chegou.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
   Optional<Usuario> findByEmail(String email);
   Optional<Usuario> findByCpf(String cpf);
}
