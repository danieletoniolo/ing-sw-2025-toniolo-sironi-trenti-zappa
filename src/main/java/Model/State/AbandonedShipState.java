package Model.State;

import Model.Cards.AbandonedShip;
import Model.Game.Board.Board;
import Model.Player.PlayerData;
import Model.State.interfaces.RemovableCrew;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AbandonedShipState extends State implements RemovableCrew {
    private final AbandonedShip card;
    private ArrayList<Pair<Integer, Integer>> crewLoss;

    /**
     * Constructor
     * @param players Sorted list of players
     * @param card type of the card
     */
    public AbandonedShipState(ArrayList<PlayerData> players, Board board, AbandonedShip card) {
        super(players, board);
        this.card = card;
        this.crewLoss = null;
    }

    /**
     * Set which cabin loses crew members
     * @param cabinsID Map of cabins ID and number of crew removed for cabins
     * @throws IllegalStateException if not in the right state in order to do the action
     */
    public void setCrewLoss(ArrayList<Pair<Integer, Integer>> cabinsID) throws IllegalStateException {
        int crewRemoved = 0;
        for (Pair<Integer, Integer> cabin : cabinsID) {
            crewRemoved += cabin.getValue1();
        }

        if (crewRemoved != card.getCrewRequired()) {
            throw new IllegalStateException("The crew removed is not equal to the crew lost");
        }
        this.crewLoss = cabinsID;
    }

    /**
     * Execute: Remove crew members from cabins.
     * Add credits to player.
     * Change player steps.
     * @param player PlayerData of the player to play
     * @throws NullPointerException if player == null
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
            played = true;
            crewLoss.forEach(cabin -> {
                player.getSpaceShip().getCabin(cabin.getValue0()).removeCrewMember(cabin.getValue1());
            });
            player.addCoins(card.getCredit());
            board.addSteps(player, -card.getFlightDays());
        }
        super.execute(player);
    }
}
