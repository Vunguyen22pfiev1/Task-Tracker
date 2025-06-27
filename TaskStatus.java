public enum TaskStatus {
    TODO("to-do"),
    IN_PROGRESS("in-progress"),
    DONE("done");

    private final String display_value;

    TaskStatus(String display_value) {
        this.display_value = display_value;
    }

    public String getDisplay_value() {
        return this.display_value;
    }

    public static TaskStatus fromString(String text) {
        for(TaskStatus b : TaskStatus.values()) {
            if(b.display_value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException(("No constant with text " + text + " found."));
    }
}

