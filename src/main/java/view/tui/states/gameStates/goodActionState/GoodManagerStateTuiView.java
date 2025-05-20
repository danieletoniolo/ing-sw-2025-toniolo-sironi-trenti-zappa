package view.tui.states.gameStates.goodActionState;

import org.javatuples.Pair;
import view.tui.input.Command;
import view.tui.states.StateTuiView;
import view.tui.states.gameStates.GameStateTuiView;

import java.util.ArrayList;

public class GoodManagerStateTuiView extends GameStateTuiView {
    private final ArrayList<String> options = new ArrayList<>();

    // First integer is the ID, second integer is the type of the good
    protected Pair<Integer, Integer> from;
    protected Pair<Integer, Integer> to;

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

    @Override
    public StateTuiView internalViewState(Command command) {
        switch (command.name()) {
            case "Swap":
                return new SwapGoodsFromStateTuiView();
        }
        return null;
    }
}
