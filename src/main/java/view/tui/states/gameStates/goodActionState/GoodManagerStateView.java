package view.tui.states.gameStates.goodActionState;

import view.tui.states.gameStates.GameStateView;

import java.util.ArrayList;

public class GoodManagerStateView extends GameStateView {
    private final ArrayList<String> options = new ArrayList<>();

    public GoodManagerStateView() {
        options.add("Swap goods");
        options.add("Take good from the card");
        options.add("Drop good from the ship");
        options.add("Done");
    }

    @Override
    public ArrayList<String> getOptions() {
        return options;
    }
}
