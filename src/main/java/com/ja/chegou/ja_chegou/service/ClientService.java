package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.Client;

import java.util.Optional;

public interface ClientService {
    Client register(Client client);

    Client login(String email, String typedPassword);

    Optional<Client> findById(Long id);

    void delete(Long id);
}
