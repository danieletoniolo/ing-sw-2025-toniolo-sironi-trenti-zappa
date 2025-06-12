package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.spaceship.DestroyComponents;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.miniModel.spaceship.SpaceShipView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.validationScreens.RowAndColValidationTuiScreen;
import it.polimi.ingsw.view.tui.screens.validationScreens.WaitingValidationTuiScreen;
import org.javatuples.Pair;
import org.jline.terminal.Terminal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValidationTuiScreen implements TuiScreenView {
    protected ArrayList<String> options = new ArrayList<>();
    protected int totalLines;
    protected int selected;

    protected static SpaceShipView spaceShipView;
    protected static List<Pair<Integer, Integer>> destroyTiles;
    private String message;

    public ValidationTuiScreen() {
        if (spaceShipView == null) {
            spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
        }
        if (destroyTiles == null) {
            destroyTiles = new ArrayList<>();
        }

        options.add("Destroy a component");
        options.add("Cancel");
        options.add("Done");

        for (PlayerDataView p : MiniModel.getInstance().getOtherPlayers()) {
            options.add("View " + p.getUsername() + "'s spaceship");
        }
        options.add("Close program");

        totalLines = spaceShipView.getRowsToDraw() + 5;
    }

    @Override
    public void readCommand(Parser parser) throws Exception {
        selected = parser.getCommand(options, totalLines);
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.Validation;
    }

    @Override
    public TuiScreenView setNewScreen() {
        if ((selected < options.size() - 1) && (selected >= options.size() - 1 - MiniModel.getInstance().getOtherPlayers().size())) {
            int i = selected - (options.size() - MiniModel.getInstance().getOtherPlayers().size() - 1);
            return new PlayerTuiScreen(MiniModel.getInstance().getOtherPlayers().get(i), this);
        }

        if (selected == options.size() - 1) {
            return new ClosingProgram();
        }

        switch (selected) {
            case 0:
                return new RowAndColValidationTuiScreen(this);
            case 1:
                spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
                destroyTiles.clear();
                return this;
            case 2:
                StatusEvent status = DestroyComponents.requester(Client.transceiver, new Object()).request(
                        new DestroyComponents(MiniModel.getInstance().getUserID(), destroyTiles));
                // TODO: Check if destroyTiles.clear() is corrected here
                // TODO: there is a problem with the discard pile, because they do not appear
                destroyTiles.clear();
                if (status.get().equals("POTA")) {
                    setMessage(((Pota) status).errorMessage());
                    spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
                    return this;
                }
                status = EndTurn.requester(Client.transceiver, new Object()).request(
                        new EndTurn(MiniModel.getInstance().getUserID()));
                if (status.get().equals("POTA")) {
                    setMessage(((Pota) status).errorMessage());
                    spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
                    return this;
                }
                setMessage(null);
                spaceShipView = MiniModel.getInstance().getClientPlayer().getShip();

                boolean isSpaceShipValid = true;
                for (ComponentView[] row : spaceShipView.getSpaceShip()) {
                    for (ComponentView component : row) {
                        if (component != null && component.getIsWrong()) {
                            isSpaceShipValid = false;
                            break;
                        }
                    }

                    if (!isSpaceShipValid) {
                        break;
                    }
                }
                if (isSpaceShipValid) {
                    return new WaitingValidationTuiScreen();
                }
        }

        return this;
    }

    @Override
    public synchronized void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        int row = 1;

        int playerCount = 0;
        for (int i = 0; i < spaceShipView.getRowsToDraw(); i++) {
            StringBuilder line = new StringBuilder();
            line.append(spaceShipView.drawLineTui(i));

            if (i == 0) {
                line.append(" Discard pile: ");
            }
            else if (i <= ((spaceShipView.getRowsToDraw() - 2) / 5 + 1) - 1) {
                line.append(spaceShipView.getDiscardReservedPile().drawLineTui((i - 1) % ComponentView.getRowsToDraw()));
            }
            else if (i > ((spaceShipView.getRowsToDraw() - 2) / 5 * 4 + 1) - 1 && i <= ((spaceShipView.getRowsToDraw() - 2) / 5 * 4 + MiniModel.getInstance().getClientPlayer().getRowsToDraw())) {
                line.append("   ").append(MiniModel.getInstance().getClientPlayer().drawLineTui(playerCount));
                if (playerCount == 0) {
                    line.append("    ");
                }
                playerCount++;
            }
            TerminalUtils.printLine(writer, line.toString(), row++);
        }

        TerminalUtils.printLine(writer, "", row++);
        TerminalUtils.printLine(writer, message == null ? "" : message, row++);
        TerminalUtils.printLine(writer, "", row++);
        TerminalUtils.printLine(writer, "Commands: ", row);

        for (int i = totalLines + options.size(); i < terminal.getSize().getRows(); i++ ) {
            TerminalUtils.printLine(writer, "", i);
        }
    }

    @Override
    public void setNextScreen(TuiScreenView nextScreen) {

    }
}
