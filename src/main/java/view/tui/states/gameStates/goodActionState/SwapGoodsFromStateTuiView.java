package view.tui.states.gameStates.goodActionState;

import view.structures.MiniModel;
import view.structures.components.StorageView;
import view.tui.states.gameStates.GameStateTuiView;

import java.util.ArrayList;

public class SwapGoodsFromStateTuiView extends GameStateTuiView {
    private final ArrayList<String> options = new ArrayList<>();


    public SwapGoodsFromStateTuiView() {
        int[] index = new int[1];
        index[0] = 1;
        MiniModel.getInstance().players.stream()
                .filter(player -> player.getUsername().equals(MiniModel.getInstance().nickname))
                .findFirst()
                .ifPresent(player -> {
                    player.getShip().getMapStorages().forEach(
                            (key, value) -> {
                                if (((StorageView) value).getGoods() != null) {
                                    for (int i = 0; i < ((StorageView) value).getGoods().length; i++) {
                                        if (((StorageView) value).getGoods()[i] != null) {
                                            options.add("Swap " + ((StorageView) value).getGoods()[i].drawTui() + " from (" + value.getRow() + "," + value.getCol() + ")");
                                        }
                                    }
                                }

                            }
                    );
                });

    }

    @Override
    public ArrayList<String> getOptions() {
        return options;
    }
}
