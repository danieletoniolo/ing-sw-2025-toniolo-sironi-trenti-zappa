package view.tui.states;

import Model.Cards.*;
import Model.Cards.Hits.Hit;
import Model.Game.Board.Deck;
import Model.Good.Good;
import Model.SpaceShip.*;
import org.javatuples.Pair;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import view.structures.MiniModel;
import view.structures.board.BoardView;
import view.structures.board.LevelView;
import view.structures.cards.*;
import view.structures.cards.hit.HitDirectionView;
import view.structures.cards.hit.HitTypeView;
import view.structures.cards.hit.HitView;
import view.structures.components.*;
import view.structures.deck.DeckView;
import view.structures.good.GoodView;
import view.structures.player.ColorView;
import view.structures.player.PlayerDataView;
import view.structures.spaceship.SpaceShipView;
import view.tui.input.Command;
import view.tui.input.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static Model.Game.Board.Level.SECOND;

public class BuildingStateTuiView implements StateTuiView {
    private final ArrayList<String> options = new ArrayList<>();
    private final int cols = 26;
    private PlayerDataView player;
    private final Pair<DeckView[], Boolean[]> decksView;

    public BuildingStateTuiView() {
        options.add("Pick tile ('row, column')");
        options.add("Put tile on spaceship");
        options.add("Put tile in reserve");
        options.add("Put the tile in the pile");
        options.add("Rotate tile");
        if (MiniModel.getInstance().boardView.getLevel() == LevelView.SECOND) {
            options.add("Pick deck 1");
            options.add("Pick deck 2");
            options.add("Pick deck 3");
            options.add("Flip Timer");
        }
        options.add("Finish");


        this.player = MiniModel.getInstance().players.stream()
                .filter(player -> player.getUsername().equals(MiniModel.getInstance().nickname))
                .findFirst()
                .orElse(null);

        this.decksView = MiniModel.getInstance().deckViews;

        for (PlayerDataView p : MiniModel.getInstance().players) {
            if (!p.equals(player)) {
                options.add("View " + p.getUsername() + "'s spaceship");
            }
        }
    }

    @Override
    public int getTotalLines() {
        return 1 + (MiniModel.getInstance().components.size() / cols) * ComponentView.getRowsToDraw()
                 + (MiniModel.getInstance().components.size() % cols == 0 ? 0 : ComponentView.getRowsToDraw())
                 + 1 + player.getShip().getRowsToDraw();
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

        drawTiles(writer, MiniModel.getInstance().components);
        writer.println();

        int deckCount = 0;
        for (int i = 0; i < player.getShip().getRowsToDraw(); i++) {
            StringBuilder line = new StringBuilder();
            line.append(player.getShip().drawLineTui(i));
            if (i == ((player.getShip().getRowsToDraw() - 2)/5*2 + 1) - 1) {
                line.append("       " + "   Hand: ");
            }
            else if(i >= ((player.getShip().getRowsToDraw() - 2)/5*2 + 1) && i < ((player.getShip().getRowsToDraw() - 2)/5*3 + 1)) {
                line.append("         ").append(player.getHand().drawLineTui((i - 1) % ComponentView.getRowsToDraw()));
            }
            else if (i >= ((player.getShip().getRowsToDraw() - 2) / 5 + 1)) {
                line.append("                ");
            }
            if (i >= ((player.getShip().getRowsToDraw() - 2)/5 + 1) && i < /*TODO: Da sistemare il 10*/ DeckView.getRowsToDraw() + ((player.getShip().getRowsToDraw() - 2)/5 + 1)) {
                line.append("          ");
                for (int j = 0; j < 3; j++) {
                    if (decksView.getValue1()[j]) {
                        decksView.getValue0()[j].setCovered(true);
                        line.append("     ").append(decksView.getValue0()[j].drawLineTui(deckCount));
                    }
                    else {
                        line.append("                              ");
                    }
                }
                deckCount++;
            }
            writer.println(line);
        }
        writer.println("Commands:");
        writer.flush();

    }

    private void drawTiles(java.io.PrintWriter writer, ArrayList<ComponentView> tiles) {
        writer.print("   ");
        for (int i = 0; i < cols; i++) {
            writer.print("  " + ((i+1) / 10 == 0 ? " " + (i+1) : (i+1)) + "   ");
        }
        writer.println();
        for (int h = 0; h < tiles.size() / cols; h++) {
            for (int i = 0; i < ComponentView.getRowsToDraw(); i++) {
                if (i == 1) {
                    writer.print(((h+1) / 10 == 0 ? ((h+1) + "  ") : (h+1) + " "));
                }else{
                    writer.print("   ");
                }
                for (int k = 0; k < cols; k++) {
                    writer.print(tiles.get(h * cols + k).drawLineTui(i));
                }
                writer.println();
            }
        }

        for (int i = 0; i < ComponentView.getRowsToDraw(); i++) {
            if (i == 1) {
                writer.print(((tiles.size()/cols+1) / 10 == 0 ? ((tiles.size()/cols+1) + "  ") : ((tiles.size()/cols+1) + " ")));
            }else{
                writer.print("   ");
            }
            for (int k = 0; k < tiles.size() % cols; k++) {
                writer.print(tiles.get((tiles.size() / cols) * cols + k).drawLineTui(i));
            }
            writer.println();
        }
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

        Component[] tiles = TilesManager.getTiles();
        for (Component tile : tiles) {
            ComponentView tileView = converter(tile);
            tileView.setCovered(false);
            MiniModel.getInstance().components.add(tileView);
        }

        ArrayList<PlayerDataView> players = MiniModel.getInstance().players;
        players.add(new PlayerDataView("Player1", ColorView.RED, new SpaceShipView(LevelView.LEARNING)));
        players.add(new PlayerDataView("Player2", ColorView.RED, new SpaceShipView(LevelView.SECOND)));
        players.add(new PlayerDataView("Player3", ColorView.RED, new SpaceShipView(LevelView.SECOND)));
        players.add(new PlayerDataView("Player4", ColorView.RED, new SpaceShipView(LevelView.SECOND)));

        MiniModel.getInstance().nickname = "Player1";
        players.getFirst().setHand(new GenericComponentView());
        players.getFirst().getHand().setCovered(false);

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


        BuildingStateTuiView buildingStateView = new BuildingStateTuiView();

        buildingStateView.printTui(terminal);
        parser.getCommand(buildingStateView.getOptions(), buildingStateView.getTotalLines());
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
