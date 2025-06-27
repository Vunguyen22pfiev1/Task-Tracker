import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private int id;
    private String description;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Task(int id, String description, TaskStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Task() {
        this.id = 0;
        this.status = TaskStatus.TODO;
        this.description = "";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public int getId() { return this.id; }
    public String getDescription() { return this.description; }
    public TaskStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return this.createdAt; }
    public LocalDateTime getUpdatedAt() { return this.updatedAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setStatus(TaskStatus new_status) { this.status = new_status; }
    public void setCreatedAt(LocalDateTime new_date_time) { this.createdAt = new_date_time; }
    public void setUpdatedAt(LocalDateTime new_day_time) { this.updatedAt = new_day_time; }
    public void setDescription(String new_description) { this.description = new_description; }

    @Override
    public String toString() {
        return "ID: " + id +
                ", Description: " + description +
                ", Status: " + (status != null ? status.getDisplay_value() : "N/A") +
                ", Created At: " + (createdAt != null ? createdAt.format(formatter) : "N/A") +
                ", Updated At: " + (updatedAt != null ? updatedAt.format(formatter) : "N/A");

    }

    public String getCreatedAtJson() {
        return (createdAt != null ? createdAt.format(formatter) : null);
    }

    public String getUpdatedAtJson() {
        return (updatedAt != null ? updatedAt.format(formatter) : null);
    }

    public static DateTimeFormatter getJsonDateTimeFormatter() {
        return formatter;
    }
}


