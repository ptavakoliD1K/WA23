package com.welfenhub.repositories;

import com.welfenhub.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    void deleteByUsername(String username);


    List<User> findAll();
    List<User> findByUsernameIn(List<String> usernames);

}
