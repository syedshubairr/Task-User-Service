package com.shah.service;

import com.shah.modal.User;

import java.util.List;

public interface UserService {
    public User getUserProfile(String jwt);

    public List<User> getAllUsers();
}
