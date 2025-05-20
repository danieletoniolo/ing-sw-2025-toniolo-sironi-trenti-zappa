package view.miniModel.cards;

import view.miniModel.Structure;

public abstract class CardView implements Structure {
    public String Up =       "╭───────────────────╮";
    public String Clear =    "│                   │";
    public String Covered1 = "│      ───┬───      │";
    public String Covered2 = "│         │         │";
    public String Covered3 = "│         │         │";
    public String Covered4 = "│      ───┴───      │";
    public String Covered5 = "│      ─┬───┬─      │";
    public String Covered6 = "│       │   │       │";
    public String Covered7 = "│       │   │       │";
    public String Covered8 = "│      ─┴───┴─      │";
    public String Empty =    "                     ";
    public String Down =     "╰───────────────────╯";

    private int ID;
    private boolean covered;
    private int level;
    
    public CardView(int ID, boolean covered, int level) {
        this.ID = ID;
        this.covered = covered;
        this.level = level;
    }

    @Override
    public void drawGui(){
        //TODO: Implement GUI drawing logic
    }

    public static int getRowsToDraw() {
        return 10;
    }

    public static int getColsToDraw() {
        return 21;
    }

    @Override
    public String drawLineTui(int line){
        return switch(line) {
            case 0 -> Up;
            case 3 -> (level == 1) ? Covered1 : Covered5;
            case 4 -> (level == 1) ? Covered2 : Covered6;
            case 5 -> (level == 1) ? Covered3 : Covered7;
            case 6 -> (level == 1) ? Covered4 : Covered8;
            case 1, 2, 7, 8 -> Clear;
            case 9 -> Down;
            default -> null;
        };
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public boolean isCovered() {
        return covered;
    }

    public void setCovered(boolean covered) {
        this.covered = covered;
    }

    public abstract CardViewType getCardViewType();
}
