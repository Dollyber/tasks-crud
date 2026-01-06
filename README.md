# CRUD de Tareas - Reto Técnico Java 17 + Spring Boot

Este proyecto implementa un **CRUD de tareas** con reglas de negocio y estadísticas usando Java 17, Spring Boot y H2 en memoria.

---

## Requisitos

- **Java 17**
- **Maven 3.5.9**

---

## Cómo ejecutar

1. Clonar el repositorio y navegar al directorio del proyecto:

```bash
git clone <https://github.com/Dollyber/tasks-crud.git>
cd <tasks-crud>
```

2. Ejecutar pruebas:

```bash
mvn clean test 
```

3. Ejecutar la aplicación:

```bash
mvn spring-boot:run
```

4. Acceder a la API en `http://localhost:8080/api/tasks`

---

## Base de Datos H2

* **URL Consola:** `http://localhost:8080/h2-console` 
* **JDBC URL:** `jdbc:h2:mem:taskdb` 
* **User:** `sa` 
* **Password:** (vacío) 
* **Driver Class:** `org.h2.Driver`

---

## Ejemplos de Uso (CURL)
### 1. Crear Tarea:
```bash
curl -X POST http://localhost:8080/api/tasks \
-H "Content-Type: application/json" \
-d '{
  "title": "Pagar recibos",
  "description": "Pagar agua y luz",
  "priority": "HIGH",
  "dueDate": "2026-01-06"
}'
```
* Respuestas:
  * `201 Created` - Tarea creada exitosamente.
  * `400 Bad Request` - Si falta `dueDate` para prioridad `HIGH`.

### 2. Obtener Todas las Tareas:
```bash
curl -X GET http://localhost:8080/api/tasks
```
* Respuesta:
  * `200 OK` - Lista de todas las tareas.`
  
### 3. Obtener Tarea por ID:
```bash
curl -X GET http://localhost:8080/api/tasks/{id}
```
* Respuestas:
  * `200 OK` - Tarea encontrada.
  * `404 Not Found` - Tarea no encontrada.

### 4. Actualizar Tarea:

``` bash
curl -X PUT http://localhost:8080/api/tasks/1 \
-H "Content-Type: application/json" \
-d '{
  "title": "Pagar recibos actualizado",
  "description": "Agua, luz y gas",
  "priority": "HIGH",
  "status": "IN_PROGRESS",
  "dueDate": "2026-01-07"
}'
```

* Respuestas:
  * `200 OK` - Tarea actualizada exitosamente.
  * `400 Bad Request` - No cumple con las reglas de negocio, como duedate obligatorio para prioridad HIGH.
  * `404 Not Found` - Tarea no encontrada.

### 5. Actualizar Estado de Tarea:
```bash
curl -X PATCH http://localhost:8080/api/tasks/{id}/status \
-H "Content-Type: application/json" \
-d '{"status": "DONE"}'
```
* Respuestas:
  * `200 OK` - Estado actualizado exitosamente.
  * `400 Bad Request` - Input invalido.
  * `404 Not Found` - Tarea no encontrada.
  * `409 Conflict` - Si se intenta marcar como `DONE` una tarea vencida

### 6. Eliminar Tarea:
```bash
curl -X DELETE http://localhost:8080/api/tasks/1
```
* Respuestas:
  * `204 No Content` - Tarea eliminada exitosamente.
  * `404 Not Found` - Tarea no encontrada.
  
### 7. Estadísticas (GET /stats)
```bash
curl http://localhost:8080/api/tasks/stats
```
* Respuesta:
  * `200 OK` - Objeto JSON con estadísticas de tareas:
    ```json
    {
      "total": 10,
      "byStatus": {
        "PENDING": 4,
        "IN_PROGRESS": 3,
        "DONE": 3
      },
      "byPriority": {
        "LOW": 2,
        "MEDIUM": 5,
        "HIGH": 3
      },
      "overdue": 2,
        "tasksNextWeek": [
        { "id": 10, 
          "title": "Pagar recibos", 
          "dueDate": "2026-01-06", 
          "priority": "HIGH", 
          "status": "TODO" }
      ]
    }
    ```
---

## Reglas de Negocio
* **Regla A:** No se puede marcar como `DONE` si la tarea está vencida (Retorna 409 Conflict).
* **Regla B:** Si la prioridad es `HIGH`, el campo `dueDate` es obligatorio (Retorna 400 Bad Request).


