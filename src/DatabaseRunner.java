import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

public class DatabaseRunner {
    private static final String URL = "jdbc:postgresql://localhost:5432/toDoList";
    private static final String USERNAME = "";
    private static final String PASSWORD = "";

    private static final String SQL_INSERT =
            "INSERT INTO TODOLIST (NAME, DESCRIPTION, DEADLINE, PRIORITY) VALUES(?,?,?,?)";

    private static final String SQL_UPDATE =
            "UPDATE TODOLIST SET DESCRIPTION = ?, DEADLINE = ?, PRIORITY = ? WHERE NAME = ?;";
    private final static String SQL_READ_WHERE
            = "SELECT * FROM TODOLIST WHERE NAME = ?;";
    private static final String SQL_READ_ALL
            = "SELECT * FROM TODOLIST ORDER BY ?1 ?2;";
    private static final String SQL_READ_GROUPED
            = "select DATE(deadline) as DATE, ARRAY_AGG(name) as TASKS from todolist group by DATE(deadline)";
    private static final String SQL_DELETE
            = "DELETE FROM  TODOLIST WHERE NAME = ?;";
    private static final String SQL_DELETE_ALL = "DELETE FROM TODOLIST;";
    private static final String SQL_COMPLETED = "UPDATE TODOLIST SET STATUS = ? WHERE NAME = ?;";

    private final Map<Command.Type, Consumer<Command>> EXECUTION_MAP;

    {
        //poniżej znajduje się interfejs funkcyjny consumer
        //do metody run add przekazywana jest odpowiednia komenda
        EXECUTION_MAP = Map.of(
                Command.Type.CREATE, this::runAdd,
                Command.Type.UPDATE, this::runEdit,
                Command.Type.READ, this::runRead,
                Command.Type.READ_ALL, this::runReadAll,
                Command.Type.READ_GROUPED, this::runGroupeddAll,
                Command.Type.DELETE, this::runDelete,
                Command.Type.DELETE_ALL, this::runDeleteAll,
                Command.Type.COMPLETED, this::runCompleted
        );
    }


    void run(final Command command) {
        //metoda decydująca co ma zostać wywołane na podstawie przekazanej komendy,
        // uruchamia się metoda runAdd
        System.out.println("##### RUNNING COMMAND #####");

        Consumer<Command> commandConsumer = Optional.ofNullable(EXECUTION_MAP.get(command.getType()))
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("command: [%s] not supported", command.getType())));
        //na tym etapie mamy zdefiniowaną mape,
        // która mówi że dla danego klucza mamy przypisaną dany interfejs funkcyjny

        //faktyczne wywołanie funkcji
        commandConsumer.accept(command);

        System.out.println("##### FINISHED COMMAND #####\n");
    }

    private void runAdd(final Command command) {
        //metoda dodająca wpisy do bazy danych
        if (!Command.Type.CREATE.equals(command.getType())) {
            throw new IllegalArgumentException(command.getType().getName());
        }
        try (
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(SQL_INSERT)
        ) {
            statement.setString(1, command.getToDoItem().getName());
            statement.setString(2, command.getToDoItem().getDescription());
            statement.setTimestamp(3, Timestamp.valueOf(command.getToDoItem().getDeadLine()));
            statement.setInt(4, command.getToDoItem().getPriority());
            int count = statement.executeUpdate();
            System.out.printf("run [%s] succesfully, modified [%s]%n", command.getType(), count);
        } catch (SQLException e) {
            System.err.printf("[%s] ERROR. Message: [%s]%n", command.getType(), e.getMessage());
        }

    }

    private void runEdit(final Command command) {
        //metoda edytująca
        if (!Command.Type.UPDATE.equals(command.getType())) {
            throw new IllegalArgumentException(command.getType().getName());
        }
        try (
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)
        ) {
            statement.setString(1, command.getToDoItem().getDescription());
            statement.setTimestamp(2, Timestamp.valueOf(command.getToDoItem().getDeadLine()));
            statement.setInt(3, command.getToDoItem().getPriority());
            statement.setString(4, command.getToDoItem().getName());
            int count = statement.executeUpdate();
            System.out.printf("run [%s] succesfully, deleted [%s]%n", command.getType(), count);
        } catch (SQLException e) {
            System.err.printf("[%s] DATA ERROR. Message: [%s]%n", command.getType(), e.getMessage());
        }
    }

    private void runDelete(final Command command) {
        //metoda kasująca wszystkie wpisy z bazy danych
        if (!Command.Type.DELETE.equals(command.getType())) {
            throw new IllegalArgumentException(command.getType().getName());
        }
        try (
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(SQL_DELETE)
        ) {
            statement.setString(1, command.getToDoItem().getName());
            int count = statement.executeUpdate();
            System.out.printf("run [%s] succesfully, deleted [%s]%n", command.getType(), count);
        } catch (SQLException e) {
            System.err.printf("[%s] DATA ERROR. Message: [%s]%n", command.getType(), e.getMessage());
        }

    }

    private void runRead(final Command command) {
        if (!Command.Type.READ.equals(command.getType())) {
            throw new IllegalArgumentException(command.getType().getName());
        }
        try (
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(SQL_READ_WHERE)
        ) {
            statement.setString(1, command.getToDoItem().getName());
            try (ResultSet resultSet = statement.executeQuery()) {
                List<ToDoItem> readItems = mapToDoItem(resultSet);
                print(readItems);
                System.out.printf("run [%s] succesfully, read [%s]%n", command.getType(), readItems.size());
            }
        } catch (SQLException e) {
            System.err.printf("[%s] ERROR. Message: [%s]%n", command.getType(), e.getMessage());
        }

    }

    private void runReadAll(final Command command) {
        if (!Command.Type.READ_ALL.equals(command.getType())) {
            throw new IllegalArgumentException(command.getType().getName());
        }
        try (
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(SQL_READ_ALL
                        .replace("?1", command.getSortBy().name())
                        .replace("?2", command.getSortDir().name()))
        ) {
            try (ResultSet resultSet = statement.executeQuery()) {
                List<ToDoItem> readItems = mapToDoItem(resultSet);
                print(readItems);
                System.out.printf("run [%s] succesfully, read [%s] rows%n", command.getType(), readItems.size());
            }
        } catch (SQLException e) {
            System.err.printf("[%s] ERROR. Message: [%s]%n", command.getType(), e.getMessage());
        }

    }

    private void runGroupeddAll(final Command command) {
        if (!Command.Type.READ_GROUPED.equals(command.getType())) {
            throw new IllegalArgumentException(command.getType().getName());
        }
        try (
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(SQL_READ_GROUPED)
        ) {
            try (ResultSet resultSet = statement.executeQuery()) {
                var grouped = mapToGrouped(resultSet);
                print(grouped);
                System.out.printf("run [%s] succesfully, read [%s] rows%n", command.getType(), grouped.size());
            }
        } catch (SQLException e) {
            System.err.printf("[%s] ERROR. Message: [%s]%n", command.getType(), e.getMessage());
        }

    }

    private Map<String, String> mapToGrouped(ResultSet resultSet) throws SQLException {
        Map<String, String> result = new HashMap<>();
        while (resultSet.next()) {
            result.put(resultSet.getString("DATE"), resultSet.getString("TASKS"));
        }
        return result;
    }

    private List<ToDoItem> mapToDoItem(ResultSet resultSet) throws SQLException {
        List<ToDoItem> result = new ArrayList<>();
        while (resultSet.next()) {
            ToDoItem toDoItem = new ToDoItem();
            toDoItem.setName(resultSet.getString(ToDoItem.Field.NAME.name()));
            toDoItem.setDescription(resultSet.getString(ToDoItem.Field.DESCRIPTION.name()));
            toDoItem.setDeadLine(resultSet.getTimestamp(ToDoItem.Field.DEADLINE.name()).toLocalDateTime());
            toDoItem.setPriority(resultSet.getInt(ToDoItem.Field.PRIORITY.name()));
            toDoItem.setStatus(ToDoItem.Status.valueOf(resultSet.getString(ToDoItem.Field.STATUS.name())));
            result.add(toDoItem);
        }
        return result;
    }

    private void print(List<ToDoItem> readItems) {
        System.out.println("PRINTING TO DO LIST");
        String schema = "%-25s%-25s%-25s%-25s%-25s%n";
        System.out.printf(schema,
                ToDoItem.Field.NAME,
                ToDoItem.Field.DESCRIPTION.name(),
                ToDoItem.Field.DEADLINE.name(),
                ToDoItem.Field.PRIORITY.name(),
                ToDoItem.Field.STATUS.name());

        readItems.forEach(entry -> System.out.printf(
                schema,
                entry.getName(),
                entry.getDescription(),
                entry.getDeadLine(),
                entry.getPriority(),
                entry.getStatus()));
    }

    private void print(Map<String, String> items) {
        System.out.println("READ GROUPED");
        String schema = "%-25s%-25s%n";
        System.out.printf(schema, "DATE", "TASKS");
        for (var stringStringEntry : items.entrySet()) {
            System.out.printf(schema, stringStringEntry.getKey(), stringStringEntry.getValue());
        }
    }

    private void runDeleteAll(final Command command) {
        //metoda kasująca wszystkie wpisy z bazy danych
        if (!Command.Type.DELETE_ALL.equals(command.getType())) {
            throw new IllegalArgumentException(command.getType().getName());
        }
        try (
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(SQL_DELETE_ALL)
        ) {
            int count = statement.executeUpdate();
            System.out.printf("run [%s] succesfully, deleted [%s]%n", command.getType(), count);
        } catch (SQLException e) {
            System.err.printf("[%s] DATA ERROR. Message: [%s]%n", command.getType(), e.getMessage());
        }

    }
    private void runCompleted(final Command command) {
        //metoda kasująca wszystkie wpisy z bazy danych
        if (!Command.Type.COMPLETED.equals(command.getType())) {
            throw new IllegalArgumentException(command.getType().getName());
        }
        try (
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(SQL_COMPLETED)
        ) {
            statement.setString(1, command.getToDoItem().getStatus().name());
            statement.setString(2, command.getToDoItem().getName());
            int count = statement.executeUpdate();
            System.out.printf("run [%s] succesfully, deleted [%s]%n", command.getType(), count);
        } catch (SQLException e) {
            System.err.printf("[%s] DATA ERROR. Message: [%s]%n", command.getType(), e.getMessage());
        }

    }
}
