package it.polimi.ingsw.view.tui.screens.gameScreens.looseScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.spaceship.SetPenaltyLoss;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.*;

/**
 * Screen for handling the loss of goods cards in the game.
 * Allows the player to select storage units to discard the most expensive goods,
 * sends the loss to the server, and manages turn ending.
 */
public class LooseGoodsCards extends CardsGame {
    /** List of storage IDs selected for discarding goods. */
    private static List<Integer> storageIDs;
    /** List of goods values to be discarded. */
    private static List<Integer> looseGoods;
    /** Flag to reset the screen state. */
    private static boolean reset;

    /**
     * Constructs the LooseGoodsCards screen, initializing the list of selectable storage units.
     */
    public LooseGoodsCards() {
        super(new ArrayList<>(){{
            if (!reset) {
                spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
            }
            if (!reset) reset = true;
            spaceShipView.getMapStorages().values().stream()
                    .filter(storage -> java.util.Arrays.stream(storage.getGoods()).anyMatch(Objects::nonNull))
                    .forEach(storage -> add("(" + storage.getRow() + " " + storage.getCol() + ")"));
            add("Cancel");
            add("Done");
        }});
        if (storageIDs == null) {
            storageIDs = new ArrayList<>();
        }
        if (looseGoods == null) {
            looseGoods = new ArrayList<>();
        }
    }

    /**
     * Returns the line to display before the input prompt.
     * @return Instructional string for the user.
     */
    @Override
    protected String lineBeforeInput() {
        return "Choose a storage unit to discard the most expensive goods.";
    }

    /**
     * Resets all static fields and restores the spaceship view.
     */
    public static void destroyStatics() {
        storageIDs = null;
        looseGoods = null;
        reset = false;
        PlayerDataView player = MiniModel.getInstance().getClientPlayer();
        spaceShipView = player == null ? null : player.getShip();
    }

    /**
     * Handles the logic for setting the new screen based on user selection.
     * Manages discarding goods, sending updates to the server, and ending the turn.
     * @return The next TuiScreenView to display.
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        int num = (int) spaceShipView.getMapStorages().values().stream()
                .filter(storage -> java.util.Arrays.stream(storage.getGoods()).anyMatch(Objects::nonNull))
                .count();

        if (selected == num) {
            destroyStatics();
            return new LooseGoodsCards();
        }

        if (selected == num + 1) {
            StatusEvent status;
            // Send the loosing goods to the server
            status = SetPenaltyLoss.requester(Client.transceiver, new Object()).request(new SetPenaltyLoss(MiniModel.getInstance().getUserID(), 0, storageIDs));
            destroyStatics();
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return new LooseGoodsCards();
            }
            // End the turn after discarding goods
            status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return new LooseGoodsCards();
            }

            return nextScreen;
        }

        spaceShipView.getMapStorages().entrySet().stream()
                .filter(entry -> java.util.Arrays.stream(entry.getValue().getGoods()).anyMatch(Objects::nonNull))
                .skip(selected)
                .findFirst()
                .ifPresent(entry -> {
                    storageIDs.add(entry.getKey());
                    looseGoods.add(entry.getValue().removeOneGood().getValue());
                });

        StringBuilder line = new StringBuilder();
        for (Integer integer : looseGoods) {
            line.append(GoodView.fromValue(integer).drawTui()).append(" ");
        }

        setMessage("You are dropping " + line);
        return new LooseGoodsCards();
    }
}
