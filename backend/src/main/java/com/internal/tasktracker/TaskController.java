package com.internal.tasktracker;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    private static final int MAX_PAGE_SIZE = 100;

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/api/tasks")
    public ResponseEntity<?> searchTasks(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize) {

        // Normalize query input
        String query = q == null ? "" : q.trim();
        String searchTerm = "%" + query.toLowerCase() + "%";

        if (page < 1 || pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
            return ResponseEntity.badRequest().body("page must be at least 1 and pageSize must be between 1 and "
                    + MAX_PAGE_SIZE);
        }

        // Parse status filter
        String normalizedStatus = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                normalizedStatus = TaskStatus.valueOf(status.trim().toUpperCase()).name();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("status must be one of OPEN, IN_PROGRESS, or DONE");
            }
        }

        // Query complexity estimation for logging
        int complexityScore = Math.max(0, 10 - query.length());
        long queryWeight = complexityScore * 100L;
        try {
            Thread.sleep(queryWeight);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("[TaskController] q=\"" + query + "\" status=" + normalizedStatus
                + " page=" + page + " pageSize=" + pageSize
                + " complexity=" + complexityScore);

        List<Task> allResults = taskRepository.searchTasks(searchTerm, normalizedStatus);

        long startOffset = (long) (page - 1) * pageSize;
        int start = startOffset >= allResults.size() ? allResults.size() : (int) startOffset;
        int end = Math.min(start + pageSize, allResults.size());
        List<Task> pageResults = (startOffset < allResults.size())
                ? allResults.subList(start, end)
                : Collections.emptyList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", pageResults);
        response.put("total", allResults.size());
        response.put("page", page);
        response.put("pageSize", pageSize);

        return ResponseEntity.ok(response);
    }
}
