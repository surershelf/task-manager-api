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

    //buscar user com suas atividades
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.activities WHERE u.id = :id")
    Optional<User> findByIdWithActivity(@Param("id") Long id);

    //  buscar por atividades ativas
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.activities a WHERE u.id=:id AND (a.active = true OR a IS NULL )")
    Optional<User> findByIdWithActiveAtivity(@Param("id") Long id);
}
