package com.example.todospring.service;

import com.example.todospring.dto.TaskRequestDTO;
import com.example.todospring.dto.TaskResponseDTO;
import com.example.todospring.model.Task;
import com.example.todospring.model.Task.Priority;
import com.example.todospring.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    private TaskResponseDTO mapToDTO(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .description(task.getDescription())
                .isCompleted(task.isCompleted())
                .priority(task.getPriority())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    public TaskResponseDTO createTask(TaskRequestDTO request) {
        Task task = new Task();
        task.setDescription(request.getDescription());
        task.setCompleted(request.isCompleted());
        task.setPriority(request.getPriority());

        Task savedTask = taskRepository.save(task);
        return mapToDTO(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Задача с ID "
                + id + " не найдена"));
        return mapToDTO(task);
    }

    public TaskResponseDTO updateTask(Long id, TaskRequestDTO request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Задача с ID "
                + id + " не найдена"));

        task.setDescription(request.getDescription());
        task.setCompleted(request.isCompleted());
        task.setPriority(request.getPriority());

        Task updatedTask = taskRepository.save(task);
        return mapToDTO(updatedTask);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Задача с ID " + id
                    + " не найдена");
        }
        taskRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksByPriority(Priority priority) {
        return taskRepository.findByPriority(priority)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskStatistics getStatistics() {
        long total = taskRepository.count();
        long completed = taskRepository.countByCompletionStatus(true);
        long pending = total - completed;

        return new TaskStatistics(total, completed, pending);
    }

    public record TaskStatistics(long total, long completed, long pending) {}
}
