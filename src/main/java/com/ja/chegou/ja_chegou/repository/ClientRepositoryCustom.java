package com.ja.chegou.ja_chegou.repository;

import com.ja.chegou.ja_chegou.entity.Client;
import org.springframework.transaction.annotation.Transactional;

public interface ClientRepositoryCustom {
    void refresh(Client client);


}
