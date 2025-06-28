package it.polimi.ingsw.view;

import it.polimi.ingsw.event.EventListener;
import it.polimi.ingsw.event.NetworkTransceiver;
import it.polimi.ingsw.event.game.serverToClient.*;
import it.polimi.ingsw.event.game.serverToClient.cards.*;
import it.polimi.ingsw.event.game.serverToClient.deck.*;
import it.polimi.ingsw.event.game.serverToClient.dice.DiceRolled;
import it.polimi.ingsw.event.game.serverToClient.energyUsed.*;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingGiveUp;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingPenalty;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingPlaceMarker;
import it.polimi.ingsw.event.game.serverToClient.goods.*;
import it.polimi.ingsw.event.game.serverToClient.pickedTile.*;
import it.polimi.ingsw.event.game.serverToClient.placedTile.*;
import it.polimi.ingsw.event.game.serverToClient.planets.PlanetSelected;
import it.polimi.ingsw.event.game.serverToClient.player.*;
import it.polimi.ingsw.event.game.serverToClient.rotatedTile.RotatedTile;
import it.polimi.ingsw.event.game.serverToClient.spaceship.*;
import it.polimi.ingsw.event.game.serverToClient.timer.*;
import it.polimi.ingsw.event.internal.ConnectionLost;
import it.polimi.ingsw.event.lobby.serverToClient.*;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.view.miniModel.GamePhases;
import it.polimi.ingsw.view.miniModel.board.BoardView;
import it.polimi.ingsw.view.miniModel.cards.*;
import it.polimi.ingsw.view.miniModel.cards.hit.HitDirectionView;
import it.polimi.ingsw.view.miniModel.cards.hit.HitTypeView;
import it.polimi.ingsw.view.miniModel.cards.hit.HitView;
import it.polimi.ingsw.view.miniModel.components.*;
import it.polimi.ingsw.view.miniModel.components.crewmembers.CrewView;
import it.polimi.ingsw.view.miniModel.deck.DeckView;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.miniModel.player.MarkerView;
import it.polimi.ingsw.view.miniModel.spaceship.SpaceShipView;
import it.polimi.ingsw.view.miniModel.timer.TimerView;
import org.javatuples.Pair;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.miniModel.countDown.CountDown;
import it.polimi.ingsw.view.miniModel.lobby.LobbyView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import org.javatuples.Triplet;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventHandlerClient {
    NetworkTransceiver transceiver;
    Manager manager;

    private final CastEventReceiver<Lobbies> lobbiesReceiver;
    private final EventListener<Lobbies> lobbiesListener;

    private final CastEventReceiver<LobbyCreated> lobbyCreatedReceiver;
    private final EventListener<LobbyCreated> lobbyCreatedListener;

    private final CastEventReceiver<LobbyJoined> lobbyJoinedReceiver;
    private final EventListener<LobbyJoined> lobbyJoinedListener;

    private final CastEventReceiver<LobbyLeft> lobbyLeftReceiver;
    private final EventListener<LobbyLeft> lobbyLeftListener;

    private final CastEventReceiver<LobbyRemoved> lobbyRemovedReceiver;
    private final EventListener<LobbyRemoved> lobbyRemovedListener;

    private final CastEventReceiver<PlayerAdded> playerAddedReceiver;
    private final EventListener<PlayerAdded> playerAddedListener;

    private final CastEventReceiver<ReadyPlayer> readyPlayerReceiver;
    private final EventListener<ReadyPlayer> readyPlayerListener;

    private final CastEventReceiver<StartingGame> startingGameReceiver;
    private final EventListener<StartingGame> startingGameListener;

    private final CastEventReceiver<GetCardAbandonedShip> getCardAbandonedShipReceiver;
    private final EventListener<GetCardAbandonedShip> getCardAbandonedShipListener;

    private final CastEventReceiver<GetCardAbandonedStation> getCardAbandonedStationReceiver;
    private final EventListener<GetCardAbandonedStation> getCardAbandonedStationListener;

    private final CastEventReceiver<GetCardCombatZone> getCardCombatZoneReceiver;
    private final EventListener<GetCardCombatZone> getCardCombatZoneListener;

    private final CastEventReceiver<GetCardEpidemic> getCardEpidemicReceiver;
    private final EventListener<GetCardEpidemic> getCardEpidemicListener;

    private final CastEventReceiver<GetCardMeteorSwarm> getCardMeteorSwarmReceiver;
    private final EventListener<GetCardMeteorSwarm> getCardMeteorSwarmListener;

    private final CastEventReceiver<GetCardOpenSpace> getCardOpenSpaceReceiver;
    private final EventListener<GetCardOpenSpace> getCardOpenSpaceListener;

    private final CastEventReceiver<GetCardPirates> getCardPiratesReceiver;
    private final EventListener<GetCardPirates> getCardPiratesListener;

    private final CastEventReceiver<GetCardPlanets> getCardPlanetsReceiver;
    private final EventListener<GetCardPlanets> getCardPlanetsListener;

    private final CastEventReceiver<GetCardSlavers> getCardSlaversReceiver;
    private final EventListener<GetCardSlavers> getCardSlaversListener;

    private final CastEventReceiver<GetCardSmugglers> getCardSmugglersReceiver;
    private final EventListener<GetCardSmugglers> getCardSmugglersListener;

    private final CastEventReceiver<GetCardStardust> getCardStardustReceiver;
    private final EventListener<GetCardStardust> getCardStardustListener;

    private final CastEventReceiver<GetDecks> getDecksReceiver;
    private final EventListener<GetDecks> getDecksListener;

    private final CastEventReceiver<GetShuffledDeck> getShuffledDeckReceiver;
    private final EventListener<GetShuffledDeck> getShuffledDeckListener;

    private final CastEventReceiver<PickedLeftDeck> pickedLeftDeckReceiver;
    private final EventListener<PickedLeftDeck> pickedLeftDeckListener;

    private final CastEventReceiver<DiceRolled> diceRolledReceiver;
    private final EventListener<DiceRolled> diceRolledListener;

    private final CastEventReceiver<BatteriesLoss> batteriesLossReceiver;
    private final EventListener<BatteriesLoss> getBatteriesLossListener;

    private final CastEventReceiver<ForcingGiveUp> forcingGiveUpReceiver;
    private final EventListener<ForcingGiveUp> forcingGiveUpListener;

    private final CastEventReceiver<ForcingPenalty> forcingPenaltyReceiver;
    private final EventListener<ForcingPenalty> forcingPenaltyListener;

    private final CastEventReceiver<ForcingPlaceMarker> forcingPlaceMarkerReceiver;
    private final EventListener<ForcingPlaceMarker> forcingPlaceMarkerListener;

    private final CastEventReceiver<UpdateGoodsExchange> updateGoodsExchangeReceiver;
    private final EventListener<UpdateGoodsExchange> updateGoodsExchangeListener;

    private final CastEventReceiver<NumberHiddenTiles> numberHiddenTilesReceiver;
    private final EventListener<NumberHiddenTiles> numberHiddenTilesListener;

    private final CastEventReceiver<PickedBatteryFromBoard> pickedBatteryFromBoardReceiver;
    private final EventListener<PickedBatteryFromBoard> pickedBatteryFromBoardListener;

    private final CastEventReceiver<PickedCabinFromBoard> pickedCabinFromBoardReceiver;
    private final EventListener<PickedCabinFromBoard> pickedCabinFromBoardListener;

    private final CastEventReceiver<PickedCannonFromBoard> pickedCannonFromBoardReceiver;
    private final EventListener<PickedCannonFromBoard> pickedCannonFromBoardListener;

    private final CastEventReceiver<PickedConnectorsFromBoard> pickedConnectorsFromBoardReceiver;
    private final EventListener<PickedConnectorsFromBoard> pickedConnectorsFromBoardListener;

    private final CastEventReceiver<PickedEngineFromBoard> pickedEngineFromBoardReceiver;
    private final EventListener<PickedEngineFromBoard> pickedEngineFromBoardListener;

    private final CastEventReceiver<PickedLifeSupportFromBoard> pickedLifeSupportFromBoardReceiver;
    private final EventListener<PickedLifeSupportFromBoard> pickedLifeSupportFromBoardListener;

    private final CastEventReceiver<PickedShieldFromBoard> pickedShieldFromBoardReceiver;
    private final EventListener<PickedShieldFromBoard> pickedShieldFromBoardListener;

    private final CastEventReceiver<PickedStorageFromBoard> pickedStorageFromBoardReceiver;
    private final EventListener<PickedStorageFromBoard> pickedStorageFromBoardListener;

    private final CastEventReceiver<PickedTileFromBoard> pickedTileFromBoardReceiver;
    private final EventListener<PickedTileFromBoard> pickedTileFromBoardListener;

    private final CastEventReceiver<PickedTileFromReserve> pickedTileFromReserveReceiver;
    private final EventListener<PickedTileFromReserve> pickedTileFromReserveListener;

    private final CastEventReceiver<PickedTileFromSpaceship> pickedTileFromSpaceshipReceiver;
    private final EventListener<PickedTileFromSpaceship> pickedTileFromSpaceshipListener;

    private final CastEventReceiver<PlacedMainCabin> placedMainCabinReceiver;
    private final EventListener<PlacedMainCabin> placedMainCabinListener;

    private final CastEventReceiver<PlacedTileToBoard> placedTileToBoardReceiver;
    private final EventListener<PlacedTileToBoard> placedTileToBoardListener;

    private final CastEventReceiver<PlacedTileToReserve> placedTileToReserveReceiver;
    private final EventListener<PlacedTileToReserve> placedTileToReserveListener;

    private final CastEventReceiver<PlacedTileToSpaceship> placedTileToSpaceshipReceiver;
    private final EventListener<PlacedTileToSpaceship> placedTileToSpaceshipListener;

    private final CastEventReceiver<PlanetSelected> planetSelectedReceiver;
    private final EventListener<PlanetSelected> planetSelectedListener;

    private final CastEventReceiver<EnemyDefeat> enemyDefeatReceiver;
    private final EventListener<EnemyDefeat> enemyDefeatListener;

    private final CastEventReceiver<MinPlayer> minPlayerReceiver;
    private final EventListener<MinPlayer> minPlayerListener;

    private final CastEventReceiver<MoveMarker> moveMarkerReceiver;
    private final EventListener<MoveMarker> moveMarkerListener;

    private final CastEventReceiver<RemoveMarker> removeMarkerReceiver;
    private final EventListener<RemoveMarker> removeMarkerListener;

    private final CastEventReceiver<PlayerGaveUp> playerGaveUpReceiver;
    private final EventListener<PlayerGaveUp> playerGaveUpListener;

    private final CastEventReceiver<CardPlayed> cardPlayedReceiver;
    private final EventListener<CardPlayed> cardPlayedListener;

    private final CastEventReceiver<CombatZonePhase> combatZonePhaseReceiver;
    private final EventListener<CombatZonePhase> combatZonePhaseListener;

    private final CastEventReceiver<CurrentPlayer> currentPlayerReceiver;
    private final EventListener<CurrentPlayer> currentPlayerListener;

    private final CastEventReceiver<Score> scoreReceiver;
    private final EventListener<Score> scoreListener;

    private final CastEventReceiver<UpdateCoins> updateCoinsReceiver;
    private final EventListener<UpdateCoins> updateCoinsListener;

    private final CastEventReceiver<RotatedTile> rotatedGenericReceiver;
    private final EventListener<RotatedTile> rotatedGenericTileListener;

    private final CastEventReceiver<BestLookingShips> bestLookingShipsReceiver;
    private final EventListener<BestLookingShips> bestLookingShipsListener;

    private final CastEventReceiver<CanProtect> canProtectReceiver;
    private final EventListener<CanProtect> canProtectListener;

    private final CastEventReceiver<ComponentDestroyed> componentDestroyedReceiver;
    private final EventListener<ComponentDestroyed> componentDestroyedListener;

    private final CastEventReceiver<Fragments> fragmentsReceiver;
    private final EventListener<Fragments> fragmentsListener;

    private final CastEventReceiver<InvalidComponents> invalidComponentsReceiver;
    private final EventListener<InvalidComponents> invalidComponentsListener;

    private final CastEventReceiver<SetCannonStrength> setCannonStrengthReceiver;
    private final EventListener<SetCannonStrength> setCannonStrengthListener;

    private final CastEventReceiver<SetEngineStrength> setEngineStrengthReceiver;
    private final EventListener<SetEngineStrength> setEngineStrengthListener;

    private final CastEventReceiver<UpdateCrewMembers> updateCrewMembersReceiver;
    private final EventListener<UpdateCrewMembers> updateCrewMembersListener;

    private final CastEventReceiver<LastTimerFinished> lastTimerFinishedReceiver;
    private final EventListener<LastTimerFinished> lastTimerFinishedListener;

    private final CastEventReceiver<TimerFlipped> timerFlippedReceiver;
    private final EventListener<TimerFlipped> timerFlippedListener;

    private final CastEventReceiver<StateChanged> stateChangedReceiver;
    private final EventListener<StateChanged> stateChangedListener;


    public EventHandlerClient(NetworkTransceiver transceiver, Manager manager) {
        this.transceiver = transceiver;
        this.manager = manager;

        /*
         * Set the userID of the client
         */
        CastEventReceiver<UserIDSet> userIDSetReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<UserIDSet> userIDSetListener = data -> {
            MiniModel.getInstance().setUserID(data.userID());
        };
        userIDSetReceiver.registerListener(userIDSetListener);

        /*
         * Set the nickname of the player
         */
        CastEventReceiver<NicknameSet> nicknameSetReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<NicknameSet> nicknameSetListener = data -> {
            MiniModel.getInstance().setNickname(data.nickname());
            manager.notifyNicknameSet(data);
            registerListeners();
        };
        nicknameSetReceiver.registerListener(nicknameSetListener);

        // Internal events
        CastEventReceiver<ConnectionLost> connectionLostReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<ConnectionLost> connectionLostListener = data -> {
            manager.notifyConnectionLost();
            System.exit(1);
        };
        connectionLostReceiver.registerListener(connectionLostListener);

        // Lobby events
        /*
         * Set all the lobbies
         */
        lobbiesReceiver = new CastEventReceiver<>(this.transceiver);
        lobbiesListener = data -> {
            MiniModel.getInstance().getLobbiesView().clear();
            for (int i = 0; i < data.lobbiesNames().size(); i++) {
                MiniModel.getInstance().getLobbiesView().add(new LobbyView(data.lobbiesNames().get(i), data.lobbiesPlayers().get(i).getValue0(),
                        data.lobbiesPlayers().get(i).getValue1(), LevelView.fromValue(data.lobbiesLevels().get(i))));
            }

            manager.notifyLobbies();
        };

        /*
         * Create a new lobby, if it is created by this client it is set as tha main current lobby
         */
        lobbyCreatedReceiver = new CastEventReceiver<>(this.transceiver);
        lobbyCreatedListener = data -> {
            LobbyView lobby = new LobbyView(data.lobbyID(), 0, data.maxPlayers(), LevelView.fromValue(data.level()));
            MiniModel.getInstance().getLobbiesView().add(lobby);
            if (data.nickname().equals(MiniModel.getInstance().getNickname())) {
                MiniModel.getInstance().setCurrentLobby(lobby);
                MiniModel.getInstance().setBoardView(new BoardView(LevelView.fromValue(data.level()), data.maxPlayers()));
            }
            lobby.addPlayer(data.nickname());

            manager.notifyCreatedLobby(data);
        };

        /*
         * Add a new player inside a specific lobby
         */
        lobbyJoinedReceiver = new CastEventReceiver<>(this.transceiver);
        lobbyJoinedListener = data -> {
            LobbyView lobbyView = MiniModel.getInstance().getLobbiesView().stream()
                    .filter(lobby -> lobby.getLobbyName().equals(data.lobbyID()))
                    .findFirst()
                    .orElse(null);

            if (lobbyView != null) {
                if (MiniModel.getInstance().getNickname().equals(data.nickname())) {
                    MiniModel.getInstance().setCurrentLobby(lobbyView);
                    MiniModel.getInstance().setBoardView(new BoardView(lobbyView.getLevel(), lobbyView.getMaxPlayer()));
                }
                lobbyView.addPlayer(data.nickname());
            }

            manager.notifyLobbyJoined(data);
        };

        /*
         * Remove a player from a specific lobby
         */
        lobbyLeftReceiver = new CastEventReceiver<>(this.transceiver);
        lobbyLeftListener = data -> {
            if (MiniModel.getInstance().getNickname().equals(data.nickname())) {
                MiniModel.getInstance().getOtherPlayers().clear();
            }
            else {
                MiniModel.getInstance().getOtherPlayers()
                        .removeIf(player -> player.getUsername().equals(data.nickname()));
            }

            if (MiniModel.getInstance().getNickname().equals(data.nickname())) {
                MiniModel.getInstance().setCurrentLobby(null);
                MiniModel.getInstance().setClientPlayer(null);
                MiniModel.getInstance().setBoardView(null);
            }

            MiniModel.getInstance().getLobbiesView().stream()
                    .filter(lobby -> lobby.getLobbyName().equals(data.lobbyID()))
                    .findFirst()
                    .ifPresent(lobby -> lobby.removePlayer(data.nickname()));

            manager.notifyLobbyLeft(data);
        };

        /*
         * Remove a lobby
         */
        lobbyRemovedReceiver = new CastEventReceiver<>(this.transceiver);
        lobbyRemovedListener = data -> {
            MiniModel.getInstance().getLobbiesView().stream()
                    .filter(lobby -> lobby.getLobbyName().equals(data.lobbyID()))
                    .findFirst()
                    .ifPresent(lobby -> MiniModel.getInstance().getLobbiesView().remove(lobby));

            MiniModel.getInstance().getOtherPlayers().clear();
            MiniModel.getInstance().setClientPlayer(null);
            MiniModel.getInstance().setBoardView(null);
            MiniModel.getInstance().setCurrentLobby(null);
            manager.notifyLobbyRemoved(data);
        };

        /*
         * Initialize playersDataView
         */
        playerAddedReceiver = new CastEventReceiver<>(this.transceiver);
        playerAddedListener = data -> {
            PlayerDataView player = new PlayerDataView(data.nickname(), MarkerView.fromValue(data.color()), new SpaceShipView(MiniModel.getInstance().getBoardView().getLevel()));
            player.setHand(new GenericComponentView(-1, -1));
            if (MiniModel.getInstance().getNickname().equals(data.nickname())) {
                MiniModel.getInstance().setClientPlayer(player);
            }
            else {
                MiniModel.getInstance().getOtherPlayers().add(player);
            }
        };

        /*
         * Set status player in the lobby
         */
        readyPlayerReceiver = new CastEventReceiver<>(this.transceiver);
        readyPlayerListener = data -> {
            MiniModel.getInstance().getCurrentLobby().setPlayerStatus(data.nickname(), data.isReady());

            manager.notifyReadyPlayer();
        };

        /*
         * Start a countdown to announce that the game is starting
         */
        startingGameReceiver = new CastEventReceiver<>(this.transceiver);
        startingGameListener = data -> {
            new Thread(() -> {
                LocalTime serverTime = LocalTime.parse(data.startingTime());
                LocalTime clientTime = LocalTime.now();
                MiniModel.getInstance().setCountDown(new CountDown());
                int time = (data.timerDuration() / 1000) - Math.max(0, (int)Duration.between(serverTime, clientTime).toSeconds());
                while (time >= 0) {
                    try {
                        MiniModel.getInstance().getCountDown().setSecondsRemaining(time);
                        manager.notifyCountDown();
                        Thread.sleep(1000);
                        time--;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                MiniModel.getInstance().setCountDown(null);
            }).start();
        };

        // GAME EVENTS
        // CARDS events
        /*
         * Initialize AbandonedShip card and add it to the shuffledDeck
         */
        getCardAbandonedShipReceiver = new CastEventReceiver<>(this.transceiver);
        getCardAbandonedShipListener = data -> {
            AbandonedShipView card = new AbandonedShipView(data.ID(), true, data.level(), data.crewRequired(), data.credit(), data.flightDays());
            MiniModel.getInstance().getShuffledDeckView().addCard(card);
        };

        /*
         * Initialize AbandonedStation card and add it to the shuffledDeck
         */
        getCardAbandonedStationReceiver = new CastEventReceiver<>(this.transceiver);
        getCardAbandonedStationListener = data -> {
            List<GoodView> goods = new ArrayList<>();
            for (Integer integer : data.goods()) {
                goods.add(GoodView.fromValue(integer));
            }
            AbandonedStationView card = new AbandonedStationView(data.ID(), true, data.level(), data.crewRequired(), data.flightDays(), goods);
            MiniModel.getInstance().getShuffledDeckView().addCard(card);
        };

        /*
         * Initialize CombatZone card and add it to the shuffledDeck
         */
        getCardCombatZoneReceiver = new CastEventReceiver<>(this.transceiver);
        getCardCombatZoneListener = data -> {
            List<HitView> hits = new ArrayList<>();
            for (Pair<Integer, Integer> pair : data.fires()) {
                hits.add(new HitView(HitTypeView.fromValue(pair.getValue0()), HitDirectionView.fromValue(pair.getValue1())));
            }
            CombatZoneView card = new CombatZoneView(data.ID(), true, data.level(), data.lost(), data.flightDays(), hits);
            MiniModel.getInstance().getShuffledDeckView().addCard(card);
        };

        /*
         * Initialize Epidemic card and add it to the shuffledDeck
         */
        getCardEpidemicReceiver = new CastEventReceiver<>(this.transceiver);
        getCardEpidemicListener = data -> {
            EpidemicView card = new EpidemicView(data.ID(), true, data.level());
            MiniModel.getInstance().getShuffledDeckView().addCard(card);
        };

        /*
         * Initialize MeteorSwarm card and add it to the shuffledDeck
         */
        getCardMeteorSwarmReceiver = new CastEventReceiver<>(this.transceiver);
        getCardMeteorSwarmListener = data -> {
            List<HitView> hits = new ArrayList<>();
            for (Pair<Integer, Integer> pair : data.meteors()) {
                hits.add(new HitView(HitTypeView.fromValue(pair.getValue0()), HitDirectionView.fromValue(pair.getValue1())));
            }

            MeteorSwarmView card = new MeteorSwarmView(data.ID(), true, data.level(), hits);
            MiniModel.getInstance().getShuffledDeckView().addCard(card);
        };

        /*
         * Initialize OpenSpace card and add it to the shuffledDeck
         */
        getCardOpenSpaceReceiver = new CastEventReceiver<>(this.transceiver);
        getCardOpenSpaceListener = data -> {
            OpenSpaceView card = new OpenSpaceView(data.ID(), true, data.level());
            MiniModel.getInstance().getShuffledDeckView().addCard(card);
        };

        /*
         * Initialize Pirates card and add it to the shuffledDeck
         */
        getCardPiratesReceiver = new CastEventReceiver<>(this.transceiver);
        getCardPiratesListener = data -> {
            List<HitView> hits = new ArrayList<>();
            for (Pair<Integer, Integer> pair : data.fires()) {
                hits.add(new HitView(HitTypeView.fromValue(pair.getValue0()), HitDirectionView.fromValue(pair.getValue1())));
            }
            PiratesView card = new PiratesView(data.ID(), true, data.level(), data.cannonStrengthRequired(), data.credit(), data.flightDays(), hits);
            
            MiniModel.getInstance().getShuffledDeckView().addCard(card);
        };

        /*
         * Initialize Pirates card and add it to the shuffledDeck
         */
        getCardPlanetsReceiver = new CastEventReceiver<>(this.transceiver);
        getCardPlanetsListener = data -> {
            List<List<GoodView>> planets = new ArrayList<>();
            for(List<Integer> planet : data.planets()) {
                List<GoodView> goods = new ArrayList<>();
                for (Integer integer : planet) {
                    goods.add(GoodView.fromValue(integer));
                }
                planets.add(goods);
            }

            PlanetsView card = new PlanetsView(data.ID(), true, data.level(), data.flightDays(), planets);
            MiniModel.getInstance().getShuffledDeckView().addCard(card);
        };

        /*
         * Initialize Slavers card and add it to the shuffledDeck
         */
        getCardSlaversReceiver = new CastEventReceiver<>(this.transceiver);
        getCardSlaversListener = data -> {
            SlaversView card = new SlaversView(data.ID(), true, data.level(), data.cannonStrengthRequired(), data.credit(), data.flightDays(), data.crewLost());
            MiniModel.getInstance().getShuffledDeckView().addCard(card);
        };

        /*
         * Initialize Smugglers card and add it to the shuffledDeck
         */
        getCardSmugglersReceiver = new CastEventReceiver<>(this.transceiver);
        getCardSmugglersListener = data -> {
            List<GoodView> goods = new ArrayList<>();
            for (Integer integer : data.goodsReward()) {
                goods.add(GoodView.fromValue(integer));
            }
            SmugglersView card = new SmugglersView(data.ID(), true, data.level(), data.cannonStrengthRequired(), data.goodsLoss(), data.flightDays(), goods);
            MiniModel.getInstance().getShuffledDeckView().addCard(card);
        };

        /*
         * Initialize StarDust card and add it to the shuffledDeck
         */
        getCardStardustReceiver = new CastEventReceiver<>(this.transceiver);
        getCardStardustListener = data -> {
            StarDustView card = new StarDustView(data.ID(), true, data.level());
            MiniModel.getInstance().getShuffledDeckView().addCard(card);
        };

        // DECK events

        /*
         * Initialize Decks in the MiniModel
         */
        getDecksReceiver = new CastEventReceiver<>(this.transceiver);
        getDecksListener = data -> {
            for (int i = 0; i < data.decks().size(); i++) {
                DeckView deck = new DeckView();
                for (Integer ID : data.decks().get(i)) {
                    MiniModel.getInstance().getShuffledDeckView().getDeck().stream()
                            .filter(card -> card.getID() == ID)
                            .findFirst()
                            .ifPresent(deck::addCard);
                }
                deck.setCovered(true);
                MiniModel.getInstance().getDeckViews().getValue0()[i] = deck;
                MiniModel.getInstance().getDeckViews().getValue1()[i] = true;
            }
        };

        /*
         * Order the shuffled deck
         */
        getShuffledDeckReceiver = new CastEventReceiver<>(this.transceiver);
        getShuffledDeckListener = data -> {
            MiniModel.getInstance().getShuffledDeckView().order(data.shuffledDeck());
            for (CardView card : MiniModel.getInstance().getShuffledDeckView().getDeck()) {
                card.setCovered(false);
            }
            MiniModel.getInstance().getShuffledDeckView().setOnlyLast((true));
        };

        /*
         * Set the viewable status of the deck. If deck == false it is not viewable in the building screen
         */
        pickedLeftDeckReceiver = new CastEventReceiver<>(this.transceiver);
        pickedLeftDeckListener = data -> {
            /* The decks are stored in a Pair: The first element is the deck views, and the second element is a boolean array.
             If boolean[i] == true the deck[i] is not taken by a player, else deck is taken and not viewable in the building screen*/
            Pair<DeckView[], Boolean[]> decksView = MiniModel.getInstance().getDeckViews();

            if (data.usage() == 0) {
                decksView.getValue1()[data.deckIndex()] = false;
                if (MiniModel.getInstance().getNickname().equals(data.nickname())) {
                    decksView.getValue0()[data.deckIndex()].setCovered(false);
                }
            }
            else{
                decksView.getValue1()[data.deckIndex()] = true;
                decksView.getValue0()[data.deckIndex()].setCovered(true);
            }

            manager.notifyPickedLeftDeck(data);
        };

        // DICE
        diceRolledReceiver = new CastEventReceiver<DiceRolled>(this.transceiver);
        diceRolledListener = data -> {
            MiniModel.getInstance().setDice(new Pair<>(data.diceValue1(), data.diceValue2()));

            manager.notifyDiceRolled(data);
        };

        // ENERGY USED events
        batteriesLossReceiver = new CastEventReceiver<>(this.transceiver);
        getBatteriesLossListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());
            for (Pair<Integer, Integer> pair : data.batteriesIDs()) {
                player.getShip().getMapBatteries().get(pair.getValue0()).setNumberOfBatteries(pair.getValue1());
            }

            manager.notifyBatteriesLoss(data);
        };

        /*
         * Force the player to do giveUp
         */
        forcingGiveUpReceiver = new CastEventReceiver<>(this.transceiver);
        forcingGiveUpListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            MiniModel.getInstance().setCurrentPlayer(player);
            manager.notifyForcingGiveUp(data);
        };

        forcingPenaltyReceiver = new CastEventReceiver<>(this.transceiver);
        forcingPenaltyListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            MiniModel.getInstance().setCurrentPlayer(player);
            if (data.penaltyType() == 3) {
                CardView card = MiniModel.getInstance().getShuffledDeckView().getDeck().peek();
                if (card.getCardViewType() == CardViewType.METEORSSWARM) {
                    ((MeteorSwarmView) card).nextHit();
                } else if (card.getCardViewType() == CardViewType.PIRATES) {
                    ((PiratesView) card).nextHit();
                }
            }

             manager.notifyForcingPenalty(data);
        };

        forcingPlaceMarkerReceiver = new CastEventReceiver<>(this.transceiver);
        forcingPlaceMarkerListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());
            manager.notifyForcingPlaceMarker(data);
        };

        // GOODS events
        /*
         * Update status of storages
         */
        updateGoodsExchangeReceiver = new CastEventReceiver<>(this.transceiver);
        updateGoodsExchangeListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());
            for (Pair<Integer, List<Integer>> pair : data.exchangeData()) {
                int i;
                int capacity = player.getShip().getMapStorages().get(pair.getValue0()).getCapacity();
                GoodView[] newGoods = new GoodView[capacity];
                for (i = 0; i < pair.getValue1().size(); i++) {
                    newGoods[i] = GoodView.fromValue(pair.getValue1().get(i));
                }

                player.getShip().getMapStorages().get(pair.getValue0()).changeGoods(newGoods);
            }

            manager.notifyUpdateGoodsExchange(data);
        };

        // PICK TILE events
        /*
         * Set the hidden tiles in the MiniModel
         */
        numberHiddenTilesReceiver = new CastEventReceiver<>(this.transceiver);
        numberHiddenTilesListener = data -> {
            MiniModel.getInstance().setNumberHiddenComponents(data.hiddenTilesCount());
        };

        /*
         * Create a new BatteryView and set it in the player's hand
         */
        pickedBatteryFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        pickedBatteryFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            BatteryView battery = new BatteryView(data.tileID(), data.connectors(), data.clockwiseRotation(), data.energyNumber());
            player.setHand(battery);

            manager.notifyPickedHiddenTile(data.nickname());
        };

        /*
         * Create a new CabinView and set it in the player's hand
         */
        pickedCabinFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        pickedCabinFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            CabinView cabin = new CabinView(data.tileID(), data.connectors(), data.clockwiseRotation());
            player.setHand(cabin);

            manager.notifyPickedHiddenTile(data.nickname());
        };

        /*
         * Create a new CannonView and set it in the player's hand
         */
        pickedCannonFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        pickedCannonFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            CannonView cannon = new CannonView(data.tileID(), data.connectors(), data.clockwiseRotation(), data.cannonStrength());
            player.setHand(cannon);

            manager.notifyPickedHiddenTile(data.nickname());
        };

        /*
         * Create a new ConnectorsView and set it in the player's hand
         */
        pickedConnectorsFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        pickedConnectorsFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            ConnectorsView pipes = new ConnectorsView(data.tileID(), data.connectors(), data.clockwiseRotation());
            player.setHand(pipes);

            manager.notifyPickedHiddenTile(data.nickname());
        };

        /*
         * Create a new EngineView and set it in the player's hand
         */
        pickedEngineFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        pickedEngineFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            EngineView engine = new EngineView(data.tileID(), data.connectors(), data.clockwiseRotation(), data.cannonStrength());
            player.setHand(engine);

            manager.notifyPickedHiddenTile(data.nickname());
        };

        /*
         * Create a new LifeSupportView and set it in the player's hand
         */
        pickedLifeSupportFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        pickedLifeSupportFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());
            ComponentView lifeSupport;
            if (data.type() == 1) {
                lifeSupport = new LifeSupportBrownView(data.tileID(), data.connectors(), data.clockwiseRotation());
            }
            else {
                lifeSupport = new LifeSupportPurpleView(data.tileID(), data.connectors(), data.clockwiseRotation());
            }
            player.setHand(lifeSupport);

            manager.notifyPickedHiddenTile(data.nickname());
        };

        /*
         * Create a new ShieldView and set it in the player's hand
         */
        pickedShieldFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        pickedShieldFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());
            boolean[] shields = new boolean[data.connectors().length];
            shields[data.clockwiseRotation()] = true;
            shields[(data.clockwiseRotation() - 1 + data.connectors().length) % data.connectors().length] = true;
            ShieldView shield = new ShieldView(data.tileID(), data.connectors(), data.clockwiseRotation(), shields);
            player.setHand(shield);

            manager.notifyPickedHiddenTile(data.nickname());
        };

        /*
         * Create a new StorageView and set it in the player's hand
         */
        pickedStorageFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        pickedStorageFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            StorageView storage = new StorageView(data.tileID(), data.connectors(), data.clockwiseRotation(), data.dangerous(), data.goodsCapacity());
            player.setHand(storage);

            manager.notifyPickedTileFromBoard();
        };

        /*
         * Picked tile from board
         */
        pickedTileFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        pickedTileFromBoardListener = data -> {
            ComponentView tile = MiniModel.getInstance().getViewablePile().getViewableComponents().stream()
                    .filter(component -> component.getID() == data.tileID())
                    .findFirst()
                    .orElse(null);

            if (tile != null) {
                MiniModel.getInstance().getViewablePile().removeComponent(tile);
            }

            PlayerDataView player = getPlayerDataView(data.nickname());
            player.setHand(tile);

            manager.notifyPickedTileFromBoard();

        };

        /*
         * Remove a tile from the reserved list and add it into the hand
         */
        pickedTileFromReserveReceiver = new CastEventReceiver<>(this.transceiver);
        pickedTileFromReserveListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            ComponentView tile = player.getShip().getDiscardReservedPile().removeDiscardReserved(data.tileID());
            player.setHand(tile);

            manager.notifyPickedTileFromBoard();
        };

        /*
         * Remove the last tile on the ship and add it into the hand
         */
        pickedTileFromSpaceshipReceiver = new CastEventReceiver<>(this.transceiver);
        pickedTileFromSpaceshipListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());
            player.setHand(player.getShip().removeLast());

            manager.notifyPickedTileFromSpaceShip(data);
        };


        // PLACED TILE events
        placedMainCabinReceiver = new CastEventReceiver<>(this.transceiver);
        placedMainCabinListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());
            CabinView cabin = new CabinView(data.tileID(), data.connectors(), 0);

            player.getShip().placeComponent(cabin, 6, 6);
        };

        /*
         * Remove the tile from the player's hand and add it to the viewable components
         */
        placedTileToBoardReceiver = new CastEventReceiver<>(this.transceiver);
        placedTileToBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            MiniModel.getInstance().getViewablePile().addComponent(player.getHand());
            player.setHand(new GenericComponentView(-1, -1));

            manager.notifyPlacedTileToBoard(data);
        };

        /*
         * Remove tile from the player's hand and add it to the reserved pile
         */
        placedTileToReserveReceiver = new CastEventReceiver<>(this.transceiver);
        placedTileToReserveListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            player.getShip().addDiscardReserved(player.getHand());
            player.setHand(new GenericComponentView(-1, -1));

            manager.notifyPlacedTileToReserve(data);
        };

        /*
         * Remove tile from the player's hand and add it to the spaceship
         */
        placedTileToSpaceshipReceiver = new CastEventReceiver<>(this.transceiver);
        placedTileToSpaceshipListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            player.getShip().placeComponent(player.getHand(), data.row(), data.column());
            player.setHand(new GenericComponentView(-1, -1));

            manager.notifyPlacedTileToSpaceship(data);
        };


        // PLANETS events
        /*
         * Set the select planet and add the player's marker on the card
         */
        planetSelectedReceiver = new CastEventReceiver<>(this.transceiver);
        planetSelectedListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());
            PlanetsView card = (PlanetsView) MiniModel.getInstance().getShuffledDeckView().getDeck().peek();
            card.setPlanetSelected(data.planetNumber());
            card.setPlayersPosition(data.planetNumber(), player.getMarkerView());

            manager.notifyPlanetSelected(data);
        };


        // PLAYER events
        enemyDefeatReceiver = new CastEventReceiver<>(this.transceiver);
        enemyDefeatListener = data -> {
            manager.notifyEnemyDefeat(data);
        };

        minPlayerReceiver = new CastEventReceiver<>(this.transceiver);
        minPlayerListener = data -> {
            //TODO
        };

        /*
         * Move the marker of the player on the board
         */
        moveMarkerReceiver = new CastEventReceiver<>(this.transceiver);
        moveMarkerListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            MiniModel.getInstance().getBoardView().movePlayer(player.getMarkerView(), data.steps());

            manager.notifyMoveMarker(data);
        };

        /*
         * Remove the marker of the player from the board
         */
        removeMarkerReceiver = new CastEventReceiver<>(this.transceiver);
        removeMarkerListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            MiniModel.getInstance().getBoardView().removePlayer(player.getMarkerView());

            manager.notifyRemoveMarker(data);
        };

        /*
         * Notify the player has given up
         */
        playerGaveUpReceiver = new CastEventReceiver<>(this.transceiver);
        playerGaveUpListener = manager::notifyPlayerGaveUp;

        /*
         * Notify the player has played a card
         */
        cardPlayedReceiver = new CastEventReceiver<>(this.transceiver);
        cardPlayedListener = manager::notifyCardPlayed;

        combatZonePhaseReceiver = new CastEventReceiver<>(this.transceiver);
        combatZonePhaseListener = data -> {
            CombatZoneView card = (CombatZoneView) MiniModel.getInstance().getShuffledDeckView().getDeck().peek();
            card.setCont(data.phaseNumber());

            manager.notifyCombatZonePhase(data);
        };

        /*
         * Notify who is the player who is playing
         */
        currentPlayerReceiver = new CastEventReceiver<>(this.transceiver);
        currentPlayerListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());
            MiniModel.getInstance().setCurrentPlayer(player);

            manager.notifyCurrentPlayer(data);
        };

        scoreReceiver = new CastEventReceiver<>(this.transceiver);
        scoreListener = data -> {
            MiniModel.getInstance().setRewardPhase(data.rewardPhase());
            for (Pair<String, Integer> p : data.playerScores()) {
                PlayerDataView player = getPlayerDataView(p.getValue0());
                player.setCoins(p.getValue1());
            }
            manager.notifyScore(data);
        };


        /*
         * Set the number if coins to the player
         */
        updateCoinsReceiver = new CastEventReceiver<>(this.transceiver);
        updateCoinsListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            player.setCoins(data.coins());

            manager.notifyUpdateCoins(data);
        };


        // ROTATED TILE events
        /*
         * Rotate tiles
         */
        rotatedGenericReceiver = new CastEventReceiver<>(this.transceiver);
        rotatedGenericTileListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());
            ComponentView tile = player.getHand();
            tile.rotate();
            if (tile.getType() == ComponentTypeView.SHIELD) {
                boolean[] shields = new boolean[data.connectors().length];
                shields[tile.getClockWise()] = true;
                shields[((tile.getClockWise() - 1) + data.connectors().length) % data.connectors().length] = true;
                ((ShieldView) tile).setShields(shields);
            }
            tile.setConnectors(data.connectors());

            manager.notifyRotatedTile(data);
        };


        // SPACESHIP events
        /*
         * Notify the best looking ships
         */
        bestLookingShipsReceiver = new CastEventReceiver<>(this.transceiver);
        bestLookingShipsListener = manager::notifyBestLookingShips;

        /*
         *
         */
        canProtectReceiver = new CastEventReceiver<>(this.transceiver);
        canProtectListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            MiniModel.getInstance().setCurrentPlayer(player);
            manager.notifyCanProtect(data);
        };


        /*
         * Move the tiles from the board to the discard pile
         */
        componentDestroyedReceiver = new CastEventReceiver<>(this.transceiver);
        componentDestroyedListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            if (player != null) {
                for (Pair<Integer, Integer> tile : data.destroyedComponents()) {
                    ComponentView tmp = player.getShip().removeComponent(tile.getValue0(), tile.getValue1());
                    player.getShip().addDiscardReserved(tmp);
                }
            }

            manager.notifyComponentDestroyed(data);
        };

        /*
         * Set the fragments of the ship
         */
        fragmentsReceiver = new CastEventReceiver<>(this.transceiver);
        fragmentsListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            if (player != null) {
                player.getShip().setFragments(data.fragments());
            }

            manager.notifyFragments(data);
        };

        /*
         * Set wrong tiles
         */
        invalidComponentsReceiver = new CastEventReceiver<>(this.transceiver);
        invalidComponentsListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());
            ComponentView[][] ship = player.getShip().getSpaceShip();
            for (ComponentView[] componentViews : ship) {
                for (ComponentView componentView : componentViews) {
                    if (componentView != null) {
                        componentView.setIsWrong(false);
                    }
                }
            }
            for (Pair<Integer, Integer> pair : data.invalidComponents()) {
                player.getShip().getComponent(pair.getValue0(), pair.getValue1()).setIsWrong(true);
            }

            manager.notifyInvalidComponents(data);
        };

        /*
         * Update cannons strength
         */
        setCannonStrengthReceiver = new CastEventReceiver<>(this.transceiver);
        setCannonStrengthListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            player.setCannonsStrength(data.singleCannonsStrength());
            player.setMaxPotentialCannonsStrength(data.maxCannonsStrength());
            manager.notifySetCannonStrength(data);
        };

        /*
         * Update engine strength
         */
        setEngineStrengthReceiver = new CastEventReceiver<>(this.transceiver);
        setEngineStrengthListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            player.setEnginesStrength(data.singleEnginesStrength());
            player.setMaxPotentialEnginesStrength(data.maxEnginesStrength());

            manager.notifySetEngineStrength(data);
        };

        /*
         * Update crew members
         */
        updateCrewMembersReceiver = new CastEventReceiver<>(this.transceiver);
        updateCrewMembersListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            for (Triplet<Integer, Integer, Integer> cabin : data.cabins()) {
                CabinView c = player.getShip().getMapCabins().get(cabin.getValue0());
                c.setCrewNumber(cabin.getValue1());
                c.setCrewType(CrewView.fromValue(cabin.getValue2()));
            }

            manager.notifyUpdateCrewMembers(data);
        };

        lastTimerFinishedReceiver = new CastEventReceiver<>(this.transceiver);
        lastTimerFinishedListener = data -> {
            manager.notifyLastTimerFlipped();
        };


        /*
         * Start the timer for the building phase
         */
        timerFlippedReceiver = new CastEventReceiver<>(this.transceiver);
        timerFlippedListener = data -> {
            TimerView timer = MiniModel.getInstance().getTimerView();
            timer.setNumberOfFlips(data.numberOfFlips());
            timer.setTotalFlips(data.maxNumberOfFlips());
            new Thread(() -> {
                boolean firstSecond = true;
                timer.setFlippedTimer(getPlayerDataView(data.nickname()));
                LocalTime serverTime = LocalTime.parse(data.startingTime());
                LocalTime clientTime = LocalTime.now();
                int time = (int) ((data.timerDuration() / 1000) - Math.max(0, Duration.between(serverTime, clientTime).toSeconds()));
                while (time >= 0) {
                    try {
                        if (MiniModel.getInstance().getGamePhase() == GamePhases.BUILDING) {
                            MiniModel.getInstance().getTimerView().setSecondsRemaining(time);
                            manager.notifyTimer(data, firstSecond);
                            if (firstSecond) firstSecond = false;
                            Thread.sleep(1000);
                            time--;
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                manager.notifyTimerFinished(data);
            }).start();
        };

        stateChangedReceiver = new CastEventReceiver<>(this.transceiver);
        stateChangedListener = data -> {
            if (data.newState() == GamePhases.CARDS.getValue() && MiniModel.getInstance().getGamePhase().getValue() == data.newState()) {
                MiniModel.getInstance().getShuffledDeckView().popCard();
                MiniModel.getInstance().getShuffledDeckView().setOnlyLast(true);
            }

            if (data.newState() == GamePhases.VALIDATION.getValue()) {
                PlayerDataView player = MiniModel.getInstance().getClientPlayer();
                player.getShip().getDiscardReservedPile().setIsDiscarded();
                for (PlayerDataView otherPlayer : MiniModel.getInstance().getOtherPlayers()) {
                    otherPlayer.getShip().getDiscardReservedPile().setIsDiscarded();
                }
            }
            if (data.newState() == GamePhases.CREW.getValue()) {
                MiniModel.getInstance().getClientPlayer().setHand(null);
                for (PlayerDataView player : MiniModel.getInstance().getOtherPlayers()) {
                    player.setHand(null);
                }
                PlayerDataView clientPlayer = MiniModel.getInstance().getClientPlayer();

                for (BatteryView battery : clientPlayer.getShip().getMapBatteries().values()) {
                    battery.setNumberOfBatteries(battery.getMaximumBatteries());
                }

                for (PlayerDataView player : MiniModel.getInstance().getOtherPlayers()) {
                    for (BatteryView battery : player.getShip().getMapBatteries().values()) {
                        battery.setNumberOfBatteries(battery.getMaximumBatteries());
                    }
                }

                if (MiniModel.getInstance().getBoardView().getLevel() == LevelView.SECOND) {
                    Arrays.fill(MiniModel.getInstance().getDeckViews().getValue1(), true);
                }

            }
            if (data.newState() == GamePhases.FINISHED.getValue()) {
                MiniModel mm = MiniModel.getInstance();
                mm.setClientPlayer(null);
                mm.setBoardView(null);
                mm.setCurrentPlayer(null);
                mm.setClientPlayer(null);
                mm.setCountDown(null);
                mm.setDice(null);
                mm.getOtherPlayers().clear();
                for (ComponentView tile : mm.getViewablePile().getViewableComponents()) {
                    mm.getViewablePile().removeComponent(tile);
                }
                mm.getShuffledDeckView().getDeck().clear();
            }

            MiniModel.getInstance().setGamePhase(data.newState());
            manager.notifyStateChange();
        };
    }

    private PlayerDataView getPlayerDataView(String nickname) {
        if (MiniModel.getInstance().getClientPlayer().getUsername().equals(nickname)) {
            return MiniModel.getInstance().getClientPlayer();
        }

        return MiniModel.getInstance().getOtherPlayers().stream()
                .filter(p -> p.getUsername().equals(nickname))
                .findFirst()
                .orElse(null);
    }

    private void registerListeners() {
        lobbiesReceiver.registerListener(lobbiesListener);
        lobbyCreatedReceiver.registerListener(lobbyCreatedListener);
        lobbyJoinedReceiver.registerListener(lobbyJoinedListener);
        lobbyLeftReceiver.registerListener(lobbyLeftListener);
        lobbyRemovedReceiver.registerListener(lobbyRemovedListener);
        playerAddedReceiver.registerListener(playerAddedListener);
        readyPlayerReceiver.registerListener(readyPlayerListener);
        startingGameReceiver.registerListener(startingGameListener);

        getCardAbandonedShipReceiver.registerListener(getCardAbandonedShipListener);
        getCardAbandonedStationReceiver.registerListener(getCardAbandonedStationListener);
        getCardCombatZoneReceiver.registerListener(getCardCombatZoneListener);
        getCardEpidemicReceiver.registerListener(getCardEpidemicListener);
        getCardMeteorSwarmReceiver.registerListener(getCardMeteorSwarmListener);
        getCardOpenSpaceReceiver.registerListener(getCardOpenSpaceListener);
        getCardPiratesReceiver.registerListener(getCardPiratesListener);
        getCardPlanetsReceiver.registerListener(getCardPlanetsListener);
        getCardSlaversReceiver.registerListener(getCardSlaversListener);
        getCardSmugglersReceiver.registerListener(getCardSmugglersListener);
        getCardStardustReceiver.registerListener(getCardStardustListener);

        getDecksReceiver.registerListener(getDecksListener);
        getShuffledDeckReceiver.registerListener(getShuffledDeckListener);
        pickedLeftDeckReceiver.registerListener(pickedLeftDeckListener);

        diceRolledReceiver.registerListener(diceRolledListener);

        batteriesLossReceiver.registerListener(getBatteriesLossListener);

        forcingGiveUpReceiver.registerListener(forcingGiveUpListener);
        forcingPenaltyReceiver.registerListener(forcingPenaltyListener);
        forcingPlaceMarkerReceiver.registerListener(forcingPlaceMarkerListener);

        updateGoodsExchangeReceiver.registerListener(updateGoodsExchangeListener);

        numberHiddenTilesReceiver.registerListener(numberHiddenTilesListener);

        pickedBatteryFromBoardReceiver.registerListener(pickedBatteryFromBoardListener);
        pickedCabinFromBoardReceiver.registerListener(pickedCabinFromBoardListener);
        pickedCannonFromBoardReceiver.registerListener(pickedCannonFromBoardListener);
        pickedConnectorsFromBoardReceiver.registerListener(pickedConnectorsFromBoardListener);
        pickedEngineFromBoardReceiver.registerListener(pickedEngineFromBoardListener);
        pickedLifeSupportFromBoardReceiver.registerListener(pickedLifeSupportFromBoardListener);
        pickedShieldFromBoardReceiver.registerListener(pickedShieldFromBoardListener);
        pickedStorageFromBoardReceiver.registerListener(pickedStorageFromBoardListener);
        pickedTileFromBoardReceiver.registerListener(pickedTileFromBoardListener);
        pickedTileFromReserveReceiver.registerListener(pickedTileFromReserveListener);
        pickedTileFromSpaceshipReceiver.registerListener(pickedTileFromSpaceshipListener);

        placedMainCabinReceiver.registerListener(placedMainCabinListener);
        placedTileToBoardReceiver.registerListener(placedTileToBoardListener);
        placedTileToReserveReceiver.registerListener(placedTileToReserveListener);
        placedTileToSpaceshipReceiver.registerListener(placedTileToSpaceshipListener);

        planetSelectedReceiver.registerListener(planetSelectedListener);

        enemyDefeatReceiver.registerListener(enemyDefeatListener);
        minPlayerReceiver.registerListener(minPlayerListener);
        moveMarkerReceiver.registerListener(moveMarkerListener);
        removeMarkerReceiver.registerListener(removeMarkerListener);
        playerGaveUpReceiver.registerListener(playerGaveUpListener);
        cardPlayedReceiver.registerListener(cardPlayedListener);
        combatZonePhaseReceiver.registerListener(combatZonePhaseListener);
        currentPlayerReceiver.registerListener(currentPlayerListener);
        scoreReceiver.registerListener(scoreListener);
        updateCoinsReceiver.registerListener(updateCoinsListener);

        rotatedGenericReceiver.registerListener(rotatedGenericTileListener);

        bestLookingShipsReceiver.registerListener(bestLookingShipsListener);
        canProtectReceiver.registerListener(canProtectListener);
        componentDestroyedReceiver.registerListener(componentDestroyedListener);
        fragmentsReceiver.registerListener(fragmentsListener);
        invalidComponentsReceiver.registerListener(invalidComponentsListener);
        setCannonStrengthReceiver.registerListener(setCannonStrengthListener);
        setEngineStrengthReceiver.registerListener(setEngineStrengthListener);
        updateCrewMembersReceiver.registerListener(updateCrewMembersListener);

        timerFlippedReceiver.registerListener(timerFlippedListener);
        lastTimerFinishedReceiver.registerListener(lastTimerFinishedListener);

        stateChangedReceiver.registerListener(stateChangedListener);
    }
}