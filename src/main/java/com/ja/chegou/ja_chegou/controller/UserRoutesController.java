// ============================
// UserRoutesController.java — FINAL ATUALIZADO
// ============================

package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.entity.UserSavedRoute;
import com.ja.chegou.ja_chegou.repository.UserSavedRouteRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/routes")
public class UserRoutesController {

    private final UserSavedRouteRepository repository;

    public UserRoutesController(UserSavedRouteRepository repository) {
        this.repository = repository;
    }

    // Salva rota completa (agora incluindo baseLat/baseLon)
    @PostMapping("/add")
    public UserSavedRoute save(@RequestBody UserSavedRoute route) {
        return repository.save(route);
    }

    @GetMapping("/{email}")
    public List<UserSavedRoute> list(@PathVariable String email) {
        String cleanEmail = email.trim(); // remove %0A, espaços, etc.
        return repository.findByEmail(cleanEmail);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
