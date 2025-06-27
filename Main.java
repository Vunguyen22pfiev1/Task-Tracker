import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        if(args.length == 0) {
            printHelp();
            return;
        }
        String command = args[0];

        try {
            switch (command) {
                case "add":
                    if(args.length < 2) {
                        System.out.println("Usage: task-cli add \"<description>\"");
                        return;
                    }
                    StringBuilder description_add = new StringBuilder();
                    for(int i = 1; i < args.length; i++) {
                        description_add.append(args[i]).append(" ");
                    }
                    String description_string = description_add.toString().trim();
                    if(description_string.startsWith("\"") && description_string.endsWith("\"")) {
                        description_string = description_string.substring(1, description_string.length() - 1);
                    }
                    TaskManager.addTask(description_string);
                    break;

                case "update":
                    if(args.length < 3) {
                        System.out.println("Usage: task-cli update <id> \"<new description>\"");
                        return;
                    }
                    int update_id = Integer.parseInt(args[1]);
                    StringBuilder description_update = new StringBuilder();
                    for(int i = 2; i < args.length; i++) {
                        description_update.append(args[i]).append(" ");
                    }
                    String description = description_update.toString().trim();
                    if(description.endsWith("\"") && description.startsWith("\""))  {
                        description = description.substring(1, description.length() - 1);
                    }
                    TaskManager.updateTask(update_id, description);
                    break;

                case "delete":
                    if(args.length < 2) {
                        System.out.println("Usage: task-cli delete <id>");
                        return;
                    }
                    int delete_id = Integer.parseInt(args[1]);
                    TaskManager.deleteTask(delete_id);
                    break;

                case "mark-in-progress":
                    if(args.length < 2) {
                        System.out.println("Usage: task-cli mark-in-progress <id>");
                        return;
                    }
                    int progress_id = Integer.parseInt(args[1]);
                    TaskManager.markInProgress(progress_id);
                    break;

                case "mark-done":
                    if(args.length < 2) {
                        System.out.println("Usage: task-cli mark-done <id>");
                        return;
                    }
                    int done_id = Integer.parseInt(args[1]);
                    TaskManager.markDone(done_id);
                    break;

                case "list":
                    if(args.length == 1) {
                        TaskManager.allTask();
                    } else if(args.length > 1) {
                        String listed_status = args[1];
                        switch (listed_status) {
                            case "done":
                                TaskManager.listTaskByStatus("done");
                                break;
                            case "todo":
                                TaskManager.listTaskByStatus("todo");
                                break;
                            case "in-progress":
                                TaskManager.listTaskByStatus("in-progress");
                                break;
                        }
                    }
                    break;
                default:
                    System.out.println("Unknown command: " + command);
                    System.out.println("Please use one of these supported commands.");
                    printHelp();
                    break;

            }
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Invalid id. Please provide a valid number.");
            printHelp();
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printHelp() {
        System.out.println("\n--- Task cli usage ---");
        System.out.println("Add a new task:         task-cli add \"<description>\"");
        System.out.println("Update a task:          task-cli update <id> \"<new description>\"");
        System.out.println("Delete a task:          task-cli delete <id>");
        System.out.println("Mark task in-progress:  task-cli mark-in-progress <id>");
        System.out.println("Mark task done:         task-cli mark-done <id>");
        System.out.println("List all tasks:         task-cli list");
        System.out.println("List done tasks:        task-cli list done");
        System.out.println("List todo tasks:        task-cli list todo");
        System.out.println("List in-progress tasks: task-cli list in-progress");
        System.out.println("---------------------\n");
    }

}


