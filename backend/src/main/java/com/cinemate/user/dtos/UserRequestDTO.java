package com.cinemate.user.dtos;

import com.cinemate.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

public class UserRequestDTO {
    private String id;
    @NotNull
    private String username;
    @Size(min = 6, max = 40)
    @NotNull
    private String password;
    @Email
    private String email;
    private String bio;
    private String avatarUrl;
    private boolean removeAvatar;
    private Date joinedAt;
    private Role role;

    public UserRequestDTO(String id, String username, String password, String email, String bio, String avatarUrl, Date joinedAt, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.joinedAt = joinedAt;
        this.role = role;
    }

    public UserRequestDTO() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Date joinedAt) {
        this.joinedAt = joinedAt;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isRemoveAvatar() {
        return removeAvatar;
    }

    public void setRemoveAvatar(boolean removeAvatar) {
        this.removeAvatar = removeAvatar;
    }

}
