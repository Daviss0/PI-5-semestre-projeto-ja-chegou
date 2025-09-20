package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.User;
import com.ja.chegou.ja_chegou.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
