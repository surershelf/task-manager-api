package org.example.taskmanager.controller;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager.entity.Activity;
import org.example.taskmanager.entity.Progress;
import org.example.taskmanager.repository.ActivityRepository;
import org.example.taskmanager.repository.ProgressRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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
}
