package it.polimi.ingsw.view.tui.card;

import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.CardsManager;
import it.polimi.ingsw.model.cards.hits.Hit;
import it.polimi.ingsw.model.game.board.Deck;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.view.miniModel.cards.*;
import it.polimi.ingsw.view.miniModel.cards.hit.HitDirectionView;
import it.polimi.ingsw.view.miniModel.cards.hit.HitTypeView;
import it.polimi.ingsw.view.miniModel.cards.hit.HitView;
import it.polimi.ingsw.view.miniModel.deck.DeckView;
import it.polimi.ingsw.view.miniModel.good.GoodView;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CardMainView {
    public static void main(String[] args) {
        try {
            Stack<CardView> cards = new Stack<>();
            Deck[] decks = CardsManager.createDecks(Level.SECOND);
            Stack<Card> shuffledDeck = CardsManager.createShuffledDeck(decks);

            ArrayList<Card> allCards = new ArrayList<>();
            for (int i = 0; i < 40; i++){
                allCards.add(CardsManager.getCard(i));
            }

            for (Card card : allCards) {
                switch (card.getCardType()) {
                    case PIRATES:
                        int cannon = ((Pirates) card).getCannonStrengthRequired();
                        int credits = ((Pirates) card).getCredit();
                        int flight = ((Pirates) card).getFlightDays();
                        ArrayList<HitView> hits = new ArrayList<>();
                        for (Hit hit : ((Pirates) card).getFires()) {
                            hits.add(new HitView(HitTypeView.valueOf(hit.getType().name()), HitDirectionView.valueOf(hit.getDirection().name())));
                        }
                        cards.add(new PiratesView(card.getID(), false, card.getCardLevel(), cannon, credits, flight, hits));
                        break;
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
                        cards.add(new PlanetsView(card.getID(), false, card.getCardLevel(), ((Planets) card).getFlightDays(), goodViews));
                        break;
                    case SLAVERS:
                        cards.add(new SlaversView(card.getID(), false, card.getCardLevel(), ((Slavers) card).getCannonStrengthRequired(), ((Slavers) card).getCredit(), ((Slavers) card).getFlightDays(), ((Slavers) card).getCrewLost()));
                        break;
                    case EPIDEMIC:
                        cards.add(new EpidemicView(card.getID(), false, card.getCardLevel()));
                        break;
                    case STARDUST:
                        cards.add(new StarDustView(card.getID(), false, card.getCardLevel()));
                        break;
                    case OPENSPACE:
                        cards.add(new OpenSpaceView(card.getID(), false, card.getCardLevel()));
                        break;
                    case SMUGGLERS:
                        int cannonStrength = ((Smugglers) card).getCannonStrengthRequired();
                        int goodsLost = ((Smugglers) card).getGoodsLoss();
                        int flightDays = ((Smugglers) card).getFlightDays();
                        List<GoodView> goods = new ArrayList<>();
                        for (Good good : ((Smugglers) card).getGoodsReward()) {
                            goods.add(GoodView.valueOf(good.getColor().name()));
                        }
                        cards.add(new SmugglersView(card.getID(), false, card.getCardLevel(), cannonStrength, goodsLost, flightDays, goods));
                        break;
                    case COMBATZONE:
                        int loss = ((CombatZone) card).getLost();
                        int flights = ((CombatZone) card).getFlightDays();
                        List<HitView> hitsList = new ArrayList<>();
                        for (Hit hit : ((CombatZone) card).getFires()) {
                            hitsList.add(new HitView(HitTypeView.valueOf(hit.getType().name()), HitDirectionView.valueOf(hit.getDirection().name())));
                        }
                        cards.add(new CombatZoneView(card.getID(), false, card.getCardLevel(), loss, flights, hitsList));
                        break;
                    case METEORSWARM:
                        List<HitView> meteorHits = new ArrayList<>();
                        for (Hit hit : ((MeteorSwarm) card).getMeteors()) {
                            meteorHits.add(new HitView(HitTypeView.valueOf(hit.getType().name()), HitDirectionView.valueOf(hit.getDirection().name())));
                        }
                        cards.add(new MeteorSwarmView(card.getID(), false, card.getCardLevel(), meteorHits));
                        break;
                    case ABANDONEDSHIP:
                        int crewLost = ((AbandonedShip) card).getCrewRequired();
                        int creditsRequired = ((AbandonedShip) card).getCredit();
                        int flightDaysRequired = ((AbandonedShip) card).getFlightDays();
                        cards.add(new AbandonedShipView(card.getID(), false, card.getCardLevel(), crewLost, creditsRequired, flightDaysRequired));
                        break;
                    case ABANDONEDSTATION:
                        int crew = ((AbandonedStation) card).getCrewRequired();
                        int days = ((AbandonedStation) card).getFlightDays();
                        List<GoodView> goodsList = new ArrayList<>();
                        for (Good good : ((AbandonedStation) card).getGoods()) {
                            goodsList.add(GoodView.valueOf(good.getColor().name()));
                        }
                        cards.add(new AbandonedStationView(card.getID(), false, card.getCardLevel(), crew, days, goodsList));
                        break;
                }
            }

            DeckView deckView = new DeckView();
            deckView.setDeck(cards);
            deckView.setCovered(false);
            deckView.setOnlyLast(true);
            printDeck(deckView);

            for (CardView card : cards) {
                card.setCovered(false);
            }
            printCards(cards, 7);




        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printDeck(DeckView deck) {
        for (int i = 0; i < deck.getRowsToDraw(); i++) {
            System.out.print(deck.drawLineTui(i));
            System.out.println();
        }
    }

    private static void printCards(Stack<CardView> cards, int cols) {
        for (int h = 0; h < cards.size() / cols; h++) {
            for (int i = 0; i < CardView.getRowsToDraw(); i++) {
                for (int k = 0; k < cols; k++) {
                    System.out.print(cards.get(h * cols + k).drawLineTui(i));
                }
                System.out.println();
            }
        }

        for (int i = 0; i < CardView.getRowsToDraw(); i++) {
            for (int k = 0; k < cards.size() % cols; k++) {
                System.out.print(cards.get((cards.size() / cols) * cols + k).drawLineTui(i));
            }
            System.out.println();
        }
    }
}