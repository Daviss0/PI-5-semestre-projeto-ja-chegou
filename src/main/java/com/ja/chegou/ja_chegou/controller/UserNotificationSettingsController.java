package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.entity.UserNotificationSettings;
import com.ja.chegou.ja_chegou.repository.UserNotificationSettingsRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/notifications")
public class UserNotificationSettingsController {

    private final UserNotificationSettingsRepository repository;

    public UserNotificationSettingsController(UserNotificationSettingsRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{email}")
    public UserNotificationSettings getSettings(@PathVariable String email) {
        return repository.findByEmail(email)
                .orElseGet(() -> {
                    // cria padrão caso não exista
                    UserNotificationSettings def = new UserNotificationSettings();
                    def.setEmail(email);
                    return repository.save(def);
                });
    }

    @PostMapping("/save")
    public UserNotificationSettings save(@RequestBody UserNotificationSettings settings) {
        // Verifica se já existe
        return repository.findByEmail(settings.getEmail())
                .map(existing -> {
                    existing.setEnabled(settings.isEnabled());
                    existing.setNotifyMinutes(settings.getNotifyMinutes());
                    existing.setNotifyDistance(settings.getNotifyDistance());
                    return repository.save(existing);
                })
                .orElseGet(() -> repository.save(settings));
    }
}
