package it.polimi.ingsw.view.tui.screens.gameScreens.engineActions;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.screens.CardsGame;

import java.util.List;

/**
 * Abstract class that manages engine and battery cards in the game.
 * Extends {@link CardsGame} to provide specific logic for engine actions.
 */
public abstract class ManagerEnginesCards extends CardsGame {
    /**
     * List of IDs representing the available engine cards.
     */
    protected static List<Integer> enginesIDs;

    /**
     * List of IDs representing the available battery cards.
     */
    protected static List<Integer> batteriesIDs;

    /**
     * Constructs a new ManagerEnginesCards with the given options.
     *
     * @param options the list of options for the card manager
     */
    public ManagerEnginesCards(List<String> options) {
        super(options);
    }

    /**
     * Destroys static references to engine and battery IDs and resets the spaceship view.
     * This method should be called to clean up static state when it is no longer needed.
     */
    public static void destroyStatics() {
        enginesIDs = null;
        batteriesIDs = null;
        PlayerDataView player = MiniModel.getInstance().getClientPlayer();
        spaceShipView = player == null ? null : player.getShip();
    }
}
