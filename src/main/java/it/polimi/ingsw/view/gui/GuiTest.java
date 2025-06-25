package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.hits.Hit;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.view.gui.controllers.misc.MessageController;
import it.polimi.ingsw.view.gui.screens.LobbyController;
import it.polimi.ingsw.view.gui.screens.MenuController;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.BoardView;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.miniModel.cards.*;
import it.polimi.ingsw.view.miniModel.cards.hit.HitDirectionView;
import it.polimi.ingsw.view.miniModel.cards.hit.HitTypeView;
import it.polimi.ingsw.view.miniModel.cards.hit.HitView;
import it.polimi.ingsw.view.miniModel.components.ConnectorsView;
import it.polimi.ingsw.view.miniModel.deck.DeckView;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.miniModel.lobby.LobbyView;
import it.polimi.ingsw.view.miniModel.player.MarkerView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.miniModel.spaceship.SpaceShipView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.javatuples.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiTest extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        /*ViewablePileView pile = new ViewablePileView();
        for (int i = 0; i < 20; i++) {
            ShieldView storageView = new ShieldView(i, new int[]{0, 0, 0, 0}, 0, new boolean[]{true, false, true, false});
            pile.addComponent(storageView);
        }

        Node root = pile.getNode();*/
        /*
        AbandonedShipView a = new AbandonedShipView(17, false, 1, 2, 3, 4);
        a.setWidth(200);
        a.setCovered(true);
        SlaversView b = new SlaversView(18, false, 1, 5, 6, 7, 8);
        b.setWidth(200);
        b.setCovered(true);
        SmugglersView c = new SmugglersView(19, false, 1, 9, 10, 11, new ArrayList<>());
        c.setWidth(200);
        c.setCovered(true);
        DeckView d = new DeckView();
        d.addCard(a);
        d.addCard(b);
        d.addCard(c);
        d.setCovered(true);
        Node root = d.getNode();
         */

        /*FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/screens/lobby.fxml"));
        Parent root = loader.load();
        LobbyController lobbyController = loader.getController();
        LobbyView lv = new LobbyView("testLobby", 1, 1, LevelView.LEARNING);
        MiniModel.getInstance().setCurrentLobby(lv);
        lv.addPlayer("Player1");
        lv.setPlayerStatus("Player1", true);
        lobbyController.react();*/

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/screens/building.fxml"));
        Parent root = loader.load();
        DeckView deckView = null;
        for (int i = 0; i < 9; i++) {
            CardView connectorsView = new OpenSpaceView(i + 10, false, 1);

            if (i % 3 == 0) {
                deckView = new DeckView();
            }
            deckView.addCard(connectorsView);

            if (i % 3 == 2) {
                MiniModel.getInstance().getDeckViews().getValue0()[i/3] = deckView;
                MiniModel.getInstance().getDeckViews().getValue1()[i/3] = false;
            }
        }
        MiniModel.getInstance().setBoardView(new BoardView(LevelView.SECOND, 2));
        MiniModel.getInstance().setClientPlayer(new PlayerDataView("lore", MarkerView.RED, new SpaceShipView(LevelView.SECOND)));

        Scene scene = new Scene(root);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    //TODO: Useful for changing cards from model to view
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
