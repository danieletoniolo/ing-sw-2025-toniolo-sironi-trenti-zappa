package it.polimi.ingsw.view.tui.screens.buildingScreens;

import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.player.PlaceMarker;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.BoardView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.TuiScreens;
import org.jline.terminal.Terminal;

import java.util.ArrayList;

public class ChoosePositionTuiScreen implements TuiScreenView {
    private final ArrayList<String> options = new ArrayList<>();
    private final int totalLines;
    private TuiScreenView nextScreen;

    private final BoardView boardView = MiniModel.getInstance().getBoardView();
    private int selected;
    private String message;
    private boolean isNewScreen;

    public ChoosePositionTuiScreen() {
        options.add("1");
        options.add("2");
        options.add("3");
        options.add("4");
        options.add("Back");

        totalLines = MiniModel.getInstance().getBoardView().getRowsToDraw() + 5;
        isNewScreen = true;
    }

    @Override
    public void readCommand(Parser parser) {
        selected = parser.getCommand(options, totalLines);
    }

    @Override
    public TuiScreenView setNewScreen() {
        if (selected == 4) {
            return new MainCommandsTuiScreen();
        }

        StatusEvent status = PlaceMarker.requester(Client.transceiver, new Object()).request(new PlaceMarker(MiniModel.getInstance().getUserID(), selected));
        if (status.get().equals("POTA")) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }
        status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
        if (status.get().equals("POTA")) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }

        if (nextScreen == null) {
            return new WatchingTuiScreen();
        }
        return nextScreen;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        int row = 1;

        for (int i = 0; i < boardView.getRowsToDraw(); i++) {
            TerminalUtils.printLine(writer, boardView.drawLineTui(i), row++);
        }

        TerminalUtils.printLine(writer, "", row++);
        TerminalUtils.printLine(writer, message == null ? "" : message, row++);
        TerminalUtils.printLine(writer, "", row++);
        TerminalUtils.printLine(writer, "Choose your starting position: ", row);

        if (isNewScreen) {
            isNewScreen = false;
            for (int i = totalLines + options.size(); i < terminal.getSize().getRows(); i++ ) {
                TerminalUtils.printLine(writer, "", i);
            }
        }
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.ChoosePosition;
    }

    @Override
    public void setNextScreen(TuiScreenView nextScreen) {
        this.nextScreen = nextScreen;
    }
}
