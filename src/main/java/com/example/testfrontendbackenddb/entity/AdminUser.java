package com.example.testfrontendbackenddb.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "admin_user")
public class AdminUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "admin_type")
    private String adminType;

    @Column(name = "admin_name")
    private String adminName;

    @Column(name = "admin_mobile_no")
    private String adminMobileNo;

    @Column(name = "admin_emailId")
    private String adminEmailId;

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getAdminType() {
        return adminType;
    }

    public void setAdminType(String adminType) {
        this.adminType = adminType;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminMobileNo() {
        return adminMobileNo;
    }

    public void setAdminMobileNo(String adminMobileNo) {
        this.adminMobileNo = adminMobileNo;
    }

    public String getAdminEmailId() {
        return adminEmailId;
    }

    public void setAdminEmailId(String adminEmailId) {
        this.adminEmailId = adminEmailId;
    }
}