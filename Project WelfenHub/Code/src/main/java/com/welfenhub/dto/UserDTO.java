package com.welfenhub.dto;

public class UserDTO {
    private String username;
    private String fullName;

    public UserDTO(String username, String fullName) {
        this.username = username;
        this.fullName = fullName;
    }

    // Getter und Setter
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
