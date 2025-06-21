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

public class ChooseLevel extends Menu {

    public ChooseLevel() {
        options.clear();
        options.add("Learning");
        options.add("Second");
        options.add("Back");
    }

    @Override
    protected String lineBeforeInput() {
        return "Choose the game's level:";
    }

    @Override
    public void readCommand(Parser parser) {
        selected = parser.getCommand(options, totalLines);
        level = selected == 0 ? 1 : 2;
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.ChooseLevelTuiScreen;
    }

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
