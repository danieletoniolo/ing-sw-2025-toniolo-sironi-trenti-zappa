package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.hits.Hit;
import it.polimi.ingsw.model.game.board.Deck;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.view.miniModel.components.CabinView;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.miniModel.cards.*;
import it.polimi.ingsw.view.miniModel.cards.hit.HitDirectionView;
import it.polimi.ingsw.view.miniModel.cards.hit.HitTypeView;
import it.polimi.ingsw.view.miniModel.cards.hit.HitView;
import it.polimi.ingsw.view.miniModel.components.CabinView;
import it.polimi.ingsw.view.miniModel.components.StorageView;
import it.polimi.ingsw.view.miniModel.components.crewmembers.CrewMembers;
import it.polimi.ingsw.view.miniModel.deck.DeckView;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.miniModel.spaceship.SpaceShipView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class GuiTest extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        /*
        //TODO: Esempio storage a 3 posti
        StorageView s = new StorageView(27, null, 0, false, 3);
        Image img_s = s.drawGui();
        Image img = GoodView.BLUE.drawGui(img_s, (int) img_s.getHeight() / 2, (int) img_s.getHeight() / 2, 45, 3, 1);
        Image img_in = GoodView.RED.drawGui(img, (int) img_s.getHeight() / 2, (int) img_s.getHeight() / 2, 45, 3, 2);
        Image img_fin = GoodView.YELLOW.drawGui(img_in, (int) img_s.getHeight() / 2, (int) img_s.getHeight() / 2, 45, 3, 3);

        ImageView img1 = new ImageView(img_fin);
        StackPane stackPane = new StackPane(img1);
        Scene scene = new Scene(stackPane);
        stage.setScene(scene);
        stage.show();
         */

        /*
        //TODO: Esempio storage a 2 posti
        StorageView s = new StorageView(20, null, 0, false, 2);
        Image img_s = s.drawGui();
        Image img = GoodView.BLUE.drawGui(img_s, (int) img_s.getHeight() / 2, (int) img_s.getHeight() / 2, 45, 2, 1);
        Image img_fin = GoodView.RED.drawGui(img, (int) img_s.getHeight() / 2, (int) img_s.getHeight() / 2, 45, 2, 2);

        ImageView img1 = new ImageView(img_fin);
        StackPane stackPane = new StackPane(img1);
        Scene scene = new Scene(stackPane);
        stage.setScene(scene);
        stage.show();
        */


        /*
        //TODO: Come usare crew members
        CabinView c = new CabinView(155, null, 1);
        Image img_center = c.drawGui();
        ImageView img = new ImageView(img_center);

        Image img_s = CrewMembers.HUMAN.drawGui(img_center, (int) img_center.getHeight() / 2, (int) img_center.getHeight() / 2, (int) img_center.getHeight(), 1);
        ImageView img1 = new ImageView(img_s);

        StackPane stackPane = new StackPane(img1);
        Scene scene = new Scene(stackPane);
        stage.setScene(scene);
        stage.show();
         */


        Parent root = FXMLLoader.load(GuiTest.class.getResource("/ship_learning.fxml"));
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
                java.util.List<java.util.List<GoodView>> goodViews = new ArrayList<>();
                for (int i = 0; i < numberOfPlanets; i++) {
                    java.util.List<GoodView> goodList = new ArrayList<>();
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
                java.util.List<GoodView> goods = new ArrayList<>();
                for (Good good : ((Smugglers) card).getGoodsReward()) {
                    goods.add(GoodView.valueOf(good.getColor().name()));
                }
                return new SmugglersView(card.getID(), false, card.getCardLevel(), cannonStrength, goodsLost, flightDays, goods);
            case COMBATZONE:
                int loss = ((CombatZone) card).getLost();
                int flights = ((CombatZone) card).getFlightDays();
                java.util.List<HitView> hitsList = new ArrayList<>();
                for (Hit hit : ((CombatZone) card).getFires()) {
                    hitsList.add(new HitView(HitTypeView.valueOf(hit.getType().name()), HitDirectionView.valueOf(hit.getDirection().name())));
                }
                return new CombatZoneView(card.getID(), false, card.getCardLevel(), loss, flights, hitsList);
            case METEORSWARM:
                java.util.List<HitView> meteorHits = new ArrayList<>();
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
                java.util.List<GoodView> goodsList = new ArrayList<>();
                for (Good good : ((AbandonedStation) card).getGoods()) {
                    goodsList.add(GoodView.valueOf(good.getColor().name()));
                }
                return new AbandonedStationView(card.getID(), false, card.getCardLevel(), crew, days, goodsList);
            default:
                return null;
        }
    }
}
