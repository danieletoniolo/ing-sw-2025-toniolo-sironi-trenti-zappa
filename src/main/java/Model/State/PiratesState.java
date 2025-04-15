package Model.State;

import Model.Cards.Pirates;
import Model.Game.Board.Board;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import Model.State.interfaces.AcceptableCredits;
import Model.State.interfaces.ChoosableFragment;
import Model.State.interfaces.Fightable;
import Model.State.interfaces.UsableCannon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

enum PiratesInternalState {
    DEFAULT,
    MIDDLE,
    PENALTY
}

public class PiratesState extends State implements Fightable, ChoosableFragment, AcceptableCredits, UsableCannon {
    private final Pirates card;
    private final Map<PlayerData, Float> stats;
    private PiratesInternalState internalState;
    Boolean piratesDefeat;
    Boolean acceptCredits;
    ArrayList<PlayerData> playersDefeated;
    FightHandler fightHandler;

    /**
     * Constructor whit players and card
     * @param board The board associated with the game
     * @param card Pirates card associated with the state
     */
    public PiratesState(Board board, Pirates card) {
        super(board);
        this.card = card;
        this.stats = new HashMap<>();
        this.piratesDefeat = false;
        this.acceptCredits = false;
        this.internalState = PiratesInternalState.DEFAULT;
        this.playersDefeated = new ArrayList<>();
        this.fightHandler = new FightHandler();
    }

    public void setInternalStatePirates(PiratesInternalState internalState) {
        this.internalState = internalState;
    }

    public Map<PlayerData, Float> getStats() {
        return stats;
    }

    public Pirates getCard() {
        return card;
    }

    /**
     * Set if the player accepts the credits
     * @param acceptCredits Boolean value
     * @throws IllegalStateException if not in the correct state
     */
    public void setAcceptCredits(boolean acceptCredits) throws IllegalStateException {
        if (internalState != PiratesInternalState.MIDDLE) {
            throw new IllegalStateException("setAcceptCredits not allowed in this state");
        }
        this.acceptCredits = acceptCredits;
    }

    /**
     * Set the fragment choice
     * @param fragmentChoice index of the fragment choice
     * @throws IllegalStateException if not in the correct state
     */
    public void setFragmentChoice(int fragmentChoice) throws IllegalStateException {
        if (internalState != PiratesInternalState.PENALTY) {
            throw new IllegalStateException("Fragment choice not allowed in this state");
        }
        fightHandler.setFragmentChoice(fragmentChoice);
    }

    /**
     * Set the use energy
     * @param protect_ use energy
     * @param batteryID_ battery ID
     * @throws IllegalStateException if not in the right state in order to do the action
     * @throws IllegalArgumentException if batteryID_ is null and protect_ is true
     */
    public void setProtect(boolean protect_, Integer batteryID_) throws IllegalStateException, IllegalArgumentException {
        if (internalState != PiratesInternalState.PENALTY) {
            throw new IllegalStateException("setProtect not allowed in this state");
        }
        fightHandler.setProtect(protect_, batteryID_);
    }

    /**
     * Set the dice
     * @param dice dice value
     */
    public void setDice(int dice) throws IllegalStateException {
        if (internalState != PiratesInternalState.PENALTY) {
            throw new IllegalStateException("setDice not allowed in this state");
        }
        fightHandler.setDice(dice);
    }

    /**
     * Use the cannon
     * @param player PlayerData of the player using the cannon
     * @param strength Strength of the cannon to be used
     */
    public void useCannon(PlayerData player, Float strength) {
        this.stats.merge(player, strength, Float::sum);
    }

    /**
     * Entry method, set the stats for the players
     */
    @Override
    public void entry() {
        for (PlayerData player : super.players) {
            this.useCannon(player, player.getSpaceShip().getSingleCannonsStrength());
            if (player.getSpaceShip().hasPurpleAlien()) {
                this.useCannon(player, SpaceShip.getAlienStrength());
            }
        }
    }

    /**
     * Execute.
     * @param player PlayerData of the player to play
     * @throws IllegalStateException if acceptCredits not set
     */
    @Override
    public void execute(PlayerData player) throws IllegalStateException {
        int cardValue = card.getCannonStrengthRequired();

        switch (internalState) {
            case DEFAULT:
                if (stats.get(player) > cardValue) {
                    piratesDefeat = true;
                } else if (stats.get(player) < cardValue) {
                    piratesDefeat = false;
                } else {
                    piratesDefeat = null;
                }
                internalState = PiratesInternalState.MIDDLE;
                break;
            case MIDDLE:
                if (piratesDefeat == null) {
                    break;
                }

                if (piratesDefeat) {
                    if (acceptCredits == null) {
                        throw new IllegalStateException("acceptCredits not set");
                    }
                    if (acceptCredits) {
                        player.addCoins(card.getCredit());
                        board.addSteps(player, -card.getFlightDays());
                    }
                } else {
                    this.playersDefeated.add(player);
                }
                break;
            case PENALTY:
                if (!playersDefeated.contains(player)) {
                    throw new IllegalStateException("Player was not defeated");
                }
                int currentHitIndex = fightHandler.getHitIndex();
                if (currentHitIndex >= card.getFires().size()) {
                    throw new IndexOutOfBoundsException("Hit index out of bounds");
                }
                fightHandler.executeFight(player.getSpaceShip(), () -> card.getFires().get(currentHitIndex));
                break;
        }

        if (piratesDefeat != null && piratesDefeat && internalState == PiratesInternalState.MIDDLE) {
            super.setStatusPlayers(PlayerStatus.PLAYED);
        } else {
            super.execute(player);
        }

        if (super.haveAllPlayersPlayed()) {
            internalState = PiratesInternalState.PENALTY;
            super.setStatusPlayers(PlayerStatus.WAITING);
        }
    }
}
