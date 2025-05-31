package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.event.lobby.clientToServer.SetNickname;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.utils.Logger;
import org.jline.terminal.Terminal;
import it.polimi.ingsw.view.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.logIn.LogInView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.function.Supplier;

public class LogInTuiScreen implements TuiScreenView {
    private String nickname;
    private int totalLines = LogInView.getRowsToDraw() + 1 + 2;
    private int row;
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
            message = "Nickname already taken, please choose another one.";
            return this;
        }
        message = null;
        return new MenuTuiScreen();
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        row = 1;

        for (int i = 0; i < LogInView.getRowsToDraw(); i++) {
            TerminalUtils.printLine(writer, MiniModel.getInstance().getLogInView().drawLineTui(i), row++);
        }

        TerminalUtils.printLine(writer, message == null ? "" : message, row++);
        TerminalUtils.printLine(writer, "", row++);

        if (isNewScreen) {
            isNewScreen = false;
            for (int i = totalLines; i < terminal.getSize().getRows(); i++ ) {
                TerminalUtils.printLine(writer, "", i);
            }
        }
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.LogIn;
    }
}
