package org.example.taskmanager.controller;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager.entity.User;
import org.example.taskmanager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // --- DTOs (Data Transfer Objects) ---
    record UpdateProfileRequest(String name, String email, LocalDate birthDate) {}
    record ChangePasswordRequest(String currentPassword, String newPassword) {}
    record UserResponse(Long id, String name, String email, LocalDate birthDate, String creationDate) {
        public UserResponse(User user){
            this(user.getId(), user.getName(), user.getEmail(), user.getBirthDate(), user.getCreationDate().toString());
        }
    }
    // DTO Otimizado: Recebe as contagens diretamente para evitar carregar a lista de atividades
    record UserWithActivitiesResponse(Long id, String name, String email, LocalDate birthDate, String creationDate,
                                      long totalActivities, long activeActivities) {
        public UserWithActivitiesResponse(User user, long totalActivities, long activeActivities) {
            this(user.getId(), user.getName(), user.getEmail(), user.getBirthDate(), user.getCreationDate().toString(),
                    totalActivities, activeActivities);
        }
    }
    record ErrorResponse(String error) {}
    record MessageResponse(String message) {}

    // Ver perfil básico
    @GetMapping("/{userId}/profile")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Usuário não encontrado!"));
        }
        return ResponseEntity.ok(new UserResponse(userOpt.get()));
    }

    // Ver perfil com contagem de atividades (forma otimizada)
    @GetMapping("/{userId}/profile-with-activities")
    public ResponseEntity<?> getProfileWithActivities(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Usuário não encontrado!"));
        }
        // Lógica otimizada: busca as contagens diretamente do banco de dados
        long totalActivities = userRepository.countTotalActivitiesByUserId(userId);
        long activeActivities = userRepository.countActiveActivitiesByUserId(userId);
        return ResponseEntity.ok(new UserWithActivitiesResponse(userOpt.get(), totalActivities, activeActivities));
    }

    // Atualizar perfil
    @PutMapping("/{userId}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId, @RequestBody UpdateProfileRequest request) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Usuário não encontrado!"));
        }

        User user = userOpt.get();

        // Atualiza o nome se fornecido
        if (request.name() != null && !request.name().trim().isEmpty()) {
            user.setName(request.name().trim());
        }

        // Atualiza o email se fornecido e verifica se o NOVO email já está em uso
        if (request.email() != null && !request.email().trim().isEmpty()) {
            // BUG CORRIGIDO AQUI: Verifica se o NOVO email já existe e não pertence ao usuário atual
            if (!request.email().equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(request.email())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("Este e-mail já está sendo usado por outro usuário!"));
            }
            user.setEmail(request.email().trim());
        }

        // Atualiza a data de nascimento se fornecida
        if (request.birthDate() != null) {
            user.setBirthDate(request.birthDate());
        }
        User updatedUser = userRepository.save(user);

        return ResponseEntity.ok(new UserResponse(updatedUser));
    }

    // Alterar senha
    @PutMapping("/{userId}/password")
    public ResponseEntity<?> changePassword(@PathVariable Long userId, @RequestBody ChangePasswordRequest request) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Usuário não encontrado!"));
        }
        User user = userOpt.get();

        // Verificar a senha atual
        if (!passwordEncoder.matches(request.currentPassword(), user.getHashPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Senha atual incorreta!"));
        }

        // Validar nova senha
        if (request.newPassword() == null || request.newPassword().length() < 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("A nova senha deve ter no mínimo 6 caracteres!"));
        }

        // Atualizar a senha
        user.setHashPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Senha alterada com sucesso!"));
    }

    // Verificar se um email já existe
    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkEmail(@PathVariable String email) {
        boolean exists = userRepository.existsByEmail(email);
        return ResponseEntity.ok(new MessageResponse(exists ? "E-mail já existe!" : "E-mail disponível!"));
    }

    // Buscar usuário por email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Usuário não encontrado!"));
        }
        return ResponseEntity.ok(new UserResponse(userOpt.get()));
    }
}