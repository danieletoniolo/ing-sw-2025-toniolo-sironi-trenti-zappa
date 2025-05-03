package view.tui;

import view.tui.input.Command;
import view.tui.input.Parser;

public class TuiManager {
    private Parser parser = new Parser();
    private Command command;
    private Menu menu;

    public void startTui(){
        System.out.println("Welcome to Space Trucker!");

        System.out.println("This is the TUI version of the game.");


        System.out.println("Insert your nickname:");
        command = parser.readCommand();

        String nickname = command.name();
        System.out.println("Your nickname is: " + nickname);

        TerminalUtils.clearTerminal();
        menu = new Menu(nickname);
        menu.drawMenu();
    }
}
