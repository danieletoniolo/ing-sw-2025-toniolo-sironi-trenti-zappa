package it.polimi.ingsw.view.miniModel.cards;

public class OpenSpaceView extends CardView {
    public OpenSpaceView(int ID, boolean covered, int level) {
        super(ID, covered, level);
    }

    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        return switch(l) {
            case 0 -> Up;
            case 1 -> "│     OPENSPACE     │";
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

    @Override
    public CardViewType getCardViewType() {
        return CardViewType.OPENSPACE;
    }
}
