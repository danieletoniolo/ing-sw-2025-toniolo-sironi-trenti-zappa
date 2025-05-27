package it.polimi.ingsw.view.tui.states.gameScreens.planetsActions;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.components.StorageView;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.tui.states.TuiScreenView;
import it.polimi.ingsw.view.tui.states.gameScreens.PlanetsTuiScreen;

import java.util.ArrayList;

public class DropGoodsTuiScreen extends PlanetsTuiScreen {
    private ArrayList<GoodView> goods;

    public DropGoodsTuiScreen() {
        super(new ArrayList<>() {{
            ArrayList<String> totalGoods = new ArrayList<>();
            MiniModel.getInstance().clientPlayer.getShip().getMapStorages().forEach(
                    (key, value) -> {
                        for (GoodView good :((StorageView) value).getGoods()) {
                            totalGoods.add(good.drawTui() + " from " + "(" + value.getRow() + "," + value.getCol() + ")");
                        }
                    }
            );
            add("Done");
        }});
        goods = new ArrayList<>();
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (selected == options.size() - MiniModel.getInstance().otherPlayers.size() - 1) {
            // send goods to the server
            return new PlanetActionsTuiScreen();
        }

        int cont = 0;
        MiniModel.getInstance().clientPlayer.getShip().getMapStorages().forEach(
                (key, value) -> {
                    for (GoodView good : ((StorageView) value).getGoods()) {
                        if (cont == selected) {
                            goods.add(good);
                            break;
                        }
                    }
                }
        );

        return this;
    }
}
