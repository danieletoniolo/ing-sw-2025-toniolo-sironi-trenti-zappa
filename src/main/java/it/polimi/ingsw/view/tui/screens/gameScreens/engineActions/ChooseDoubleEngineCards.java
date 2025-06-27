package it.polimi.ingsw.view.tui.screens.gameScreens.engineActions;

import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

/**
 * Screen for choosing double engine cards to activate in the TUI.
 * Extends {@link ManagerEnginesCards} and manages the selection and activation
 * of double engine cards, providing options to cancel or complete the selection.
 */
public class ChooseDoubleEngineCards extends ManagerEnginesCards {

    /**
     * Constructs the screen with a list of selectable double engine cards,
     * along with "Cancel" and "Done" options.
     */
    public ChooseDoubleEngineCards() {
        super(new ArrayList<>(){{
            if (enginesIDs == null) {
                enginesIDs = new ArrayList<>();
            }
            spaceShipView.getMapDoubleEngines().forEach(
                    (key, value) -> {
                        if (!enginesIDs.contains(key)) {
                            add("Active double engines (" + value.getRow() + " " + value.getCol() + ")");
                        }
                    }
            );
            add("Cancel");
            add("Done");
        }});
    }

    /**
     * Returns the line to display before the user input.
     *
     * @return the prompt string for the user
     */
    @Override
    protected String lineBeforeInput() {
        return "Choose double engines to activate";
    }

    /**
     * Handles the logic for setting the next screen based on user selection.
     * Updates the list of selected engine IDs, manages navigation between screens,
     * and sets the appropriate message for the user.
     *
     * @return the next {@link TuiScreenView} to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        int num = (int) spaceShipView.getMapDoubleEngines().keySet().stream()
                .filter(key -> !enginesIDs.contains(key))
                .count();

        if (selected == num) {
            destroyStatics();
            setMessage(null);
            return new ChooseDoubleEngineCards();
        }

        if (selected == num + 1) {
            return new EngineBatteryCards();
        }

        spaceShipView.getMapDoubleEngines().keySet().stream()
                .filter(key -> !enginesIDs.contains(key))
                .skip(selected)
                .findFirst()
                .ifPresent(enginesIDs::add);

        StringBuilder line = new StringBuilder();
        for (Integer ID : enginesIDs) {
            line.append("(").append(spaceShipView.getMapDoubleEngines().get(ID).getRow()).append(" ").append(spaceShipView.getMapDoubleEngines().get(ID).getCol()).append(") ");
        }

        setMessage("You are activating " + line);
        return new ChooseDoubleEngineCards();
    }
}
