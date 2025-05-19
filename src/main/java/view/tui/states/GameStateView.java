package view.tui.states;

import Model.Cards.*;
import Model.Cards.Hits.Hit;
import Model.Good.Good;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import view.structures.MiniModel;
import view.structures.board.BoardView;
import view.structures.board.LevelView;
import view.structures.cards.*;
import view.structures.cards.hit.HitDirectionView;
import view.structures.cards.hit.HitTypeView;
import view.structures.cards.hit.HitView;
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

public class GameStateView implements StateView {
    private ArrayList<String> options;

    private BoardView boardView = MiniModel.getInstance().boardView;
    private PlayerDataView player;
    private DeckView shuffledDeckView = MiniModel.getInstance().shuffledDeckView;


    public GameStateView() {
        this.options = new ArrayList<>();
        this.options.add("Roll dice");
        this.options.add("Use battery");

        this.player = MiniModel.getInstance().players.stream()
                .filter(player -> player.getUsername().equals(MiniModel.getInstance().nickname))
                .findFirst()
                .orElse(null);

        for (PlayerDataView p : MiniModel.getInstance().players) {
            if (!p.equals(player)) {
                options.add("View " + p.getUsername() + "'s spaceship");
            }
        }

        shuffledDeckView.setOnlyLast(true);

    }


    @Override
    public int getTotalLines() {
        return Math.max(boardView.getRowsToDraw(), shuffledDeckView.getRowsToDraw()) + 1 + player.getShip().getRowsToDraw() + 1;
    }

    @Override
    public ArrayList<String> getOptions() {
        return options;
    }

    @Override
    public StateView internalViewState(Command command) {
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
        players.add(new PlayerDataView("Player2", ColorView.RED, new SpaceShipView(LevelView.SECOND)));
        players.add(new PlayerDataView("Player3", ColorView.RED, new SpaceShipView(LevelView.SECOND)));
        players.add(new PlayerDataView("Player4", ColorView.RED, new SpaceShipView(LevelView.SECOND)));

        MiniModel.getInstance().nickname = "Player1";

        Stack<CardView> stack = new Stack<>();
        for (Card card :CardsManager.createLearningDeck()) {
            stack.add(convertCard(card));
        }
        MiniModel.getInstance().shuffledDeckView = new DeckView();
        MiniModel.getInstance().shuffledDeckView.setDeck(stack);


        GameStateView gameStateView = new GameStateView();
        gameStateView.printTui(terminal);
        parser.getCommand(gameStateView.getOptions(), gameStateView.getTotalLines());

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
