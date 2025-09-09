package org.example.taskmanager.repository;

import org.example.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <User, Long> {

    //para autenticacao - buscar por email
    Optional<User> findByEmail(String email);

    //verificar se existe o email
    boolean existsByEmail(String email);

    @Query("SELECT COUNT(a) FROM Activity a WHERE a.user.id = :userId")
    long countTotalActivitiesByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(a) FROM Activity a WHERE a.user.id = :userId AND a.active = true")
    long countActiveActivitiesByUserId(@Param("userId") Long userId);
}
