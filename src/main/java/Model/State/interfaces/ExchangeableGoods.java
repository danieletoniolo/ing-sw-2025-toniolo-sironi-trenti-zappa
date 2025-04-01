package Model.State.interfaces;

import Model.Good.Good;
import Model.Player.PlayerData;
import org.javatuples.Triplet;

import java.util.ArrayList;

public interface ExchangeableGoods {
    /**
     * This method is used to exchange goods between the player and the station.
     * The actual exchange is done when the execute method is called.
     * @param player player that wants to exchange the goods
     * @param exchangeData contains an arraylist of triplets, each triplet contains (in this order) the goods that the player wants to get, the good that the player wants to leave and the storage ID
     * @apiNote The control of if the exchange is valid or not must be done from the caller.
     */
    void setGoodsToExchange(PlayerData player, ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData) throws IllegalStateException;

}
