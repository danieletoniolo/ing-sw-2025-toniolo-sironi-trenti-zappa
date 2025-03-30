package Model.State;

import Model.Cards.Planets;
import Model.Good.Good;
import Model.Player.PlayerData;
import Model.SpaceShip.Storage;
import Model.State.interfaces.ExchangeableGoods;
import Model.State.interfaces.SelectablePlanet;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.ArrayList;

public class PlanetsState extends State implements SelectablePlanet, ExchangeableGoods {
    private final Planets card;
    private PlayerData[] planetSelected;
    private ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData;
    /**
     * Constructor for PlanetsState
     * @param players List of players in the current order to play
     * @param card Planet card associated with the state
     */
    public PlanetsState(ArrayList<PlayerData> players, Planets card) {
        super(players);
        this.card = card;
        planetSelected = new PlayerData[card.getPlanetNumbers()];
    }

    /**
     * Selects a planet for a player if it is not already selected
     * @param player Player that wants to select a planet
     * @param planetNumber Number (index) of the planet to select
     * @throws IllegalStateException If the planet is already selected
     */
    public void selectPlanet(PlayerData player, int planetNumber) throws IllegalStateException{
        if (planetSelected[planetNumber] == null) {
            planetSelected[planetNumber] = player;
        } else {
            throw new IllegalStateException("Planet already selected by" + planetSelected[planetNumber].getUsername());
        }
    }

    public void exchangeGoods(PlayerData player, ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData) {
        this.exchangeData = exchangeData;
    }

    @Override
    public void entry() {
        super.entry();
    }

    /**
     * Marks the player as played
     * @param player PlayerData of the player to play
     */
    @Override
    public void execute(PlayerData player) throws NullPointerException {
        for (Pair<PlayerData, PlayerStatus> p : players) {
            if (p.getValue0().equals(player)) {
                if (p.getValue1() == PlayerStatus.PLAYING) {
                    p.setAt1(PlayerStatus.PLAYED);

                    // Execute the exchange
                    for (Triplet<ArrayList<Good>, ArrayList<Good>, Integer> triplet : exchangeData) {
                        ArrayList<Good> goodsToGet = triplet.getValue0();
                        ArrayList<Good> goodsToLeave = triplet.getValue1();
                        int storageId = triplet.getValue2();

                        Storage storage = player.getSpaceShip().getStorage(storageId);
                        for (Good good : goodsToGet) {
                            storage.addGood(good);
                        }
                        for (Good good : goodsToLeave) {
                            storage.removeGood(good);
                        }
                    }

                } else if (p.getValue1() == PlayerStatus.WAITING) {
                    p.setAt1(PlayerStatus.SKIPPED);
                }
            }
        }
    }

    /**
     * Exits the state and removes the flight days from the players that have selected a planet
     * If a player has not selected a planet, the flight days are not removed
     * @throws IllegalStateException If not all players have played
     */
    @Override
    public void exit() throws IllegalStateException{
        super.exit();
        int flightDays = card.getFlightDays();
        for (Pair<PlayerData, PlayerStatus> p : players) {
            if (p.getValue1() == PlayerStatus.PLAYED) {
                p.getValue0().addSteps(-flightDays);
            } else if (p.getValue1() == PlayerStatus.WAITING || p.getValue1() == PlayerStatus.PLAYING) {
                throw new IllegalStateException("Not all players have played");
            }
        }
    }
}
