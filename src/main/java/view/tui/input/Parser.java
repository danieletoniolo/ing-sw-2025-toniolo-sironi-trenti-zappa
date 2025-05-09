package view.tui.input;

import java.util.Scanner;

public class Parser {
    private static Scanner scanner = new Scanner(System.in);

    public static Command readCommand() {
        System.out.print("> ");
        String line = scanner.nextLine().trim();
        String[] strings = line.split("\\s+");

        String name = strings.length > 0 ? strings[0].toLowerCase() : "";
        String[] parameters = (strings.length > 1)
                ? java.util.Arrays.copyOfRange(strings, 1, strings.length)
                : new String[0];

        return new Command(name, parameters);
    }

    public void closeScanner() {
        scanner.close();
    }
}
