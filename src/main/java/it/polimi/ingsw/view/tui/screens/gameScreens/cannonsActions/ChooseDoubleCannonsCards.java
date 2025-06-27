package it.polimi.ingsw.view.tui.screens.gameScreens.cannonsActions;

import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

/**
 * Screen for choosing double cannons cards in the TUI.
 * Extends {@link ManagerCannonsCards} to manage the selection of double cannons.
 */
public class ChooseDoubleCannonsCards extends ManagerCannonsCards {

    /**
     * Constructs the screen for choosing double cannons cards.
     * Initializes the list of selectable options based on the available double cannons.
     */
    public ChooseDoubleCannonsCards() {
        super(new ArrayList<>(){{
            if (cannonsIDs == null) {
                cannonsIDs = new ArrayList<>();
            }
            spaceShipView.getMapDoubleCannons().forEach(
                    (key, value) -> {
                        if (!cannonsIDs.contains(key)) {
                            add("Active double cannon (" + value.getRow() + " " + value.getCol() + ")");
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
        return "Choose double cannons to activate";
    }

    /**
     * Handles the logic for setting the new screen based on user selection.
     * Updates the state and navigates to the appropriate screen.
     *
     * @return the next {@link TuiScreenView} to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        int num = (int) spaceShipView.getMapDoubleCannons().keySet().stream()
                .filter(key -> !cannonsIDs.contains(key))
                .count();

        if (selected == num) {
            destroyStatics();
            setMessage(null);
            return new ChooseDoubleCannonsCards();
        }

        if (selected == num + 1) {
            return new CannonsBatteryCards();
        }

        spaceShipView.getMapDoubleCannons().keySet().stream()
                .filter(key -> !cannonsIDs.contains(key))
                .skip(selected)
                .findFirst()
                .ifPresent(cannonsIDs::add);

        StringBuilder line = new StringBuilder();
        for (Integer ID : cannonsIDs) {
            line.append("(").append(spaceShipView.getMapDoubleCannons().get(ID).getRow()).append(" ").append(spaceShipView.getMapDoubleCannons().get(ID).getCol()).append(") ");
        }

        TuiScreenView newScreen = new ChooseDoubleCannonsCards();
        newScreen.setMessage("You are activating " + line);
        return newScreen;
    }
}
