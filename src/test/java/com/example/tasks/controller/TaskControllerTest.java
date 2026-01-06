package com.example.tasks.controller;

import com.example.tasks.domain.TaskPriority;
import com.example.tasks.domain.TaskStatus;
import com.example.tasks.dto.request.TaskCreateRequestDTO;
import com.example.tasks.dto.request.TaskStatusUpdateRequestDTO;
import com.example.tasks.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll(); // limpiar antes de cada prueba
    }

    // -------------------- TEST 1 - Crear task válida --------------------
    @Test
    void testCreateValidTask() throws Exception {
        TaskCreateRequestDTO request = new TaskCreateRequestDTO();
        request.setTitle("Pagar luz");
        request.setDescription("Pagar recibos de luz del mes");
        request.setPriority(TaskPriority.MEDIUM);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Pagar luz"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.status").value("TODO")); // default
    }

    // -------------------- TEST 2 - Regla B: HIGH sin dueDate --------------------
    @Test
    void testHighPriorityWithoutDueDate() throws Exception {
        TaskCreateRequestDTO request = new TaskCreateRequestDTO();
        request.setTitle("Tarea urgente");
        request.setPriority(TaskPriority.HIGH);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("dueDate es obligatorio")));
    }

    // -------------------- TEST 3 - Regla A: no permitir DONE en vencida --------------------
    @Test
    void testCannotMarkDoneIfOverdue() throws Exception {
        // Primero crear la tarea vencida
        TaskCreateRequestDTO request = new TaskCreateRequestDTO();
        request.setTitle("Tarea vencida");
        request.setPriority(TaskPriority.MEDIUM);
        request.setDueDate(LocalDate.now().minusDays(3));

        String content = objectMapper.writeValueAsString(request);

        // Crear task
        String response = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long taskId = objectMapper.readTree(response).get("id").asLong();

        // Intentar marcar DONE
        TaskStatusUpdateRequestDTO statusRequest = new TaskStatusUpdateRequestDTO();
        statusRequest.setStatus(TaskStatus.DONE);

        mockMvc.perform(patch("/api/tasks/{id}/status", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("No se puede completar una tarea vencida")));
    }

    // -------------------- TEST 4 - Stats --------------------
    @Test
    void testStatsEndpoint() throws Exception {
        // Crear varias tareas
        for (int i = 1; i <= 6; i++) {
            TaskCreateRequestDTO req = new TaskCreateRequestDTO();
            req.setTitle("Task " + i);
            req.setPriority(i % 2 == 0 ? TaskPriority.HIGH : TaskPriority.LOW);
            req.setStatus(i % 3 == 0 ? TaskStatus.DONE : TaskStatus.TODO);
            req.setDueDate(LocalDate.now().plusDays(i - 3)); // algunas vencidas, otras próximas
            mockMvc.perform(post("/api/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated());
        }

        // Verificar stats
        mockMvc.perform(get("/api/tasks/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(6)))
                .andExpect(jsonPath("$.byStatus.TODO", notNullValue()))
                .andExpect(jsonPath("$.byPriority.HIGH", notNullValue()))
                .andExpect(jsonPath("$.overdue", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.tasksNextWeek", hasSize(lessThanOrEqualTo(5))))
                .andExpect(jsonPath("$.tasksNextWeek[0].dueDate", notNullValue())); // orden ascendente implícito
    }
}
