package view.structures.cards;

public abstract class CardView {
    public String Up =      "╭─────────────────────╮";
    public String Clear =   "│                     │";
    public String Covered = "│       Covered       │";
    public String Empty =   "                       ";
    public String Down =    "╰─────────────────────╯";
    private int ID;
    private boolean covered;
    
    public CardView(int ID, boolean covered) {
        this.ID = ID;
        this.covered = covered;
    }

    public void drawCardGui(){
        //TODO: Implement GUI drawing logic
    }

    public static int getRowsToDraw() {
        return 10;
    }

    public String drawLineTui(int line){
        return switch(line) {
            case 0 -> Up;
            case 3 -> Covered;
            case 1,2,4,5,6,7,8 -> Clear;
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
}
