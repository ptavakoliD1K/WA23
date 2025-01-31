package com.welfenhub.repositories;

import com.welfenhub.models.PasswordResetTokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface PasswordResetTokensRepository extends JpaRepository<PasswordResetTokens, Long> {

    @Query("SELECT p.expiresAt FROM PasswordResetTokens p WHERE p.token = :token")
    List<String> selectExpiresAt(@Param("token") String token);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO password_reset_tokens (token, UserID, expires_at, is_password_changed) VALUES (:token, :userId, :expiresAt, :isPasswordChanged)", nativeQuery = true)
    void insertToken(@Param("token") String token, @Param("userId") Long userId, @Param("expiresAt") String expiresAt, @Param("isPasswordChanged") int isPasswordChanged);

    @Query("SELECT p.isPasswordChanged FROM PasswordResetTokens p WHERE p.token = :token")
    List<Integer> selectIsPasswordChanged(@Param("token") String token);


    @Transactional
    @Modifying
    @Query(value = "UPDATE password_reset_tokens SET is_password_changed = 1 WHERE token = :token", nativeQuery = true)
    void updateIsPasswordChanged(@Param("token") String token);
}
