package org.example.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false,unique = true, length = 100)
    private String email;

    @Column(name = "hash_password", nullable = false, length = 255)
    private String hashPassword;

    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @OneToMany(mappedBy ="user",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Activity> activities;

    @PrePersist
    public void onCreate() {
        this.creationDate = LocalDateTime.now();
    }

}
