package com.example.tasks.controller;

import com.example.tasks.domain.TaskStatus;
import com.example.tasks.domain.TaskPriority;
import com.example.tasks.dto.request.TaskCreateRequestDTO;
import com.example.tasks.dto.request.TaskUpdateRequestDTO;
import com.example.tasks.dto.request.TaskStatusUpdateRequestDTO;
import com.example.tasks.dto.response.TaskResponseDTO;
import com.example.tasks.dto.response.TaskStatsResponseDTO;
import com.example.tasks.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    // -------------------- CREATE --------------------
    @PostMapping
    public ResponseEntity<TaskResponseDTO> create(@Valid @RequestBody TaskCreateRequestDTO request) {
        TaskResponseDTO response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // -------------------- READ --------------------
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> findAll(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String q
    ) {
        List<TaskResponseDTO> tasks = service.findAll(status, priority, q);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> findById(@PathVariable Long id) {
        TaskResponseDTO response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    // -------------------- UPDATE --------------------
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequestDTO request
    ) {
        TaskResponseDTO response = service.update(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatusUpdateRequestDTO request
    ) {
        TaskResponseDTO response = service.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(response);
    }

    // -------------------- DELETE --------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------- STATS --------------------
    @GetMapping("/stats")
    public ResponseEntity<TaskStatsResponseDTO> stats() {
        TaskStatsResponseDTO stats = service.getStats();
        return ResponseEntity.ok(stats);
    }
}
