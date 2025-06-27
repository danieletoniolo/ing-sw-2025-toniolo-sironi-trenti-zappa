package it.polimi.ingsw.view.tui.screens.menuScreens;

import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.Menu;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.TuiScreens;

/**
 * Screen for choosing the number of players in the game.
 * Extends the Menu class and provides options for 2, 3, or 4 players, or to go back.
 */
public class ChooseNumberPlayers extends Menu {

    /**
     * Constructs the ChooseNumberPlayers screen, initializing the options.
     */
    public ChooseNumberPlayers() {
        options.clear();
        options.add("2 players");
        options.add("3 players");
        options.add("4 players");
        options.add("Back");
    }

    /**
     * Reads the user's command and sets the selected option and maxPlayers.
     *
     * @param parser the Parser used to read user input
     */
    @Override
    public void readCommand(Parser parser) {
        selected = parser.getCommand(options, totalLines);
        maxPlayers = selected + 2;
    }

    /**
     * Determines and returns the next screen based on the user's selection.
     *
     * @return the next TuiScreenView to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        if (selected >= 0 && selected <= 2) {
            return new ChooseLevel();
        }

        if (selected == 3) {
            return new Menu();
        }

        return this;
    }

    /**
     * Returns the prompt line shown before user input.
     *
     * @return the prompt string
     */
    @Override
    protected String lineBeforeInput() {
        return "Choose the number of player for the game:";
    }

    /**
     * Returns the type of this TUI screen.
     *
     * @return the TuiScreens enum value for this screen
     */
    @Override
    public TuiScreens getType() {
        return TuiScreens.ChooseNumberPlayersTuiScreen;
    }
}
