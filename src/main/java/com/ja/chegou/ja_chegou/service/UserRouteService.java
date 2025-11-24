package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.UserSavedRoute;
import com.ja.chegou.ja_chegou.repository.UserSavedRouteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRouteService {

    private final UserSavedRouteRepository repository;

    public UserRouteService(UserSavedRouteRepository repository) {
        this.repository = repository;
    }

    public UserSavedRoute save(UserSavedRoute route) {
        return repository.save(route);
    }

    public List<UserSavedRoute> list(String email) {
        return repository.findByEmail(email);
    }
}
