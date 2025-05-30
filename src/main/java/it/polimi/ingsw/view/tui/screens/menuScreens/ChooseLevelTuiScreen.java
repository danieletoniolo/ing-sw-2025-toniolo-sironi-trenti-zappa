package it.polimi.ingsw.view.tui.screens.menuScreens;

import it.polimi.ingsw.event.lobby.clientToServer.CreateLobby;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.LobbyTuiScreen;
import it.polimi.ingsw.view.tui.screens.MenuTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.TuiScreens;

import java.util.function.Supplier;

public class ChooseLevelTuiScreen extends MenuTuiScreen {

    public ChooseLevelTuiScreen() {
        options.clear();
        options.add("Learning");
        options.add("Second");
    }

    @Override
    protected String lineBeforeInput() {
        return "Choose the game's level";
    }

    @Override
    public void readCommand(Parser parser, Supplier<Boolean> isStillCurrentScreen) throws Exception {
        selected = parser.getCommand(options, totalLines, isStillCurrentScreen);
        level = selected == 0 ? 0 : 2;
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.ChooseLevelTuiScreen;
    }

    @Override
    public TuiScreenView setNewScreen() {
        StatusEvent status = CreateLobby.requester(Client.transceiver, new Object()).request(new CreateLobby(MiniModel.getInstance().getUserID(), maxPlayers, level));
        maxPlayers = 0;
        if (status.get().equals("POTA")) {
            TuiScreenView newScreen = new MenuTuiScreen();
            newScreen.setMessage("Creation of the lobby failed. please try again.");
            return newScreen;
        }

        return new LobbyTuiScreen();
    }
}
