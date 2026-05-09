package com.example.todospring.repository;

import com.example.todospring.model.Task;
import com.example.todospring.model.Task.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByIsCompletedTrue();

    List<Task> findByIsCompletedFalse();

    List<Task> findByPriority(Priority priority);

    List<Task> findByDescriptionContainingIgnoreCase(String keyword);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.isCompleted = :status")
    long countByCompletionStatus(boolean status);

    List<Task> findAllByOrderByCreatedAtDesc();
}
