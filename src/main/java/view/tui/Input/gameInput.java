package view.tui.Input;

import java.util.Scanner;

enum COMMAND {
    QUIT("q"),
    HELP("h"),
    ROLL("r"),
    MOVE("m"),
    USE("u"),
    PASS("p"),
    GIVE_UP("g"),
    RESTART("rs"),
    LEAVE("l");

    private final String command;

    COMMAND(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}

public class gameInput {
    private Scanner input = new Scanner(System.in);

    public String getInput() {
        do {

        }while()

        return input.nextLine();
    }
}
