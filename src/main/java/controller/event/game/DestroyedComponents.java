package controller.event.game;

import Model.Player.PlayerColor;
import org.javatuples.Pair;

import java.util.ArrayList;

public record DestroyedComponents(
        PlayerColor player,
        ArrayList<Pair<Integer, Integer>> destroyedComponents
) {
}
