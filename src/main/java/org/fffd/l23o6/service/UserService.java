package org.fffd.l23o6.service;

import org.fffd.l23o6.pojo.entity.UserEntity;

public interface UserService {
    void login(String username, String password);
    void register(String username, String password);

    UserEntity findByUserName(String username);
}