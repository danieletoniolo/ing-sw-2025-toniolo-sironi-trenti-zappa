package it.polimi.ingsw.view.tui.screens.gameScreens.cannonsActions;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.screens.CardsGame;

import java.util.List;

/**
 * Abstract class that manages the cannon cards in the game.
 * Extends {@link CardsGame} and provides static management for cannon and battery IDs.
 */
public abstract class ManagerCannonsCards extends CardsGame {
    /**
     * List of IDs representing the cannons.
     */
    protected static List<Integer> cannonsIDs;

    /**
     * List of IDs representing the batteries.
     */
    protected static List<Integer> batteriesIDs;

    /**
     * Constructs a new ManagerCannonsCards with the given options.
     *
     * @param options the list of options for the cards game
     */
    public ManagerCannonsCards(List<String> options) {
        super(options);
    }

    /**
     * Destroys static references to cannon and battery IDs, and resets the player's spaceship view.
     */
    public static void destroyStatics() {
        cannonsIDs = null;
        batteriesIDs = null;
        PlayerDataView player = MiniModel.getInstance().getClientPlayer();
        spaceShipView = player == null ? null : player.getShip();
    }
}
