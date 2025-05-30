package it.polimi.ingsw.view.tui.screens.gameScreens.planetsActions;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.components.StorageView;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.PlanetsTuiScreen;

import java.util.ArrayList;

public class DropGoodsTuiScreen extends PlanetsTuiScreen {
    private ArrayList<GoodView> goods;

    public DropGoodsTuiScreen() {
        super(new ArrayList<>() {{
            ArrayList<String> totalGoods = new ArrayList<>();
            MiniModel.getInstance().getClientPlayer().getShip().getMapStorages().forEach(
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

        if (selected == options.size() - MiniModel.getInstance().getOtherPlayers().size() - 1) {
            // send goods to the server
            return new PlanetActionsTuiScreen();
        }

        int cont = 0;
        MiniModel.getInstance().getClientPlayer().getShip().getMapStorages().forEach(
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
