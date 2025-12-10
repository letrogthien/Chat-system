package com.JRobusta.chat.core_services.auth_module.entities;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "user_bases")
@Setter
@Getter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserBase {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
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
