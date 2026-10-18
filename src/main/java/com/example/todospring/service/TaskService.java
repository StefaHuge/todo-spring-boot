package com.example.todospring.service;

import com.example.todospring.dto.TaskRequestDTO;
import com.example.todospring.dto.TaskResponseDTO;
import com.example.todospring.model.Task;
import com.example.todospring.model.Task.Priority;
import com.example.todospring.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
          "createdAt", "updatedAt", "description", "priority", "id");

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

        log.info("Создание новой задачи: {}", request.getDescription());
        Task task = new Task();
        task.setDescription(request.getDescription());
        task.setCompleted(request.isCompleted());
        task.setPriority(request.getPriority());

        Task savedTask = taskRepository.save(task);
        log.debug("Задача создана с ID: {}", savedTask.getId());
        return mapToDTO(savedTask);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getAllTasks(int page, int size, String
            sortBy, String direction) {

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException(
                 "Недопустимое поле для сортировки: " + sortBy +
                 ". Допустимые: " + ALLOWED_SORT_FIELDS);
        }

        Sort.Direction dir;
        try {
            dir = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
            "Направление сортировки должно быть 'asc' или 'desc'");
        }

        Sort sort = Sort.by(dir, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return taskRepository.findAll(pageable)
                .map(this::mapToDTO);
    }

    @Cacheable(value = "tasks", key = "#id")
    public TaskResponseDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Задача с ID "
                + id + " не найдена"));
        return mapToDTO(task);
    }

    @CachePut(value = "tasks", key = "#id")
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

    @CacheEvict(value = "tasks", key = "#id")
    public void deleteTask(Long id) {
        log.warn("Удаление задачи с ID: {}", id);
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
