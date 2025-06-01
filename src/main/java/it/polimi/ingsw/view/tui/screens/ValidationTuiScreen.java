package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.miniModel.spaceship.SpaceShipView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.gameScreens.NotClientTurnTuiScreen;
import it.polimi.ingsw.view.tui.screens.validationScreens.RowAndColValidationTuiScreen;
import org.javatuples.Pair;
import org.jline.terminal.Terminal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ValidationTuiScreen implements TuiScreenView {
    protected ArrayList<String> options = new ArrayList<>();
    protected Pair<Integer, Integer> rowAndCol;
    protected int totalLines;
    private int selected;
    protected static SpaceShipView spaceShipView;
    protected static List<Integer> tileIDs;
    private boolean isNewScreen;
    private String message;

    public ValidationTuiScreen() {
        if (spaceShipView == null) {
            spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
        }
        if (tileIDs == null) {
            tileIDs = new ArrayList<>();
        }

        options.add("Destroy a component");
        options.add("Revert Changes");
        options.add("Done");

        for (PlayerDataView p : MiniModel.getInstance().getOtherPlayers()) {
            options.add("View " + p.getUsername() + "'s spaceship");
        }

        isNewScreen = true;
        totalLines = spaceShipView.getRowsToDraw() + 5;
    }


    @Override
    public void readCommand(Parser parser, Supplier<Boolean> isStillCurrentScreen) throws Exception {
        selected = parser.getCommand(options, totalLines, isStillCurrentScreen);
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.Validation;
    }

    @Override
    public TuiScreenView setNewScreen() {
        if (selected >= options.size() - MiniModel.getInstance().getOtherPlayers().size()) {
            int i = selected - (options.size() - MiniModel.getInstance().getOtherPlayers().size());
            return new PlayerTuiScreen(MiniModel.getInstance().getOtherPlayers().get(i), this);
        }

        if (selected == 0) {
            return new RowAndColValidationTuiScreen();
        }
        if (selected == 1) {
            spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
            tileIDs.clear();
            return this;
        }

        if (selected == 2) {
            /*StatusEvent status = Evento;
            if (status.get().equals("POTA")) {
                setMessage("Problem Occurred, Try Again!");
                spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
                tileIDs.clear();
                return this;
            }
            spaceShipView = null;
            tileIDs = null;*/
            return new NotClientTurnTuiScreen();
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

        if (isNewScreen) {
            isNewScreen = false;
            for (int i = totalLines + options.size(); i < terminal.getSize().getRows(); i++ ) {
                TerminalUtils.printLine(writer, "", i);
            }
        }
    }
}
