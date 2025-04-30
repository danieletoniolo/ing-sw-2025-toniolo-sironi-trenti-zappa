package view.tui.tiles;

import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.Good.Good;
import Model.Good.GoodType;
import Model.SpaceShip.Component;
import Model.SpaceShip.Storage;
import org.javatuples.Pair;

import java.util.ArrayList;

public class ComponentMainView {
    public static void main(String[] args) {
        try{
            int COLS = 13;
            Board board = new Board(Level.LEARNING);

            ComponentView componentView = new ComponentView();

            ArrayList<Component> toVisualize = new ArrayList<>();
            for(int i = 0; i < 156; i++) {
                toVisualize.add(board.getTiles()[i]);
            }

            ((Storage) toVisualize.get(31)).addGood(new Good(GoodType.YELLOW));
            ((Storage) toVisualize.get(31)).addGood(new Good(GoodType.YELLOW));
            ((Storage) toVisualize.get(31)).addGood(new Good(GoodType.YELLOW));

            toVisualize.get(155).rotateClockwise();
            
            drawToVisualize(COLS, toVisualize, componentView);
            drawSpaceShip(toVisualize, componentView);


        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }

    private static void drawToVisualize(int COLS, ArrayList<Component> toVisualize, ComponentView componentView) {
        int i;
        for (i = 0; i < toVisualize.size() / COLS; i++) {
            ArrayList<Pair<Component, Boolean>> components = new ArrayList<>();
            for (Component component : toVisualize.subList(i * COLS, i * COLS + COLS)) {
                components.add(new Pair<>(component, true));
            }
            componentView.drawTilesOnOneLine(components);
        }
        ArrayList<Pair<Component, Boolean>> components = new ArrayList<>();
        for (Component component : toVisualize.subList(i * COLS, toVisualize.size())) {
            components.add(new Pair<>(component, true));
        }
        componentView.drawTilesOnOneLine(components);
    }

    private static void drawSpaceShip(ArrayList<Component> toVisualize, ComponentView componentView) {
        ArrayList<Pair<Component, Boolean>> spaceShip;
        for (int i = 0; i < 5; i++) {
            switch (i) {
                case 0:
                    spaceShip = new ArrayList<>();
                    spaceShip.add(new Pair<>(null, null));
                    spaceShip.add(new Pair<>(null, null));
                    spaceShip.add(new Pair<>(toVisualize.get(7), true));
                    spaceShip.add(new Pair<>(null, null));
                    spaceShip.add(new Pair<>(null, null));
                    componentView.drawTilesOnOneLine(spaceShip);
                    break;
                case 1:
                    spaceShip = new ArrayList<>();
                    spaceShip.add(new Pair<>(null, null));
                    spaceShip.add(new Pair<>(toVisualize.get(25), true));
                    spaceShip.add(new Pair<>(toVisualize.get(30), true));
                    spaceShip.add(new Pair<>(toVisualize.get(35), true));
                    spaceShip.add(new Pair<>(null, null));
                    componentView.drawTilesOnOneLine(spaceShip);
                    break;
                case 2, 3, 4:
                    spaceShip = new ArrayList<>();
                    spaceShip.add(new Pair<>(toVisualize.get(50 + i), true));
                    spaceShip.add(new Pair<>(toVisualize.get(55 + i), true));
                    spaceShip.add(new Pair<>(toVisualize.get(60 + i), true));
                    spaceShip.add(new Pair<>(toVisualize.get(70 + i), true));
                    spaceShip.add(new Pair<>(toVisualize.get(90 + i), true));
                    componentView.drawTilesOnOneLine(spaceShip);
                    break;
            }
        }
    }
}
