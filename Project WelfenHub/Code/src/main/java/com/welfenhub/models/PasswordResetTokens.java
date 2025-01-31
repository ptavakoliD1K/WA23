package com.welfenhub.models;


import javax.persistence.*;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetTokens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token")
    private String token;

    @Column(name = "UserID")
    private int UserId;

    @Column(name = "expires_at")
    private String expiresAt;

    @Column(name = "is_password_changed")
    private int isPasswordChanged;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public int getIsPasswordChanged() {
        return isPasswordChanged;
    }

    public void setIsPasswordChanged(int isPasswordChanged) {
        this.isPasswordChanged = isPasswordChanged;
    }
}
