package com.ja.chegou.ja_chegou.repository;

import com.ja.chegou.ja_chegou.entity.UserSavedRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSavedRouteRepository extends JpaRepository<UserSavedRoute, Long> {
    List<UserSavedRoute> findByEmail(String email);
}
