package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.BoardView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.ArrayList;
import java.util.List;

public abstract class Reward implements TuiScreenView {
    private final ArrayList<String> options = new ArrayList<>();
    protected int totalLines;
    protected int selected;
    private String message;
    private boolean isNewScreen;
    protected TuiScreenView nextScreen;

    private final BoardView boardView = MiniModel.getInstance().getBoardView();
    protected final List<PlayerDataView> sortedPlayers;

    public Reward(List<String> otherOptions) {
        if (otherOptions != null && !otherOptions.isEmpty()) options.addAll(otherOptions);

        options.add("View your spaceship");
        for (PlayerDataView p : MiniModel.getInstance().getOtherPlayers()) {
            options.add("View " + p.drawLineTui(0) + "'s spaceship");
        }
        options.add("Close program");

        this.isNewScreen = true;

        sortedPlayers = new ArrayList<>();

        sortedPlayers.add(MiniModel.getInstance().getClientPlayer());
        sortedPlayers.addAll(MiniModel.getInstance().getOtherPlayers());
        sortedPlayers.sort((p1, p2) -> Integer.compare(p2.getCoins(), p1.getCoins()));

        totalLines = boardView.getRowsToDraw() + 1 + sortedPlayers.size() + 5;
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

        return null;
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

        for (int i = 0; i < boardView.getRowsToDraw(); i++) {
            newLines.add(boardView.drawLineTui(i));
        }

        newLines.add("");

        for (PlayerDataView p : sortedPlayers) {
            newLines.add(p.drawLineTui(0) + ": " + p.getCoins() + " coins");
        }

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
