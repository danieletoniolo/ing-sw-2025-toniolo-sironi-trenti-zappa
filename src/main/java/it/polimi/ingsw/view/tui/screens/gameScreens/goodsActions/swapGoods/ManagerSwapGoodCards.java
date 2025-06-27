package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.swapGoods;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.components.StorageView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.screens.CardsGame;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract manager class for handling the swap of good cards between storages in the TUI game.
 * Maintains static references to the source and destination storages and their respective lists of goods.
 * Extends {@link CardsGame}.
 */
public abstract class ManagerSwapGoodCards extends CardsGame {
    /**
     * The storage from which goods are being swapped.
     */
    protected static StorageView fromStorage;

    /**
     * The list of good indices from the source storage.
     */
    protected static ArrayList<Integer> fromList;

    /**
     * The storage with which goods are being swapped.
     */
    protected static StorageView withStorage;

    /**
     * The list of good indices from the destination storage.
     */
    protected static ArrayList<Integer> withList;

    /**
     * Constructs a ManagerSwapGoodCards with the given options.
     * Initializes the static lists if they are null.
     *
     * @param options the list of options for the card game screen
     */
    public ManagerSwapGoodCards(List<String> options) {
        super(options);

        if (fromList == null) {
            fromList = new ArrayList<>();
        }
        if (withList == null) {
            withList = new ArrayList<>();
        }
    }

    /**
     * Destroys and resets all static references used for swapping goods.
     * Also resets the player's spaceship view.
     */
    public static void destroyStatics() {
        fromList = null;
        fromStorage = null;
        withList = null;
        withStorage = null;
        PlayerDataView player = MiniModel.getInstance().getClientPlayer();
        spaceShipView = player == null ? null : player.getShip();
    }
}
