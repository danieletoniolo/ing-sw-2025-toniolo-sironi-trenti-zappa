package view.tui.states.gameStates;

import Model.Cards.*;
import Model.Cards.Hits.Hit;
import Model.Good.Good;
import Model.SpaceShip.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
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
import view.tui.input.Command;
import view.tui.input.Parser;
import view.tui.states.PlayerStateTuiView;
import view.tui.states.StateTuiView;
import view.tui.states.gameStates.goodActionState.SwapGoodsFromStateTuiView;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameStateTuiView implements StateTuiView {
    private ArrayList<String> options;
    private BoardView boardView = MiniModel.getInstance().boardView;
    private PlayerDataView player;
    private DeckView shuffledDeckView = MiniModel.getInstance().shuffledDeckView;


    public GameStateTuiView() {
        this.options = new ArrayList<>();


        this.player = MiniModel.getInstance().players.stream()
                .filter(player -> player.getUsername().equals(MiniModel.getInstance().nickname))
                .findFirst()
                .orElse(null);

        if (MiniModel.getInstance().playerTurn.equals(MiniModel.getInstance().nickname)) {
            switch (MiniModel.getInstance().shuffledDeckView.getDeck().peek().getCardViewType()) {
                case ABANDONEDSTATION:
                    options.add("Accept");
                    options.add("Refuse");
                    break;
                case ABANDONEDSHIP:
                    options.add("Accept");
                    options.add("Refuse");
                    break;
                case METEORSWARM:
                    options.add("Use shield");
                    options.add("Ignore");
                    break;
                case COMBATZONE:

                    break;
                case SMUGGLERS:
                    break;
                case OPENSPACE:
                    MiniModel.getInstance().players.stream()
                            .filter(player -> player.getUsername().equals(MiniModel.getInstance().nickname))
                            .findFirst()
                            .ifPresent(player -> {
                                player.getShip().getMapBatteries().forEach(
                                        (key, value) -> {
                                            for (int i = 0; i < ((BatteryView) value).getNumberOfBatteries(); i++) {
                                                options.add("Battery " + "row: " + value.getRow() + ", Col: " + value.getCol());
                                            }
                                        }
                                );
                            });
                    break;
                case STARDUST:
                    break;
                case EPIDEMIC:
                    break;
                case SLAVERS:
                    break;
                case PLANETS:
                    break;
                case PIRATES:
                    break;
            }
        }

        for (PlayerDataView p : MiniModel.getInstance().players) {
            if (!p.equals(player)) {
                options.add("View " + p.getUsername() + "'s spaceship");
            }
        }

        shuffledDeckView.setOnlyLast(true);

    }


    @Override
    public int getTotalLines() {
        return Math.max(boardView.getRowsToDraw(), shuffledDeckView.getRowsToDraw()) + 1 + player.getShip().getRowsToDraw() + 3;
    }

    @Override
    public ArrayList<String> getOptions() {
        return options;
    }

    @Override
    public StateTuiView internalViewState(Command command) {
        switch (command.name()) {
            case "View":
                String name = command.parameters()[0].replace("'s", "");
                MiniModel.getInstance().playerToView = name;
                return new PlayerStateTuiView();
        }
        return null;
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        writer.print("\033[H\033[2J");
        writer.flush();

        for (int i = 0; i < Math.max(boardView.getRowsToDraw(), shuffledDeckView.getRowsToDraw()); i++) {
            StringBuilder str = new StringBuilder();

            if (i < boardView.getRowsToDraw()) {
                str.append(boardView.drawLineTui(i));
            } else {
                str.append(" ".repeat(Math.max(0, boardView.getColsToDraw())));
            }

            str.append("                       ");
            if (i < shuffledDeckView.getRowsToDraw()) {
                str.append(shuffledDeckView.drawLineTui(i));
            } else {
                str.append(" ".repeat(Math.max(0, shuffledDeckView.getColsToDraw())));
            }

            writer.println(str);
        }
        writer.flush();
        writer.println();

        int playerCont = 0;
        for (int i = 0; i < player.getShip().getRowsToDraw(); i++) {
            StringBuilder str = new StringBuilder();
            str.append(player.getShip().drawLineTui(i));

            if (i > (player.getShip().getRowsToDraw()-1)/5) {
                if (i >= (player.getShip().getRowsToDraw()-1) / 5 * 3 && i < (player.getShip().getRowsToDraw()-1) / 5 * 3 + player.getRowsToDraw()) {
                    str.append("       ");
                    str.append(player.drawLineTui(playerCont));
                    playerCont++;
                }
            }
            writer.println(str);
        }
        writer.flush();
        writer.println();

        PlayerDataView playerTurnName = MiniModel.getInstance().players.stream()
                .filter(playerDataView -> playerDataView.getUsername().equals(MiniModel.getInstance().playerTurn))
                .findFirst()
                .orElse(null);

        writer.println(MiniModel.getInstance().playerTurn.equals(MiniModel.getInstance().nickname) ? "Your turn" : "Waiting for " + playerTurnName.drawLineTui(0) + "'s turn");
        writer.println();
        writer.println("Commands:");
        writer.flush();
    }


    public static void main(String[] args) throws Exception {
        Terminal terminal;
        try {
            terminal = TerminalBuilder.builder()
                    .jna(true)
                    .jansi(true)
                    .build();
        } catch (Exception e) {
            System.err.println("Creation terminal error: " + e.getMessage());
            return;
        }
        Parser parser = new Parser(terminal);

        MiniModel.getInstance().boardView = new BoardView(LevelView.SECOND);

        ArrayList<PlayerDataView> players = MiniModel.getInstance().players;
        players.add(new PlayerDataView("Player1", ColorView.RED, new SpaceShipView(LevelView.LEARNING)));
        players.add(new PlayerDataView("Player2", ColorView.YELLOW, new SpaceShipView(LevelView.SECOND)));
        players.add(new PlayerDataView("Player3", ColorView.GREEN, new SpaceShipView(LevelView.SECOND)));
        players.add(new PlayerDataView("Player4", ColorView.BLUE, new SpaceShipView(LevelView.SECOND)));

        MiniModel.getInstance().nickname = "Player1";
        MiniModel.getInstance().playerTurn = "Player1";

        Stack<CardView> stack = new Stack<>();
        for (Card card :CardsManager.createLearningDeck()) {
            stack.add(convertCard(card));
        }
        MiniModel.getInstance().shuffledDeckView = new DeckView();
        MiniModel.getInstance().shuffledDeckView.setDeck(stack);

        ArrayList<Component> tiles = TilesManager.getTiles();
        for (Component tile : tiles) {
            ComponentView tileView = converter(tile);
            tileView.setCovered(false);
            MiniModel.getInstance().components.add(tileView);
        }

        for (ComponentView tile : MiniModel.getInstance().components) {
            if (tile instanceof StorageView && ((StorageView) tile).getGoods().length > 1) {
                MiniModel.getInstance().players.stream()
                        .filter(player -> player.getUsername().equals("Player1"))
                        .findFirst()
                        .ifPresent(player -> {
                            player.getShip().placeComponent(tile, 7, 5);
                            ((StorageView) tile).setGood(GoodView.BLUE, 0);
                            ((StorageView) tile).setGood(GoodView.YELLOW, 0);
                            ((StorageView) tile).setGood(GoodView.GREEN, 1);

                        });
                break;
            }
        }

        /*
        GameStateTuiView gameStateView = new GameStateTuiView();
        gameStateView.printTui(terminal);
        parser.getCommand(gameStateView.getOptions(), gameStateView.getTotalLines());
        */

        SwapGoodsFromStateTuiView swapGoodsFromStateTuiView = new SwapGoodsFromStateTuiView();
        swapGoodsFromStateTuiView.printTui(terminal);
        parser.getCommand(swapGoodsFromStateTuiView.getOptions(), swapGoodsFromStateTuiView.getTotalLines());
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
