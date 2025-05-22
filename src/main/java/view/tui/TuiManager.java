package view.tui;

import Model.Cards.*;
import Model.Cards.Hits.Hit;
import Model.Game.Board.Deck;
import Model.Good.Good;
import Model.SpaceShip.*;
import event.lobby.CreateLobby;
import event.lobby.JoinLobby;
import event.lobby.LeaveLobby;
import event.lobby.RemoveLobby;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import view.Manager;
import view.miniModel.MiniModel;
import view.miniModel.board.BoardView;
import view.miniModel.board.LevelView;
import view.miniModel.cards.*;
import view.miniModel.cards.hit.HitDirectionView;
import view.miniModel.cards.hit.HitTypeView;
import view.miniModel.cards.hit.HitView;
import view.miniModel.components.*;
import view.miniModel.deck.DeckView;
import view.miniModel.good.GoodView;
import view.miniModel.player.ColorView;
import view.miniModel.player.PlayerDataView;
import view.miniModel.spaceship.SpaceShipView;
import view.tui.input.Parser;
import view.tui.states.*;
import view.tui.states.gameScreens.NotClientTurnScreenTui;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static Model.Game.Board.Level.SECOND;

public class TuiManager implements Manager {
    private final Object stateLock = new Object();
    private ScreenTuiView currentState;
    private Parser parser;
    private Terminal terminal;
    private boolean printInput;

    public TuiManager() {
        try {
            this.terminal = TerminalBuilder.builder()
                    .jna(true)
                    .jansi(true)
                    .build();
        } catch (Exception e) {
            System.err.println("Creation terminal error: " + e.getMessage());
            return;
        }
        parser = new Parser(terminal);

        currentState = new NotClientTurnScreenTui();
    }


    @Override
    public void notifyCreateLobby(CreateLobby data) {
        if (data.userID() == null || data.userID().equals(MiniModel.getInstance().clientPlayer.getUsername())) { // Create a new lobbyState if the user is the one who created it or the server said to do so
            //currentState = new LobbyStateView(data.lobbyID());
            stateLock.notifyAll();
        }
        else {
            if (currentState instanceof MenuScreenTui) { // Refresh the menu if another user creates a lobby because nd we are in the menu state
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyRemoveLobby(RemoveLobby data){
        // placeHolder
    }

    @Override
    public void notifyJoinLobby(JoinLobby data) {
        if (data.userID() == null || data.userID().equals(MiniModel.getInstance().clientPlayer.getUsername())) { // Create a new lobbyState if the user is the one who created it or the server said to do so
            currentState = new LobbyScreenTui();
            stateLock.notifyAll();
        }
        else {
            if (currentState instanceof LobbyScreenTui) { // Refresh the lobby view if another user joins because we are in the lobby state
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyLeaveLobby(LeaveLobby data) {
        if (data.userID() == null || data.userID().equals(MiniModel.getInstance().clientPlayer.getUsername())) { // Create a new MenuState if the user or the server said to do so
            //currentState = new MenuStateView();
            stateLock.notifyAll();
        }
        else {
            if (currentState instanceof LobbyScreenTui) { // Refresh the lobby view if another user leaves because we are in the lobby state
                stateLock.notifyAll();
            }
        }
    }

    public void startTui(){
        //Reading inputs thread
        Thread parserThread = new Thread(() -> {
            while (true) {
                try {
                    synchronized (stateLock) {
                        while (!printInput) stateLock.wait();
                    }
                    currentState.readCommand(parser);
                    ScreenTuiView possibleNewState = currentState.isViewCommand();
                    if (possibleNewState == null) {
                        currentState.sendCommandToServer();
                    }
                    else {
                        currentState = possibleNewState;
                        synchronized (stateLock) {
                            printInput = false;
                            stateLock.notifyAll();
                        }
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });

        //Thread for printing the TUI
        Thread viewThread = new Thread(() -> {
            while (true) {
                try {
                    currentState.printTui(terminal);
                    printInput = true;
                    synchronized (stateLock){
                        stateLock.notifyAll();
                        stateLock.wait();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        parserThread.start();
        viewThread.start();
    }


    public static void main(String[] args) {
        ArrayList<Component> tiles = TilesManager.getTiles();
        for (Component tile : tiles) {
            ComponentView tileView = converter(tile);
            tileView.setCovered(false);
            MiniModel.getInstance().components.add(tileView);
        }

        ArrayList<PlayerDataView> otherPlayers = MiniModel.getInstance().otherPlayers;
        PlayerDataView player = new PlayerDataView("Player1", ColorView.YELLOW, new SpaceShipView(LevelView.SECOND));
        otherPlayers.add(new PlayerDataView("Player2", ColorView.RED, new SpaceShipView(LevelView.SECOND)));
        otherPlayers.add(new PlayerDataView("Player3", ColorView.GREEN, new SpaceShipView(LevelView.SECOND)));
        otherPlayers.add(new PlayerDataView("Player4", ColorView.BLUE, new SpaceShipView(LevelView.SECOND)));

        MiniModel.getInstance().clientPlayer = player;
        MiniModel.getInstance().clientPlayer.setHand(new GenericComponentView());
        MiniModel.getInstance().clientPlayer.getHand().setCovered(false);

        Deck[] decks = CardsManager.createDecks(SECOND);

        MiniModel.getInstance().boardView = new BoardView(LevelView.LEARNING);

        for (int i = 0; i < 3; i++) {
            Stack<CardView> cards = new Stack<>();
            for (Card card : decks[i].getCards()) {
                cards.add(convertCard(card));
            }
            DeckView deckView = new DeckView();
            deckView.setDeck(cards);
            MiniModel.getInstance().deckViews.getValue0()[i] = deckView;
            MiniModel.getInstance().deckViews.getValue1()[i] = true;
        }

        MiniModel.getInstance().currentPlayer = otherPlayers.getFirst();

        Stack<CardView> stack = new Stack<>();
        for (Card card : CardsManager.createLearningDeck()) {
            stack.add(convertCard(card));
        }
        MiniModel.getInstance().shuffledDeckView = new DeckView();
        MiniModel.getInstance().shuffledDeckView.setDeck(stack);
        MiniModel.getInstance().shuffledDeckView.setOnlyLast(true);

        for (ComponentView tile : MiniModel.getInstance().components) {
            if (tile instanceof StorageView && ((StorageView) tile).getGoods().length > 1) {
                MiniModel.getInstance().clientPlayer.getShip().placeComponent(tile, 7, 5);
                ((StorageView) tile).setGood(GoodView.BLUE, 0);
                ((StorageView) tile).setGood(GoodView.YELLOW, 0);
                ((StorageView) tile).setGood(GoodView.GREEN, 1);
                break;
            }
        }

        TuiManager tui = new TuiManager();
        tui.startTui();
    }

    private static ComponentView converter(Component tile) {
        int[] connectors = new int[4];
        for (int j = 0; j < 4; j++) {
            switch (tile.getConnection(j)) {
                case EMPTY -> connectors[j] = 0;
                case SINGLE -> connectors[j] = 1;
                case DOUBLE -> connectors[j] = 2;
                case TRIPLE -> connectors[j] = 3;
            }
        }

        return switch (tile.getComponentType()) {
            case BATTERY -> new BatteryView(tile.getID(), connectors, ((Battery) tile).getEnergyNumber());
            case CABIN -> new CabinView(tile.getID(), connectors);
            case STORAGE -> new StorageView(tile.getID(), connectors, ((Storage) tile).isDangerous(), ((Storage) tile).getGoodsCapacity());
            case BROWN_LIFE_SUPPORT -> new LifeSupportBrownView(tile.getID(), connectors);
            case PURPLE_LIFE_SUPPORT -> new LifeSupportPurpleView(tile.getID(), connectors);
            case SINGLE_CANNON, DOUBLE_CANNON -> new CannonView(tile.getID(), connectors, ((Cannon) tile).getCannonStrength(), tile.getClockwiseRotation());
            case SINGLE_ENGINE, DOUBLE_ENGINE -> new EngineView(tile.getID(), connectors, ((Engine) tile).getEngineStrength(), tile.getClockwiseRotation());
            case SHIELD -> {
                boolean[] shields = new boolean[4];
                for (int i = 0; i < 4; i++) shields[i] = ((Shield) tile).canShield(i);
                yield new ShieldView(tile.getID(), connectors, shields);
            }
            case CONNECTORS -> new ConnectorsView(tile.getID(), connectors);
            default -> throw new IllegalStateException("Unexpected value: " + tile.getComponentType());
        };
    }

    public static CardView convertCard(Card card) {
        switch (card.getCardType()) {
            case PIRATES:
                int cannon = ((Pirates) card).getCannonStrengthRequired();
                int credits = ((Pirates) card).getCredit();
                int flight = ((Pirates) card).getFlightDays();
                ArrayList<HitView> hits = new ArrayList<>();
                for (Hit hit : ((Pirates) card).getFires()) {
                    hits.add(new HitView(HitTypeView.valueOf(hit.getType().name()), HitDirectionView.valueOf(hit.getDirection().name())));
                }
                return new PiratesView(card.getID(), false, card.getCardLevel(), cannon, credits, flight, hits);
            case PLANETS:
                int numberOfPlanets = ((Planets) card).getPlanetNumbers();
                List<List<GoodView>> goodViews = new ArrayList<>();
                for (int i = 0; i < numberOfPlanets; i++) {
                    List<GoodView> goodList = new ArrayList<>();
                    for (Good good : ((Planets) card).getPlanet(i)) {
                        goodList.add(GoodView.valueOf(good.getColor().name()));
                    }
                    goodViews.add(goodList);
                }
                return new PlanetsView(card.getID(), false, card.getCardLevel(), ((Planets) card).getFlightDays(), goodViews);
            case SLAVERS:
                return new SlaversView(card.getID(), false, card.getCardLevel(), ((Slavers) card).getCannonStrengthRequired(), ((Slavers) card).getCredit(), ((Slavers) card).getFlightDays(), ((Slavers) card).getCrewLost());
            case EPIDEMIC:
                return new EpidemicView(card.getID(), false, card.getCardLevel());
            case STARDUST:
                return new StarDustView(card.getID(), false, card.getCardLevel());
            case OPENSPACE:
                return new OpenSpaceView(card.getID(), false, card.getCardLevel());
            case SMUGGLERS:
                int cannonStrength = ((Smugglers) card).getCannonStrengthRequired();
                int goodsLost = ((Smugglers) card).getGoodsLoss();
                int flightDays = ((Smugglers) card).getFlightDays();
                List<GoodView> goods = new ArrayList<>();
                for (Good good : ((Smugglers) card).getGoodsReward()) {
                    goods.add(GoodView.valueOf(good.getColor().name()));
                }
                return new SmugglersView(card.getID(), false, card.getCardLevel(), cannonStrength, goodsLost, flightDays, goods);
            case COMBATZONE:
                int loss = ((CombatZone) card).getLost();
                int flights = ((CombatZone) card).getFlightDays();
                List<HitView> hitsList = new ArrayList<>();
                for (Hit hit : ((CombatZone) card).getFires()) {
                    hitsList.add(new HitView(HitTypeView.valueOf(hit.getType().name()), HitDirectionView.valueOf(hit.getDirection().name())));
                }
                return new CombatZoneView(card.getID(), false, card.getCardLevel(), loss, flights, hitsList);
            case METEORSWARM:
                List<HitView> meteorHits = new ArrayList<>();
                for (Hit hit : ((MeteorSwarm) card).getMeteors()) {
                    meteorHits.add(new HitView(HitTypeView.valueOf(hit.getType().name()), HitDirectionView.valueOf(hit.getDirection().name())));
                }
                return new MeteorSwarmView(card.getID(), false, card.getCardLevel(), meteorHits);
            case ABANDONEDSHIP:
                int crewLost = ((AbandonedShip) card).getCrewRequired();
                int creditsRequired = ((AbandonedShip) card).getCredit();
                int flightDaysRequired = ((AbandonedShip) card).getFlightDays();
                return new AbandonedShipView(card.getID(), false, card.getCardLevel(), crewLost, creditsRequired, flightDaysRequired);
            case ABANDONEDSTATION:
                int crew = ((AbandonedStation) card).getCrewRequired();
                int days = ((AbandonedStation) card).getFlightDays();
                List<GoodView> goodsList = new ArrayList<>();
                for (Good good : ((AbandonedStation) card).getGoods()) {
                    goodsList.add(GoodView.valueOf(good.getColor().name()));
                }
                return new AbandonedStationView(card.getID(), false, card.getCardLevel(), crew, days, goodsList);
            default:
                return null;
        }
    }
}
