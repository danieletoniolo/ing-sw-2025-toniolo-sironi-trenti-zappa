package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.cards.*;
import it.polimi.ingsw.event.game.serverToClient.deck.GetDecks;
import it.polimi.ingsw.event.game.serverToClient.placedTile.PlacedMainCabin;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.board.Deck;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.MatchController;
import it.polimi.ingsw.model.spaceship.Component;
import it.polimi.ingsw.utils.Logger;
import org.javatuples.Pair;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class represents the lobby state of the game. In this state, players can join, leave and start the game.
 * The lobby state is the initial state of the game, and it is created when the game is created by the first
 * {@link PlayerData}.
 * <p>
 * The method {@link #manageLobby(PlayerData, int)} is used to add/remove a player to the game, and the method.
 * In order to start the game, the method {@link #execute(PlayerData)} must be called from all the players in the lobby.
 * <p>
 * The lobby state is a subclass of the {@link State} class, which is the base class for all game states.
 * @see State
 * @author Daniele Toniolo
 */
public class LobbyState extends State {

    /**
     * Constructs a new LobbyState object associated with the given board.
     * When this object is created we just call the super constructor {@link State(Board)}.
     * @param board The board associated with this state.
     */
    public LobbyState(Board board, EventCallback callback, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
    }

    /**
     * This method is used to load the cards from the board and trigger the corresponding events.
     * It iterates through the shuffled deck of cards in the board and triggers the appropriate event
     * based on the type of card.
     */
    private void loadCards() {
        for (Card card: board.getShuffledDeck()) {
            switch (card.getCardType()) {
                case ABANDONEDSHIP -> {
                    AbandonedShip cardAbandonedShip = (AbandonedShip) card;
                    GetCardAbandonedShip getCardAbandonedShip = new GetCardAbandonedShip(
                            cardAbandonedShip.getID(),
                            cardAbandonedShip.getCardLevel(),
                            cardAbandonedShip.getCrewRequired(),
                            cardAbandonedShip.getFlightDays(),
                            cardAbandonedShip.getCredit()
                    );
                    eventCallback.trigger(getCardAbandonedShip);
                }
                case ABANDONEDSTATION -> {
                    AbandonedStation cardAbandonedStation = (AbandonedStation) card;
                    GetCardAbandonedStation getCardAbandonedStation = new GetCardAbandonedStation(
                            cardAbandonedStation.getID(),
                            cardAbandonedStation.getCardLevel(),
                            cardAbandonedStation.getCrewRequired(),
                            cardAbandonedStation.getFlightDays(),
                            cardAbandonedStation.getGoods().stream().map(t -> t.getColor().getValue()).toList()
                    );
                    eventCallback.trigger(getCardAbandonedStation);
                }
                case SMUGGLERS -> {
                    Smugglers cardSmugglers = (Smugglers) card;
                    GetCardSmugglers getCardSmugglers = new GetCardSmugglers(
                            cardSmugglers.getID(),
                            cardSmugglers.getCardLevel(),
                            cardSmugglers.getCannonStrengthRequired(),
                            cardSmugglers.getFlightDays(),
                            cardSmugglers.getGoodsReward().stream().map(t -> t.getColor().getValue()).toList(),
                            cardSmugglers.getGoodsLoss()
                    );
                    eventCallback.trigger(getCardSmugglers);
                }
                case SLAVERS -> {
                    Slavers cardSlavers = (Slavers) card;
                    GetCardSlavers getCardSlavers = new GetCardSlavers(
                            cardSlavers.getID(),
                            cardSlavers.getCardLevel(),
                            cardSlavers.getCannonStrengthRequired(),
                            cardSlavers.getFlightDays(),
                            cardSlavers.getCrewLost(),
                            cardSlavers.getCredit()
                    );
                    eventCallback.trigger(getCardSlavers);
                }
                case PIRATES -> {
                    Pirates cardPirates = (Pirates) card;
                    GetCardPirates getCardPirates = new GetCardPirates(
                            card.getID(),
                            card.getCardLevel(),
                            cardPirates.getCannonStrengthRequired(),
                            cardPirates.getFlightDays(),
                            cardPirates.getFires().stream().map(t -> new Pair<>(t.getType().getValue(), t.getDirection().getValue())).toList(),
                            cardPirates.getCredit()
                    );
                    eventCallback.trigger(getCardPirates);
                }
                case PLANETS -> {
                    Planets cardPlanets = (Planets) card;
                    GetCardPlanets getCardPlanets = new GetCardPlanets(
                            cardPlanets.getID(),
                            cardPlanets.getCardLevel(),
                            cardPlanets.getPlanets().stream().map(t -> t.stream().map(temp -> temp.getColor().getValue()).toList()).toList(),
                            cardPlanets.getFlightDays()
                    );
                    eventCallback.trigger(getCardPlanets);
                }
                case OPENSPACE -> {
                    GetCardOpenSpace getCardOpenSpace = new GetCardOpenSpace(
                            card.getID(),
                            card.getCardLevel()
                    );
                    eventCallback.trigger(getCardOpenSpace);
                }
                case METEORSWARM -> {
                    MeteorSwarm cardMeteorSwarm = (MeteorSwarm) card;
                    GetCardMeteorSwarm getCardMeteorSwarm = new GetCardMeteorSwarm(
                            cardMeteorSwarm.getID(),
                            cardMeteorSwarm.getCardLevel(),
                            cardMeteorSwarm.getMeteors().stream().map(t -> new Pair<>(t.getType().getValue(), t.getDirection().getValue())).toList()
                    );
                    eventCallback.trigger(getCardMeteorSwarm);
                }
                case COMBATZONE -> {
                    CombatZone cardCombatZone = (CombatZone) card;
                    GetCardCombatZone getCardCombatZone = new GetCardCombatZone(
                            cardCombatZone.getID(),
                            cardCombatZone.getCardLevel(),
                            cardCombatZone.getFlightDays(),
                            cardCombatZone.getLost(),
                            cardCombatZone.getFires().stream().map(t -> new Pair<>(t.getType().getValue(), t.getDirection().getValue())).toList()
                    );
                    eventCallback.trigger(getCardCombatZone);
                }
                case STARDUST -> {
                    GetCardStardust getCardStardust = new GetCardStardust(
                            card.getID(),
                            card.getCardLevel()
                    );
                    eventCallback.trigger(getCardStardust);
                }
                case EPIDEMIC -> {
                    GetCardEpidemic getCardEpidemic = new GetCardEpidemic(
                            card.getID(),
                            card.getCardLevel()
                    );
                    eventCallback.trigger(getCardEpidemic);
                }
            }
        }
    }

    /**
     * This method is used to start the game. It is called when all the players in the lobby are ready to play.
     * It transitions the game to the {@link GameState#BUILDING} state.
     */
    @Override
    public void startGame(LocalTime startTime, int timerDuration) {
        loadCards();

        if (board.getBoardLevel() == Level.SECOND) {
            List<List<Integer>> decks = new ArrayList<>();
            for (Deck deck : board.getDecks()) {
                if (deck.isPickable()) {
                    decks.add(deck.getCards().stream().map(Card::getID).toList());
                }
            }
            eventCallback.trigger(new GetDecks(decks));
        }

        for (PlayerData player : players) {
            Component mainCabin = player.getSpaceShip().getComponent(6, 6);
            int[] connectors = new int[4];
            for (int i = 0; i < 4; i++) {
                connectors[i] = mainCabin.getConnection(i).getValue();
            }

            PlacedMainCabin placedMainCabinEvent = new PlacedMainCabin(player.getUsername(),
                    player.getSpaceShip().getComponent(6, 6).getID(),
                    connectors);
            eventCallback.trigger(placedMainCabinEvent);
        }

        LocalTime localTime = LocalTime.now();
        (new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
                nextState(GameState.BUILDING);
            }
        }, timerDuration - Math.max(0, (int) Duration.between(startTime, localTime).toMillis()));
    }

    /**
     * This method is used to add/remove a player to the game.
     * The event associated with this method are implemented in the {@link MatchController} class.
     * @param player The player to add/remove
     * @param type 0 to add the player, 1 to remove the player
     */
    @Override
    public void manageLobby(PlayerData player, int type) {
        switch (type) {
            case 0 -> {
                // Add the player to the board
                board.addInGamePlayers(player);

                // Add the player to the list of players in the LobbyState
                players.add(player);
            }
            case 1 -> {
                // Remove the player from the board
                board.removeInGamePlayer(player);

                // Remove the player from the list of players in the LobbyState
                players.remove(player);
            }
            default -> throw new IllegalArgumentException("Invalid type: " + type + ". Expected 0 or 1.");
        }
    }


    /**
     * The entry method in this state is called when the state is entered.
     * @see State#entry()
     */
    @Override
    public void entry() {}

    /**
     * The execute method in this state is used to communicate that the player is isReady to play the game.
     * In order to do this we call the {@link State#execute(PlayerData)} that set the player status as
     * {@link PlayerStatus#PLAYED}.
     * @param player PlayerData of the player that is isReady to play
     */
    @Override
    public void execute(PlayerData player) {
        super.execute(player);
    }

    /**
     * The exit method in this state is used to advance to the next state of the game.
     * @see State#exit()
     */
    @Override
    public void exit() {
        super.exit();
    }
}
