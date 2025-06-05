package it.polimi.ingsw.view.miniModel.cards;

public class StarDustView extends CardView {
    public StarDustView(int ID, boolean covered, int level) {
        super(ID, covered, level);
    }

    @Override
    public void drawGui() {

    }

    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        return switch(l) {
            case 0 -> Up;
            case 1 -> "│     STARDUST      │";
            case 2,3,4,5,6,7,8 -> Clear;
            case 9 -> Down;
            default -> null;
        };
    }

    @Override
    public CardViewType getCardViewType() {
        return CardViewType.STARDUST;
    }
}
