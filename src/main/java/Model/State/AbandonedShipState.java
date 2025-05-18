package Model.State;

import Model.Cards.AbandonedShip;
import Model.Game.Board.Board;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import controller.EventCallback;
import event.game.AddCoins;
import event.game.AddLoseCrew;
import event.game.MoveMarker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbandonedShipState extends State {
    private final AbandonedShip card;
    private List<Integer> crewLoss;

    /**
     * Constructor for AbandonedShipState
     * @param board The board associated with the game
     * @param card The AbandonedShip card associated with the state
     */
    public AbandonedShipState(Board board, EventCallback callback, AbandonedShip card) {
        super(board, callback);
        this.card = card;
        this.crewLoss = null;
    }

    /**
     * Implementation of the {@link State #setPenaltyLoss(PlayerData, int, List)} to set the crew to lose in
     * order to serve the penalty.
     * @throws IllegalArgumentException if the type is not 0, 1 or 2.
     */
    @Override
    public void setPenaltyLoss(PlayerData player, int type, List<Integer> cabinsID) {
        switch (type) {
            case 0 -> throw new IllegalStateException("No goods to remove in this state");
            case 1 -> throw new IllegalStateException("No batteries to remove in this state");
            case 2 -> {
                // Check if there are the provided number of crew members in the provided cabins
                Map<Integer, Integer> cabinCrewMap = new HashMap<>();
                for (int cabinID : cabinsID) {
                    cabinCrewMap.merge(cabinID, 1, Integer::sum);
                }
                SpaceShip ship = player.getSpaceShip();
                for (int cabinID : cabinCrewMap.keySet()) {
                    if (ship.getCabin(cabinID).getCrewNumber() < cabinCrewMap.get(cabinID)) {
                        throw new IllegalStateException("Not enough crew members in cabin " + cabinID);
                    }
                }
                // Check if the number of crew members to remove is equal to the number of crew members required to lose
                if (cabinsID.size() != card.getCrewRequired()) {
                    throw new IllegalStateException("The crew removed is not equal to the crew lost");
                }
                this.crewLoss = cabinsID;
            }
            default -> throw new IllegalArgumentException("Invalid type: " + type + ". Expected 0, 1 or 2.");
        }
    }

    /**
     * Execute:
     * <ul>
     *     <li> Remove crew members from cabins.</li>
     *     <li> Add credits to player. </li>
     *     <li> Change player steps. </li>
     * </ul>
     * @param player PlayerData of the player to play
     * @throws NullPointerException  if player == null
     * @throws IllegalStateException if played == true: Card playable just once
     * @throws IllegalStateException if crew loss does not match the card requirements
     * @throws IllegalStateException if crew loss not set
     */
    @Override
    public void execute(PlayerData player) throws NullPointerException, IllegalStateException {
        if (player == null) {
            throw new NullPointerException("Player is null");
        }
        if (super.played) {
            throw new IllegalStateException("State already played");
        }
        if (crewLoss == null) {
            throw new IllegalStateException("Crew loss does not match the card requirements");
        }

        if (playersStatus.get(player.getColor()).equals(PlayerStatus.PLAYING)) {
            SpaceShip ship = player.getSpaceShip();

            for (Integer cabinID : crewLoss) {
                ship.getCabin(cabinID).removeCrewMember(1);
            }

            player.addCoins(card.getCredit());

            AddLoseCrew crewEvent = new AddLoseCrew(getCurrentPlayer().getUsername(), false, (ArrayList<Integer>) crewLoss);
            eventCallback.trigger(crewEvent);

            AddCoins coinsEvent = new AddCoins(player.getUsername(), card.getCredit());
            eventCallback.trigger(coinsEvent);

            played = true;
        }
        super.execute(player);
    }

    @Override
    public void exit() throws IllegalStateException{
        for (PlayerData player : players) {
            PlayerStatus status = playersStatus.get(player.getColor());
            if (status == PlayerStatus.PLAYED) {
                int flightDays = card.getFlightDays();
                board.addSteps(player, -flightDays);

                MoveMarker stepEvent = new MoveMarker(player.getUsername(), player.getStep());
                eventCallback.trigger(stepEvent);

                break;
            } else if (status == PlayerStatus.WAITING || status == PlayerStatus.PLAYING) {
                throw new IllegalStateException("Not all players have played");
            }
        }
        super.played = true;
    }
}
