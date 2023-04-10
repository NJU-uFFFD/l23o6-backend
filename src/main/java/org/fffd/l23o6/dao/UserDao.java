package org.fffd.l23o6.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.fffd.l23o6.entity.UserEntity;

public interface UserDao extends JpaRepository<UserEntity, Integer> {
    UserEntity findByUsername(String username);
}