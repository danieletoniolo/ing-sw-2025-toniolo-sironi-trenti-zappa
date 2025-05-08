package view.structures.cards;

public class EpidemicView extends CardView {
    public EpidemicView(int ID, boolean covered, int level) {
        super(ID, covered, level);
    }

    @Override
    public void drawCardGui(){

    }

    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        return switch(l) {
            case 0 -> Up;
            case 1 -> "│       EPIDEMIC      │";
            case 2,3,4,5,6,7,8 -> Clear;
            case 9 -> Down;
            default -> null;
        };
    }
}
