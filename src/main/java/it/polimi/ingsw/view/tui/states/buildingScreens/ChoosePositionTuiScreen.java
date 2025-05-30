package it.polimi.ingsw.view.tui.states.buildingScreens;

import it.polimi.ingsw.event.game.clientToServer.PlaceMarker;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.BoardView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.states.TuiScreenView;
import it.polimi.ingsw.view.tui.states.TuiScreens;
import org.jline.terminal.Terminal;

import java.util.ArrayList;
import java.util.function.Supplier;

public class ChoosePositionTuiScreen implements TuiScreenView {
    private ArrayList<String> options = new ArrayList<>();
    private int totalLines;

    private BoardView boardView = MiniModel.getInstance().getBoardView();
    private int selected;
    private String message;
    private boolean isNewScreen;

    public ChoosePositionTuiScreen() {
        options.add("1");
        options.add("2");
        options.add("3");
        options.add("4");

        totalLines = MiniModel.getInstance().getBoardView().getRowsToDraw() + 5;
        isNewScreen = true;
    }

    @Override
    public void readCommand(Parser parser, Supplier<Boolean> isStillCurrentScreen) throws Exception {
        selected = parser.getCommand(options, totalLines, isStillCurrentScreen);
    }

    @Override
    public TuiScreenView setNewScreen() {

        StatusEvent status = PlaceMarker.requester(Client.transceiver, new Object()).request(new PlaceMarker(MiniModel.getInstance().getUserID(), selected));
        if (status.get().equals("POTA")) {
            setMessage("Can't place the marker there! Pick a different spot.");
            return this;
        }

        return new WatchingTuiScreen();
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
}
