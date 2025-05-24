package view.tui.states.gameScreens.planetsActions;

import view.miniModel.MiniModel;
import view.miniModel.components.StorageView;
import view.miniModel.good.GoodView;
import view.tui.states.TuiScreenView;
import view.tui.states.gameScreens.PlanetsTuiScreen;

import java.lang.reflect.Array;
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
