package view.tui.translater;

import view.tui.input.Command;
import view.tui.states.StateView;

public class CommandHandler {
    //TODO: Deve avere la comunicazione con il controller

    public void createEvent(Command command) {

        //StateView possibleNewState = currentState.isValidCommand(command);
        //if (possibleNewState != null) {
            //return possibleNewState;
        //}

        try {
            switch (command.name()) {
                case "/login":
                    ///TODO: Comunica con controller
                    //create a structure

                case "/ready":
                    //TODO: Comunica con controller


                    break;
                default:
                    throw new IllegalArgumentException("Invalid command: " + command.name());
            }
        } catch (Exception e) {
            System.out.println("Elaboration error: " + e.getMessage());
        }

        //return currentState;
    }
}
