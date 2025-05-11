package view.tui.translater;

import view.tui.input.Command;
import view.tui.states.StateView;

import java.util.function.Consumer;

public class CommandHandler {
    public void processCommand(Command command, Consumer<StateView> collback) {
        switch (command) {
            case "ready":
                try {
                    // Comunica con controller
                    // Ricevo dati e aggiorno strutture dati
                }
        }
    }
}
