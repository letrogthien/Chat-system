package com.JRobusta.chat.core_services.auth_module.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "user_bases")
@Setter
@Getter
@ToString
public class UserBase {

    @Id
    protected String id;

    @Column(nullable = false, unique = true)
    protected String email;

    @Column(nullable = false, unique = true)
    protected String userName;
    
    @Column(nullable = false)
    protected String passwordHash;

    @Column(nullable = false)
    protected Instant createdAt;

    @Column(nullable = false)
    protected Instant updatedAt;


    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
