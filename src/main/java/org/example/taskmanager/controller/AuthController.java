package org.example.taskmanager.controller;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.example.taskmanager.entity.User;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    record AccountCreateRequest(String name, String email, String password, LocalDate birthDate) {
    }

    // DTO para enviar a resposta com os dados do usuário criado
    record UserResponse(Long id, String name, String email, String creationDate) {
        // Construtor auxiliar para facilitar a criação a partir da entidade User
        public UserResponse(User user) {
            this(user.getId(), user.getName(), user.getEmail(), user.getCreationDate().toString());
        }
    }

    record ErrorResponse(String error) {
    }

    record LoginRequest(String email, String password) {
    }

    record RecuperarSenhaRequest(String email) {
    }

    record LoginResponse(Long userId, String name, String email, String message) {
    }

    record MessageResponse(String message) {
    }


    //Criar conta
    @PostMapping("/register")
    public ResponseEntity<?> criarConta(@RequestBody AccountCreateRequest request) {
        try {
            //verifica se email já existe
            if (userRepository.existsByEmail(request.email())) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Email já em uso"));
            }

            User newUser = new User();
            newUser.setName(request.name());
            newUser.setEmail(request.email());
            newUser.setHashPassword(passwordEncoder.encode(request.password()));
            newUser.setBirthDate(request.birthDate());


            User savedUser = userRepository.save(newUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(savedUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Erro interno no servidor!"));

        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(request.email());

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Credenciais Invalidas"));
            }
            User user = userOpt.get();

            //verifica se a password está correta
            if (!passwordEncoder.matches(request.password(), user.getHashPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Credenciais invalidas"));
            }
            return ResponseEntity.ok(new LoginResponse(user.getId(),
                    user.getName(),
                    user.getEmail(), "Login realizado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Erro interno no servidor!"));

        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody RecuperarSenhaRequest request) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(request.email());
            if (userOpt.isEmpty()) {
                return ResponseEntity.ok(new MessageResponse("Se o email existir, você receberá instruções " +
                        "para redefinir a password"));

            }
            return ResponseEntity.ok(new MessageResponse("Instruções para redefinir sua password enviada no email "));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno no servidor!"));
        }
    }
}

