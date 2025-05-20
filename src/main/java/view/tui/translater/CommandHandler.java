package view.tui.translater;

import view.tui.input.Command;
import view.tui.states.StateTuiView;

public class CommandHandler {
    //TODO: Deve avere la comunicazione con il controller


    /**
     * Creates an event based on the command received and sends it to the controller.
     * @param command
     * @param currentState
     */
    public void createEvent(Command command, StateTuiView currentState) {

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

    }
}
