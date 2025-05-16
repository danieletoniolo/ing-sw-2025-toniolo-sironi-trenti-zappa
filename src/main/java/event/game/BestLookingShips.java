package event.game;

import event.Event;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents the best looking ships in the game.
 * @param usersIDs The list of users' username with the best looking ships
 */
public record BestLookingShips(
        ArrayList<String> usersIDs
) implements Event, Serializable {
}
