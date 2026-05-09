package com.example.todospring.dto;

import com.example.todospring.model.Task.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDTO {

    @NotBlank(message = "Описание задачи обязательно")
    private String description;

    private boolean isCompleted = false;

    @NotNull(message = "Приоритет обязателен")
    private Priority priority = Priority.MEDIUM;
}
