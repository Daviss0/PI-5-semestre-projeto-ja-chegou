package com.ja.chegou.ja_chegou.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @PostMapping("/subscribe")
     public void subscribe(@RequestBody Map<String, Object> subscription) {
        System.out.println("Nova inscrição recebida");
        System.out.println(subscription);
    }
}
