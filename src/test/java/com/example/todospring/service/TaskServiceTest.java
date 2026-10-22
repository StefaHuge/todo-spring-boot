package com.example.todospring.service;

import com.example.todospring.dto.TaskRequestDTO;
import com.example.todospring.dto.TaskResponseDTO;
import com.example.todospring.model.Task;
import com.example.todospring.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldCreateTask() {
        TaskRequestDTO request = new TaskRequestDTO();
        request.setDescription("Купить хлеб");
        request.setPriority(Task.Priority.HIGH);
        request.setIsCompleted(false);


        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setDescription("Купить хлеб");
        savedTask.setPriority(Task.Priority.HIGH);
        savedTask.setCompleted(false);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskResponseDTO result = taskService.createTask(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Купить хлеб");
        assertThat(result.getPriority()).isEqualTo(Task.Priority.HIGH);
        assertThat(result.isCompleted()).isFalse();

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void shouldReturnTaskWhenExists() {
        Task task = new Task();
        task.setId(1L);
        task.setDescription("Купить хлеб");
        task.setPriority(Task.Priority.HIGH);
        task.setCompleted(false);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponseDTO result = taskService.getTaskById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Купить хлеб");
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(99L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void shouldUpdateTaskWhenExists() {
        Long taskId = 1L;
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setDescription("Старое описание");
        existingTask.setPriority(Task.Priority.LOW);
        existingTask.setCompleted(false);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

        TaskRequestDTO request = new TaskRequestDTO();
        request.setDescription("Новое описание");
        request.setPriority(Task.Priority.HIGH);
        request.setIsCompleted(true);

        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

        TaskResponseDTO result = taskService.updateTask(taskId, request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(taskId);
        assertThat(result.getDescription()).isEqualTo("Новое описание");
        assertThat(result.getPriority()).isEqualTo(Task.Priority.HIGH);
        assertThat(result.isCompleted()).isTrue();

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonexistentTask() {
        Long taskId = 99L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        TaskRequestDTO request = new TaskRequestDTO();
        request.setDescription("Новое описание");
        request.setPriority(Task.Priority.HIGH);
        request.setIsCompleted(true);

        assertThatThrownBy(() -> taskService.updateTask(taskId, request))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("99");

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldDeleteTaskWhenExists() {
        Long taskId = 1L;
        when(taskRepository.existsById(taskId)).thenReturn(true);

        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).deleteById(taskId);
    }
    @Test
    void shouldThrowExceptionWhenDeletingNonexistentTask() {
        Long taskId = 99L;
        when(taskRepository.existsById(taskId)).thenReturn(false);

        assertThatThrownBy(() -> taskService.deleteTask(taskId))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("99");

        verify(taskRepository, never()).deleteById(taskId);
    }
}