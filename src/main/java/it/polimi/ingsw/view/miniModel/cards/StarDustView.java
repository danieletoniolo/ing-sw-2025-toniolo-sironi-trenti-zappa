package it.polimi.ingsw.view.miniModel.cards;

/**
 * View class for representing a StarDust card in the text-based user interface.
 * Extends CardView to provide specific visual representation for StarDust cards.
 */
public class StarDustView extends CardView {
    /** ANSI color code for red text */
    private static final String red = "\033[31m";
    /** ANSI reset code to clear formatting */
    private static final String reset = "\033[0m";

    /**
     * Constructs a new StarDustView with the specified parameters.
     *
     * @param ID the unique identifier of the card
     * @param covered whether the card is covered or face up
     * @param level the level of the card
     */
    public StarDustView(int ID, boolean covered, int level) {
        super(ID, covered, level);
    }

    /**
     * Draws a specific line of the card's text-based representation.
     * If the card is covered, delegates to the parent implementation.
     * Otherwise, returns the appropriate line for the StarDust card design.
     *
     * @param l the line number to draw (0-based)
     * @return the string representation of the specified line
     */
    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        StringBuilder line = new StringBuilder(switch(l) {
            case 0 -> Up;
            case 1 -> "│     STARDUST      │";
            case 2 -> Clear;
            case 3 -> "│      ╭─────╮      │";
            case 4 -> "│    1 " + red + "═     ≣" + reset + " 1";
            case 5 -> "│      ╰─|||─╯      │";
            case 6 -> "│      ╭─|||─╮      │";
            case 7 -> "│      │(   )│      │";
            case 8 -> "│      ╰─────╯      │";
            case 9 -> Down;
            default -> "";
        });

        while (line.toString().replaceAll("\033\\[[0-9;]*m", "").length() < getColsToDraw() - 1) {
            line.append(" ");
        }
        if (line.toString().replaceAll("\033\\[[0-9;]*m", "").length() == getColsToDraw() - 1) {
            line.append("│");
        }
        return line.toString();
    }

    /**
     * Returns the card view type for this StarDust card.
     *
     * @return CardViewType.STARDUST indicating this is a StarDust card view
     */
    @Override
    public CardViewType getCardViewType() {
        return CardViewType.STARDUST;
    }
}
