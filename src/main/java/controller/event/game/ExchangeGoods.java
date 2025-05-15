package controller.event.game;

import Model.Good.Good;
import Model.Player.PlayerColor;
import controller.event.Event;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This event is used when a player have to exchange goods between two storages.
 * @param userID is the user username when the event is sent to the other client.
 *               The userID is the UUID when the event is sent from the client to the server.
 *               In this way other client cannot fake to be another client, because the UUID is known only by the correct client
 * @param storageID1 The ID of the first storage
 * @param storageID2 The ID of the second storage
 * @param goods1to2 The list of goods that the player has to exchange from the first storage to the second storage
 * @param goods2to1 The list of goods that the player has to exchange from the second storage to the first storage
 * */
public record ExchangeGoods(
        String userID,
        int storageID1,
        int storageID2,
        ArrayList<Good> goods1to2,
        ArrayList<Good> goods2to1
) implements Event, Serializable {
}
