package com.ja.chegou.ja_chegou.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UserNotificationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private boolean enabled = false;
    private Integer notifyMinutes = 3;  // exemplo padrão
    private Integer notifyDistance = 600; // exemplo padrão (em metros)
}
