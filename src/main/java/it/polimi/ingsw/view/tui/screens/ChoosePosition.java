package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.player.PlaceMarker;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.BoardView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.buildingScreens.MainBuilding;
import it.polimi.ingsw.view.tui.screens.buildingScreens.WatchingBuilding;

import java.util.ArrayList;
import java.util.List;

public class ChoosePosition implements TuiScreenView {
    private final ArrayList<String> options = new ArrayList<>();
    private boolean isNewScreen;

    private final int totalLines;
    private TuiScreenView nextScreen;

    private final BoardView boardView = MiniModel.getInstance().getBoardView();
    private int selected;
    private String message;

    public ChoosePosition() {
        options.add("1");
        options.add("2");
        options.add("3");
        options.add("4");
        if (MiniModel.getInstance().getTimerView().getNumberOfFlips() != MiniModel.getInstance().getTimerView().getTotalFlips()) {
            options.add("Back");
        }

        totalLines = MiniModel.getInstance().getBoardView().getRowsToDraw() + 5;

        this.isNewScreen = true;
    }

    @Override
    public void readCommand(Parser parser) {
        selected = parser.getCommand(options, totalLines);
    }

    @Override
    public TuiScreenView setNewScreen() {
        if (selected == 4) {
            return new MainBuilding();
        }

        // Validate the selected position
        StatusEvent status = PlaceMarker.requester(Client.transceiver, new Object()).request(new PlaceMarker(MiniModel.getInstance().getUserID(), selected));
        if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }

        // If the position is valid, end the turn
        status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
        if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }

        if (nextScreen == null) {
            return new WatchingBuilding();
        }
        return nextScreen;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void printTui() {
        List<String> newLines = new ArrayList<>();

        for (int i = 0; i < boardView.getRowsToDraw(); i++) {
            newLines.add(boardView.drawLineTui(i));
        }

        newLines.add("");
        newLines.add(message == null ? "" : message);
        newLines.add("");
        newLines.add("Choose your starting position: ");

        TerminalUtils.printScreen(newLines, totalLines + options.size());

        if (isNewScreen) {
            isNewScreen = false;
            TerminalUtils.clearLastLines(totalLines + options.size());
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
