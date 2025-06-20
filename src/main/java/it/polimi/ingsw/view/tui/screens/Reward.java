package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.ArrayList;
import java.util.List;

public class Reward implements TuiScreenView {
    private final ArrayList<String> options;
    private final int totalLines;
    private int selected;
    private String message;
    private boolean isNewScreen;
    private TuiScreenView nextScreen;

    public Reward() {
        options = new ArrayList<>();

        options.add("Claim rewards");
        options.add("View your spaceship");
        for (PlayerDataView p : MiniModel.getInstance().getOtherPlayers()) {
            options.add("View " + p.drawLineTui(0) + "'s spaceship");
        }
        options.add("Close program");

        this.isNewScreen = true;

        totalLines = 3;
    }

    @Override
    public void readCommand(Parser parser) {
        selected = parser.getCommand(options, totalLines);
    }

    @Override
    public TuiScreenView setNewScreen() {
        if (selected == options.size() - 2 - MiniModel.getInstance().getOtherPlayers().size()) {
            return new OtherPlayer(MiniModel.getInstance().getClientPlayer(), this);
        }

        if ((selected < options.size() - 1) && (selected >= options.size() - 1 - MiniModel.getInstance().getOtherPlayers().size())) {
            int i = selected - (options.size() - MiniModel.getInstance().getOtherPlayers().size() - 1);

            return new OtherPlayer(MiniModel.getInstance().getOtherPlayers().get(i), this);
        }

        if (selected == options.size() - 1) {
            return new ClosingProgram();
        }

        if (selected == 0) {
            StatusEvent status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return this;
            }
            return nextScreen;
        }

        return this;
    }

    @Override
    public synchronized void setMessage(String message) {
        this.message = message;
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.Reward;
    }

    @Override
    public void setNextScreen(TuiScreenView nextScreen) {
        this.nextScreen = nextScreen;
    }

    @Override
    public void printTui() {
        List<String> newLines = new ArrayList<>();

        newLines.add("Game is over!");
        newLines.add("You can now view the rewards of the game.");

        newLines.add("");
        newLines.add(message == null ? "" : message);
        newLines.add("");
        newLines.add(lineBeforeInput());

        TerminalUtils.printScreen(newLines, totalLines + options.size());

        if (isNewScreen) {
            isNewScreen = false;
            TerminalUtils.clearLastLines(totalLines + options.size());
        }
    }

    private String lineBeforeInput() {
        return "Select an option:";
    }
}
