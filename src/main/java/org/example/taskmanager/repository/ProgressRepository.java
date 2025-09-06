package org.example.taskmanager.repository;

import org.example.taskmanager.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress,Long> {

    //Ver pprogresso de uma atividade especifica
    List<Progress> findByActivityIdOrderByFinishDateDesc(Long activityId);

    // Ver progresso de um usuario especifico
    @Query("SELECT p FROM Progress p WHERE p.activity.user.id = :userId ORDER BY p.finishDate DESC")
    List<Progress> findByUserId(@Param("userId")Long userId);

    //Buscar progresso por data
    List<Progress> findByFinishDateBetween(LocalDate start, LocalDate finish);

    //Progresso de hoje para um usuario
    @Query("SELECT p FROM Progress p WHERE p.activity.user.id = :userId AND p.finishDate = :today")
    List<Progress> findProgressToday(@Param("userID")Long userID, @Param("today")LocalDate today);

    //Contar progressos por status
    long countByStatus(Progress.Status status);

    //Contar progressos de um usuário por status
    @Query("SELECT COUNT(p) FROM Progress p WHERE p.activity.user.id = :userId AND p.status = :status")
    long countByUserAndStatus(@Param("userID")Long userID, @Param("status") Progress.Status status);

    //Ultimo progresso de uma atividade
    Optional<Progress> findFirstByActivityIdOrderByFinishDate(Long activityId);

    //Verificar se já existe progresso para atividade em data especifica
    Optional<Progress> findByActivityIdAndFinishDate(Long activityId, LocalDate date);

    //Estatisticas: progresso nos últimos 30 dias
    @Query("SELECT p FROM Progress p WHERE p.activity.user.id = :userId AND p.finishDate>= :dataInicio ORDER BY p.finishDate DESC")
    List<Progress> findProgressLast30Days(@Param("userId")Long userId, @Param("dataInicio")LocalDate dataInicio);
}
