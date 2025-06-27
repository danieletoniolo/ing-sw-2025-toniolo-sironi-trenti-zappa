package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.exchangeGoods;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.components.StorageView;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.MenuGoodsCards;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class that manages the exchange of goods cards in the TUI game screens.
 * Handles the static state for exchanges, goods to get/leave, storage, and remaining copies.
 * Extends {@link CardsGame}.
 */
public abstract class ManagerExchangeGoodsCards extends CardsGame {
    /**
     * List of exchanges, each represented as a Triplet containing:
     * - List of goods to get (indices)
     * - List of goods to leave (indices)
     * - An integer representing the exchange id or other property
     */
    protected static List<Triplet<List<Integer>, List<Integer>, Integer>> exchanges;

    /**
     * List of indices representing goods to get in the current exchange.
     */
    protected static List<Integer> goodsToGet;

    /**
     * List of indices representing goods to leave in the current exchange.
     */
    protected static List<Integer> goodsToLeave;

    /**
     * Reference to the player's storage view.
     */
    protected static StorageView storage;

    /**
     * List of remaining copies of goods available for exchange.
     */
    protected static List<GoodView> remainCopy;

    /**
     * Constructs a ManagerExchangeGoodsCards with the given options.
     * Initializes static lists if they are null.
     *
     * @param options the list of options for the cards game
     */
    public ManagerExchangeGoodsCards(List<String> options) {
        super(options);

        if (exchanges == null) {
            exchanges = new ArrayList<>();
        }
        if (goodsToGet == null) {
            goodsToGet = new ArrayList<>();
        }
        if (goodsToLeave == null) {
            goodsToLeave = new ArrayList<>();
        }
        if (remainCopy == null) {
            remainCopy = new ArrayList<>(MenuGoodsCards.getCopy());
        }
    }

    /**
     * Destroys all static fields, resetting them to null.
     * Also resets the player's spaceship view.
     */
    public static void destroyStatics() {
        exchanges = null;
        goodsToGet = null;
        goodsToLeave = null;
        storage = null;
        remainCopy = null;
        PlayerDataView player = MiniModel.getInstance().getClientPlayer();
        spaceShipView = player == null ? null : player.getShip();
    }
}
