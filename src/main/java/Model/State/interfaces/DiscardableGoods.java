package Model.State.interfaces;

import Model.Good.Good;
import Model.Player.PlayerData;
import org.javatuples.Pair;

import java.util.ArrayList;

public interface DiscardableGoods {
    public void setGoodsToDiscard(PlayerData player, ArrayList<Pair<ArrayList<Good>, Integer>> goodsToDiscard);
}
