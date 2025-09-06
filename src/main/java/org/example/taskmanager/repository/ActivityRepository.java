package org.example.taskmanager.repository;

import org.example.taskmanager.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByUserId(Long userId);

    // Buscar apenas atividades ativas do usuário
    List<Activity> findByUserIdAndActiveTrue(Long userId);

    //Buscar atividade por frequencia
    List<Activity> findByUserIdAndFrequency(Long userId, Activity.Frequency frequency);

    //Buscar ativdade especifica do usuario
    Optional<Activity> findByIdAndUserId(Long id, Long userId);

    //contar atividade ativas do usuário
    long countByUserIdAndActiveTrue(Long userId);

    //buscar atividade em progresso
    @Query("SELECT a FROM Activity a LEFT JOIN FETCH a.progresses p WHERE a.user.id = :userId AND a.active = true")
    List<Activity> findActivityWithProgress(@Param("userId") Long userId);

    //Buscar Titulo Similar
    List<Activity> findByUserIdAndTitleContainingIgnoreCase(Long userId, String title);
}