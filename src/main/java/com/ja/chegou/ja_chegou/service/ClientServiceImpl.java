package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.Client;
import com.ja.chegou.ja_chegou.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientServiceImpl implements  ClientService{

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Client register(Client client) {
        if (clientRepository.findByEmail(client.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado!");
        }

        client.setPassword(passwordEncoder.encode(client.getPassword()));

        return clientRepository.save(client);
    }

    @Override
    public Client login(String email, String typedPassword) {
        var client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(typedPassword, client.getPassword())) {
            throw new RuntimeException("Senha incorreta");
        }
        return client;
    }

    @Override
    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }
    @Override
    public void delete(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("cliente não localizado");
        }
        clientRepository.deleteById(id);
    }
}
