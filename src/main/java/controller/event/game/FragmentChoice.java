package controller.event.game;

import org.javatuples.Pair;

import java.util.ArrayList;

public record FragmentChoice(
        ArrayList<ArrayList<Pair<Integer, Integer>>> fragments
) {
}
