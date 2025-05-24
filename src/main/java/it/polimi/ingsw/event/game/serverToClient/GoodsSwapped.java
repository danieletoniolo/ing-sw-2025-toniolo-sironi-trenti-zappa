package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents the swap of goods between two storages.
 * @param nickname   is the user username when the event is sent to the other client.
 * @param storageID1 is the ID of the first storage.
 * @param storageID2 is the ID of the second storage.
 * @param goods1to2  is the list of goods to swap from storage 1 to storage 2.
 * @param goods2to1  is the list of goods to swap from storage 2 to storage 1.
 */
public record GoodsSwapped(
        String nickname,
        int storageID1,
        int storageID2,
        List<Integer> goods1to2,
        List<Integer> goods2to1
) implements Event, Serializable {}
