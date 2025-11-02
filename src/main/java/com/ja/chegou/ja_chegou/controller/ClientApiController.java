package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.entity.Client;
import com.ja.chegou.ja_chegou.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
public class ClientApiController {

    @Autowired
    private ClientService clientService;

    @PostMapping("/register")
    public ResponseEntity<?> register (@RequestBody @Valid Client client) {
        try {
            return ResponseEntity.ok(clientService.register(client));
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        Optional<Client> optClient = clientService.findByEmail(email);
        if (optClient.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail não encontrado.");

        Client client = optClient.get();
        if (!client.getPassword().equals(password))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha incorreta.");

        client.setLastAcess(LocalDateTime.now());
        clientService.update(client);

        return ResponseEntity.ok("Login realizado com sucesso!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClient(@PathVariable Long id) {
        return clientService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
