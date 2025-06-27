package it.polimi.ingsw.view.miniModel.cards;

import it.polimi.ingsw.view.miniModel.components.crewmembers.CrewView;

/**
 * View class for representing an Epidemic card in the text-based user interface.
 * This class extends CardView and provides specific rendering for epidemic cards
 * with colored ASCII art depicting the epidemic effect.
 */
public class EpidemicView extends CardView {
    /** ANSI escape code for red color */
    private static final String red = "\033[31m";
    /** ANSI escape code to reset color formatting */
    private static final String reset = "\033[0m";

    /**
     * Constructs a new EpidemicView with the specified parameters.
     *
     * @param ID the unique identifier of the card
     * @param covered whether the card is currently covered/hidden
     * @param level the level or value associated with the card
     */
    public EpidemicView(int ID, boolean covered, int level) {
        super(ID, covered, level);
    }

    /**
     * Draws a specific line of the epidemic card for the text-based user interface.
     * If the card is covered, delegates to the parent class implementation.
     * Otherwise, renders the epidemic-specific ASCII art with colored crew members.
     *
     * @param l the line number to draw (0-based index)
     * @return the string representation of the specified line
     */
    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        StringBuilder line = new StringBuilder(switch(l) {
            case 0 -> Up;
            case 1 -> "│     EPIDEMIC      │";
            case 2 -> Clear;
            case 3 -> "│      ╭─────╮      │";
            case 4 -> "│      │(" + CrewView.HUMAN.drawTui() + " " + red +  CrewView.HUMAN.drawTui() + reset + ")│";
            case 5 -> "│      ╰─|||─╯      │";
            case 6 -> "│      ╭─|||─╮      │";
            case 7 -> "│      │( " + red + CrewView.UNCOLOREDALIEN.drawTui() + reset + " )│";
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
     * Returns the specific card view type for this epidemic card.
     * This method is used to identify the card type in the user interface system.
     *
     * @return CardViewType.EPIDEMIC indicating this is an epidemic card view
     */
    @Override
    public CardViewType getCardViewType() {
        return CardViewType.EPIDEMIC;
    }
}
