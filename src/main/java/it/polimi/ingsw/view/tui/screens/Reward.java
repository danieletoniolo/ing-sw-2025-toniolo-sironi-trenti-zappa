package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.BoardView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a reward screen in the TUI.
 * Handles the display of options, player information, and navigation between screens.
 */
public abstract class Reward implements TuiScreenView {
    /** List of selectable options for the user. */
    private final ArrayList<String> options = new ArrayList<>();
    /** Total number of lines to be displayed on the screen. */
    protected int totalLines;
    /** Index of the currently selected option. */
    protected int selected;
    /** Message to be displayed to the user. */
    private String message;
    /** Flag indicating if this is a new screen instance. */
    private boolean isNewScreen;
    /** Reference to the next screen to be displayed. */
    protected TuiScreenView nextScreen;

    /** Board view instance for drawing the board. */
    private final BoardView boardView = MiniModel.getInstance().getBoardView();
    /** List of players sorted by coins (descending). */
    protected final List<PlayerDataView> sortedPlayers;

    /**
     * Constructs a Reward screen with additional options.
     * @param otherOptions List of extra options to be added to the screen.
     */
    public Reward(List<String> otherOptions) {
        if (otherOptions != null && !otherOptions.isEmpty()) options.addAll(otherOptions);

        options.add("View your spaceship");
        for (PlayerDataView p : MiniModel.getInstance().getOtherPlayers()) {
            options.add("View " + p.drawLineTui(0) + "'s spaceship");
        }
        options.add("Close program");

        this.isNewScreen = true;

        sortedPlayers = new ArrayList<>();

        sortedPlayers.add(MiniModel.getInstance().getClientPlayer());
        sortedPlayers.addAll(MiniModel.getInstance().getOtherPlayers());
        sortedPlayers.sort((p1, p2) -> Integer.compare(p2.getCoins(), p1.getCoins()));

        totalLines = boardView.getRowsToDraw() + 1 + sortedPlayers.size() + 5;
    }

    /**
     * Reads the user's command input and updates the selected option.
     * @param parser The parser used to interpret user input.
     */
    @Override
    public void readCommand(Parser parser) {
        selected = parser.getCommand(options, totalLines);
    }

    /**
     * Determines and returns the next screen based on the selected option.
     * @return The next TuiScreenView to display, or null if no transition.
     */
    @Override
    public TuiScreenView setNewScreen() {
        if (selected == options.size() - 2 - MiniModel.getInstance().getOtherPlayers().size()) {
            return new OtherPlayer(MiniModel.getInstance().getClientPlayer(), this);
        }

        if ((selected < options.size() - 1) && (selected >= options.size() - 1 - MiniModel.getInstance().getOtherPlayers().size())) {
            int i = selected - (options.size() - MiniModel.getInstance().getOtherPlayers().size() - 1);

            return new OtherPlayer(MiniModel.getInstance().getOtherPlayers().get(i), this);
        }

        if (selected == options.size() - 1) {
            return new ClosingProgram();
        }

        return null;
    }

    /**
     * Sets the message to be displayed on the screen.
     * @param message The message to display.
     */
    @Override
    public synchronized void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the type of this TUI screen.
     * @return The TuiScreens enum value for this screen.
     */
    @Override
    public TuiScreens getType() {
        return TuiScreens.Reward;
    }

    /**
     * Sets the next screen to be displayed after this one.
     * @param nextScreen The next TuiScreenView.
     */
    @Override
    public void setNextScreen(TuiScreenView nextScreen) {
        this.nextScreen = nextScreen;
    }

    /**
     * Prints the TUI screen, including the board, player info, messages, and options.
     */
    @Override
    public void printTui() {
        List<String> newLines = new ArrayList<>();

        for (int i = 0; i < boardView.getRowsToDraw(); i++) {
            newLines.add(boardView.drawLineTui(i));
        }

        newLines.add("");

        for (PlayerDataView p : sortedPlayers) {
            newLines.add(p.drawLineTui(0) + ": " + p.getCoins() + " coins");
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
     * Returns the prompt line shown before user input.
     * @return The prompt string.
     */
    private String lineBeforeInput() {
        return "Select an option:";
    }
}
