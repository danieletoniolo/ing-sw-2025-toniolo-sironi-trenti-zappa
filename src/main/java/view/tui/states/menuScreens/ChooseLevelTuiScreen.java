package view.tui.states.menuScreens;

import it.polimi.ingsw.event.lobby.clientToServer.CreateLobby;
import it.polimi.ingsw.event.type.StatusEvent;
import view.Client;
import view.miniModel.MiniModel;
import view.tui.input.Parser;
import view.tui.states.LobbyTuiScreen;
import view.tui.states.MenuTuiScreen;
import view.tui.states.TuiScreenView;
import view.tui.states.TuiScreens;

import java.util.function.Supplier;

public class ChooseLevelTuiScreen extends MenuTuiScreen {

    public ChooseLevelTuiScreen() {
        options.clear();
        options.add("Learning");
        options.add("Second");
    }

    @Override
    public String lineBeforeInput() {
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

        StatusEvent status = CreateLobby.requester(Client.transceiver, new Object()).request(new CreateLobby(MiniModel.getInstance().userID, null, maxPlayers, level));
        if (status.get().equals("POTA")) {
            TuiScreenView newScreen = new MenuTuiScreen();
            newScreen.setMessage("Creation of the lobby failed. please try again.");
            return newScreen;
        }

        return new LobbyTuiScreen();
    }
}
