package it.polimi.ingsw.view.tui.screens.gameScreens.looseScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.spaceship.SetPenaltyLoss;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;
import java.util.List;

/**
 * Screen for handling the loss of battery cards in the game.
 * Allows the player to select batteries to lose energy from, sends the selection to the server,
 * and manages the turn ending process.
 */
public class LooseBatteryCards extends CardsGame {
    /** Flag to determine if the screen should reset the spaceship view. */
    private static boolean reset;
    /** List of battery IDs selected for energy loss. */
    private static List<Integer> batteryIDs;

    /**
     * Constructs the LooseBatteryCards screen, initializing the list of selectable batteries.
     */
    public LooseBatteryCards() {
        super(new ArrayList<>(){{
            if (!reset) {
                spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
            }
            if (!reset) reset = true;
            spaceShipView.getMapBatteries().values().stream()
                    .filter(value -> value.getNumberOfBatteries() != 0)
                    .forEach(value -> add("(" + value.getRow() + " " + value.getCol() + ")"));
            add("Cancel");
            add("Done");
        }});
        if (batteryIDs == null) {
            batteryIDs = new ArrayList<>();
        }
    }

    /**
     * Returns the line to display before the user input prompt.
     * @return the prompt string
     */
    @Override
    protected String lineBeforeInput() {
        return "Select a battery where to drop energy from:";
    }

    /**
     * Resets static fields and restores the spaceship view to the current player's ship.
     */
    public static void destroyStatics() {
        batteryIDs = null;
        reset = false;
        PlayerDataView player = MiniModel.getInstance().getClientPlayer();
        spaceShipView = player == null ? null : player.getShip();
    }

    /**
     * Handles the logic for setting the next screen based on the user's selection.
     * Sends the selected batteries to the server and manages error handling and turn ending.
     * @return the next TuiScreenView to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        int num = (int) spaceShipView.getMapBatteries().values().stream()
                .filter(value -> value.getNumberOfBatteries() != 0)
                .count();

        if (selected == num) {
            destroyStatics();
            return new LooseBatteryCards();
        }

        if (selected == num + 1) {
            StatusEvent status;
            // Send the loosing batteryIDs to the server
            status = SetPenaltyLoss.requester(Client.transceiver, new Object()).request(new SetPenaltyLoss(MiniModel.getInstance().getUserID(), 1, batteryIDs));
            destroyStatics();
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return new LooseBatteryCards();
            }
            // After selected loosing batteries end the turn
            status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return new LooseBatteryCards();
            }
            return nextScreen;
        }

        spaceShipView.getMapBatteries().entrySet().stream()
                .filter(entry -> entry.getValue().getNumberOfBatteries() != 0)
                .skip(selected)
                .findFirst()
                .ifPresent(entry -> {
                    batteryIDs.add(entry.getKey());
                    entry.getValue().setNumberOfBatteries(entry.getValue().getNumberOfBatteries() - 1);
                });

        StringBuilder line = new StringBuilder();
        for (Integer integer : batteryIDs) {
            line.append("(").append(spaceShipView.getMapBatteries().get(integer).getRow()).append(" ").append(spaceShipView.getMapBatteries().get(integer).getCol()).append(") ");
        }
        setMessage("You are loosing batteries from " + line);
        return new LooseBatteryCards();
    }
}
