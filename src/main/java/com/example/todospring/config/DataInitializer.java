package com.example.todospring.config;

import com.example.todospring.model.Task;
import com.example.todospring.model.Task.Priority;
import com.example.todospring.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final TaskRepository taskRepository;

    @Override
    public void run(String... args) {
        if (taskRepository.count() == 0) {
            log.info("\uD83D\uDCDD Создаю тестовые задачи...");

            Task task1 = new Task();
            task1.setDescription("Изучить Spring Boot");
            task1.setPriority(Priority.HIGH);
            task1.setCompleted(false);

            Task task2 = new Task();
            task2.setDescription("Сделать пет-проект");
            task2.setPriority(Priority.HIGH);
            task2.setCompleted(false);

            Task task3 = new Task();
            task3.setDescription("Прочитать документацию");
            task3.setPriority(Priority.MEDIUM);
            task3.setCompleted(true);

            taskRepository.save(task1);
            taskRepository.save(task2);
            taskRepository.save(task3);

            log.info("✅ Создано 3 тестовые задачи");
        }
    }
}
