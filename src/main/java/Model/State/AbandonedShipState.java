package Model.State;

import Model.Cards.AbandonedShip;
import Model.Game.Board.Board;
import Model.Player.PlayerData;
import Model.State.interfaces.RemovableCrew;
import controller.EventCallback;
import event.game.AddCoins;
import event.game.AddLoseCrew;
import event.game.MoveMarker;
import org.javatuples.Pair;

import java.util.ArrayList;

public class AbandonedShipState extends State implements RemovableCrew {
    private final AbandonedShip card;
    private ArrayList<Pair<Integer, Integer>> crewLoss;

    /**
     * Constructor
     * @param board The board associated with the game
     * @param card type of the card
     */
    public AbandonedShipState(Board board, EventCallback callback, AbandonedShip card) {
        super(board, callback);
        this.card = card;
        this.crewLoss = null;
    }

    /**
     * Getter for the card
     * @return The card
     */
    public AbandonedShip getCard() {
        return card;
    }

    /**
     * Getter for the crew loss
     * @return The crew loss
     */
    public ArrayList<Pair<Integer, Integer>> getCrewLoss() {
        return crewLoss;
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
     *
     * @param player PlayerData of the player to play
     * @return Pair of EventType and Object which contains the record that will be sent to the client
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
            crewLoss.forEach(cabin -> {
                player.getSpaceShip().getCabin(cabin.getValue0()).removeCrewMember(cabin.getValue1());
            });
            player.addCoins(card.getCredit());

            AddLoseCrew crewEvent = new AddLoseCrew(getCurrentPlayer().getUsername(), false, crewLoss);
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
