package view.tui.states.gameStates.goodActionState;

import view.structures.MiniModel;
import view.structures.components.StorageView;
import view.structures.good.GoodView;
import view.tui.states.gameStates.GameStateView;

import java.util.ArrayList;

public class SwapGoodsFromStateView extends GameStateView {
    private final ArrayList<String> options = new ArrayList<>();

    public SwapGoodsFromStateView() {
        int[] index = new int[1];
        index[0] = 1;
        MiniModel.getInstance().players.stream()
                .filter(player -> player.getUsername().equals(MiniModel.getInstance().nickname))
                .findFirst()
                .ifPresent(player -> {
                    player.getShip().getMapStorages().forEach(
                            (key, value) -> {
                                options.add("Storage " + index[0]);
                                for (GoodView good : ((StorageView) value).getGoods()) {
                                    options.add("\tGood " + good.drawTui());
                                }
                                index[0]++;
                            }
                    );
                });

    }

    @Override
    public ArrayList<String> getOptions() {
        return options;
    }
}
