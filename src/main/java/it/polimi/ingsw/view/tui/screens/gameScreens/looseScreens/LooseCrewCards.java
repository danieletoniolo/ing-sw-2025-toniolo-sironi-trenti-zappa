package it.polimi.ingsw.view.tui.screens.gameScreens.looseScreens;

import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.spaceship.SetPenaltyLoss;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;
import java.util.List;

public class LooseCrewCards extends CardsGame {
    private static List<Integer> cabinIDs;
    private static boolean reset;

    public LooseCrewCards() {
        super(new ArrayList<>(){{
            if (!reset) {
                spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
            }
            if (!reset) reset = true;
            addAll(
                    spaceShipView.getMapCabins().values().stream()
                            .filter(cabin -> cabin.getCrewNumber() != 0)
                            .map(cabin -> "(" + cabin.getRow() + " " + cabin.getCol() + ")")
                            .toList()
            );
            add("Cancel");
            add("Done");
        }});
        if (cabinIDs == null) {
            cabinIDs = new ArrayList<>();
        }
    }

    public static void destroyStatics() {
        cabinIDs = null;
        reset = false;
        PlayerDataView player = MiniModel.getInstance().getClientPlayer();
        spaceShipView = player == null ? null : player.getShip();

    }

    @Override
    protected String lineBeforeInput() {
        return "Select a cabin to remove a crew member from";
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        int num = (int) spaceShipView.getMapCabins().values().stream()
                .filter(cabin -> cabin.getCrewNumber() != 0)
                .count();

        if (selected == num) {
            destroyStatics();
            return new LooseCrewCards();
        }

        if (selected == num + 1) {
            StatusEvent status;
            // Send the loosing cabin IDs to the server
            status = SetPenaltyLoss.requester(Client.transceiver, new Object())
                    .request(new SetPenaltyLoss(MiniModel.getInstance().getUserID(), 2, cabinIDs));
            destroyStatics();
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return new LooseCrewCards();
            }
            // Request to end the turn after loosing crew members
            status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return new LooseCrewCards();
            }
            return nextScreen;
        }

        spaceShipView.getMapCabins().entrySet().stream()
                .filter(entry -> entry.getValue().getCrewNumber() != 0)
                .skip(selected)
                .findFirst()
                .ifPresent(entry -> {
                    cabinIDs.add(entry.getKey());
                    entry.getValue().setCrewNumber(entry.getValue().getCrewNumber() - 1);
                });

        StringBuilder line = new StringBuilder();
        for (Integer integer : cabinIDs) {
            line.append(spaceShipView.getMapCabins().get(integer).getCrewType().drawTui()).append("(").append(spaceShipView.getMapCabins().get(integer).getRow()).append(" ").append(spaceShipView.getMapCabins().get(integer).getCol()).append(") ");
        }
        setMessage("You are loosing a crew member from " + line);
        return new LooseCrewCards();
    }
}
