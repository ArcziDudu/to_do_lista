import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CommandBuilder {
    Optional<Command> buildCommand(final String line) {
        String[] split = line.split(";");
        String commandType = split[0];
        if (!Command.Type.valuesAsList().contains(commandType)) {
            System.err.printf("User provided unknown command: [%s]%n", commandType);
            return Optional.empty();
        }
        List<String> stringCommandWithParamsExtracted = Arrays.asList(split).subList(1,  split.length);

        var parametersMap = stringCommandWithParamsExtracted.stream()
                .map(item -> item.split("="))
                .collect(Collectors.toMap(itemSplit -> itemSplit[0], itemSplit -> itemSplit[1]));

        List<String> sortingParams = Optional.ofNullable(parametersMap.get(ToDoItem.Field.SORT.name()))
                .map(params -> List.of(params.split(",")))
                .orElse(Collections.emptyList());

        ToDoItem toDoItem = buildToDoItem(parametersMap);
        return Optional.of(new Command(
                Command.Type.from(commandType),
                toDoItem,
                findSortingField(sortingParams),
                findSortingDir(sortingParams)
        ));
    }

    private ToDoItem.Field findSortingField(List<String> sortingParams) {
        if(sortingParams.isEmpty()){
            return ToDoItem.Field.NAME;
        }
        try{
            return ToDoItem.Field.valueOf(sortingParams.get(0));
        }catch (Exception e){
            System.err.printf("Sorting field is not specified. Default: [%s]%n", ToDoItem.Field.NAME);
            return ToDoItem.Field.NAME;
        }
    }

    private Command.SortDir findSortingDir(List<String> sortingParams) {
        if(sortingParams.isEmpty()){
            return Command.SortDir.ASC;
        }
        try{
           return Command.SortDir.valueOf(sortingParams.get(1));
        }catch (Exception e){
            System.err.printf("Sorting dir is not specified. Default: [%s]%n", Command.SortDir.ASC);
            return Command.SortDir.ASC;
        }
    }

    private ToDoItem buildToDoItem(final Map<String, String> parametersMap) {
        ToDoItem toDoItem = new ToDoItem();
        Optional.ofNullable(parametersMap.get(ToDoItem.Field.NAME.name()))
                .ifPresent(toDoItem::setName);

        Optional.ofNullable(parametersMap.get(ToDoItem.Field.DESCRIPTION.name()))
                .ifPresent(toDoItem::setDescription);

        Optional.ofNullable(parametersMap.get(ToDoItem.Field.DEADLINE.name()))
                .ifPresent(deadline -> toDoItem.setDeadLine(LocalDateTime.parse(deadline, ToDoItem.DATE_FORMAT)));

        Optional.ofNullable(parametersMap.get(ToDoItem.Field.PRIORITY.name()))
                .ifPresent(priority -> toDoItem.setPriority(Integer.valueOf(priority)));
        return toDoItem;
    }
}
