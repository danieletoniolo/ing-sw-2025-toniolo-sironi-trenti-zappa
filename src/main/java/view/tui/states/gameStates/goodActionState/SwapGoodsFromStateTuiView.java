package view.tui.states.gameStates.goodActionState;

import org.javatuples.Pair;
import view.miniModel.MiniModel;
import view.miniModel.components.StorageView;
import view.miniModel.good.GoodView;
import view.tui.input.Command;
import view.tui.states.StateTuiView;

import java.util.ArrayList;

public class SwapGoodsFromStateTuiView extends GoodManagerStateTuiView {
    private final ArrayList<String> options = new ArrayList<>();


    public SwapGoodsFromStateTuiView() {
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

    @Override
    public StateTuiView internalViewState(Command command){
        switch (command.name()) {
            case "Swap":
                StringBuilder str = new StringBuilder();
                str.append(command.name()).append(" ");
                for (int i = 0; i < command.parameters().length - 1; i++) {
                    str.append(command.parameters()[i]).append(" ");
                }
                str.append(command.parameters()[command.parameters().length - 1]);

                int[] cont = new int[1];
                for (String option : options) {
                    if (option.equals(command.name())) {
                        break;
                    }
                    cont[0]++;
                }

                int[] ID = new int[1];
                GoodView[] good = new GoodView[1];
                MiniModel.getInstance().players.stream()
                        .filter(player -> player.getUsername().equals(MiniModel.getInstance().nickname))
                        .findFirst()
                        .ifPresent(player -> {
                            player.getShip().getMapStorages().forEach(
                                    (key, value) -> {
                                        int index = 0;
                                        for (int i = 0; i < ((StorageView) value).getGoods().length; i++) {
                                            if (cont[0] == 0) {
                                                ID[0] = key;
                                                good[0] = ((StorageView) value).getGoods()[index];
                                            }
                                            else{
                                                cont[0]--;
                                                index++;
                                            }
                                        }

                                    }
                            );
                        });
                from = new Pair<>(ID[0], good[0].getValue());

        }
        return null;
    }
}
