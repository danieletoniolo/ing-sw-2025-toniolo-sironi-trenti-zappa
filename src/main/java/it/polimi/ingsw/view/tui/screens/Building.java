package it.polimi.ingsw.view.tui.screens;

import org.javatuples.Pair;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.miniModel.components.*;
import it.polimi.ingsw.view.miniModel.deck.DeckView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.miniModel.timer.TimerView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a building screen in the TUI.
 * Handles the display and interaction logic for the building phase,
 * including options, player data, decks, and timer.
 */
public abstract class Building implements TuiScreenView {
    /** List of selectable options for the user. */
    protected final ArrayList<String> options = new ArrayList<>();
    /** Flag indicating if this is a new screen to be rendered. */
    protected boolean isNewScreen;
    /** Total number of lines to be displayed on the screen. */
    protected int totalLines;
    /** Message to be shown to the user. */
    protected String message;
    /** Reference to the client player data. */
    protected final PlayerDataView clientPlayer = MiniModel.getInstance().getClientPlayer();
    /** Pair containing deck views and their visibility status. */
    protected final Pair<DeckView[], Boolean[]> decksView;
    /** Reference to the timer view. */
    private final TimerView timerView = MiniModel.getInstance().getTimerView();
    /** Index of the currently selected option. */
    protected int selected;

    /**
     * Constructs a Building screen with additional options.
     * @param otherOptions List of extra options to be added to the screen.
     */
    public Building(List<String> otherOptions) {
        this.decksView = MiniModel.getInstance().getDeckViews();

        if (otherOptions != null && !otherOptions.isEmpty()) options.addAll(otherOptions);
        for (PlayerDataView p : MiniModel.getInstance().getOtherPlayers()) {
            options.add("View " + p.drawLineTui(0) + "'s spaceship");
        }
        options.add("Close program");

        totalLines = MiniModel.getInstance().getViewablePile().getRowsToDraw()
                + 1 + clientPlayer.getShip().getRowsToDraw() + 3 + 2;

        this.isNewScreen = true;
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
        if ((selected < options.size() - 1) && (selected >= options.size() - 1 - MiniModel.getInstance().getOtherPlayers().size())) {
            int i = selected - (options.size() - MiniModel.getInstance().getOtherPlayers().size() - 1);

            return new OtherPlayer(MiniModel.getInstance().getOtherPlayers().get(i), this);
        }

        if (selected == options.size() - 1) {
            return new ClosingProgram();
        }

        if (getType() != TuiScreens.MainBuilding && selected < 0 || selected >= options.size()) {
            return this;
        }

        return null;
    }

    /**
     * Returns the type of this TUI screen.
     * @return The TuiScreens enum value representing this screen.
     */
    @Override
    public TuiScreens getType() {
        return TuiScreens.Building;
    }

    /**
     * Prints the TUI representation of the building screen, including
     * the player's ship, decks, timer, and available options.
     */
    @Override
    public void printTui() {
        List<String> newLines = new ArrayList<>();

        ViewablePileView tiles = MiniModel.getInstance().getViewablePile();
        for (int i = 0; i < tiles.getRowsToDraw(); i++) {
            newLines.add(tiles.drawLineTui(i));
        }
        newLines.add("");

        int deckCount = 0;
        int discardCount = 0;
        for (int i = 0; i < clientPlayer.getShip().getRowsToDraw(); i++) {
            StringBuilder line = new StringBuilder();
            line.append(clientPlayer.getShip().drawLineTui(i));
            if (i <= clientPlayer.getShip().getDiscardReservedPile().getRowsToDraw()) {
                line.append(" ").append(clientPlayer.getShip().getDiscardReservedPile().drawLineTui(discardCount));
                discardCount++;
            }
            else if (i == ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 * 3 + 1) - 1) {
                line.append("       " + "   Hand: ");
            }
            else if (i >= ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 * 3 + 1) && i < ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 * 3 + 1) + ComponentView.getRowsToDraw()) {
                line.append("         ").append(clientPlayer.getHand().drawLineTui((i - 1) % ComponentView.getRowsToDraw()));
            }
            else if (i == clientPlayer.getShip().getRowsToDraw() - 1) {
                line.append("   ").append(clientPlayer.drawLineTui(0));
            }
            else {
                line.append("                ");
            }
            if (clientPlayer.getShip().getLevel().equals(LevelView.SECOND)) {
                if (i == 0) {
                    line.append("    ");
                    int hiddenTiles = MiniModel.getInstance().getNumberViewableComponents();
                    int fraction = hiddenTiles / 10;
                    line.append(timerView.drawLineTui(0));
                    line.append(" ".repeat(Math.max(0, decksView.getValue0()[0].getColsToDraw())));
                    line.append("            ").append("Hidden tiles: ").append(hiddenTiles).append(fraction >= 10 ? "" : fraction > 0 ? " " : "  ");
                }
                if (i == 1) {
                    line.append("    ");
                    line.append(timerView.drawLineTui(1));
                }
                if (i == ((clientPlayer.getShip().getRowsToDraw() - 2) / 5) + 2) {
                    line.append("    ");
                    for (int j = 0; j < 3; j++) {
                        line.append("        ").append("Deck ").append(j + 1).append(":").append("        ").append("   ");
                    }
                }
                if (i > ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 + 1) + 1 && i <= DeckView.getRowsToDraw() + ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 + 1) + 1) {
                    line.append("  ");
                    for (int j = 0; j < 3; j++) {
                        if (decksView.getValue1()[j]) {
                            line.append("   ").append(decksView.getValue0()[j].drawLineTui(deckCount));
                        } else {
                            line.append("                          ");
                        }
                    }
                    deckCount++;
                }
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
     * Returns the line to be displayed before the input prompt.
     * @return The string to show before user input.
     */
    protected String lineBeforeInput(){
        return "Commands:";
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
     * Sets the next screen to be displayed.
     * @param nextScreen The next TuiScreenView to display.
     */
    @Override
    public void setNextScreen(TuiScreenView nextScreen) {

    }
}
