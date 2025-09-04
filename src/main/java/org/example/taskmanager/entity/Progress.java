package org.example.taskmanager.entity;

import jakarta.persistence.*;
import jdk.jshell.Snippet;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "progress")
public class Progress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long id;

    @Column(name = "finish_date",nullable = false)
    private LocalDate finishDate;

    @Column(name = "status",nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false,foreignKey = @ForeignKey(name = "fk_progress_activity"))
    @ToString.Exclude
    private Activity activity;

    public enum Status{
        STARTED("started"),
        FINISHED("finished");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
