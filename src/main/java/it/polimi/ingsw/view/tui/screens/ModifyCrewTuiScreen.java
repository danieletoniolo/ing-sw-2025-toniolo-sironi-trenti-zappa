package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.miniModel.spaceship.SpaceShipView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.crewScreens.AddCrewTuiScreen;
import org.jline.terminal.Terminal;

import java.util.ArrayList;
import java.util.function.Supplier;

public class ModifyCrewTuiScreen implements TuiScreenView{
    protected ArrayList<String> options;
    protected int totalLines;
    protected int selected;
    protected String message;

    protected static int typeOfOption;

    protected final SpaceShipView spaceShipView = MiniModel.getInstance().getClientPlayer().getShip();
    protected final PlayerDataView clientPlayer = MiniModel.getInstance().getClientPlayer();

    public ModifyCrewTuiScreen() {
        options = new ArrayList<>();

        options.add("Add human");
        options.add("Add brown alien");
        options.add("Add purple alien");
        options.add("Remove crew from cabin");
        for (PlayerDataView p : MiniModel.getInstance().getOtherPlayers()) {
            options.add("View " + p.getUsername() + "'s spaceship");
        }
        options.add("Close program");

        totalLines = spaceShipView.getMapCabins().size() + 4;
    }

    @Override
    public void readCommand(Parser parser, Supplier<Boolean> isStillCurrentScreen) throws Exception {
        selected = parser.getCommand(options, totalLines, isStillCurrentScreen);
        typeOfOption = selected;
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

        return new AddCrewTuiScreen(this);
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.ModifyCrew;
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
