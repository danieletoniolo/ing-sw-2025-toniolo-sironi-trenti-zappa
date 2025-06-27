package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.miniModel.spaceship.SpaceShipView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a TUI screen for modifying the crew.
 * Handles the display and navigation logic for the crew modification screen,
 * including options for viewing other players' spaceships and closing the program.
 */
public abstract class ModifyCrew implements TuiScreenView {
    /**
     * List of available options for the user.
     */
    protected ArrayList<String> options = new ArrayList<>();
    /**
     * Indicates if this is a new screen instance.
     */
    private boolean isNewScreen;

    /**
     * Total number of lines to display in the TUI.
     */
    protected int totalLines;
    /**
     * Index of the currently selected option.
     */
    protected int selected;
    /**
     * Message to display to the user.
     */
    protected String message;
    /**
     * Reference to the next screen to display.
     */
    protected TuiScreenView nextScreen;

    /**
     * Reference to the client's spaceship view.
     */
    protected final SpaceShipView spaceShipView = MiniModel.getInstance().getClientPlayer().getShip();
    /**
     * Reference to the client's player data view.
     */
    protected final PlayerDataView clientPlayer = MiniModel.getInstance().getClientPlayer();

    /**
     * Constructs a ModifyCrew screen with additional options.
     * Adds options for viewing other players' spaceships and closing the program.
     *
     * @param otherOptions Additional options to display.
     */
    public ModifyCrew(List<String> otherOptions) {
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
     * @param parser The parser to read user input.
     */
    @Override
    public void readCommand(Parser parser) {
        selected = parser.getCommand(options, totalLines);
    }

    /**
     * Determines and returns the next screen based on the user's selection.
     *
     * @return The next TuiScreenView to display.
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
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the type of this TUI screen.
     *
     * @return The TuiScreens enum value for ModifyCrew.
     */
    @Override
    public TuiScreens getType() {
        return TuiScreens.ModifyCrew;
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
                line.append(spaceShipView.getDiscardReservedPile().drawLineTui(i));
            }
            else if (i > ((spaceShipView.getRowsToDraw() - 2) / 5 * 3 + 1) - 1 && i <= ((spaceShipView.getRowsToDraw() - 2) / 5 * 3 + clientPlayer.getRowsToDraw())) {
                line.append("   ").append(clientPlayer.drawLineTui(playerCount));
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
     * @return The string to display before input.
     */
    protected String lineBeforeInput() {
        return "Commands";
    }

    /**
     * Sets the next screen to be displayed.
     *
     * @param nextScreen The next TuiScreenView.
     */
    @Override
    public void setNextScreen(TuiScreenView nextScreen) {
        this.nextScreen = nextScreen;
    }
}
