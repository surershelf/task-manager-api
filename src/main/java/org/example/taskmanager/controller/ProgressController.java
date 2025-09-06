package org.example.taskmanager.controller;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager.entity.Activity;
import org.example.taskmanager.entity.Progress;
import org.example.taskmanager.repository.ActivityRepository;
import org.example.taskmanager.repository.ProgressRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressRepository progressRepository;
    private final ActivityRepository activityRepository;

    record CreateProgressRequest(Long activityId, LocalDate finishDate) {}

    record ProgressResponse(Long id, LocalDate finishDate, String status, Long activityId, String activityTitle) {
        public ProgressResponse(Progress progress){
            this(   progress.getId(),
                    progress.getFinishDate(),
                    progress.getStatus().toString(),
                    progress.getActivity().getId(),
                    progress.getActivity().getTitle()
            );
        }
    }

    record ErrorResponse(String error){}
    record MessageResponse(String message){}
    record ProgressStatsResponse(long totalFinished, Long totalStarted, double completionRate) {}

    //Criar Progresso ( concluir atividade)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CreateProgressRequest request){
        try {
            Optional<Activity> activityOpt = activityRepository.findById(request.activityId);

            if (activityOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Atividade nao encontrada"));
            }

            Activity activity = activityOpt.get();
            LocalDate finishDate = request.finishDate() != null ? request.finishDate() : LocalDate.now();

            //Verificar se já existe progresso para esta data
            Optional <Progress> existingProgress = progressRepository.findByActivityIdAndFinishDate(request.activityId, finishDate);
            if (existingProgress.isPresent()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Atividade já foi concluida nesta data"));
            }

            Progress progress = new Progress();
            progress.setActivity(activity);
            progress.setFinishDate(finishDate);
            progress.setStatus(Progress.Status.FINISHED);

            Progress savedProgress = progressRepository.save(progress);

            return ResponseEntity.status(HttpStatus.CREATED).body(new ProgressResponse(savedProgress));


        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Erro interno no servidor!"));
        }
    }

    //buscar progresso por atividade
    @GetMapping("/activity/{activityId}")
    public ResponseEntity<?> getProgressActivity(@PathVariable Long activityId) {
        try {
            List<Progress> progressList = progressRepository.findByActivityIdOrderByFinishDateDesc(activityId);

            List<ProgressResponse> progressResponse = progressList.stream().map(ProgressResponse::new).toList();

            return ResponseEntity.ok(progressResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Erro interno no servidor!"));
        }
    }

    //buscar progresso por usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getProgressUser(@PathVariable Long userId) {
        try {
            List<Progress> progressList = progressRepository.findByUserId(userId);

            List<ProgressResponse> progressResponse = progressList.stream().map(ProgressResponse::new).toList();

            return ResponseEntity.ok(progressResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno no servidor!"));
        }
    }

    //Busca o progresso de hoje
    @GetMapping("/user/{userId}/today")
    public ResponseEntity<?> getProgressUserToday(@PathVariable Long userId) {
        try {
            List<Progress> progressList = progressRepository.findProgressToday(userId, LocalDate.now());

            List<ProgressResponse> progressResponse = progressList.stream().map(ProgressResponse::new).toList();

            return ResponseEntity.ok(progressResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno no servidor!"));
        }
    }

    //Estatistica do Progresso
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<?> getProgressStats(@PathVariable Long userId) {
        try{
            long totalFinished = progressRepository.countByUserAndStatus(userId, Progress.Status.FINISHED);
            long totalStarted = progressRepository.countByUserAndStatus(userId, Progress.Status.STARTED);

            double completionRate = (totalStarted + totalFinished) > 0 ?
                    (double) totalFinished / (totalStarted + totalFinished) * 100 : 0;

            return ResponseEntity.ok(new ProgressStatsResponse(totalFinished, totalStarted,
                    Math.round(completionRate * 100)/100));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno no servidor!"));
        }
    }

    //Deletar progresso
    @DeleteMapping("/{progressId}")
    public ResponseEntity<?> deleteProgress(@PathVariable Long progressId) {
        try {
            Optional<Progress> progressOpt = progressRepository.findById(progressId);

            if (progressOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Progresso nao encontrado"));
            }

            progressRepository.deleteById(progressId);

            return ResponseEntity.ok(new MessageResponse("Deletado com sucesso!"));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno no servidor!"));
        }
    }
}
