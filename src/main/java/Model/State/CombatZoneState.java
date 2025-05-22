package Model.State;

import Model.Cards.CombatZone;
import Model.Game.Board.Board;
import Model.Good.Good;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import controller.EventCallback;
import event.game.serverToClient.*;

import java.util.*;



public class CombatZoneState extends State {
    private CombatZoneInternalState internalState;
    private final CombatZone card;
    private final ArrayList<Map<PlayerData, Float>> stats;
    private PlayerData minPlayerEngines;
    private PlayerData minPlayerCannons;
    private PlayerData minPlayerCrew;
    private List<Integer> crewLoss;
    private List<Integer> goodsToDiscard;
    private List<Integer> batteriesToDiscard;
    private final FightHandlerSubState fightHandler;

    /**
     * Enum to represent the internal state of the combat zone state.
     */
    private enum CombatZoneInternalState {
        CREW(0),
        ENGINES(1),
        CANNONS(2);

        private final int index;

        CombatZoneInternalState(int index) {
            this.index = index;
        }

        public int getIndex(int level) {
            int out = this.index;
            if (level == 2) {
                if (index == 0) {
                    out = 2;
                } else if (index == 2) {
                    out = 0;
                }
            }
            return out;
        }
    }

    /**
     * Constructor whit players and card
     * @param board The board associated with the game
     * @param card CombatZone card associated with the state
     */
    public CombatZoneState(Board board, EventCallback callback, CombatZone card) {
        super(board, callback);
        this.card = card;
        this.stats = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            stats.add(new HashMap<>());
        }
        if (card.getCardLevel() == 2) {
            this.internalState = CombatZoneInternalState.CANNONS;
        } else {
            this.internalState = CombatZoneInternalState.CREW;
        }
        this.minPlayerCannons = null;
        this.minPlayerEngines = null;
        this.minPlayerCrew = null;
        this.crewLoss = null;
        this.goodsToDiscard = null;
        this.batteriesToDiscard = null;
        this.fightHandler = new FightHandlerSubState(super.board, super.eventCallback);
    }

    /**
     * Add stats to the player
     * @param statsType type of stats to add
     * @param player player to add to the stats
     * @param value value to add to the stats
     */
    private void addStats(CombatZoneInternalState statsType, PlayerData player, Float value) {
        stats.get(statsType.getIndex(card.getCardLevel())).merge(player, value, Float::sum);
        // TODO: This method is useless, we should just use the stats map directly
    }

    /**
     * Execute the subState crew
     */
    private void executeSubStateFlightDays(PlayerData player) {
        int flightDays = card.getFlightDays();
        board.addSteps(player, -flightDays);
        playersStatus.replace(minPlayerCrew.getColor(), PlayerStatus.WAITING);

        MoveMarker stepsEvent = new MoveMarker(player.getUsername(), player.getStep());
        eventCallback.trigger(stepsEvent);
    }

    /**
     * Execute the subState engines
     * @param player player to execute
     */
    private void executeSubStateRemoveCrew(PlayerData player) throws IllegalArgumentException {
        int crewLost = card.getLost();

        SpaceShip spaceShip = player.getSpaceShip();
        if (spaceShip.getCrewNumber() <= crewLost) {
            player.setGaveUp(true);
            this.players = super.board.getInGamePlayers();

            PlayerLost gaveUpEvent = new PlayerLost(player.getUsername());
            eventCallback.trigger(gaveUpEvent);
        } else {
            for (int cabinID : crewLoss) {
                spaceShip.removeCrewMember(cabinID, 1);
            }
            playersStatus.replace(minPlayerCrew.getColor(), PlayerStatus.WAITING);

            // TODO: Due to the change of crewLoss to List<Integer> we need to change the event
            //AddLoseCrew crewLossEvent = new AddLoseCrew(player.getUsername(), false, crewLoss);
            //eventCallback.trigger(crewLossEvent);
        }
    }

    private void executeSubStateHits(PlayerData player) throws IndexOutOfBoundsException {
        int currentHitIndex = fightHandler.getHitIndex();
        if (currentHitIndex >= card.getFires().size()) {
            throw new IndexOutOfBoundsException("Hit index out of bounds");
        }

        boolean complete = fightHandler.executeFight(player, () -> card.getFires().get(currentHitIndex));
        if (complete && currentHitIndex == card.getFires().size() - 1) {
            super.execute(player);
        }
    }

    private void executeSubStateRemoveGoods(PlayerData player) throws IllegalStateException {
        // If the player has not set the goods to discard, we throw an exception
        if (goodsToDiscard == null) {
            throw new IllegalStateException("No goods to discard set");
        }
        SpaceShip ship = player.getSpaceShip();
        // Remove the goods from the ship
        for (int storageID : goodsToDiscard) {
            ship.pollGood(storageID);
        }
    }

    private void executeSubStateRemoveBatteries(PlayerData player) throws IllegalStateException {
        // If the player has not set the batteries to discard, we throw an exception
        if (batteriesToDiscard == null) {
            throw new IllegalStateException("No batteries to discard set");
        }
        SpaceShip ship = player.getSpaceShip();
        // Remove the batteries from the ship
        for (int batteryID : batteriesToDiscard) {
            ship.useEnergy(batteryID);
        }
        // Reset the goods to discard
        this.goodsToDiscard = null;
        // Reset the batteries to discard
        this.batteriesToDiscard = null;
        // Set the player as played
        playersStatus.replace(minPlayerCrew.getColor(), PlayerStatus.WAITING);
    }

    /**
     * Implementation of the {@link State#setFragmentChoice(int)} to set the fragment choice.
     */
    @Override
    public void setFragmentChoice(int fragmentChoice) throws IllegalStateException {
        if ((internalState != CombatZoneInternalState.CANNONS && card.getCardLevel() != 2) ||
                (internalState != CombatZoneInternalState.CREW && card.getCardLevel() == 2)) {
            throw new IllegalStateException("Fragment choice not allowed in this state");
        }
        if (fragmentChoice < 0 || fragmentChoice >= card.getFires().size()) {
            throw new IllegalArgumentException("Fragment choice is out of bounds");
        }
        fightHandler.setFragmentChoice(fragmentChoice);
    }

    /**
     * Implementation of the {@link State#setProtect(PlayerData, int)} to set whether the player wants to protect or not.
     */
    @Override
    public void setProtect(PlayerData player, int batteryID) throws IllegalStateException, IllegalArgumentException {
        if ((internalState != CombatZoneInternalState.CANNONS && card.getCardLevel() != 2) ||
                (internalState != CombatZoneInternalState.CREW && card.getCardLevel() == 2)) {
            throw new IllegalStateException("Battery ID not allowed in this state");
        }
        try {
            if (batteryID != -1) {
                SpaceShip ship = player.getSpaceShip();
                if (ship.getBattery(batteryID).getEnergyNumber() < 1) {
                    throw new IllegalArgumentException("Not enough energy in battery " + batteryID);
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid battery ID: " + batteryID);
        }
        if (batteryID == -1) {
            fightHandler.setProtect(false, null);
        } else {
            fightHandler.setProtect(true, batteryID);
        }
    }

    /**
     * Implementation of the {@link State#rollDice()} to roll the dice and set the value in the fight handler.
     */
    @Override
    public void rollDice() throws IllegalStateException{
        if ((internalState != CombatZoneInternalState.CANNONS && card.getCardLevel() != 2) ||
                (internalState != CombatZoneInternalState.CREW && card.getCardLevel() == 2)) {
            throw new IllegalStateException("Dice not allowed in this state");
        }
        // Roll the first dice
        int firstDice = (int) (Math.random() * 6) + 1;
        // Roll the second dice
        int secondDice = (int) (Math.random() * 6) + 1;
        // TODO: We should notify the two dice separately to handle visualization in the UI
        fightHandler.setDice(firstDice + secondDice);
    }

    /**
     * Implementation of the {@link State#setPenaltyLoss(PlayerData, int, List)} to set crew members, batteries or goods to leave
     * @throws IllegalArgumentException if the type is not 0, 1 or 2.
     */
    @Override
    public void setPenaltyLoss(PlayerData player, int type, List<Integer> penaltyLoss) throws IllegalStateException {
        switch (type) {
            case 0 -> {
                Map <Integer, Integer> goodsMap = new HashMap<>();
                for (int storageID : penaltyLoss) {
                    goodsMap.merge(storageID, 1, Integer::sum);
                }
                // Check that the selected goods to discard are the most valuable
                PriorityQueue<Good> goodsToDiscardQueue = new PriorityQueue<>(Comparator.comparingInt(Good::getValue).reversed());
                for (int storageID : goodsMap.keySet()) {
                    for (int i = 0; i < goodsMap.get(storageID); i++) {
                        Good good = player.getSpaceShip().getStorage(storageID).peekGood(i);
                        if (good == null) {
                            throw new IllegalStateException("Not enough goods in storage " + storageID);
                        }
                        goodsToDiscardQueue.add(good);
                    }
                }
                PriorityQueue<Good> mostValuableGoods = new PriorityQueue<>(player.getSpaceShip().getGoods());
                for (int i = 0; i < goodsToDiscardQueue.size(); i++) {
                    if (goodsToDiscardQueue.peek().getValue() != mostValuableGoods.peek().getValue()) {
                        throw new IllegalStateException("The goods to discard are not the most valuable");
                    }
                    goodsToDiscardQueue.poll();
                    mostValuableGoods.poll();
                }
                // Finally we can set the goods to discard
                this.goodsToDiscard = penaltyLoss;
            }
            case 1 -> {
                // Check if there are the provided number of batteries in the provided batteries slots.
                Map<Integer, Integer> batteriesMap = new HashMap<>();
                for (int batteryID : penaltyLoss) {
                    batteriesMap.merge(batteryID, 1, Integer::sum);
                }
                SpaceShip ship = player.getSpaceShip();
                for (int batteryID : batteriesMap.keySet()) {
                    if (ship.getBattery(batteryID).getEnergyNumber() < batteriesMap.get(batteryID)) {
                        throw new IllegalStateException("Not enough energy in battery " + batteryID);
                    }
                }
                // Check if the number of batteries to remove is equal to the number of batteries required to lose
                // The number of batteries to lose is the number of goods to discard minus the number of goods already discarded
                if (penaltyLoss.size() != card.getLost() - goodsToDiscard.size()) {
                    throw new IllegalStateException("The batteries removed is not equal to the batteries required to lose");
                }
                this.batteriesToDiscard = penaltyLoss;
            }
            case 2 -> {
                // Check if there are the provided number of crew members in the provided cabins
                Map<Integer, Integer> cabinCrewMap = new HashMap<>();
                for (int cabinID : penaltyLoss) {
                    cabinCrewMap.merge(cabinID, 1, Integer::sum);
                }
                SpaceShip ship = player.getSpaceShip();
                for (int cabinID : cabinCrewMap.keySet()) {
                    if (ship.getCabin(cabinID).getCrewNumber() < cabinCrewMap.get(cabinID)) {
                        throw new IllegalStateException("Not enough crew members in cabin " + cabinID);
                    }
                }
                // Check if the number of crew members to remove is equal to the number of crew members required to lose
                if (penaltyLoss.size() != card.getLost()) {
                    throw new IllegalStateException("The crew removed is not equal to the crew required to lose");
                }
                this.crewLoss = penaltyLoss;
            }
            default -> throw new IllegalArgumentException("Invalid type: " + type + ". Expected 0, 1 or 2.");
        }
    }

    /**
     * Implementation of the {@link State#useExtraStrength(PlayerData, int, List, List)} to use double engines
     * or double cannons based on the {@link CombatZoneInternalState} of the state.
     * @throws IllegalArgumentException if the type is not 0 or 1.
     */
    @Override
    public void useExtraStrength(PlayerData player, int type, List<Integer> IDs, List<Integer> batteriesID) throws IllegalStateException, IllegalArgumentException {
        switch (type) {
            case 0 -> {
                if (internalState != CombatZoneInternalState.ENGINES) {
                    throw new IllegalStateException("useEngine not allowed in this state");
                }
                EnginesUsed useEnginesEvent = new EnginesUsed(player.getUsername(), IDs, (ArrayList<Integer>) batteriesID);
                eventCallback.trigger(useEnginesEvent);
            }
            case 1 -> {
                if (internalState != CombatZoneInternalState.CANNONS) {
                    throw new IllegalStateException("useCannon not allowed in this state");
                }
                CannonsUsed useCannonsEvent = new CannonsUsed(player.getUsername(), IDs, (ArrayList<Integer>) batteriesID);
                eventCallback.trigger(useCannonsEvent);
            }
            default -> throw new IllegalArgumentException("Invalid type: " + type + ". Expected 0 or 1.");
        }

        // Use the energy to power the engine
        SpaceShip ship = player.getSpaceShip();
        for (Integer batteryID : batteriesID) {
            ship.useEnergy(batteryID);
        }

        // Update the engine or cannons strength stats
        this.addStats(internalState, player, (type == 0 ? IDs.size() * 2 : ship.getCannonsStrength(IDs)));

        Float statPlayer = stats.get(internalState.getIndex(card.getCardLevel())).get(player);
        MinPlayer minPlayerEvent = null;
        switch (type) {
            case 0 -> {
                if (statPlayer > stats.get(internalState.getIndex(card.getCardLevel())).get(minPlayerEngines)) {
                    playersStatus.replace(minPlayerEngines.getColor(), PlayerStatus.WAITING);
                    playersStatus.replace(player.getColor(), PlayerStatus.PLAYING);
                    minPlayerEngines = player;
                    minPlayerEvent = new MinPlayer(player.getUsername());
                }
            }
            case 1 -> {
                if (statPlayer > stats.get(internalState.getIndex(card.getCardLevel())).get(minPlayerCannons)) {
                    playersStatus.replace(minPlayerCannons.getColor(), PlayerStatus.WAITING);
                    playersStatus.replace(player.getColor(), PlayerStatus.PLAYING);
                    minPlayerCannons = player;
                    minPlayerEvent = new MinPlayer(player.getUsername());
                }
            }
        }

        if (minPlayerEvent != null) {
            eventCallback.trigger(minPlayerEvent);
        }
    }

    /**
     * Entry: Add the default stats to the player
     */
    @Override
    public void entry() {
        SpaceShip spaceShip;
        for (PlayerData player : super.players) {
            spaceShip = player.getSpaceShip();
            this.addStats(CombatZoneInternalState.CREW, player, (float) spaceShip.getCrewNumber());
            if (minPlayerCrew == null || minPlayerCrew.getSpaceShip().getCrewNumber() > spaceShip.getCrewNumber()) {
                minPlayerCrew = player;
            }

            this.addStats(CombatZoneInternalState.ENGINES, player, (float) spaceShip.getSingleEnginesStrength());
            if (spaceShip.hasBrownAlien()) {
                this.addStats(CombatZoneInternalState.ENGINES, player, SpaceShip.getAlienStrength());
            }
            if (minPlayerEngines == null || minPlayerEngines.getSpaceShip().getSingleEnginesStrength() > spaceShip.getSingleEnginesStrength()) {
                minPlayerEngines = player;
            }

            this.addStats(CombatZoneInternalState.CANNONS, player, spaceShip.getSingleCannonsStrength());
            if (spaceShip.hasPurpleAlien()) {
                this.addStats(CombatZoneInternalState.CANNONS, player, SpaceShip.getAlienStrength());
            }
            if (minPlayerCannons == null || minPlayerCannons.getSpaceShip().getSingleCannonsStrength() > spaceShip.getSingleCannonsStrength()) {
                minPlayerCannons = player;
            }
        }
        if (card.getCardLevel() == 2) {
            playersStatus.replace(minPlayerEngines.getColor(), PlayerStatus.PLAYING);
        } else {
            playersStatus.replace(minPlayerCrew.getColor(), PlayerStatus.PLAYING);
        }
    }

    /**
     * Execute one state each of teh cards
     * @param player PlayerData of the player to play
     */
    @Override
    public void execute(PlayerData player) {
        switch (internalState) {
            case CREW:
                if (card.getCardLevel() == 2) {
                    executeSubStateHits(minPlayerCrew);
                } else {
                    executeSubStateFlightDays(minPlayerCrew);
                    playersStatus.replace(minPlayerEngines.getColor(), PlayerStatus.PLAYING);
                    internalState = CombatZoneInternalState.ENGINES;
                }
                break;
            case ENGINES:
                if (card.getCardLevel() == 2) {
                    int crewFulfillment = minPlayerEngines.getSpaceShip().getGoods().size() - card.getLost();
                    if (crewFulfillment < 0 && minPlayerEngines.getSpaceShip().getCrewNumber() < Math.abs(crewFulfillment)) {
                        minPlayerEngines.setGaveUp(true);
                        this.players = super.board.getInGamePlayers();

                        PlayerLost gaveUpEvent = new PlayerLost(player.getUsername());
                        eventCallback.trigger(gaveUpEvent);
                    } else {
                        executeSubStateRemoveGoods(minPlayerEngines);
                        if (goodsToDiscard.size() < card.getLost()) {
                            executeSubStateRemoveBatteries(minPlayerEngines);
                        } else {
                            executeSubStateRemoveCrew(minPlayerEngines);
                            goodsToDiscard = null;
                        }
                    }
                    playersStatus.replace(minPlayerCrew.getColor(), PlayerStatus.PLAYING);
                } else {
                    executeSubStateRemoveCrew(minPlayerEngines);
                    playersStatus.replace(minPlayerCannons.getColor(), PlayerStatus.PLAYING);
                }
                break;
            case CANNONS:
                if (card.getCardLevel() == 2) {
                    executeSubStateFlightDays(minPlayerCannons);
                    playersStatus.replace(minPlayerEngines.getColor(), PlayerStatus.PLAYING);
                    internalState = CombatZoneInternalState.CANNONS;
                } else {
                    executeSubStateHits(minPlayerCannons);
                }
                break;
        }
    }
}