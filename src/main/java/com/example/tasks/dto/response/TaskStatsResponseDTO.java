package com.example.tasks.dto.response;

import com.example.tasks.domain.TaskPriority;
import com.example.tasks.domain.TaskStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TaskStatsResponseDTO {
    private long total;

    private Map<TaskStatus, Long> byStatus;

    private Map<TaskPriority, Long> byPriority;

    private long overdue;

    private List<NextTaskResponseDTO> tasksNextWeek;

    public TaskStatsResponseDTO() {
    }

    public TaskStatsResponseDTO(
            long total,
            Map<TaskStatus, Long> byStatus,
            Map<TaskPriority, Long> byPriority,
            long overdue,
            List<NextTaskResponseDTO> tasksNextWeek
    ) {
        this.total = total;
        this.byStatus = byStatus;
        this.byPriority = byPriority;
        this.overdue = overdue;
        this.tasksNextWeek = tasksNextWeek;
    }

    public long getTotal() {
        return total;
    }

    public Map<TaskStatus, Long> getByStatus() {
        return byStatus;
    }

    public Map<TaskPriority, Long> getByPriority() {
        return byPriority;
    }

    public long getOverdue() {
        return overdue;
    }

    public List<NextTaskResponseDTO> getTasksNextWeek() {
        return tasksNextWeek;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void setByStatus(Map<TaskStatus, Long> byStatus) {
        this.byStatus = byStatus;
    }

    public void setByPriority(Map<TaskPriority, Long> byPriority) {
        this.byPriority = byPriority;
    }

    public void setOverdue(long overdue) {
        this.overdue = overdue;
    }

    public void setTasksNextWeek(List<NextTaskResponseDTO> tasksNextWeek) {
        this.tasksNextWeek = tasksNextWeek;
    }

    //DTO interno para la lista tasksNextWeek
    public static class NextTaskResponseDTO {

        private Long id;
        private String title;
        private LocalDate dueDate;
        private TaskPriority priority;
        private TaskStatus status;

        public NextTaskResponseDTO() {
        }

        public NextTaskResponseDTO(
                Long id,
                String title,
                LocalDate dueDate,
                TaskPriority priority,
                TaskStatus status
        ) {
            this.id = id;
            this.title = title;
            this.dueDate = dueDate;
            this.priority = priority;
            this.status = status;
        }

        public Long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public TaskPriority getPriority() {
            return priority;
        }

        public TaskStatus getStatus() {
            return status;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
        }

        public void setPriority(TaskPriority priority) {
            this.priority = priority;
        }

        public void setStatus(TaskStatus status) {
            this.status = status;
        }
    }
}
