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

public class LogIn implements TuiScreenView {
    private String nickname;
    private final int totalLines = LogInView.getRowsToDraw() + 3;
    protected String message;

    public LogIn() {
    }

    @Override
    public void readCommand(Parser parser) {
        nickname = parser.readNickname("Enter your nickname: ", totalLines);
    }

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

    @Override
    public synchronized void setMessage(String message) {
        this.message = message;
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.LogIn;
    }

    @Override
    public void setNextScreen(TuiScreenView nextScreen) {

    }
}
