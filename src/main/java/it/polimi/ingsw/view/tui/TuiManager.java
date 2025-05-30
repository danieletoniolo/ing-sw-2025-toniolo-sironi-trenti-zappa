package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.event.game.serverToClient.*;
import it.polimi.ingsw.event.lobby.serverToClient.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.hits.Hit;
import it.polimi.ingsw.model.game.board.Deck;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.model.spaceship.*;
import it.polimi.ingsw.view.tui.states.buildingScreens.WatchingTuiScreen;
import it.polimi.ingsw.view.tui.states.gameScreens.NotClientTurnTuiScreen;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import it.polimi.ingsw.view.Manager;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.BoardView;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.miniModel.cards.*;
import it.polimi.ingsw.view.miniModel.cards.hit.HitDirectionView;
import it.polimi.ingsw.view.miniModel.cards.hit.HitTypeView;
import it.polimi.ingsw.view.miniModel.cards.hit.HitView;
import it.polimi.ingsw.view.miniModel.components.*;
import it.polimi.ingsw.view.miniModel.deck.DeckView;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.miniModel.lobby.LobbyView;
import it.polimi.ingsw.view.miniModel.player.MarkerView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.miniModel.spaceship.SpaceShipView;
import it.polimi.ingsw.view.miniModel.timer.TimerView;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.states.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TuiManager implements Manager {
    private final Object stateLock = new Object();
    private TuiScreenView currentScreen;
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

        currentScreen = new ValidationTuiScreen();

        /// Se metodo crea un nuovo stato impostare anche printInput a false
    }

    @Override
    public void notifyUserIDSet() {

    }

    /**
     * Create the Menu screen after the nickname is set
     */
    @Override
    public void notifyNicknameSet() {
        synchronized (stateLock) {
            currentScreen = new MenuTuiScreen();
            printInput = false;
            stateLock.notifyAll();
        }

    }

    @Override
    public void notifyLobbies() {
        if (currentScreen instanceof MenuTuiScreen) {
            synchronized (stateLock) {
                printInput = false;
                currentScreen = new MenuTuiScreen();
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyCreatedLobby(LobbyCreated data) {
        printInput = false;
        currentScreen = new LobbyTuiScreen();
    }

    @Override
    public void notifyLobbyJoined(LobbyJoined data) {
        if (currentScreen.getType().equals(TuiScreens.Lobby)) {
            synchronized (stateLock) {
                printInput = false;
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyLobbyLeft(LobbyLeft data) {
        if (MiniModel.getInstance().getCurrentLobby().getLobbyName().equals(data.lobbyID())) {
            synchronized (stateLock) {
                printInput = false;
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyLobbyRemoved(LobbyRemoved data) {
        if (currentScreen instanceof MenuTuiScreen) {
            currentScreen = new MenuTuiScreen();
            printInput = false;
        }
    }

    /**
     * Refresh the starting countdown on the screen
     */
    @Override
    public void notifyCountDown() {
        if (currentScreen.getType().equals(TuiScreens.Building)) {
            synchronized (stateLock) {
                stateLock.notifyAll();
            }
        }
    }

    /**
     * Change the screen: from lobby screen to building screen
     */
    @Override
    public void notifyStartingGame() {
        synchronized (stateLock) {
            currentScreen = new BuildingTuiScreen();
            printInput = false;
            stateLock.notifyAll();
        }
    }

    @Override
    public void notifyBestLookingShips(BestLookingShips data) {
        synchronized (stateLock) {
            StringBuilder message = new StringBuilder();
            if (data.nicknames().size() == 1) {
                message.append(data.nicknames().getFirst()).append(" has the best looking ship!");
            }
            else {
                message.append("The best looking ships are:\n");
                for (int i = 0; i < data.nicknames().size(); i++) {
                    message.append(data.nicknames().get(i));
                    if (i != data.nicknames().size() - 1) message.append(", ");
                }
            }
            currentScreen.setMessage(message.toString());
            stateLock.notifyAll();
        }
    }

    @Override
    public void notifyCannonsUsed(CannonsUsed data) {
        if (currentScreen.getType().equals(TuiScreens.Player)) {
            if (((PlayerTuiScreen) currentScreen).getPlayerToView().getUsername().equals(data.nickname())) {
                synchronized (stateLock) {
                    currentScreen.setMessage(data.nickname() + " has used " + data.batteriesIDs().size() + " batteries!");
                    stateLock.notifyAll();
                }
            }
        }
    }

    @Override
    public void notifyCanShield(CanProtect data) {

    }

    @Override
    public void notifyComponentDestroyed(ComponentDestroyed data) {
        if (currentScreen.getType().equals(TuiScreens.Player)) {
            if (((PlayerTuiScreen) currentScreen).getPlayerToView().getUsername().equals(data.nickname())) {
                synchronized (stateLock) {
                    currentScreen.setMessage(data.nickname() + " has lost " + data.destroyedComponents().size() + " components");
                    stateLock.notifyAll();
                }
            }
        }
    }

    @Override
    public void notifyEnemyDrew(EnemyDefeat data) {

    }

    @Override
    public void notifyEnemyLost(EnemyDefeat data) {

    }

    @Override
    public void notifyEnemyWon(EnemyDefeat data) {

    }

    @Override
    public void notifyEnginesUsed(EnginesUsed data) {

    }

    //@Override
    public void notifyStartTimer() {
        if (currentScreen.getType().equals(TuiScreens.Building)) {
            synchronized (stateLock) {
                stateLock.notifyAll();
            }
        }
    }

    public void startTui(){
        //Reading inputs thread
        Thread parserThread = new Thread(() -> {
            while (true) {
                try {
                    TuiScreenView screenToUse;
                    synchronized (stateLock) {
                        while (!printInput) stateLock.wait();
                        screenToUse = currentScreen;
                    }

                    screenToUse.readCommand(parser, () -> screenToUse == currentScreen);

                    synchronized (stateLock) {
                        if (currentScreen == screenToUse) {
                            currentScreen = currentScreen.setNewScreen();
                        }
                        printInput = false;
                        stateLock.notifyAll();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        //Thread for printing the TUI
        Thread viewThread = new Thread(() -> {
            while (true) {
                try {
                    currentScreen.printTui(terminal);
                    currentScreen.setMessage(null);
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

    public void set() {
        synchronized (stateLock) {
            currentScreen = new NotClientTurnTuiScreen();
            stateLock.notifyAll();
        }
    }

    public static void main(String[] args) {
        ArrayList<Component> tiles = TilesManager.getTiles();
        for (int i = 0; i < 50; i++) {
            ComponentView tileView = converter(tiles.get(i));
            tileView.setCovered(false);
            MiniModel.getInstance().getViewableComponents().add(tileView);
        }

        ArrayList<LobbyView> currentLobbies = MiniModel.getInstance().getLobbiesView();
        LobbyView currentLobby = new LobbyView("pippo", 0, 4, LevelView.SECOND);
        currentLobbies.add(new LobbyView("nico", 0, 4, LevelView.LEARNING));
        currentLobbies.add(new LobbyView("eli", 0, 4, LevelView.LEARNING));
        currentLobbies.add(new LobbyView("lolo", 0, 4, LevelView.LEARNING));
        currentLobbies.add(new LobbyView("lore", 0, 4, LevelView.LEARNING));
        currentLobbies.add(new LobbyView("vitto", 0, 4, LevelView.LEARNING));

        MiniModel.getInstance().setCurrentLobby(currentLobby);

        ArrayList<PlayerDataView> otherPlayers = MiniModel.getInstance().getOtherPlayers();
        PlayerDataView player = new PlayerDataView("Player1", MarkerView.YELLOW, new SpaceShipView(currentLobby.getLevel()));
        otherPlayers.add(new PlayerDataView("Player2", MarkerView.RED, new SpaceShipView(currentLobby.getLevel())));
        otherPlayers.add(new PlayerDataView("Player3", MarkerView.GREEN, new SpaceShipView(currentLobby.getLevel())));
        otherPlayers.add(new PlayerDataView("Player4", MarkerView.BLUE, new SpaceShipView(currentLobby.getLevel())));

        MiniModel.getInstance().setClientPlayer(player);
        MiniModel.getInstance().getClientPlayer().setHand(new GenericComponentView());
        MiniModel.getInstance().getClientPlayer().getHand().setCovered(false);

        Deck[] decks = CardsManager.createDecks(Level.SECOND);

        MiniModel.getInstance().setBoardView(new BoardView(currentLobby.getLevel()));
        if (currentLobby.getLevel() == LevelView.SECOND) {
            MiniModel.getInstance().setTimerView(new TimerView(3));
            //MiniModel.getInstance().timerView.setFlippedTimer(player);
        }

        for (int i = 0; i < 3; i++) {
            Stack<CardView> cards = new Stack<>();
            for (Card card : decks[i].getCards()) {
                cards.add(convertCard(card));
            }
            DeckView deckView = new DeckView();
            deckView.setDeck(cards);
            MiniModel.getInstance().getDeckViews().getValue0()[i] = deckView;
            MiniModel.getInstance().getDeckViews().getValue1()[i] = true;
        }

        MiniModel.getInstance().setCurrentPlayer(otherPlayers.getFirst());

        Stack<CardView> stack = new Stack<>();
        for (Card card : CardsManager.createLearningDeck()) {
            stack.add(convertCard(card));
        }
        MiniModel.getInstance().setShuffledDeckView(new DeckView());
        MiniModel.getInstance().getShuffledDeckView().setDeck(stack);
        MiniModel.getInstance().getShuffledDeckView().setOnlyLast(true);

        for (ComponentView tile : MiniModel.getInstance().getViewableComponents()) {
            if (tile instanceof StorageView && ((StorageView) tile).getGoods().length > 1) {
                MiniModel.getInstance().getClientPlayer().getShip().placeComponent(tile, 7, 6);
                ((StorageView) tile).setGood(GoodView.BLUE, 0);
                ((StorageView) tile).setGood(GoodView.YELLOW, 0);
                ((StorageView) tile).setGood(GoodView.GREEN, 1);
                tile.setIsWrong(true);
                break;
            }
        }



        TuiManager tui = new TuiManager();
        tui.startTui();
        /*
        final int[] secondsRemaining = {15};
        new Thread(() -> {
            while (secondsRemaining[0] >= 0) {
                try {
                    MiniModel.getInstance().timerView.setSecondsRemaining(secondsRemaining[0]);
                    tui.notifyStartTimer();
                    Thread.sleep(1000);
                    secondsRemaining[0]--;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            tui.set();
        }).start();*/
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
