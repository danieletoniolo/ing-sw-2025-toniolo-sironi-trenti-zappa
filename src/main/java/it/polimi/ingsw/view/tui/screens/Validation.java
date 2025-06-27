package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.miniModel.spaceship.SpaceShipView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a validation screen in the TUI.
 * Handles the display and navigation logic for validation-related screens,
 * including options for viewing other players' spaceships and closing the program.
 */
public abstract class Validation implements TuiScreenView {
    /** List of selectable options for the screen. */
    protected ArrayList<String> options = new ArrayList<>();
    /** Indicates if this is a new screen instance. */
    private boolean isNewScreen;

    /** Total number of lines to display in the TUI. */
    protected int totalLines;
    /** Index of the currently selected option. */
    protected int selected;
    /** Reference to the next screen state. */
    protected TuiScreenView nextState;

    /** Static reference to the current player's spaceship view. */
    protected static SpaceShipView spaceShipView;
    /** Static flag to indicate if the screen should be reset. */
    private static boolean reset;
    /** Static list of coordinates of tiles to be destroyed. */
    protected static List<Pair<Integer, Integer>> destroyTiles; // List of coordinates of tiles to be destroyed
    /** Message to be displayed on the screen. */
    private String message;

    /**
     * Constructs a Validation screen with additional options.
     * Initializes static fields if necessary and populates the options list.
     *
     * @param otherOptions Additional options to display on the screen.
     */
    public Validation(List<String> otherOptions) {
        if (!reset) {
            spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
            reset = true;
        }
        if (destroyTiles == null) {
            destroyTiles = new ArrayList<>();
        }

        if (otherOptions != null && !otherOptions.isEmpty()) options.addAll(otherOptions);

        for (PlayerDataView p : MiniModel.getInstance().getOtherPlayers()) {
            options.add("View " + p.drawLineTui(0) + "'s spaceship");
        }
        options.add("Close program");

        totalLines = spaceShipView.getRowsToDraw() + 5;
        this.isNewScreen = true;
    }

    /**
     * Reads the user's command input and updates the selected option.
     *
     * @param parser The parser used to read user input.
     */
    @Override
    public void readCommand(Parser parser) {
        selected = parser.getCommand(options, totalLines);
    }

    /**
     * Returns the type of the current TUI screen.
     *
     * @return The TuiScreens enum value representing this screen.
     */
    @Override
    public TuiScreens getType() {
        return TuiScreens.Validation;
    }

    /**
     * Determines and returns the next screen based on the user's selection.
     *
     * @return The next TuiScreenView, or null if no transition is needed.
     */
    @Override
    public TuiScreenView setNewScreen() {
        if ((selected < options.size() - 1) && (selected >= options.size() - 1 - MiniModel.getInstance().getOtherPlayers().size())) {
            int i = selected - (options.size() - MiniModel.getInstance().getOtherPlayers().size() - 1);

            return new OtherPlayer(MiniModel.getInstance().getOtherPlayers().get(i), this);
        }

        if (selected == options.size() - 1) {
            return new ClosingProgram();
        }

        if (selected < 0 || selected >= options.size()) {
            return this;
        }

        return null;
    }

    /**
     * Sets the message to be displayed on the screen.
     *
     * @param message The message to display.
     */
    @Override
    public synchronized void setMessage(String message) {
        this.message = message;
    }

    /**
     * Prints the TUI screen, including the spaceship, player info, and options.
     */
    @Override
    public void printTui() {
        List<String> newLines = new ArrayList<>();

        int playerCount = 0;
        for (int i = 0; i < spaceShipView.getRowsToDraw(); i++) {
            StringBuilder line = new StringBuilder();
            line.append(spaceShipView.drawLineTui(i));

            if (i <= spaceShipView.getDiscardReservedPile().getRowsToDraw()) {
                line.append(MiniModel.getInstance().getClientPlayer().getShip().getDiscardReservedPile().drawLineTui(i));
            }
            else if (i > ((spaceShipView.getRowsToDraw() - 2) / 5 * 3 + 1) - 1 && i <= ((spaceShipView.getRowsToDraw() - 2) / 5 * 3 + MiniModel.getInstance().getClientPlayer().getRowsToDraw())) {
                line.append("   ").append(MiniModel.getInstance().getClientPlayer().drawLineTui(playerCount));
                if (playerCount == 0) {
                    line.append("    ");
                }
                playerCount++;
            }
            newLines.add(line.toString());
        }

        newLines.add("");
        newLines.add(message == null ? "" : message);
        newLines.add("");
        newLines.add(lineBeforeInput());

        TerminalUtils.printScreen(newLines, totalLines + options.size());

        if (isNewScreen) {
            isNewScreen = false;
            TerminalUtils.clearLastLines(totalLines + options.size());
        }
    }

    /**
     * Returns the line to display before the input prompt.
     *
     * @return The command prompt line.
     */
    protected String lineBeforeInput() {
        return "Commands:";
    }

    /**
     * Sets the next screen to be displayed.
     *
     * @param nextScreen The next TuiScreenView to transition to.
     */
    @Override
    public void setNextScreen(TuiScreenView nextScreen) {
        this.nextState = nextScreen;
    }

    /**
     * Resets static fields to their initial state.
     * Used to clear static data between different validation screens.
     */
    public static void destroyStatics() {
        destroyTiles = null;
        reset = false;
        PlayerDataView player = MiniModel.getInstance().getClientPlayer();
        spaceShipView = player == null ? null : player.getShip();
    }
}
