package com.example.tasks.mapper;
import com.example.tasks.domain.Task;
import com.example.tasks.domain.TaskStatus;
import com.example.tasks.dto.request.TaskCreateRequestDTO;
import com.example.tasks.dto.request.TaskUpdateRequestDTO;
import com.example.tasks.dto.response.TaskResponseDTO;
import com.example.tasks.dto.response.TaskStatsResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public static Task toEntity(TaskCreateRequestDTO request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setStatus(
                request.getStatus() != null ? request.getStatus() : TaskStatus.TODO
        );
        return task;
    }

    public static void updateEntity(Task task, TaskUpdateRequestDTO request) {
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());
    }

    public TaskResponseDTO toResponse(Task task) {
        TaskResponseDTO response = new TaskResponseDTO();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setDueDate(task.getDueDate());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        return response;
    }

    public TaskStatsResponseDTO.NextTaskResponseDTO toNextTaskResponse(Task task) {
        return new TaskStatsResponseDTO.NextTaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDueDate(),
                task.getPriority(),
                task.getStatus()
        );
    }
}
