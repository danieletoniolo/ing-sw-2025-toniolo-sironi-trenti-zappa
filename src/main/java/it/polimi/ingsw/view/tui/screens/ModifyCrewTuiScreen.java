package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.miniModel.spaceship.SpaceShipView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;
import org.jline.terminal.Terminal;

import java.util.ArrayList;
import java.util.function.Supplier;

public class ModifyCrewTuiScreen implements TuiScreenView{
    protected ArrayList<String> options;
    protected int totalLines;
    protected int selected;
    protected String message;

    private final SpaceShipView spaceShipView = MiniModel.getInstance().getClientPlayer().getShip();
    private final PlayerDataView clientPlayer = MiniModel.getInstance().getClientPlayer();

    public ModifyCrewTuiScreen() {
        options = new ArrayList<>();

        options.addAll(spaceShipView.getMapCabins().values().stream()
                .map(cabin -> "(" + cabin.getRow() + " " + cabin.getCol() + ")")
                .toList());

        totalLines = spaceShipView.getMapCabins().size() + 4;
    }

    @Override
    public TuiScreenView setNewScreen() {
        return null;
    }

    @Override
    public void readCommand(Parser parser, Supplier<Boolean> isStillCurrentScreen) throws Exception {
        selected = parser.getCommand(options, totalLines, isStillCurrentScreen);
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.AddCrew;
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
            else if (i > ((spaceShipView.getRowsToDraw() - 2) / 5 * 4 + 1) - 1 && i <= ((spaceShipView.getRowsToDraw() - 2) / 5 * 4 + clientPlayer.getRowsToDraw())) {
                line.append("   ").append(clientPlayer.drawLineTui(playerCount));
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
        TerminalUtils.printLine(writer, lineBeforeInput(), row);

        for (int i = totalLines + options.size(); i < terminal.getSize().getRows(); i++ ) {
            TerminalUtils.printLine(writer, "", i);
        }
    }

    protected String lineBeforeInput() {
        return "Commands";
    }
}
