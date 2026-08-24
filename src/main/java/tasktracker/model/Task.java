package main.java.tasktracker.model;

import java.time.Instant;
import java.util.UUID;

public class Task {
    private UUID id;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

}
