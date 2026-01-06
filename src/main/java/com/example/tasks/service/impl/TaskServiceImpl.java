package com.example.tasks.service.impl;

import com.example.tasks.domain.Task;
import com.example.tasks.domain.TaskPriority;
import com.example.tasks.domain.TaskStatus;
import com.example.tasks.dto.request.TaskCreateRequestDTO;
import com.example.tasks.dto.request.TaskUpdateRequestDTO;
import com.example.tasks.dto.response.TaskResponseDTO;
import com.example.tasks.dto.response.TaskStatsResponseDTO;
import com.example.tasks.exception.BadRequestException;
import com.example.tasks.exception.BusinessException;
import com.example.tasks.exception.ResourceNotFoundException;
import com.example.tasks.mapper.TaskMapper;
import com.example.tasks.repository.TaskRepository;
import com.example.tasks.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;

    public TaskServiceImpl(TaskRepository repository, TaskMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // -------------------- CREATE --------------------

    @Override
    public TaskResponseDTO create(TaskCreateRequestDTO request) {
        validateHighPriority(request.getPriority(), request.getDueDate());

        Task task = mapper.toEntity(request);

        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.TODO);
        }

        Task saved = repository.save(task);
        return mapper.toResponse(saved);
    }

    // -------------------- READ --------------------

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> findAll(TaskStatus status, TaskPriority priority, String query) {

        return repository.findAll()
                .stream()
                .filter(task -> status == null || task.getStatus() == status)
                .filter(task -> priority == null || task.getPriority() == priority)
                .filter(task ->
                        query == null ||
                                task.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                                (task.getDescription() != null &&
                                        task.getDescription().toLowerCase().contains(query.toLowerCase()))
                )
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDTO findById(Long id) {
        Task task = getTaskById(id);
        return mapper.toResponse(task);
    }

    // -------------------- UPDATE --------------------

    @Override
    public TaskResponseDTO update(Long id, TaskUpdateRequestDTO request) {
        Task task = getTaskById(id);

        validateHighPriority(request.getPriority(), request.getDueDate());

        mapper.updateEntity(task, request);

        Task updated = repository.save(task);
        return mapper.toResponse(updated);
    }

    @Override
    public TaskResponseDTO updateStatus(Long id, TaskStatus status) {
        Task task = getTaskById(id);

        //se valida la Regla A
        validateNotOverdue(task, status);

        task.setStatus(status);

        Task updated = repository.save(task);
        return mapper.toResponse(updated);
    }

    // -------------------- DELETE --------------------

    @Override
    public void delete(Long id) {
        Task task = getTaskById(id);
        repository.delete(task);
    }

    // -------------------- STATS --------------------

    @Override
    @Transactional(readOnly = true)
    public TaskStatsResponseDTO getStats() {

        List<Task> tasks = repository.findAll();
        LocalDate today = LocalDate.now();

        long total = tasks.size();

        Map<TaskStatus, Long> byStatus = tasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));

        Map<TaskPriority, Long> byPriority = tasks.stream()
                .collect(Collectors.groupingBy(Task::getPriority, Collectors.counting()));

        long overdue = tasks.stream()
                .filter(task -> task.getDueDate() != null)
                .filter(task -> task.getDueDate().isBefore(today))
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .count();

        List<TaskStatsResponseDTO.NextTaskResponseDTO> tasksNextWeek = tasks.stream()
                .filter(task -> task.getDueDate() != null)
                .filter(task -> !task.getDueDate().isBefore(today))
                .filter(task -> !task.getDueDate().isAfter(today.plusDays(7)))
                .sorted(Comparator.comparing(Task::getDueDate))
                .limit(5)
                .map(mapper::toNextTaskResponse)
                .collect(Collectors.toList());

        return new TaskStatsResponseDTO(
                total,
                byStatus,
                byPriority,
                overdue,
                tasksNextWeek
        );
    }

    // -------------------- PRIVATE HELPERS --------------------

    private Task getTaskById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task no encontrada con id " + id)
                );
    }

    private void validateHighPriority(TaskPriority priority, LocalDate dueDate) {
        if (priority == TaskPriority.HIGH && dueDate == null) {
            throw new BadRequestException(
                    "dueDate es obligatorio cuando la prioridad es HIGH"
            );
        }
    }

    private void validateNotOverdue(Task task, TaskStatus newStatus) {
        if (newStatus == TaskStatus.DONE &&
                task.getDueDate() != null &&
                task.getDueDate().isBefore(LocalDate.now())) {

            throw new BusinessException(
                    "No se puede completar una tarea vencida"
            );
        }
    }
}