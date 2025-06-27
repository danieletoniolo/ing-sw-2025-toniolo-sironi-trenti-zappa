package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.player.PlaceMarker;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.BoardView;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.buildingScreens.MainBuilding;
import it.polimi.ingsw.view.tui.screens.buildingScreens.WatchingBuilding;

import java.util.ArrayList;
import java.util.List;

/**
 * The ChoosePosition class represents a TUI screen where the player can choose a starting position
 * or perform building actions in the game. It manages user input, validates the selected position,
 * and handles transitions to other screens based on the game state.
 */
public class ChoosePosition implements TuiScreenView {
    /** List of selectable options for the user. */
    private final ArrayList<String> options = new ArrayList<>();
    /** Indicates if this is a new screen instance. */
    private boolean isNewScreen;

    /** Total number of lines to display in the TUI. */
    private final int totalLines;
    /** The next screen to transition to after this one. */
    private TuiScreenView nextScreen;

    /** Reference to the board view for rendering. */
    private final BoardView boardView = MiniModel.getInstance().getBoardView();
    /** The currently selected option index. */
    private int selected;
    /** Message to display to the user (e.g., errors). */
    private String message;

    /**
     * Constructs a ChoosePosition screen.
     * @param isBuilding true if the screen is for building actions, false otherwise
     */
    public ChoosePosition(boolean isBuilding) {
        options.add("1");
        options.add("2");
        options.add("3");
        options.add("4");

        if (isBuilding) {
            options.add("Back");
        }

        totalLines = MiniModel.getInstance().getBoardView().getRowsToDraw() + 5;

        this.isNewScreen = true;
    }

    /**
     * Reads the user's command from the parser and updates the selected option.
     * @param parser the input parser
     */
    @Override
    public void readCommand(Parser parser) {
        selected = parser.getCommand(options, totalLines);
    }

    /**
     * Handles the logic for transitioning to a new screen based on the user's selection.
     * Validates the selected position and manages error messages.
     * @return the next TuiScreenView to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        if (selected == 4) {
            return new MainBuilding();
        }

        // Validate the selected position
        StatusEvent status = PlaceMarker.requester(Client.transceiver, new Object()).request(new PlaceMarker(MiniModel.getInstance().getUserID(), selected));
        if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }

        // If the position is valid, end the turn
        status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
        if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }

        if (nextScreen == null) {
            return new WatchingBuilding();
        }
        return nextScreen;
    }

    /**
     * Sets the message to be displayed to the user.
     * @param message the message string
     */
    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Prints the TUI screen, including the board, messages, and options.
     */
    @Override
    public void printTui() {
        List<String> newLines = new ArrayList<>();

        for (int i = 0; i < boardView.getRowsToDraw(); i++) {
            newLines.add(boardView.drawLineTui(i));
        }

        newLines.add("");
        newLines.add(message == null ? "" : message);
        newLines.add("");
        newLines.add("Choose your starting position: ");

        TerminalUtils.printScreen(newLines, totalLines + options.size());

        if (isNewScreen) {
            isNewScreen = false;
            TerminalUtils.clearLastLines(totalLines + options.size());
        }
    }

    /**
     * Returns the type of this TUI screen.
     * @return the TuiScreens enum value for this screen
     */
    @Override
    public TuiScreens getType() {
        return TuiScreens.ChoosePosition;
    }

    /**
     * Sets the next screen to transition to after this one.
     * @param nextScreen the next TuiScreenView
     */
    @Override
    public void setNextScreen(TuiScreenView nextScreen) {
        this.nextScreen = nextScreen;
    }
}
