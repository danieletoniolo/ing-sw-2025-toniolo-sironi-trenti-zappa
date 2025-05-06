package view.tui.card;

import Model.Cards.Card;
import Model.Cards.CardsManager;
import Model.Cards.Hits.Direction;
import Model.Cards.Hits.Hit;
import Model.Cards.Hits.HitType;
import Model.Game.Board.Deck;
import Model.Game.Board.Level;
import Model.Good.Good;
import Model.Good.GoodType;
import org.javatuples.Pair;
import view.structures.cards.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CardMainView {
    public static void main(String[] args) {
        try{
            CardView c1 = new AbandonedShipView(1, false, 2, 3, 4);
            CardView c2 = new AbandonedShipView(2, true, 2, 3, 4);
            CardView c3 = new AbandonedStationView(3, false, 2, 3, new ArrayList<>(Arrays.asList(new Good(GoodType.RED), new Good(GoodType.GREEN))));
            CardView c4 = new CombatZoneView(4, false, 3, 4, new ArrayList<>(Arrays.asList(new Hit(HitType.LARGEMETEOR, Direction.NORTH), new Hit(HitType.SMALLMETEOR, Direction.SOUTH))));
            CardView c5 = new EpidemicView(5, false);
            CardView c6 = new EpidemicView(6, true);
            CardView c7 = new MeteorSwarmView(7, false, new ArrayList<>(Arrays.asList(new Hit(HitType.LIGHTFIRE, Direction.WEST), new Hit(HitType.HEAVYFIRE, Direction.EAST), new Hit(HitType.LIGHTFIRE, Direction.SOUTH))));
            CardView c8 = new OpenSpaceView(8, false);
            CardView c9 = new PiratesView(9, false, 2, 3, 4, new ArrayList<>(Arrays.asList(new Hit(HitType.LARGEMETEOR, Direction.NORTH), new Hit(HitType.SMALLMETEOR, Direction.SOUTH), new Hit(HitType.LIGHTFIRE, Direction.EAST))));
            List<Good> goodsList1 = new ArrayList<>(Arrays.asList(new Good(GoodType.GREEN), new Good(GoodType.YELLOW)));
            List<Good> goodsList2 = new ArrayList<>(Arrays.asList(new Good(GoodType.BLUE), new Good(GoodType.RED)));
            List<List<Good>> goodsMatrix = new ArrayList<>();
            goodsMatrix.add(goodsList1);
            goodsMatrix.add(goodsList2);
            CardView c10 = new PlanetsView(10, false, 2, 3, goodsMatrix);
            CardView c11 = new SlaversView(11, false, 2, 3, 4, 5);
            CardView c12 = new SmugglersView(12, false, 2, 3, 4, goodsList1);
            CardView c13 = new StarDustView(13, false);
            for(int i = 0; i < 10; i++){
                System.out.print(c1.drawLineTui(i));
                System.out.print(c2.drawLineTui(i));
                System.out.print(c3.drawLineTui(i));
                System.out.print(c4.drawLineTui(i));
                System.out.print(c5.drawLineTui(i));
                System.out.print(c6.drawLineTui(i));
                System.out.println();
            }
            for(int i = 0; i < 10; i++){
                System.out.print(c7.drawLineTui(i));
                System.out.print(c8.drawLineTui(i));
                System.out.print(c9.drawLineTui(i));
                System.out.print(c10.drawLineTui(i));
                System.out.print(c11.drawLineTui(i));
                System.out.print(c12.drawLineTui(i));
                System.out.println();
            }
            for(int i = 0; i < 10; i++){
                System.out.print(c13.drawLineTui(i));
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        /*try{
            int COLS = 7;
            CardView cardView = new CardView();

            ArrayList<Card> toVisualize = new ArrayList<>();
            for(int i = 0; i < 40; i++) {
                toVisualize.add(CardsManager.getCard(i));
            }

            drawToVisualize(COLS, toVisualize, cardView);

            Deck[] decks = CardsManager.createDecks(Level.SECOND);
            drawDecks(decks, cardView);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

         */
    }

    private static void drawToVisualize(int COLS, ArrayList<Card> toVisualize, CardViewFake cv) {
        int i;
        for (i = 0; i < toVisualize.size() / COLS; i++) {
            ArrayList<Pair<Card, Boolean>> cards = new ArrayList<>();
            for (Card c : toVisualize.subList(i * COLS, i * COLS + COLS)) {
                cards.add(new Pair<>(c, true));
            }
            cv.drawCardsOnOneLine(cards);
        }
        ArrayList<Pair<Card, Boolean>> cards = new ArrayList<>();
        for (Card c : toVisualize.subList(i * COLS, toVisualize.size())) {
            cards.add(new Pair<>(c, true));
        }
        cv.drawCardsOnOneLine(cards);
    }

    private static void drawDecks(Deck[] toVisualize, CardView cv) {
        for (int i = 0; i < toVisualize.length; i++) {
            ArrayList<Pair<Card, Boolean>> deck = new ArrayList<>();
            for (Card c : toVisualize[i].getCards()) {
                deck.add(new Pair<>(c, true));
            }
            int j = i + 1;
            System.out.println("Deck " + j + " ( pickable: " + toVisualize[i].isPickable() + " ) " + ":");
            //cv.drawCardsOnOneLine(deck);
            System.out.println();
        }
    }
}
