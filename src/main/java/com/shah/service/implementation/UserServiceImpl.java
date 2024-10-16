package com.shah.service.implementation;

import com.shah.config.JwtProvider;
import com.shah.modal.User;
import com.shah.repository.UserRepository;
import com.shah.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUserProfile(String jwt) {
        String email = JwtProvider.getEmailFromJwt(jwt);
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
