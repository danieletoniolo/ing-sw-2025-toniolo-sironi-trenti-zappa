package it.polimi.ingsw.view.tui.screens.buildingScreens;

import it.polimi.ingsw.event.game.clientToServer.pickTile.PickTileFromBoard;
import it.polimi.ingsw.event.game.clientToServer.placeTile.PlaceTileToSpaceship;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.tui.screens.TuiScreens;
import org.javatuples.Pair;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.BuildingTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

public class RowAndColTuiScreen extends BuildingTuiScreen {
    private Pair<Integer, Integer> rowAndCol;
    private final int num;

    public RowAndColTuiScreen(int num) {
        super(null);
        options.clear();
        this.num = num;
    }

    @Override
    public void readCommand(Parser parser) throws Exception {
        rowAndCol = parser.getRowAndCol("Type coordinates (row col): ", totalLines);
    }

    @Override
    public TuiScreenView setNewScreen() {
        if (num == 0) {
            StatusEvent status = PlaceTileToSpaceship.requester(Client.transceiver, new Object())
                    .request(new PlaceTileToSpaceship(MiniModel.getInstance().getUserID(), rowAndCol.getValue0() - 1, rowAndCol.getValue1() - 1));
            if (status.get().equals("POTA")) {
                TuiScreenView newScreen = new MainCommandsTuiScreen();
                newScreen.setMessage(((Pota) status).errorMessage());
                return newScreen;
            }
        }

        if (num == 1) {
            int ID = MiniModel.getInstance().getViewableComponents().stream()
                    .skip(((long) rowAndCol.getValue0() * rowAndCol.getValue1()) - 1)
                    .map(ComponentView::getID)
                    .findFirst()
                    .orElse(-1);

            if (ID != -1) {
                StatusEvent status = PickTileFromBoard.requester(Client.transceiver, new Object())
                        .request(new PickTileFromBoard(MiniModel.getInstance().getUserID(), ID));
                if (status.get().equals("POTA")) {
                    TuiScreenView newScreen = new MainCommandsTuiScreen();
                    newScreen.setMessage(((Pota) status).errorMessage());
                    return newScreen;
                }
            }
            else {
                setMessage("Not valid coordinates");
            }
        }

        return new MainCommandsTuiScreen();
    }
}
