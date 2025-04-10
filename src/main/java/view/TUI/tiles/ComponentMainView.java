package view.TUI.tiles;

import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.Good.Good;
import Model.Good.GoodType;
import Model.SpaceShip.Component;
import Model.SpaceShip.Storage;

import java.util.ArrayList;

public class ComponentMainView {
    public static void main(String[] args) {
        try{
            Board board = new Board(Level.LEARNING);

            ComponentView componentView = new ComponentView();

            ArrayList<Component> toVisualize = new ArrayList<>();

            for(int i = 0; i < 156; i++) {
                toVisualize.add(board.getTile(i));
            }

            componentView.setCompnentsToVisualize(toVisualize);
            ((Storage) toVisualize.get(31)).addGood(new Good(GoodType.YELLOW));
            ((Storage) toVisualize.get(31)).addGood(new Good(GoodType.YELLOW));
            ((Storage) toVisualize.get(31)).addGood(new Good(GoodType.YELLOW));

            toVisualize.get(155).rotateClockwise();

            for (int i = 0; i < 156; i++) {
                componentView.setViewedTile(toVisualize.get(i), true);
            }

            componentView.drawOnBoardComponents();

        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }
}
