package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.lobby.clientToServer.SetNickname;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.logIn.LogInView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the LogIn screen in the TUI (Text User Interface).
 * Handles user input for nickname, manages login requests, and displays messages.
 */
public class LogIn implements TuiScreenView {
    /** The nickname entered by the user. */
    private String nickname;
    /** The total number of lines to draw on the screen. */
    private final int totalLines = LogInView.getRowsToDraw() + 3;
    /** Message to be displayed to the user (e.g., errors). */
    protected String message;

    /**
     * Constructs a new LogIn screen.
     */
    public LogIn() {
    }

    /**
     * Reads the user's nickname from the input parser.
     *
     * @param parser the input parser to read the nickname from
     */
    @Override
    public void readCommand(Parser parser) {
        nickname = parser.readNickname("Enter your nickname: ", totalLines);
    }

    /**
     * Handles the transition to a new screen after attempting to set the nickname.
     * If there is an error, displays the error message and stays on the login screen.
     * Otherwise, transitions to the Menu screen.
     *
     * @return the next TuiScreenView to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        // Request to set the nickname
        StatusEvent status = SetNickname.requester(Client.transceiver, new Object()).request(new SetNickname(MiniModel.getInstance().getUserID(), nickname));
        if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }
        message = null;
        return new Menu();
    }

    /**
     * Prints the TUI for the login screen, including any messages.
     */
    @Override
    public void printTui() {
        List<String> newLines = new ArrayList<>();

        for (int i = 0; i < LogInView.getRowsToDraw(); i++) {
            newLines.add(MiniModel.getInstance().getLogInView().drawLineTui(i));
        }

        newLines.add(message == null ? "" : message);
        newLines.add("");

        TerminalUtils.printScreen(newLines, totalLines + 1);

        TerminalUtils.clearLastLines(totalLines + 1);
    }

    /**
     * Sets the message to be displayed to the user.
     *
     * @param message the message to display
     */
    @Override
    public synchronized void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the type of this TUI screen.
     *
     * @return the TuiScreens enum value representing this screen
     */
    @Override
    public TuiScreens getType() {
        return TuiScreens.LogIn;
    }

    /**
     * Sets the next screen to be displayed.
     *
     * @param nextScreen the next TuiScreenView to display
     */
    @Override
    public void setNextScreen(TuiScreenView nextScreen) {

    }
}
