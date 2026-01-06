package com.example.tasks.dto.request;

import com.example.tasks.domain.TaskStatus;
import jakarta.validation.constraints.NotNull;

public class TaskStatusUpdateRequestDTO {
    @NotNull
    private TaskStatus status;

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
