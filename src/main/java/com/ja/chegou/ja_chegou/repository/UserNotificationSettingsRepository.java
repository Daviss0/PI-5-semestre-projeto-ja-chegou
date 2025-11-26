package com.ja.chegou.ja_chegou.repository;

import com.ja.chegou.ja_chegou.entity.UserNotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserNotificationSettingsRepository extends JpaRepository<UserNotificationSettings, Long> {
    Optional<UserNotificationSettings> findByEmail(String email);
}
