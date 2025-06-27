package it.polimi.ingsw.view.tui.screens.menuScreens;

import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.lobby.clientToServer.CreateLobby;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.Lobby;
import it.polimi.ingsw.view.tui.screens.Menu;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.TuiScreens;

/**
 * The ChooseLevel class represents a menu screen where the user can select the game's difficulty level.
 * It extends the Menu class and provides options for different levels and navigation.
 */
public class ChooseLevel extends Menu {

    /**
     * Constructs a ChooseLevel menu, initializing the available options.
     */
    public ChooseLevel() {
        options.clear();
        options.add("Learning");
        options.add("Second");
        options.add("Back");
    }

    /**
     * Returns the line to display before the user input prompt.
     *
     * @return a String prompting the user to choose the game's level
     */
    @Override
    protected String lineBeforeInput() {
        return "Choose the game's level:";
    }

    /**
     * Reads the user's command and sets the selected level based on the input.
     *
     * @param parser the Parser object used to interpret user input
     */
    @Override
    public void readCommand(Parser parser) {
        selected = parser.getCommand(options, totalLines);
        level = selected == 0 ? 1 : 2;
    }

    /**
     * Returns the type of this TUI screen.
     *
     * @return the TuiScreens enum value representing this screen
     */
    @Override
    public TuiScreens getType() {
        return TuiScreens.ChooseLevelTuiScreen;
    }

    /**
     * Sets and returns the next screen based on the user's selection.
     * Handles lobby creation and error management.
     *
     * @return the next TuiScreenView to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        if (selected == 0 || selected == 1) {
            // Create a lobby with the selected level and max players
            StatusEvent status = CreateLobby.requester(Client.transceiver, new Object()).request(new CreateLobby(MiniModel.getInstance().getUserID(), maxPlayers, level));
            maxPlayers = 0;
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                TuiScreenView newScreen = new Menu();
                newScreen.setMessage(((Pota) status).errorMessage());
                return newScreen;
            }
            return new Lobby();
        }

        if (selected == 2) {
            return new ChooseNumberPlayers();
        }

        return this;
    }
}
