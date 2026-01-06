package com.example.tasks.service;

import com.example.tasks.domain.TaskPriority;
import com.example.tasks.domain.TaskStatus;
import com.example.tasks.dto.request.TaskCreateRequestDTO;
import com.example.tasks.dto.request.TaskUpdateRequestDTO;
import com.example.tasks.dto.response.TaskResponseDTO;
import com.example.tasks.dto.response.TaskStatsResponseDTO;

import java.util.List;

public interface TaskService {
    TaskResponseDTO create(TaskCreateRequestDTO request);

    List<TaskResponseDTO> findAll(
            TaskStatus status,
            TaskPriority priority,
            String query
    );

    TaskResponseDTO findById(Long id);

    TaskResponseDTO update(Long id, TaskUpdateRequestDTO request);

    TaskResponseDTO updateStatus(Long id, TaskStatus status);

    void delete(Long id);

    TaskStatsResponseDTO getStats();
}
