package com.lostfound.model;

import com.lostfound.util.IdGenerator;
import java.time.LocalDateTime;

public class User implements Auditable {

    private int userId;
    private String username;
    private String password;
    private String phone;
    private String email;
    private String role;

    private String createdBy;
    private LocalDateTime createdAt;

    private String updatedBy;
    private LocalDateTime updatedAt;

    public User(String username, String password, String phone, String email, String role) {

        this.userId = IdGenerator.generateUserId();
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.role = role;
    }

    public User(int userId, String username, String password, String phone, String email, String role,
                String createdBy, String createdAt, String updatedBy, String updatedAt) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.role = role;

        this.createdBy = createdBy;
        if (createdAt != null && !createdAt.isBlank()) {
            this.createdAt = LocalDateTime.parse(createdAt);
        }

        this.updatedBy = updatedBy;
        if (updatedAt != null && !updatedAt.isBlank()) {
            this.updatedAt = LocalDateTime.parse(updatedAt);
        }
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername(){ return username; }

    public String getPassword(){ return password; }

    public String getPhone(){ return phone; }

    public String getEmail(){ return email; }

    public String getCreatedBy() { return createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public String getUpdatedBy() { return updatedBy; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public String getRole(){
    return role;
}

    @Override
    public void setAuditInfo(String user){

        createdBy = user;
        createdAt = LocalDateTime.now();
    }

    @Override
    public void updateAuditInfo(String user){

        updatedBy = user;
        updatedAt = LocalDateTime.now();
    }

    public String toFile(){

        return userId+","+username+","+password+","+phone+","+email;
    }
}