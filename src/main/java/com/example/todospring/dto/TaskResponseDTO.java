package com.example.todospring.dto;

import com.example.todospring.model.Task.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {

    private Long id;
    private String description;
    private boolean isCompleted;
    private Priority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
