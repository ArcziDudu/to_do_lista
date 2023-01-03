import javax.crypto.spec.PSource;
import java.util.Scanner;

public class ScannerRunner {
    public static void main(String[] args) {
        String exampleCreate = "CREATE;NAME=TASK1;DESCRIPTION=SOME DESCRIPTION1;DEADLINE=11.02.2021 20:10;PRIORITY=0";
        System.out.println("wpisz zadanie w podanym formacie(tresc jest przykładowa) : "+ exampleCreate);
        System.out.println("wpisz DELETE ALL ABY USUNĄĆ WSZYSTKO");
        System.out.println("wpisz DELETE;NAME=NAZWATASKA ABY USUNĄĆ WYBRANY TASK");
        System.out.println("WPISZ READ ALL ABY WYSWIETLIC WSZYSTKO");
        System.out.println("WPISZ COMPLETED;NAME=NAZWATASKA ABY ZMIENIC STATUS NA WYKONANY");
        System.out.println("WPISZ READ ALL;SORT=DEADLINE,DESC ABY POGRUPOWAĆ TASKI PO DACIE");
        System.out.println("WPISZ READ GROUPED ABY POGRUPOWAĆ TASKI");

        Scanner console = new Scanner(System.in);


        CommandBuilder commandBuilder = new CommandBuilder();
        DatabaseRunner databaseRunner = new DatabaseRunner();

        while (console.hasNext()){
            String command = console.nextLine().toUpperCase();
            commandBuilder.buildCommand(command)
                    .ifPresent(databaseRunner::run);
        }
    }
}
