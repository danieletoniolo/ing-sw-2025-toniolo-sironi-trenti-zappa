package Model.State;

import Model.Cards.Card;
import Model.Cards.Slavers;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SlaversState extends State {
    private int execForPlayer;
    private final Card card;
    private final Map<PlayerData, Float> stats;
    private final Map<Integer, Integer> crewLost;
    private Boolean slaversDefeat;
    private boolean acceptCredits;

    public SlaversState(ArrayList<PlayerData> players, Card card) {
        super(players);
        this.execForPlayer = 0;
        this.card = card;
        this.stats = new HashMap<>();
        this.crewLost = new HashMap<>();
        this.slaversDefeat = false;
        this.acceptCredits = false;
    }

    public void addStats(PlayerData player, Float value) {
        stats.merge(player, value, Float::sum);
    }

    public void setCrewLost(int cabin, int crew) {
        crewLost.put(cabin, crew);
    }

    public void setAcceptCredits(boolean acceptCredits) {
        this.acceptCredits = acceptCredits;
    }

    public Boolean isSlaversDefeat() {
        return slaversDefeat;
    }

    @Override
    public void entry() {
        PlayerData value0;
        for (Pair<PlayerData, PlayerStatus> player : super.players) {
            value0 = player.getValue0();
            addStats(value0, value0.getSpaceShip().getSingleCannonsStrength());
            if (value0.getSpaceShip().getPurpleAlien()) {
                addStats(value0, SpaceShip.getAlienStrength());
            }
        }
    }

    @Override
    public void execute(PlayerData player) throws IllegalStateException {
        Slavers slavers = (Slavers) card;
        SpaceShip spaceShip = player.getSpaceShip();
        int cardValue = slavers.getCannonStrengthRequired();

        switch (execForPlayer) {
            case 0:
                if (stats.get(player) > cardValue) {
                    slaversDefeat = true;
                } else if (stats.get(player) < cardValue) {
                    slaversDefeat = false;
                } else {
                    slaversDefeat = null;
                }
                execForPlayer++;
                break;
            case 1:
                if (slaversDefeat == null)
                    break;
                if (slaversDefeat) {
                    if (acceptCredits) {
                        player.addCoins(slavers.getCredit());
                        player.addSteps(-slavers.getFlightDays());
                    }
                } else {
                    if (spaceShip.getCrewNumber() >= slavers.getCrewLost()) {
                        for (Map.Entry<Integer, Integer> entry : crewLost.entrySet()) {
                            spaceShip.getCabin(entry.getKey()).removeCrewMember(entry.getValue());
                        }
                    } else {
                        player.setGaveUp(true);
                    }
                }
                break;
        }

        if (slaversDefeat != null && slaversDefeat && execForPlayer == 1) {
            for(Pair<PlayerData, PlayerStatus> p : super.players) {
                p.setAt1(PlayerStatus.PLAYED);
            }
        } else {
            super.execute(player);
        }
    }
}
