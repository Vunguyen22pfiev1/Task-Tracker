import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskManager {
    private static final Path FILE_PATH = Path.of("tasks.json");
    private static List<Task> tasks = new ArrayList<>();

    static {
        loadTask();
    }

    private static void loadTask() {
        if(!Files.exists(FILE_PATH)) {
            // File tasks.json no exist. Creating a new one
            try {
                Files.createFile(FILE_PATH);
            } catch (IOException e) {
                System.err.println("Error creating tasks.json: " + e.getMessage());
            }
        }
        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if(!line.trim().isEmpty()) {
                    try {
                        Task task = parseJsonLineToTask(line);
                        if(task != null){
                            tasks.add(task);
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR: Could not parse task line: " + line + " - " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading tasks.json." + e.getMessage());
        }
    }

    private static void saveTask() {
        try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH)) {
            for (Task task : tasks) {
                writer.write(taskToJsonLine(task));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing tasks.json. " + e.getMessage());
        }
    }

    private static String taskToJsonLine(Task task) {
        return String.format("{\"id\":%d, \"description\":\"%s\", \"status\":\"%s\",\"createdAt\":\"%s\",\"updatedAt\":\"%s\"}",
                task.getId(),
                escapeJson(task.getDescription()), // escapeJson -> Process special chars
                task.getStatus().getDisplay_value(),
                task.getCreatedAtJson(),
                task.getUpdatedAtJson()
        );
    }

    private static Task parseJsonLineToTask(String json_line) {
        Task task = new Task();

        Pattern pattern = Pattern.compile("\"([^\"]+)\":\\s*(?:\"([^\"]*?)\"|(\\d+))(?=[,}])");
        Matcher matcher = pattern.matcher(json_line);

        while(matcher.find()) {
            String key = matcher.group(1);
            String string_value = matcher.group(2);
            String number_value = matcher.group(3);

            String value = (string_value != null) ? string_value : number_value;

            if(value == null) {
                System.out.println("Value for key: " + key + " could not be extracted from Json file.");
                continue;
            }
            try {
                switch (key) {
                    case "id":
                        task.setId(Integer.parseInt(value));
                        break;
                    case "description":
                        task.setDescription(unescapeJson(value));
                        break;
                    case "status":
                        task.setStatus(TaskStatus.fromString(unescapeJson(value)));
                        break;
                    case "createdAt":
                        task.setCreatedAt(LocalDateTime.parse(value, Task.getJsonDateTimeFormatter()));
                        break;
                    case "updatedAt":
                        task.setUpdatedAt(LocalDateTime.parse(value, Task.getJsonDateTimeFormatter()));
                        break;
                    default:
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error parsing number for key: " + key + " : " + value);
                System.out.println(e.getMessage());
                return null;
            } catch (DateTimeParseException e) {
                System.out.println("Error parsing date time for key: " + key + " : " + value);
                System.out.println(e.getMessage());
                return null;
            } catch (IllegalArgumentException e){
                System.out.println("Error parsing status for key: " + key + " : " + value);
                System.out.println(e.getMessage());
                return null;
            }
        }
        return task;
    }


    private static  String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String unescapeJson(String text) {
        if(text == null) return "";
        return text.replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\\", "\\");
    }

    public static void addTask(String description) {
        int new_id = tasks.isEmpty() ? 1 : tasks.stream().mapToInt(Task::getId).max().orElse(0) + 1;
        LocalDateTime now = LocalDateTime.now();
        Task new_task = new Task(new_id, description, TaskStatus.TODO, now, now);
        tasks.add(new_task);
        saveTask();
        System.out.println("Task added successfully (ID: " + new_id + ")");
    }

    public static void updateTask(int id, String new_description) {
        Optional<Task> task_to_update = tasks.stream().filter(t -> t.getId() == id).findFirst();
        if(task_to_update.isPresent()) {
            Task task = task_to_update.get();
            task.setDescription(new_description);
            task.setUpdatedAt(LocalDateTime.now());
            saveTask();
            System.out.println("Task with id: " + id + " has been updated.");
        } else {
            System.out.println("Task with id: " + id + "not found!");
        }
    }

    public static void deleteTask(int id) {
        boolean removed_id = tasks.removeIf(t -> t.getId() == id);
        if(removed_id) {
            saveTask();
            System.out.println("Task with id: " + id + " has been deleted.");
        } else {
            System.out.println("Task with id: " + id + "not found!");
        }
    }

    public static void allTask() {
        if(!tasks.isEmpty()) {
            System.out.println("--- All tasks ---");
            for(Task task : tasks) {
                System.out.println(task.toString());
            }
            System.out.println("---------------------");
        } else {
            System.out.println(" No tasks found!");
        }
    }

    public static void listTaskByStatus(String status_string) {
        try {
            TaskStatus status = TaskStatus.fromString(status_string);
            List<Task> filtered_tasks = tasks.stream()
                    .filter(t -> t.getStatus() == status)
                    .sorted(Comparator.comparing(Task::getId))
                    .toList();
            if(!filtered_tasks.isEmpty()) {
                System.out.println("--- Tasks with status: " + status.getDisplay_value() + " ---");
                filtered_tasks.forEach(System.out::println);
                System.out.println("------------------------------------");
            } else {
                System.out.println("No tasks found with status: " + status);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: invalid status filter: " + status_string + ". Valid filters are: todo, in-progress, done.");
        }
    }

    public static void markInProgress(int id) {
        Optional<Task> taskToMark = tasks.stream().filter(t -> t.getId() == id).findFirst();
        if(taskToMark.isPresent()) {
            Task task = taskToMark.get();
            if(task.getStatus() != TaskStatus.IN_PROGRESS) {
                task.setStatus(TaskStatus.IN_PROGRESS);
                task.setUpdatedAt(LocalDateTime.now());
                saveTask();
                System.out.println("Task with id: " + id + " marked as in-progress.");
            } else {
                System.out.println("Task with id: " + id + " is already in-progress.");
            }
        } else {
            System.out.println("Task with id: " + id + " not found!");
        }
    }

    public static void markDone(int id) {
        Optional<Task> taskToMark = tasks.stream().filter(t -> t.getId() == id).findFirst();
        if(taskToMark.isPresent()) {
            Task task = taskToMark.get();
            if(task.getStatus() != TaskStatus.DONE) {
                task.setStatus(TaskStatus.DONE);
                task.setUpdatedAt(LocalDateTime.now());
                saveTask();
                System.out.println("Task with id: " + id + " marked as done.");
            } else {
                System.out.println("Task with id: " + id + " is already done.");
            }
        } else {
            System.out.println("Task with id: " + id + " not found!");
        }
    }


}
