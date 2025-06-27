package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the TUI screen for viewing another player's information.
 * Displays the player's ship, discard/reserved pile, and other relevant data.
 */
public class OtherPlayer implements TuiScreenView {
    /** List of available options for this screen. */
    private final ArrayList<String> options = new ArrayList<>(List.of("Back"));
    /** Indicates if this is a newly displayed screen. */
    private boolean isNewScreen;

    /** Total number of lines to display on the screen. */
    private final int totalLines;
    /** Reference to the previous screen. */
    private final TuiScreenView oldScreen;
    /** Message to display on the screen. */
    protected String message;
    /** The next screen to display, if set. */
    private TuiScreenView nextScreen;

    /** The player whose data is being viewed. */
    private final PlayerDataView playerToView;

    /**
     * Constructs a new OtherPlayer screen.
     * @param playerToView the player whose data will be shown
     * @param oldScreen the previous screen to return to
     */
    public OtherPlayer(PlayerDataView playerToView, TuiScreenView oldScreen) {
        this.playerToView = playerToView;
        totalLines = playerToView.getShip().getRowsToDraw() + 3 + 2;
        this.oldScreen = oldScreen;
        this.isNewScreen = true;
    }

    /**
     * Gets the player whose data is being viewed.
     * @return the PlayerDataView instance
     */
    public PlayerDataView getPlayerToView() {
        return playerToView;
    }

    /**
     * Reads a command from the user using the provided parser.
     * @param parser the input parser
     */
    @Override
    public void readCommand(Parser parser) {
        parser.getCommand(options, totalLines);
    }

    /**
     * Determines and returns the next screen to display.
     * @return the next TuiScreenView to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        if (nextScreen != null) {
            return nextScreen;
        }
        return oldScreen;
    }

    /**
     * Prints the TUI representation of the other player's data.
     */
    @Override
    public void printTui() {
        List<String> newLines = new ArrayList<>();

        int playerCount = 0;
        for (int i = 0; i < playerToView.getShip().getRowsToDraw(); i++) {
            StringBuilder line = new StringBuilder();
            line.append(playerToView.getShip().drawLineTui(i));

            if (i <= playerToView.getShip().getDiscardReservedPile().getRowsToDraw()) {
                line.append(playerToView.getShip().getDiscardReservedPile().drawLineTui(i));
            }
            else if (i > ((playerToView.getShip().getRowsToDraw() - 2) / 5 * 3 + 1) - 1 && i <= ((playerToView.getShip().getRowsToDraw() - 2) / 5 * 3 + playerToView.getRowsToDraw())) {
                line.append("   ").append(playerToView.drawLineTui(playerCount));
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
        newLines.add("Commands:");

        TerminalUtils.printScreen(newLines, totalLines + options.size());

        if (isNewScreen) {
            isNewScreen = false;
            TerminalUtils.clearLastLines(totalLines + options.size());
        }
    }

    /**
     * Sets a message to be displayed on this and the previous screen.
     * @param message the message to display
     */
    @Override
    public synchronized void setMessage(String message) {
        this.message = message;
        oldScreen.setMessage(message);
    }

    /**
     * Gets the type of this TUI screen.
     * @return the TuiScreens enum value for this screen
     */
    @Override
    public TuiScreens getType() {
        return TuiScreens.OtherPlayer;
    }

    /**
     * Sets the next screen to be displayed after this one.
     * @param nextScreen the next TuiScreenView
     */
    @Override
    public void setNextScreen(TuiScreenView nextScreen) {
        this.nextScreen = nextScreen;
    }
}
