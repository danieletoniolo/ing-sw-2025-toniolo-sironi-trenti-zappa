package view.tui.states.gameStates.goodActionState;

import view.tui.states.gameStates.GameStateTuiView;

import java.util.ArrayList;

public class GoodManagerStateTuiView extends GameStateTuiView {
    private final ArrayList<String> options = new ArrayList<>();

    public GoodManagerStateTuiView() {
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
