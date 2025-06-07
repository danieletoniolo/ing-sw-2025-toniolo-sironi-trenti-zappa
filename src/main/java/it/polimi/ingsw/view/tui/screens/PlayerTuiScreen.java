package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.ArrayList;
import java.util.List;

public class PlayerTuiScreen implements TuiScreenView {
    private final ArrayList<String> options = new ArrayList<>(List.of("Back"));
    private final int totalLines;
    private final TuiScreenView oldScreen;
    protected String message;
    protected boolean isNewScreen;

    private final PlayerDataView playerToView;

    public PlayerTuiScreen(PlayerDataView playerToView, TuiScreenView oldScreen) {
        this.playerToView = playerToView;
        totalLines = playerToView.getShip().getRowsToDraw() + 3 + 2;
        this.oldScreen = oldScreen;
        isNewScreen = true;
    }

    public PlayerDataView getPlayerToView() {
        return playerToView;
    }

    @Override
    public void readCommand(Parser parser) throws Exception {
        parser.getCommand(options, totalLines);
    }

    @Override
    public TuiScreenView setNewScreen() {
        return oldScreen;
    }

    @Override
    public void printTui(org.jline.terminal.Terminal terminal) {
        var writer = terminal.writer();
        int row = 1;

        int countPlayer = 0;
        for (int i = 0; i < playerToView.getShip().getRowsToDraw(); i++) {
            StringBuilder line = new StringBuilder();
            if (i >= ((playerToView.getShip().getRowsToDraw() - 2)/5*4 + 1) && i < ((playerToView.getShip().getRowsToDraw() - 2)/5*4 + 1) + playerToView.getRowsToDraw()) {
                line.append(playerToView.getShip().drawLineTui(i)).append("   ").append(playerToView.drawLineTui(countPlayer));
                countPlayer++;
            }else{
                line.append(playerToView.getShip().drawLineTui(i));
            }
            TerminalUtils.printLine(writer, line.toString(), row++);
        }

        TerminalUtils.printLine(writer, "", row++);
        TerminalUtils.printLine(writer, message == null ? "" : message, row++);
        TerminalUtils.printLine(writer, "", row++);
        TerminalUtils.printLine(writer, "Commands:", row);

        if (isNewScreen) {
            isNewScreen = false;
            for (int i = totalLines + options.size(); i < terminal.getSize().getRows(); i++ ) {
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
        return TuiScreens.Player;
    }

    @Override
    public void setNextScreen(TuiScreenView nextScreen) {

    }
}
