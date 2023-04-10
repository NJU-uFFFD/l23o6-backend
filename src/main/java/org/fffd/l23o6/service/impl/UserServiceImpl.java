package org.fffd.l23o6.service.impl;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.fffd.l23o6.service.UserService;
import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.entity.UserEntity;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    @Override
    public void login(String username, String password) {
        UserEntity user = userDao.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new RuntimeException("用户名或密码错误");
        }
    }
}