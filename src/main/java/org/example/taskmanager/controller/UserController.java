package org.example.taskmanager.controller;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager.entity.User;
import org.example.taskmanager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    record UpdateProfileRequest(String name, String email, LocalDate birthDate) {}

    record ChangePasswordRequest(String currentPassword, String newPassword) {}
    record UserResponse (Long id, String name, String email, LocalDate birthDate, String creationDate) {
        public UserResponse(User user){
            this(user.getId(), user.getName(), user.getEmail(), user.getBirthDate(),user.getCreationDate().toString());
        }
    }
    record UserWithActivitiesResponse(Long id, String name, String email, LocalDate birthDate, String creationDate,
                                      int totalActivities, int activeActivities) {
        public UserWithActivitiesResponse(User user){
            this(user.getId(), user.getName(), user.getEmail(), user.getBirthDate(), user.getCreationDate().toString(),
                    user.getActivities() != null ? user.getActivities().size() : 0,
                    user.getActivities() != null ? (int) user.getActivities().stream().filter(a -> a.getActive()).count() : 0);
        }
    }
    record ErrorResponse (String error){}
    record MessageResponse (String message){}

    //Ver perfil básico
    @GetMapping("/{userId}/profile")
    public ResponseEntity<?> getProfile(@PathVariable Long userId){
        try{
            Optional<User> userOpt = userRepository.findById(userId);

            if (userOpt.isEmpty()){
                return ResponseEntity.badRequest().body(new ErrorResponse("Usuário não encontrado!"));
            }
            return ResponseEntity.ok(new UserResponse(userOpt.get()));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Erro interno no servidor!"));
        }
    }

    //Ver perfil com atividades
    @GetMapping("/{userId}/profile-with-activities")
    public ResponseEntity<?> getProfileWithActivities(@PathVariable Long userId){
        try {
            Optional<User> userOpt = userRepository.findById(userId);

            if (userOpt.isEmpty()){
                return ResponseEntity.badRequest().body(new ErrorResponse("Usuário não encontrado!"));
            }
            return ResponseEntity.ok(new UserWithActivitiesResponse(userOpt.get()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno no servidor!"));
        }
    }

    //Ver perfil com atividades ativas
    @GetMapping("/{userId}/profile-with-active-activities")
    public ResponseEntity<?> getProfileWithActiveActivities(@PathVariable Long userId){
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()){
                return ResponseEntity.badRequest().body(new ErrorResponse("Usuário não encontrado!"));
            }

            return ResponseEntity.ok(new UserWithActivitiesResponse(userOpt.get()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno no servidor!"));
        }
    }

    //Atualizar perfil
    @PutMapping("/{userId}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId, @RequestBody UpdateProfileRequest request){
        try{
            Optional<User> userOpt = userRepository.findById(userId);

            if (userOpt.isEmpty()){
                return ResponseEntity.badRequest().body(new ErrorResponse("Usuário não encontrado!"));
            }

            User user =  userOpt.get();

            //atualizar os campos se fornecido
            if (request.name() != null && !request.name().trim().isEmpty()){
                user.setName(request.name().trim());
            }

            if (request.email() != null && !request.email().trim().isEmpty()){
                if (!request.email().equals(user.getEmail())&& userRepository.existsByEmail(user.getEmail())){
                    return ResponseEntity.badRequest()
                            .body(new ErrorResponse("Email já esta sendo usado por outro usuário!"));
                }
                user.setEmail(request.email().trim());
            }

            if (request.birthDate() != null){
                user.setBirthDate(request.birthDate());
            }
            User updatedUser = userRepository.save(user);

            return ResponseEntity.ok(new UserResponse(updatedUser));
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno no servidor!"));
        }
    }

    //Alterar senha
    @PutMapping("/{userId}/password")
    public ResponseEntity<?> changePassword(@PathVariable Long userId, @RequestBody ChangePasswordRequest request){
        try{
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()){
                return ResponseEntity.badRequest().body(new ErrorResponse("Usuário não encontrado!"));
            }
            User user =  userOpt.get();
            //Verificar a senha atual
            if (!passwordEncoder.matches(request.currentPassword(), user.getHashPassword())){
                return ResponseEntity.badRequest().body(new ErrorResponse("Senha incorreta!"));
            }

            //validar nova senha
            if (request.newPassword() == null || request.newPassword().length() < 6){
                return ResponseEntity.badRequest().body(new ErrorResponse("Nova senha deve ter no mínimo 6 caracteres!"));
            }

            //atualizar a senha
            user.setHashPassword(passwordEncoder.encode(request.newPassword()));
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse("Senha alterada com sucesso!"));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno no servidor!"));
        }
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkEmail(@PathVariable String email){
        try{
            boolean exists = userRepository.existsByEmail(email);
            return ResponseEntity.ok(new MessageResponse(exists ? "Email já existe!" : "Email desponível!" ));
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno no servidor!"));
        }
    }
    //buscar user por email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getEmail(@PathVariable String email){
        try{
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()){
                return ResponseEntity.badRequest().body("Usuário não encontrado!");
            }
            return ResponseEntity.ok(new UserResponse(userOpt.get()));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Erro interno no servidor!"));
        }
    }
}
