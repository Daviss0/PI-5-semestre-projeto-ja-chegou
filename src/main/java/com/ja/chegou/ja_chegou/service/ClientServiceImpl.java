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

    @Override
    public Optional<Client> findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    @Override
    public Client getByEmail(String email) {
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado para o e-mail informado."));
    }

    @Override
    public Client update(Client client) {
        if (client.getId() == null) {
            throw new RuntimeException("ID do cliente não pode ser nulo para atualização.");
        }

        Optional<Client> existingOpt = clientRepository.findById(client.getId());
        if (existingOpt.isEmpty()) {
            throw new RuntimeException("Cliente não encontrado para atualização.");
        }

        Client existing = existingOpt.get();
        existing.setName(client.getName());
        existing.setCpf(client.getCpf());
        existing.setEmail(client.getEmail());
        existing.setPhone(client.getPhone());
        existing.setCep(client.getCep());
        existing.setLogradouro(client.getLogradouro());
        existing.setNumber(client.getNumber());
        existing.setComplement(client.getComplement());
        existing.setHood(client.getHood());
        existing.setCity(client.getCity());
        existing.setState(client.getState());
        existing.setLastAcess(client.getLastAcess());
        existing.setActive(client.getActive());

        if (client.getPassword() != null && !client.getPassword().isBlank()) {
            existing.setPassword(client.getPassword());
        }

        return clientRepository.save(existing);
    }

}
