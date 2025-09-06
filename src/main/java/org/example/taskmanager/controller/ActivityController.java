package org.example.taskmanager.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.taskmanager.entity.Activity;
import org.example.taskmanager.entity.User;
import org.example.taskmanager.repository.ActivityRepository;
import org.example.taskmanager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    //DTOs
    record CreateActivityRequest(String title, String description, String frequency, LocalDate startDate) {
    }

    record UpdateActivityRequest(String title, String description, String frequency, LocalDate startDate) {
    }

    record ActivityResponse(Long id, String title, String description, String frequency, LocalDate startDate,
                            boolean active) {
        public ActivityResponse(Activity activity) {
            this(activity.getId(), activity.getTitle(), activity.getDescription(), activity.getFrequency().name(),
                    activity.getInitDate(), activity.getActive());
        }
    }

    record ErrorResponse(String error) {
    }

    record MessageResponse(String message) {
    }

    //Ver todas as atividades
    @GetMapping("/user/{user_id}")
    public ResponseEntity<?> seeAllActivities(@PathVariable Long user_id) {
        try {
            List<Activity> activities = activityRepository.findByUserIdAndActiveTrue(user_id);

            List<ActivityResponse> responses = activities.stream().map(ActivityResponse::new)
                    .toList();
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro ao buscar as atividades"));
        }
    }

    @GetMapping("/{activity_id}/user/{user_id}")
    public ResponseEntity<?> seeActivity(@PathVariable Long activity_id, @PathVariable Long user_id) {
        try {
            Optional<Activity> activityOpt = activityRepository.findByIdAndUserId(activity_id, user_id);

            if (activityOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(new ActivityResponse(activityOpt.get()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro ao buscar a atividade"));
        }
    }

    @PostMapping("/user/{user_id}")
    public ResponseEntity<?> createActivity(@RequestBody CreateActivityRequest request, @PathVariable Long user_id) {
        try {
            Optional<User> userOpt = userRepository.findById(user_id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Usuário não encontrado"));
            }

            //verificar se existe titulo similar
            List<Activity> similarActivities = activityRepository
                    .findByUserIdAndTitleContainingIgnoreCase(user_id, request.title());

            if (!similarActivities.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Já existe uma atividade com este nome"));
            }

            //Criar atividade
            Activity newActivity = new Activity();
            newActivity.setTitle(request.title());
            newActivity.setDescription(request.description());
            newActivity.setFrequency(Activity.Frequency.valueOf(request.frequency().toUpperCase()));
            newActivity.setInitDate(request.startDate());
            newActivity.setActive(true);
            newActivity.setUser(userOpt.get());

            Activity savedActivity = activityRepository.save(newActivity);

            return ResponseEntity.status(HttpStatus.CREATED).body(new ActivityResponse(savedActivity));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Erro ao criar atividade"));
        }
    }

    @PutMapping("/{activity_id}/user/{user_id}")
    public ResponseEntity<?> ActivityUpdate(@PathVariable Long activity_id,
                                            @RequestBody UpdateActivityRequest request,
                                            @PathVariable Long user_id) {
        try {
            Optional<Activity> activityOpt = activityRepository.findByIdAndUserId(activity_id, user_id);
            if (activityOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Activity activity = activityOpt.get();

            //atualizar os campos
            if (request.title() != null) {
                activity.setTitle(request.title());
            }
            if (request.description != null) {
                activity.setDescription(request.description());
            }
            if (request.frequency != null) {
                activity.setFrequency(Activity.Frequency.valueOf(request.frequency().toUpperCase()));
            }
            if (request.startDate != null) {
                activity.setInitDate(request.startDate());
            }

            Activity savedActivity = activityRepository.save(activity);

            return ResponseEntity.ok(new ActivityResponse(savedActivity));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro ao atualizar atividade"));
        }
    }

    @DeleteMapping("/{activity_id}/user/{user_id}")
    public ResponseEntity<?> deleteActivity(@PathVariable Long activity_id, @PathVariable Long user_id) {
        try {
            Optional<Activity> activityOpt = activityRepository.findByIdAndUserId(activity_id, user_id);

            if (activityOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Activity activity = activityOpt.get();
            activity.setActive(false);
            activityRepository.save(activity);

            return ResponseEntity.ok(new MessageResponse("Atividade deletada com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro ao deletar atividade"));
        }

    }

    // Buscar atividade por frequencia
    @GetMapping("/user/{user_id}/frequency/{frequency}")
    public ResponseEntity<?> searchByFrequency(@PathVariable Long user_id, @PathVariable String frequency) {
        try {
            Activity.Frequency freq = Activity.Frequency.valueOf(frequency.toUpperCase());

            List<Activity> activities =  activityRepository.findByUserIdAndFrequency(user_id, freq);

            List<ActivityResponse> responses = activities.stream()
                    .map(ActivityResponse::new).toList();
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Frequência inválida. Use: DAILY, WEEKLY, MONTHLY"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro ao buscar atividade"));
        }
    }
}

