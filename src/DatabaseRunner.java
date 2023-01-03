import java.sql.*;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class DatabaseRunner {
    private static final String URL = "jdbc:postgresql://localhost:5432/toDoList";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "anakonda123";

    private static final String SQL_INSERT =
            "INSERT INTO TODOLIST (NAME, DESCRIPTION, DEADLINE, PRIORITY) VALUES(?,?,?,?)";

    private static final String SQL_UPDATE =
            "UPDATE TODOLIST SET DESCRIPTION = ?, DEADLINE = ?, PRIORITY = ?, WHERE NAME = ?;";
    private final static String SQL_READ_WHERE
            = "SELECT * FROM TODOLIST WHERE NAME = ?;";
    private static final String SQL_READ_ALL
            = "SELECT * FROM TO DO LIST;";
    private static final String SQL_DELETE
            = "DELETE FROM  TO DO LIST WHERE NAME = ?;";
    private static final String SQL_DELETE_ALL = "DELETE FROM TODOLIST;";

    private final Map<Command.Type, Consumer<Command>> EXECUTION_MAP;
    {
        //poniżej znajduje się interfejs funkcyjny consumer
        //do metody run add przekazywana jest odpowiednia komenda
        EXECUTION_MAP = Map.of(
          Command.Type.CREATE, this::runAdd,
                Command.Type.DELETE_ALL, this::runDeleteAll
        );
    }

    void run(final Command command){
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

        System.out.println("##### FINISHED COMMAND #####");
    }

    private void runAdd(final Command command){
        if(!Command.Type.CREATE.equals(command.getType())){
            throw new IllegalArgumentException(command.getType().getName());
        }
        try(
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(SQL_INSERT)
        ){
            statement.setString(1, command.getToDoItem().getName());
            statement.setString(2, command.getToDoItem().getDescription());
            statement.setTimestamp(3, Timestamp.valueOf(command.getToDoItem().getDeadLine()));
            statement.setInt(4, command.getToDoItem().getPriority());
            int count = statement.executeUpdate();
            System.out.printf("run [%s] succesfully, modified [%s]%n", command.getType(), count);
        }catch (SQLException e){
            System.err.printf("[%s] ERROR. Message: [%s]%n",command.getType(), e.getMessage());
        }

    }

    private void runDeleteAll(final Command command){
        if(!Command.Type.DELETE_ALL.equals(command.getType())){
            throw new IllegalArgumentException(command.getType().getName());
        }
        try(
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(SQL_DELETE_ALL)
        ){
            int count = statement.executeUpdate();
            System.out.printf("run [%s] succesfully, deleted [%s]%n", command.getType(), count);
        }catch (SQLException e){
            System.err.printf("[%s] DATA ERROR. Message: [%s]%n",command.getType(), e.getMessage());
        }

    }
}
