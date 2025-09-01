package com.example.testfrontendbackenddb.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "security_user")
public class SecurityUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "security_name")
    private String securityName;

    @Column(name = "security_mobileNo")
    private String securityMobileNo;

    @Column(name = "security_emailId")
    private String securityEmailId;

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getSecurityName() { return securityName; }
    public void setSecurityName(String securityName) { this.securityName = securityName; }

    public String getSecurityMobileNo() { return securityMobileNo; }
    public void setSecurityMobileNo(String securityMobileNo) { this.securityMobileNo = securityMobileNo; }

    public String getSecurityEmailId() { return securityEmailId; }
    public void setSecurityEmailId(String securityEmailId) { this.securityEmailId = securityEmailId; }
}