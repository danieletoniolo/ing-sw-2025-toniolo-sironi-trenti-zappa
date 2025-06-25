package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.ArrayList;
import java.util.List;

public class OtherPlayer implements TuiScreenView {
    private final ArrayList<String> options = new ArrayList<>(List.of("Back"));
    private boolean isNewScreen;

    private final int totalLines;
    private final TuiScreenView oldScreen;
    protected String message;
    private TuiScreenView nextScreen;

    private final PlayerDataView playerToView;

    public OtherPlayer(PlayerDataView playerToView, TuiScreenView oldScreen) {
        this.playerToView = playerToView;
        totalLines = playerToView.getShip().getRowsToDraw() + 3 + 2;
        this.oldScreen = oldScreen;
        this.isNewScreen = true;
    }

    public PlayerDataView getPlayerToView() {
        return playerToView;
    }

    @Override
    public void readCommand(Parser parser) {
        parser.getCommand(options, totalLines);
    }

    @Override
    public TuiScreenView setNewScreen() {
        if (nextScreen != null) {
            return nextScreen;
        }
        return oldScreen;
    }

    @Override
    public void printTui() {
        List<String> newLines = new ArrayList<>();

        int playerCount = 0;
        for (int i = 0; i < playerToView.getShip().getRowsToDraw(); i++) {
            StringBuilder line = new StringBuilder();
            line.append(playerToView.getShip().drawLineTui(i));

            if (i <= playerToView.getShip().getDiscardReservedPile().getRowsToDraw()) {
                line.append(playerToView.getShip().getDiscardReservedPile().drawLineTui(i));
            }
            else if (i > ((playerToView.getShip().getRowsToDraw() - 2) / 5 * 3 + 1) - 1 && i <= ((playerToView.getShip().getRowsToDraw() - 2) / 5 * 3 + playerToView.getRowsToDraw())) {
                line.append("   ").append(playerToView.drawLineTui(playerCount));
                if (playerCount == 0) {
                    line.append("    ");
                }
                playerCount++;
            }
            newLines.add(line.toString());
        }

        newLines.add("");
        newLines.add(message == null ? "" : message);
        newLines.add("");
        newLines.add("Commands:");

        TerminalUtils.printScreen(newLines, totalLines + options.size());

        if (isNewScreen) {
            isNewScreen = false;
            TerminalUtils.clearLastLines(totalLines + options.size());
        }
    }

    @Override
    public synchronized void setMessage(String message) {
        this.message = message;
        oldScreen.setMessage(message);
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.OtherPlayer;
    }

    @Override
    public void setNextScreen(TuiScreenView nextScreen) {
        this.nextScreen = nextScreen;
    }
}
