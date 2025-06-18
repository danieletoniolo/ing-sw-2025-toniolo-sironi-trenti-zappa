package it.polimi.ingsw.view.miniModel.cards;

public class StarDustView extends CardView {
    private static final String red = "\033[31m";
    private static final String reset = "\033[0m";

    public StarDustView(int ID, boolean covered, int level) {
        super(ID, covered, level);
    }

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

    @Override
    public CardViewType getCardViewType() {
        return CardViewType.STARDUST;
    }
}
