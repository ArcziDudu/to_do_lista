import java.sql.*;

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
    private static final String SQL_DELETE_ALL
            = "DELETE FROM TODOLIST;";

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
            System.out.printf("run [%s] succesfully, inserted [%s]%n", command.getType(), count);
        }catch (SQLException e){
            System.err.printf("INSERT DATA ERROR. Message: [%s]%n", e.getMessage());
        }

    }

}
