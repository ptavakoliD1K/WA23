package com.welfenhub.repositories;

import com.welfenhub.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    void deleteByUsername(String username);
    List<User> findAll();
    List<User> findByUsernameIn(List<String> usernames);

    @Transactional
    @Modifying
    @Query(value = "UPDATE user SET password = :newPassword WHERE id = (SELECT UserID FROM password_reset_tokens WHERE token = :token)", nativeQuery = true)
    void updateUserPassword(@Param("token") String token, @Param("newPassword") String newPassword);



}
