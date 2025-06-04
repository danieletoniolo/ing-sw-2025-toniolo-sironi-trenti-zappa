package it.polimi.ingsw.view;

import it.polimi.ingsw.event.EventListener;
import it.polimi.ingsw.event.NetworkTransceiver;
import it.polimi.ingsw.event.game.serverToClient.*;
import it.polimi.ingsw.event.game.serverToClient.cards.*;
import it.polimi.ingsw.event.game.serverToClient.deck.*;
import it.polimi.ingsw.event.game.serverToClient.dice.DiceRolled;
import it.polimi.ingsw.event.game.serverToClient.energyUsed.*;
import it.polimi.ingsw.event.game.serverToClient.goods.*;
import it.polimi.ingsw.event.game.serverToClient.pickedTile.*;
import it.polimi.ingsw.event.game.serverToClient.placedTile.*;
import it.polimi.ingsw.event.game.serverToClient.planets.PlanetSelected;
import it.polimi.ingsw.event.game.serverToClient.player.*;
import it.polimi.ingsw.event.game.serverToClient.rotatedTile.RotatedTile;
import it.polimi.ingsw.event.game.serverToClient.spaceship.*;
import it.polimi.ingsw.event.game.serverToClient.timer.*;
import it.polimi.ingsw.event.lobby.serverToClient.*;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.view.miniModel.board.BoardView;
import it.polimi.ingsw.view.miniModel.cards.*;
import it.polimi.ingsw.view.miniModel.cards.hit.HitDirectionView;
import it.polimi.ingsw.view.miniModel.cards.hit.HitTypeView;
import it.polimi.ingsw.view.miniModel.cards.hit.HitView;
import it.polimi.ingsw.view.miniModel.components.*;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventHandlerClient {
    NetworkTransceiver transceiver;
    Manager manager;

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

            manager.notifyNicknameSet();
        };
        nicknameSetReceiver.registerListener(nicknameSetListener);

        // Lobby events
        /*
         * Set all the lobbies
         */
        CastEventReceiver<Lobbies> lobbiesReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<Lobbies> lobbiesListener = data -> {
            for (int i = 0; i < data.lobbiesNames().size(); i++) {
                MiniModel.getInstance().getLobbiesView().add(new LobbyView(data.lobbiesNames().get(i), data.lobbiesPlayers().get(i).getValue0(),
                        data.lobbiesPlayers().get(i).getValue1(), LevelView.fromValue(data.lobbiesLevels().get(i))));
            }

            manager.notifyLobbies();
        };
        lobbiesReceiver.registerListener(lobbiesListener);

        /*
         * Create a new lobby, if it is created by this client it is set as tha main current lobby
         */
        CastEventReceiver<LobbyCreated> lobbyCreatedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<LobbyCreated> lobbyCreatedListener = data -> {
            LobbyView lobby = new LobbyView(data.lobbyID(), 0, data.maxPlayers(), LevelView.fromValue(data.level()));
            MiniModel.getInstance().getLobbiesView().add(lobby);
            if (data.nickname().equals(MiniModel.getInstance().getNickname())) {
                MiniModel.getInstance().setCurrentLobby(lobby);
                MiniModel.getInstance().setBoardView(new BoardView(LevelView.fromValue(data.level())));
            }
            lobby.addPlayer(data.nickname());

            manager.notifyCreatedLobby(data);
        };
        lobbyCreatedReceiver.registerListener(lobbyCreatedListener);

        /*
         * Add a new player inside a specific lobby
         */
        CastEventReceiver<LobbyJoined> lobbyJoinedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<LobbyJoined> lobbyJoinedListener = data -> {
            LobbyView lobbyView = MiniModel.getInstance().getLobbiesView().stream()
                    .filter(lobby -> lobby.getLobbyName().equals(data.lobbyID()))
                    .findFirst()
                    .orElse(null);

            if (lobbyView != null) {
                if (MiniModel.getInstance().getNickname().equals(data.nickname())) {
                    MiniModel.getInstance().setCurrentLobby(lobbyView);
                    MiniModel.getInstance().setBoardView(new BoardView(lobbyView.getLevel()));
                }
                lobbyView.addPlayer(data.nickname());
            }

            manager.notifyLobbyJoined(data);
        };
        lobbyJoinedReceiver.registerListener(lobbyJoinedListener);

        /*
         * Remove a player from a specific lobby
         */
        CastEventReceiver<LobbyLeft> lobbyLeftReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<LobbyLeft> lobbyLeftListener = data -> {
            MiniModel.getInstance().getOtherPlayers()
                    .removeIf(player -> player.getUsername().equals(data.nickname()));

            if (MiniModel.getInstance().getNickname().equals(data.nickname())) {
                MiniModel.getInstance().setCurrentLobby(null);
                MiniModel.getInstance().setCurrentPlayer(null);
                MiniModel.getInstance().setBoardView(null);
            }

            MiniModel.getInstance().getLobbiesView().stream()
                    .filter(lobby -> lobby.getLobbyName().equals(data.lobbyID()))
                    .findFirst()
                    .ifPresent(lobby -> lobby.removePlayer(data.nickname()));

            manager.notifyLobbyLeft(data);
        };
        lobbyLeftReceiver.registerListener(lobbyLeftListener);

        /*
         * Remove a lobby
         */
        CastEventReceiver<LobbyRemoved> lobbyRemovedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<LobbyRemoved> lobbyRemovedListener = data -> {
            MiniModel.getInstance().getLobbiesView().stream()
                    .filter(lobby -> lobby.getLobbyName().equals(data.lobbyID()))
                    .findFirst()
                    .ifPresent(lobby -> MiniModel.getInstance().getLobbiesView().remove(lobby));

            MiniModel.getInstance().setCurrentLobby(null);
            MiniModel.getInstance().setCurrentPlayer(null);
            manager.notifyLobbyRemoved(data);
        };
        lobbyRemovedReceiver.registerListener(lobbyRemovedListener);

        /*
         * Initialize playersDataView
         */
        CastEventReceiver<PlayerAdded> playerAddedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PlayerAdded> playerAddedListener = data -> {
            PlayerDataView player = new PlayerDataView(data.nickname(), MarkerView.fromValue(data.color()), new SpaceShipView(MiniModel.getInstance().getBoardView().getLevel()));
            player.setHand(new GenericComponentView());
            if (MiniModel.getInstance().getNickname().equals(data.nickname())) {
                MiniModel.getInstance().setClientPlayer(player);
            }
            else {
                MiniModel.getInstance().getOtherPlayers().add(player);
            }
        };
        playerAddedReceiver.registerListener(playerAddedListener);

        /*
         * Set status player in the lobby
         */
        CastEventReceiver<ReadyPlayer> playerReadyReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<ReadyPlayer> playerReadyListener = data -> {
            MiniModel.getInstance().getCurrentLobby().setPlayerStatus(data.nickname(), data.isReady());
        };
        playerReadyReceiver.registerListener(playerReadyListener);

        /*
         * Start a countdown to announce that the game is starting
         */
        CastEventReceiver<StartingGame> startingGameReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<StartingGame> startingGameListener = data -> {
            new Thread(() -> {
                LocalDateTime serverTime = LocalDateTime.parse(data.startingTime());
                LocalDateTime clientTime = LocalDateTime.now();
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
                manager.notifyStartingGame(data);
            }).start();
        };
        startingGameReceiver.registerListener(startingGameListener);

        // GAME EVENTS
        // CARDS events
        /*
         * Initialize AbandonedShip card and add it to the shuffledDeck
         */
        CastEventReceiver<GetCardAbandonedShip> getCardAbandonedShipReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<GetCardAbandonedShip> getCardAbandonedShipListener = data -> {
            AbandonedShipView card = new AbandonedShipView(data.ID(), false, data.level(), data.crewRequired(), data.credit(), data.flightDays());
            MiniModel.getInstance().getShuffledDeckView().getDeck().add(card);
        };
        getCardAbandonedShipReceiver.registerListener(getCardAbandonedShipListener);

        /*
         * Initialize AbandonedStation card and add it to the shuffledDeck
         */
        CastEventReceiver<GetCardAbandonedStation> getCardAbandonedStationReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<GetCardAbandonedStation> getCardAbandonedStationListener = data -> {
            List<GoodView> goods = new ArrayList<>();
            for (Integer integer : data.goods()) {
                goods.add(GoodView.fromValue(integer));
            }
            AbandonedStationView card = new AbandonedStationView(data.ID(), false, data.level(), data.crewRequired(), data.flightDays(), goods);
            MiniModel.getInstance().getShuffledDeckView().getDeck().add(card);
        };
        getCardAbandonedStationReceiver.registerListener(getCardAbandonedStationListener);

        /*
         * Initialize CombatZone card and add it to the shuffledDeck
         */
        CastEventReceiver<GetCardCombatZone> getCardCombatZoneReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<GetCardCombatZone> getCardCombatZoneListener = data -> {
            List<HitView> hits = new ArrayList<>();
            for (Pair<Integer, Integer> pair : data.fires()) {
                hits.add(new HitView(HitTypeView.fromValue(pair.getValue0()), HitDirectionView.fromValue(pair.getValue1())));
            }
            CombatZoneView card = new CombatZoneView(data.ID(), false, data.level(), data.lost(), data.flightDays(), hits);
            MiniModel.getInstance().getShuffledDeckView().getDeck().add(card);
        };
        getCardCombatZoneReceiver.registerListener(getCardCombatZoneListener);

        /*
         * Initialize Epidemic card and add it to the shuffledDeck
         */
        CastEventReceiver<GetCardEpidemic> getCardEpidemicReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<GetCardEpidemic> getCardEpidemicListener = data -> {
            EpidemicView card = new EpidemicView(data.ID(), false, data.level());
            MiniModel.getInstance().getShuffledDeckView().getDeck().add(card);
        };
        getCardEpidemicReceiver.registerListener(getCardEpidemicListener);

        /*
         * Initialize MeteorSwarm card and add it to the shuffledDeck
         */
        CastEventReceiver<GetCardMeteorSwarm> getCardMeteorSwarmReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<GetCardMeteorSwarm> getCardMeteorSwarmListener = data -> {
            List<HitView> hits = new ArrayList<>();
            for (Pair<Integer, Integer> pair : data.meteors()) {
                hits.add(new HitView(HitTypeView.fromValue(pair.getValue0()), HitDirectionView.fromValue(pair.getValue1())));
            }

            MeteorSwarmView card = new MeteorSwarmView(data.ID(), false, data.level(), hits);
            MiniModel.getInstance().getShuffledDeckView().getDeck().add(card);
        };
        getCardMeteorSwarmReceiver.registerListener(getCardMeteorSwarmListener);

        /*
         * Initialize OpenSpace card and add it to the shuffledDeck
         */
        CastEventReceiver<GetCardOpenSpace> getCardOpenSpaceReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<GetCardOpenSpace> getCardOpenSpaceListener = data -> {
            OpenSpaceView card = new OpenSpaceView(data.ID(), false, data.level());
            MiniModel.getInstance().getShuffledDeckView().getDeck().add(card);
        };
        getCardOpenSpaceReceiver.registerListener(getCardOpenSpaceListener);

        /*
         * Initialize Pirates card and add it to the shuffledDeck
         */
        CastEventReceiver<GetCardPirates> getCardPiratesReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<GetCardPirates> getCardPiratesListener = data -> {
            List<HitView> hits = new ArrayList<>();
            for (Pair<Integer, Integer> pair : data.fires()) {
                hits.add(new HitView(HitTypeView.fromValue(pair.getValue0()), HitDirectionView.fromValue(pair.getValue1())));
            }
            PiratesView card = new PiratesView(data.ID(), false, data.level(), data.cannonStrengthRequired(), data.credit(), data.flightDays(), hits);

            MiniModel.getInstance().getShuffledDeckView().getDeck().add(card);
        };
        getCardPiratesReceiver.registerListener(getCardPiratesListener);

        CastEventReceiver<GetCardPlanets> getCardPlanetsReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<GetCardPlanets> getCardPlanetsListener = data -> {
            List<List<GoodView>> planets = new ArrayList<>();
            for(List<Integer> planet : data.planets()) {
                List<GoodView> goods = new ArrayList<>();
                for (Integer integer : planet) {
                    goods.add(GoodView.fromValue(integer));
                }
                planets.add(goods);
            }

            PlanetsView card = new PlanetsView(data.ID(), false, data.level(), data.flightDays(), planets);
        };
        getCardPlanetsReceiver.registerListener(getCardPlanetsListener);

        /*
         * Initialize Slavers card and add it to the shuffledDeck
         */
        CastEventReceiver<GetCardSlavers> getCardSlaversReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<GetCardSlavers> getCardSlaversListener = data -> {
            SlaversView card = new SlaversView(data.ID(), false, data.level(), data.cannonStrengthRequired(), data.credit(), data.flightDays(), data.crewLost());
            MiniModel.getInstance().getShuffledDeckView().getDeck().add(card);
        };
        getCardSlaversReceiver.registerListener(getCardSlaversListener);

        /*
         * Initialize Smugglers card and add it to the shuffledDeck
         */
        CastEventReceiver<GetCardSmugglers> getCardSmugglersReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<GetCardSmugglers> getCardSmugglersListener = data -> {
            List<GoodView> goods = new ArrayList<>();
            for (Integer integer : data.goodsReward()) {
                goods.add(GoodView.fromValue(integer));
            }
            SmugglersView card = new SmugglersView(data.ID(), false, data.level(), data.cannonStrengthRequired(), data.goodsLoss(), data.flightDays(), goods);
            MiniModel.getInstance().getShuffledDeckView().getDeck().add(card);
        };
        getCardSmugglersReceiver.registerListener(getCardSmugglersListener);

        /*
         * Initialize StarDust card and add it to the shuffledDeck
         */
        CastEventReceiver<GetCardStardust> getCardStardustReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<GetCardStardust> getCardStardustListener = data -> {
            StarDustView card = new StarDustView(data.ID(), false, data.level());
            MiniModel.getInstance().getShuffledDeckView().getDeck().add(card);
        };
        getCardStardustReceiver.registerListener(getCardStardustListener);

        // DECK events
        /*
         * Set the next line of the shuffled deck;
         */
        CastEventReceiver<DrawCard> drawCardReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<DrawCard> getCardDrawListener = data -> {
            MiniModel.getInstance().getShuffledDeckView().getDeck().pop();

            manager.notifyDrawCard();
        };
        drawCardReceiver.registerListener(getCardDrawListener);

        /*
         * Initialize Decks in the MiniModel
         */
        CastEventReceiver<GetDecks> getDecksReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<GetDecks> getDecksListener = data -> {
            for (int i = 0; i < data.decks().size(); i++) {
                DeckView deck = new DeckView();
                for (Integer integer : data.decks().get(i)) {
                    CardView card = MiniModel.getInstance().getShuffledDeckView().getDeck()
                            .stream()
                            .filter(c -> c.getID() == integer)
                            .findFirst()
                            .orElse(null);
                    deck.addCard(card);
                }
                MiniModel.getInstance().getDeckViews().getValue0()[i] = deck;
                MiniModel.getInstance().getDeckViews().getValue1()[i] = true;
            }
        };
        getDecksReceiver.registerListener(getDecksListener);

        /*
         * Order the shuffled deck
         */
        CastEventReceiver<GetShuffledDeck> getShuffledDeckReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<GetShuffledDeck> getShuffledDeckListener = data -> {
            MiniModel.getInstance().getShuffledDeckView().order(data.shuffledDeck());
        };
        getShuffledDeckReceiver.registerListener(getShuffledDeckListener);

        /*
         * Set the viewable status of the deck. If deck == false it is not viewable in the building screen
         */
        CastEventReceiver<PickedLeftDeck> pickedLeftDeckReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedLeftDeck> pickedLeftDeckListener = data -> {
            MiniModel.getInstance().getDeckViews().getValue1()[data.deckIndex()] = data.usage() == 1;

            manager.notifyPickedLeftDeck(data);
        };
        pickedLeftDeckReceiver.registerListener(pickedLeftDeckListener);

        // DICE
        CastEventReceiver<DiceRolled> diceRolledReceiver = new CastEventReceiver<DiceRolled>(this.transceiver);
        EventListener<DiceRolled> diceRolledListener = data -> {
            MiniModel.getInstance().setDice(new Pair<>(data.diceValue1(), data.diceValue2()));


        };
        diceRolledReceiver.registerListener(diceRolledListener);

        // ENERGY USED events
        CastEventReceiver<BatteriesLoss> batteriesUsedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<BatteriesLoss> getBatteriesUsedListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());
            for (Pair<Integer, Integer> pair : data.batteriesIDs()) {
                player.getShip().getMapBatteries().get(pair.getValue0()).setNumberOfBatteries(pair.getValue1());
            }

            manager.notifyBatteriesUsed(data);
        };
        batteriesUsedReceiver.registerListener(getBatteriesUsedListener);

        /*
         * Remove a battery from the tiles in the batteriesIDs list
         */
        CastEventReceiver<CannonsUsed> cannonsUsedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<CannonsUsed> cannonsUsedListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            if (player != null) {
                for (Pair<Integer, Integer> pair : data.batteriesIDs()) {
                    player.getShip().getMapBatteries().get(pair.getValue0()).setNumberOfBatteries(pair.getValue1());
                }
            }
            manager.notifyCannonsUsed(data);
        };
        cannonsUsedReceiver.registerListener(cannonsUsedListener);

        /*
         * Remove a battery from the tiles in the batteriesIDs list
         */
        CastEventReceiver<EnginesUsed> enginesUsedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<EnginesUsed> enginesUsedListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            if (player != null) {
                for (Pair<Integer, Integer> pair : data.batteriesIDs()) {
                    player.getShip().getMapBatteries().get(pair.getValue0()).setNumberOfBatteries(pair.getValue1());
                }
            }
            manager.notifyEnginesUsed(data);
        };
        enginesUsedReceiver.registerListener(enginesUsedListener);

        /*
         * Reduce the number of battery
         */
        CastEventReceiver<ShieldUsed> shieldUsedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<ShieldUsed> shieldUsedListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());
            player.getShip().getMapBatteries().get(data.batteryID().getValue0()).setNumberOfBatteries(data.batteryID().getValue1());

            manager.notifyShieldUsed(data);
        };
        shieldUsedReceiver.registerListener(shieldUsedListener);

        // GOODS events
        /*
         * Update the status of storages
         */
        CastEventReceiver<GoodsSwapped> goodsSwappedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<GoodsSwapped> goodsSwappedListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            GoodView[] newGoods1 = new GoodView[data.goods2to1().size()];
            for (int i = 0; i < data.goods2to1().size(); i++) {
                newGoods1[i] = GoodView.fromValue(data.goods2to1().get(i));
            }
            player.getShip().getMapStorages().get(data.storageID1()).changeGoods(newGoods1);

            GoodView[] newGoods2 = new GoodView[data.goods1to2().size()];
            for (int i = 0; i < data.goods1to2().size(); i++) {
                newGoods2[i] = GoodView.fromValue(data.goods1to2().get(i));
            }
            player.getShip().getMapStorages().get(data.storageID1()).changeGoods(newGoods2);

            manager.notifyGoodsSwapped(data);
        };
        goodsSwappedReceiver.registerListener(goodsSwappedListener);

        /*
         * Update status of storages
         */
        CastEventReceiver<UpdateGoodsExchange> updateGoodsExchangeReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<UpdateGoodsExchange> updateGoodsExchangeListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());
            for (Pair<Integer, List<Integer>> pair : data.exchangeData()) {
                GoodView[] newGoods = new GoodView[pair.getValue1().size()];
                for (int i = 0; i < newGoods.length; i++) {
                    newGoods[i] = GoodView.fromValue(pair.getValue1().get(i));
                }
                player.getShip().getMapStorages().get(pair.getValue0()).changeGoods(newGoods);
            }

            manager.notifyUpdateGoodsExchange(data);
        };
        updateGoodsExchangeReceiver.registerListener(updateGoodsExchangeListener);


        // PICK TILE events
        /*
         * Create a new BatteryView and set it in the player's hand
         */
        CastEventReceiver<PickedBatteryFromBoard> pickedBatteryFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedBatteryFromBoard> pickedBatteryFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            BatteryView battery = new BatteryView(data.tileID(), data.connectors(), data.clockwiseRotation(), data.energyNumber());
            player.setHand(battery);
            MiniModel.getInstance().reduceViewableComponents();

            manager.notifyPickedTileFromBoard();
        };
        pickedBatteryFromBoardReceiver.registerListener(pickedBatteryFromBoardListener);

        /*
         * Create a new CabinView and set it in the player's hand
         */
        CastEventReceiver<PickedCabinFromBoard> pickedCabinFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedCabinFromBoard> pickedCabinFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            CabinView cabin = new CabinView(data.tileID(), data.connectors(), data.clockwiseRotation());
            player.setHand(cabin);
            MiniModel.getInstance().reduceViewableComponents();

            manager.notifyPickedTileFromBoard();
        };
        pickedCabinFromBoardReceiver.registerListener(pickedCabinFromBoardListener);

        /*
         * Create a new CannonView and set it in the player's hand
         */
        CastEventReceiver<PickedCannonFromBoard> pickedCannonFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedCannonFromBoard> pickedCannonFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            CannonView cannon = new CannonView(data.tileID(), data.connectors(), data.clockwiseRotation(), data.cannonStrength(), 0);
            player.setHand(cannon);
            MiniModel.getInstance().reduceViewableComponents();

            manager.notifyPickedTileFromBoard();
        };
        pickedCannonFromBoardReceiver.registerListener(pickedCannonFromBoardListener);

        /*
         * Create a new ConnectorsView and set it in the player's hand
         */
        CastEventReceiver<PickedConnectorsFromBoard> pickedConnectorsFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedConnectorsFromBoard> pickedConnectorsFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            ConnectorsView pipes = new ConnectorsView(data.tileID(), data.connectors(), data.clockwiseRotation());
            player.setHand(pipes);
            MiniModel.getInstance().reduceViewableComponents();

            manager.notifyPickedTileFromBoard();
        };
        pickedConnectorsFromBoardReceiver.registerListener(pickedConnectorsFromBoardListener);

        /*
         * Create a new EngineView and set it in the player's hand
         */
        CastEventReceiver<PickedEngineFromBoard> pickedEngineFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedEngineFromBoard> pickedEngineFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            EngineView engine = new EngineView(data.tileID(), data.connectors(), data.clockwiseRotation(), data.cannonStrength(), 0);
            player.setHand(engine);
            MiniModel.getInstance().reduceViewableComponents();

            manager.notifyPickedTileFromBoard();
        };
        pickedEngineFromBoardReceiver.registerListener(pickedEngineFromBoardListener);

        /*
         * Create a new LifeSupportView and set it in the player's hand
         */
        CastEventReceiver<PickedLifeSupportFromBoard> pickedLifeSupportFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedLifeSupportFromBoard> pickedLifeSupportFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            ComponentView lifeSupport;
            if (data.type() == 1) {
                lifeSupport = new LifeSupportBrownView(data.tileID(), data.connectors(), data.clockwiseRotation());
            }
            else {
                lifeSupport = new LifeSupportPurpleView(data.tileID(), data.connectors(), data.clockwiseRotation());
            }
            player.setHand(lifeSupport);
            MiniModel.getInstance().reduceViewableComponents();

            manager.notifyPickedTileFromBoard();
        };
        pickedLifeSupportFromBoardReceiver.registerListener(pickedLifeSupportFromBoardListener);

        /*
         * Create a new ShieldView and set it in the player's hand
         */
        CastEventReceiver<PickedShieldFromBoard> pickedShieldFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedShieldFromBoard> pickedShieldFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());
            boolean[] shields = new boolean[data.connectors().length];
            shields[data.clockwiseRotation()] = true;
            shields[(data.clockwiseRotation() - 1 + data.connectors().length) % data.connectors().length] = true;
            ShieldView shield = new ShieldView(data.tileID(), data.connectors(), data.clockwiseRotation(), shields);
            player.setHand(shield);
            MiniModel.getInstance().reduceViewableComponents();

            manager.notifyPickedTileFromBoard();
        };
        pickedShieldFromBoardReceiver.registerListener(pickedShieldFromBoardListener);

        /*
         * Create a new StorageView and set it in the player's hand
         */
        CastEventReceiver<PickedStorageFromBoard> pickedStorageFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedStorageFromBoard> pickedStorageFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            StorageView storage = new StorageView(data.tileID(), data.connectors(), data.clockwiseRotation(), false, data.goodsCapacity());
            player.setHand(storage);
            MiniModel.getInstance().reduceViewableComponents();

            manager.notifyPickedTileFromBoard();
        };
        pickedStorageFromBoardReceiver.registerListener(pickedStorageFromBoardListener);

        /*
         * Remove a tile from the reserved list and add it into the hand
         */
        CastEventReceiver<PickedTileFromReserve> pickedTileFromReserveReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedTileFromReserve> pickedTileFromReserveListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            ComponentView tile = player.getShip().getDiscardReservedPile().removeDiscardReserved(data.tileID());
            player.setHand(tile);

            manager.notifyPickedTileFromBoard();
        };
        pickedTileFromReserveReceiver.registerListener(pickedTileFromReserveListener);

        /*
         * Remove the last tile on the ship and add it into the hand
         */
        CastEventReceiver<PickedTileFromSpaceship> pickedTileFromSpaceshipReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedTileFromSpaceship> pickedTileFromSpaceshipListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            player.setHand(player.getShip().removeLast());

            manager.notifyPickedTileFromBoard();
        };
        pickedTileFromSpaceshipReceiver.registerListener(pickedTileFromSpaceshipListener);


        // PLACED TILE events
        /*
         * Remove the tile from the player's hand and add it to the viewable components
         */
        CastEventReceiver<PlacedTileToBoard> placedTileToBoardReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PlacedTileToBoard> placedTileToBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            MiniModel.getInstance().getViewableComponents().add(player.getHand());
            player.setHand(new GenericComponentView());

            manager.notifyPlacedTileToBoard(data);
        };
        placedTileToBoardReceiver.registerListener(placedTileToBoardListener);

        /*
         * Remove tile from the player's hand and add it to the reserved pile
         */
        CastEventReceiver<PlacedTileToReserve> placedTileToReserveReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PlacedTileToReserve> placedTileToReserveListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            player.getShip().getDiscardReservedPile().addDiscardReserved(player.getHand());
            player.setHand(new GenericComponentView());

            manager.notifyPlacedTileToReserve(data);
        };
        placedTileToReserveReceiver.registerListener(placedTileToReserveListener);

        /*
         * Remove tile from the player's hand and add it to the spaceship
         */
        CastEventReceiver<PlacedTileToSpaceship> placedTileToSpaceshipReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PlacedTileToSpaceship> placedTileToSpaceshipListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            player.getShip().placeComponent(player.getHand(), data.row(), data.column());
            player.setHand(new GenericComponentView());

            manager.notifyPlacedTileToSpaceship(data);
        };
        placedTileToSpaceshipReceiver.registerListener(placedTileToSpaceshipListener);


        // PLANETS events
        /*
         * Set the select planet and add the player's marker on the card
         */
        CastEventReceiver<PlanetSelected> planetSelectedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PlanetSelected> planetSelectedListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());
            PlanetsView card = (PlanetsView) MiniModel.getInstance().getShuffledDeckView().getDeck().peek();
            card.setPlanetSelected(data.planetNumber());
            card.setPlayersPosition(data.planetNumber(), player.getMarkerView());

            manager.notifyPlanetSelected(data);
        };
        planetSelectedReceiver.registerListener(planetSelectedListener);


        // PLAYER events
        CastEventReceiver<EnemyDefeat> enemyDefeatReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<EnemyDefeat> enemyDefeatListener = data -> {
            manager.notifyEnemyDefeat(data);
        };
        enemyDefeatReceiver.registerListener(enemyDefeatListener);

        CastEventReceiver<MinPlayer> minPlayerReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<MinPlayer> minPlayerListener = data -> {
            //TODO
        };
        minPlayerReceiver.registerListener(minPlayerListener);

        /*
         * Move the marker of the player on the board
         */
        CastEventReceiver<MoveMarker> moveMarkerReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<MoveMarker> moveMarkerListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());
            MiniModel.getInstance().getBoardView().movePlayer(player.getMarkerView(), data.steps());

            manager.notifyMoveMarker(data);
        };
        moveMarkerReceiver.registerListener(moveMarkerListener);

        /*
         * Notify the player has given up
         */
        CastEventReceiver<PlayerGaveUp> playerGaveUpReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PlayerGaveUp> playerGaveUpListener = data -> {
            manager.notifyPlayerGaveUp(data);
        };
        playerGaveUpReceiver.registerListener(playerGaveUpListener);

        CastEventReceiver<PlayerLost> playerLostReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PlayerLost> playerLostListener = data -> {
            //TODO
            manager.notifyPlayerLost(data);
        };
        playerLostReceiver.registerListener(playerLostListener);

        /*
         * Notify who is the player who is playing
         */
        CastEventReceiver<CurrentPlayer> playingReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<CurrentPlayer> playingListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());
            MiniModel.getInstance().setCurrentPlayer(player);

            manager.notifyPlaying(data);
        };
        playingReceiver.registerListener(playingListener);

        CastEventReceiver<Score> scoreReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<Score> scoreListener = data -> {
            //TODO
            manager.notifyScore(data);
        };
        scoreReceiver.registerListener(scoreListener);

        /*
         * Set the number if coins to the player
         */
        CastEventReceiver<UpdateCoins> updateCoinsReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<UpdateCoins> updateCoinsListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            player.setCoins(data.coins());

            manager.notifyUpdateCoins(data);
        };
        updateCoinsReceiver.registerListener(updateCoinsListener);


        // ROTATED TILE events
        /*
         * Rotate tiles
         */
        CastEventReceiver<RotatedTile> rotatedGenericReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<RotatedTile> rotatedGenericTileListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());
            ComponentView card = player.getHand();
            card.rotate();
            switch (card.getType()) {
                case DOUBLE_CANNON, SINGLE_CANNON -> ((CannonView) card).setArrowRotation(card.getClockWise());
                case DOUBLE_ENGINE, SINGLE_ENGINE -> ((EngineView) card).setEngineRotation((card.getClockWise() + 2) % data.connectors().length);
                case SHIELD -> {
                    boolean[] shields = new boolean[data.connectors().length];
                    shields[card.getClockWise()] = true;
                    shields[((card.getClockWise() - 1) + data.connectors().length) % data.connectors().length] = true;
                    ((ShieldView) card).setShields(shields);
                }
            }
            card.setConnectors(data.connectors());

            manager.notifyRotatedTile(data);
        };
        rotatedGenericReceiver.registerListener(rotatedGenericTileListener);

        // SPACESHIP events
        /*
         * Notify the best looking ships
         */
        CastEventReceiver<BestLookingShips> bestLookingShipsReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<BestLookingShips> bestLookingShipsListener = data -> {
            manager.notifyBestLookingShips(data);
        };
        bestLookingShipsReceiver.registerListener(bestLookingShipsListener);

        /*
         *
         */
        CastEventReceiver<CanProtect> canProtectReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<CanProtect> canProtectListener = data -> {
            manager.notifyCanProtect(data);
        };
        canProtectReceiver.registerListener(canProtectListener);

        /*
         * Move the tiles from the board to the discard pile
         */
        CastEventReceiver<ComponentDestroyed> componentDestroyedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<ComponentDestroyed> componentDestroyedListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            if (player != null) {
                for (Pair<Integer, Integer> tile : data.destroyedComponents()) {
                    ComponentView tmp = player.getShip().removeComponent(tile.getValue0(), tile.getValue1());
                    player.getShip().getDiscardReservedPile().addDiscardReserved(tmp);
                }
            }

            manager.notifyComponentDestroyed(data);
        };
        componentDestroyedReceiver.registerListener(componentDestroyedListener);

        /*
         * Set the fragments of the ship
         */
        CastEventReceiver<Fragments> fragmentsReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<Fragments> fragmentsListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            if (player != null) {
                player.getShip().setFragments(data.fragments());
            }

            manager.notifyFragments(data);
        };
        fragmentsReceiver.registerListener(fragmentsListener);

        /*
         * Set wrong tiles
         */
        CastEventReceiver<InvalidComponents> invalidComponentsReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<InvalidComponents> invalidComponentsListener = data -> {
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
                ship[pair.getValue0()][pair.getValue1()].setIsWrong(true);
            }

            manager.notifyInvalidComponents(data);
        };
        invalidComponentsReceiver.registerListener(invalidComponentsListener);


        CastEventReceiver<NextHit> nextHitReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<NextHit> nextHitListener = data -> {
            MeteorSwarmView card = (MeteorSwarmView) MiniModel.getInstance().getShuffledDeckView().getDeck().peek();
            card.nextHit();

            manager.notifyNextHit(data);
        };
        nextHitReceiver.registerListener(nextHitListener);

        /*
         * Update crew members
         */
        CastEventReceiver<UpdateCrewMembers> updateCrewMembersReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<UpdateCrewMembers> updateCrewMembersListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            for (Triplet<Integer, Integer, Integer> cabin : data.cabins()) {
                CabinView c = player.getShip().getMapCabins().get(cabin.getValue0());
                if (cabin.getValue2() == 1) {
                    c.setCrewNumber(0);
                    c.setBrownAlien(false);
                    c.setPurpleAlien(false);
                }
                else {
                    c.setCrewNumber(cabin.getValue1());
                }
            }

            manager.notifyUpdateCrewMembers(data);
        };
        updateCrewMembersReceiver.registerListener(updateCrewMembersListener);

        /*
         * Start the timer for the building phase
         */
        CastEventReceiver<TimerFlipped> timerFlippedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<TimerFlipped> timerFlippedListener = data -> {
            new Thread(() -> {
                MiniModel.getInstance().getTimerView().setFlippedTimer(getPlayerDataView(data.nickname()));
                LocalDateTime serverTime = LocalDateTime.parse(data.startingTime());
                LocalDateTime clientTime = LocalDateTime.now();
                int time = (int)(data.timerDuration() - Math.max(0, Duration.between(serverTime, clientTime).toSeconds()));
                while (time >= 0) {
                    try {
                        MiniModel.getInstance().getTimerView().setSecondsRemaining(time);
                        manager.notifyTimer();
                        Thread.sleep(1000);
                        time--;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }).start();
        };
        timerFlippedReceiver.registerListener(timerFlippedListener);



        CastEventReceiver<StateChanged> stateChangedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<StateChanged> stateChangedListener = data -> {
            manager.notifyStateChange(data);
        };
        stateChangedReceiver.registerListener(stateChangedListener);
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
}
