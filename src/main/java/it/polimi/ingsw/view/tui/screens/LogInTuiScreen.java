package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.lobby.clientToServer.SetNickname;
import it.polimi.ingsw.event.type.StatusEvent;
import org.jline.terminal.Terminal;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.logIn.LogInView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.function.Supplier;

public class LogInTuiScreen implements TuiScreenView {
    private String nickname;
    private final int totalLines = LogInView.getRowsToDraw() + 1 + 2;
    protected String message;
    protected boolean isNewScreen;

    public LogInTuiScreen() {
        isNewScreen = true;
    }

    @Override
    public void readCommand(Parser parser, Supplier<Boolean> isStillCurrentScreen) throws Exception {
        nickname = parser.readNickname("Enter your nickname: ", totalLines, isStillCurrentScreen);
    }

    @Override
    public TuiScreenView setNewScreen() {
        StatusEvent status = SetNickname.requester(Client.transceiver, new Object()).request(new SetNickname(MiniModel.getInstance().getUserID(), nickname));
        if (status.get().equals("POTA")) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }
        message = null;
        return new MenuTuiScreen();
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        int row = 1;

        for (int i = 0; i < LogInView.getRowsToDraw(); i++) {
            TerminalUtils.printLine(writer, MiniModel.getInstance().getLogInView().drawLineTui(i), row++);
        }

        TerminalUtils.printLine(writer, message == null ? "" : message, row++);
        TerminalUtils.printLine(writer, "", row);

        if (isNewScreen) {
            isNewScreen = false;
            for (int i = totalLines; i < terminal.getSize().getRows(); i++ ) {
                TerminalUtils.printLine(writer, "", i);
            }
        }
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
