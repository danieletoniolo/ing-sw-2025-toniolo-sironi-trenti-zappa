package Model.State.interfaces;

import Model.Player.PlayerData;
import org.javatuples.Pair;

import java.util.ArrayList;

public interface DestroyableComponent {
    public void setComponentToDestroy(PlayerData player, ArrayList<Pair<Integer, Integer>> componentsToDestroy);
}
