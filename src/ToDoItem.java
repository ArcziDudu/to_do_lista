
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ToDoItem {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private String name;
    private String description;
    private LocalDateTime deadLine;
    private Integer priority;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(LocalDateTime deadLine) {
        this.deadLine = deadLine;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "ToDoItem{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", deadLine=" + deadLine +
                ", priority=" + priority +
                '}';
    }

    public enum Field {
        NAME,
        DESCRIPTION,
        DEADLINE,
        PRIORITY,
        SORT
    }
}
