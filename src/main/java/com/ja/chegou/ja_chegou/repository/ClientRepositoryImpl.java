package com.ja.chegou.ja_chegou.repository;

import com.ja.chegou.ja_chegou.entity.Client;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

public class ClientRepositoryImpl implements ClientRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public void refresh(Client client) {
        entityManager.refresh(client);
    }
}
