package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.GiveUp;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.spaceship.SpaceShipView;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.BoardView;
import it.polimi.ingsw.view.miniModel.deck.DeckView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing the main game screen for the TUI (Text User Interface).
 * Handles the display and interaction logic for the cards game, including options for viewing spaceships,
 * giving up, and closing the program.
 */
public abstract class CardsGame implements TuiScreenView {
    /**
     * List of selectable options for the user.
     */
    protected ArrayList<String> options = new ArrayList<>();
    /**
     * Indicates if the screen is being shown for the first time.
     */
    private boolean isNewScreen;

    /**
     * Total number of lines to be drawn on the screen.
     */
    private final int totalLines;
    /**
     * Index of the currently selected option.
     */
    protected int selected;
    /**
     * Message to be displayed on the screen.
     */
    protected static String message;
    /**
     * The spaceship view currently being displayed.
     */
    protected static SpaceShipView spaceShipView;
    /**
     * The next screen to be shown after this one.
     */
    protected TuiScreenView nextScreen;

    /**
     * Reference to the board view.
     */
    protected BoardView boardView = MiniModel.getInstance().getBoardView();
    /**
     * Reference to the client player's data.
     */
    protected PlayerDataView clientPlayer = MiniModel.getInstance().getClientPlayer();
    /**
     * Reference to the shuffled deck view.
     */
    protected DeckView shuffledDeckView = MiniModel.getInstance().getShuffledDeckView();

    /**
     * Constructs a new CardsGame screen, initializing options and calculating the total lines to draw.
     * @param otherOptions Additional options to be included in the options list.
     */
    public CardsGame(List<String> otherOptions) {
        if (otherOptions != null && !otherOptions.isEmpty()) options.addAll(otherOptions);

        for (PlayerDataView p : MiniModel.getInstance().getOtherPlayers()) {
            options.add("View " + p.drawLineTui(0) + "'s spaceship");
        }
        options.add("Give up");
        options.add("Close program");

        totalLines = Math.max(boardView.getRowsToDraw(), DeckView.getRowsToDraw())
                + 1 + clientPlayer.getShip().getRowsToDraw() + 2 + 3 + 2;
        this.isNewScreen = true;
    }

    /**
     * Reads the user's command from the parser and updates the selected option.
     * @param parser The parser handling user input.
     */
    @Override
    public void readCommand(Parser parser) {
        selected = parser.getCommand(options, totalLines);
    }

    /**
     * Determines and returns the next screen based on the user's selection.
     * Handles viewing other players, giving up, and closing the program.
     * @return The next TuiScreenView to display.
     */
    @Override
    public TuiScreenView setNewScreen() {
        if ((selected < options.size() - 2) && (selected >= options.size() - 2 - MiniModel.getInstance().getOtherPlayers().size())) {
            int i = selected - (options.size() - MiniModel.getInstance().getOtherPlayers().size() - 2);

            return new OtherPlayer(MiniModel.getInstance().getOtherPlayers().get(i), this);
        }

        if (selected == options.size() - 2) {
            // Send a request to give up
            StatusEvent status = GiveUp.requester(Client.transceiver, new Object()).request(new GiveUp(MiniModel.getInstance().getUserID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
            }
            return this;
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
     * Prints the TUI screen, including the board, deck, player spaceship, and options.
     */
    @Override
    public void printTui() {
        List<String> newLines = new ArrayList<>();

        if (spaceShipView == null) {
            spaceShipView = clientPlayer.getShip();
        }

        for (int i = 0; i < Math.max(boardView.getRowsToDraw(), DeckView.getRowsToDraw()); i++) {
            StringBuilder line = new StringBuilder();

            if (i < boardView.getRowsToDraw()) {
                line.append(boardView.drawLineTui(i));
            } else {
                line.append(" ".repeat(Math.max(0, boardView.getColsToDraw())));
            }

            line.append("              ");
            if (i < DeckView.getRowsToDraw()) {
                line.append(shuffledDeckView.drawLineTui(i));
            } else {
                line.append(" ".repeat(Math.max(0, shuffledDeckView.getColsToDraw())));
            }
            newLines.add(line.toString());
        }
        newLines.add("");

        int playerCount = 0;
        for (int i = 0; i < spaceShipView.getRowsToDraw(); i++) {
            StringBuilder line = new StringBuilder();
            line.append(spaceShipView.drawLineTui(i));

            if (i <= spaceShipView.getDiscardReservedPile().getRowsToDraw()) {
                line.append(" ").append(spaceShipView.getDiscardReservedPile().drawLineTui(i));
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
        PlayerDataView currentPlayer = MiniModel.getInstance().getCurrentPlayer();
        String turn = currentPlayer.equals(clientPlayer) ? "Your turn" : "Waiting for " + currentPlayer.drawLineTui(0) + "'s turn";
        newLines.add(turn);

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
     * @return The command prompt line.
     */
    protected String lineBeforeInput() {
        return "Commands: ";
    }

    /**
     * Sets the message to be displayed on the screen.
     * @param message The message to display.
     */
    @Override
    public synchronized void setMessage(String message) {
        CardsGame.message = message;
    }

    /**
     * Returns the type of the TUI screen.
     * @return The TuiScreens enum value representing this screen.
     */
    @Override
    public TuiScreens getType() {
        return TuiScreens.Game;
    }

    /**
     * Sets the next screen to be displayed after this one.
     * @param nextScreen The next TuiScreenView.
     */
    @Override
    public void setNextScreen(TuiScreenView nextScreen) {
        this.nextScreen = nextScreen;
    }
}
