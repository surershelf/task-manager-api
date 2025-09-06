package org.example.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "activity")
public class Activity {
    @Id
    @Column(name = "activity_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "frequency", nullable = false)
    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    @Column(name = "init_date",nullable = false)
    private LocalDate initDate;

    @Column(name = "active",nullable = false)
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false,foreignKey = @ForeignKey(name = "fk_activity_user"))
    @ToString.Exclude
    private User user;

    @OneToMany(mappedBy ="activity",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Progress> progresses;

    public enum Frequency {
        DAILY("daily"),
        WEEKLY("weekly"),
        MONTHLY("monthly");

        private final String value;

        Frequency(String value) {
            this.value = value;}

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}