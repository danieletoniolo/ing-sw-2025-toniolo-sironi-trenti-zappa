package it.polimi.ingsw.view.tui.screens.buildingScreens;

import it.polimi.ingsw.event.game.clientToServer.placeTile.PlaceTileToSpaceship;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import org.javatuples.Pair;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.BuildingTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.TuiScreens;

import java.util.function.Supplier;

public class RowAndColTuiScreen extends BuildingTuiScreen {
    private Pair<Integer, Integer> rowAndCol;
    private final TuiScreens oldScreenType;

    public RowAndColTuiScreen(TuiScreens oldScreenType) {
        options.clear();
        this.oldScreenType = oldScreenType;
    }

    @Override
    public void readCommand(Parser parser, Supplier<Boolean> isStillCurrentScreen) throws Exception {
        rowAndCol = parser.getRowAndCol("Type coordinates (row col): ", totalLines, isStillCurrentScreen);
    }

    @Override
    public TuiScreenView setNewScreen() {
        if (oldScreenType.equals(TuiScreens.RowColShip)) {
            StatusEvent status = PlaceTileToSpaceship.requester(Client.transceiver, new Object())
                    .request(new PlaceTileToSpaceship(MiniModel.getInstance().getUserID(), rowAndCol.getValue0(), rowAndCol.getValue1()));
            if (status.get().equals("POTA")) {
                TuiScreenView newScreen = new BuildingTuiScreen();
                newScreen.setMessage("Problem Occurred, Try Again!");
                return newScreen;
            }
        }

        if (oldScreenType.equals(TuiScreens.RowColBoard)) {
            /*StatusEvent status = PickTileFromBoard.requester(Client.transceiver, new Object())
                    .request(new PickTileFromBoard(MiniModel.getInstance().getUserID(), rowAndCol.getValue0(), rowAndCol.getValue1()));
            if (status.get().equals("POTA")) {
                TuiScreenView newScreen = new BuildingTuiScreen();
                newScreen.setMessage("Problem Occurred, Try Again!");
                return newScreen;
            }*/
        }

        return new BuildingTuiScreen();
    }

    @Override
    public TuiScreens getType() {
        return oldScreenType;
    }
}
