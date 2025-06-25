package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.BoardView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.ArrayList;
import java.util.List;

public class Reward implements TuiScreenView {
    private final ArrayList<String> options = new ArrayList<>();
    private final int totalLines;
    protected int selected;
    private String message;
    private boolean isNewScreen;
    private TuiScreenView nextScreen;

    private final BoardView boardView = MiniModel.getInstance().getBoardView();
    private final List<PlayerDataView> sortedPlayers;

    public Reward() {
        String command = switch (MiniModel.getInstance().getRewardPhase()) {
            case 0 -> "Claim coins for finish position";
            case 1 -> "Claim coins for best looking ship";
            case 2 -> "Sell goods for coins";
            case 3 -> "Loose coins for losses";
            case 4 -> "Leave the game";
            default -> throw new IllegalStateException("Unexpected value: " + MiniModel.getInstance().getRewardPhase());
        };

        options.add(command);

        options.add("View your spaceship");
        for (PlayerDataView p : MiniModel.getInstance().getOtherPlayers()) {
            options.add("View " + p.drawLineTui(0) + "'s spaceship");
        }
        options.add("Close program");

        this.isNewScreen = true;

        totalLines = 6;
        sortedPlayers = new ArrayList<>();

        sortedPlayers.add(MiniModel.getInstance().getClientPlayer());
        sortedPlayers.addAll(MiniModel.getInstance().getOtherPlayers());
        sortedPlayers.sort((p1, p2) -> Integer.compare(p2.getCoins(), p1.getCoins()));

        if (MiniModel.getInstance().getRewardPhase() == 0) {
            setMessage("Game is over! Let's discover who is the winner.");
        }
        else {
            StringBuilder mess = new StringBuilder();
            int cont = 0;
            for (PlayerDataView p : sortedPlayers) {
                if (p.getCoins() > 0) {
                    if (cont == 0) {
                        mess.append(p.drawLineTui(0));
                        cont++;
                    }
                    else {
                        mess.append(", ").append(p.drawLineTui(0));
                    }
                }
            }
            mess.append(" ").append(cont == 0 ? "is" : "are").append(" winnig");
        }
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
            // Send a request to change the reward phase
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
