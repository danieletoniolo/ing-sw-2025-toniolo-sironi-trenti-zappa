package it.polimi.ingsw.view.miniModel.cards;

/**
 * Represents a view for an open space card in the game.
 * This class extends CardView and provides specific rendering for open space cards.
 * @author Matteo Zappa
 */
public class OpenSpaceView extends CardView {
    /**
     * Constructs an OpenSpaceView with the specified parameters.
     *
     * @param ID the unique identifier of the card
     * @param covered whether the card is covered or not
     * @param level the level of the card
     */
    public OpenSpaceView(int ID, boolean covered, int level) {
        super(ID, covered, level);
    }

    /**
     * Draws a specific line of the card for text-based user interface rendering.
     *
     * @param l the line number to draw (0-9)
     * @return the string representation of the specified line, or null if line number is invalid
     */
    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        return switch(l) {
            case 0 -> Up;
            case 1 -> "│     OPEN SPACE    │";
            case 2 -> Clear;
            case 3 -> "│        / \\        │";
            case 4 -> "│       /   \\       │";
            case 5 -> "│       │===│       │";
            case 6 -> "│      /     \\      │";
            case 7 -> "│      │_____│      │";
            case 8 -> "│       /|||\\       │";
            case 9 -> Down;
            default -> null;
        };
    }

    /**
     * Returns the type of this card view.
     *
     * @return CardViewType.OPENSPACE indicating this is an open space card view
     */
    @Override
    public CardViewType getCardViewType() {
        return CardViewType.OPENSPACE;
    }
}
