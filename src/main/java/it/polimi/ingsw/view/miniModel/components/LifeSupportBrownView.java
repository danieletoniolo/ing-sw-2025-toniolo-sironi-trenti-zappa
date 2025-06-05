package it.polimi.ingsw.view.miniModel.components;

public class LifeSupportBrownView extends ComponentView {
    private String brown = "\033[38;5;220m";
    private String reset = "\033[0m";

    public LifeSupportBrownView(int ID, int[] connectors, int clockWise) {
        super(ID, connectors, clockWise);
    }

    @Override
    public void drawGui(){
        //TODO: Implement the GUI drawing logic for the LifeSupportBrown component here
    }

    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + " " + brown + " * " + reset + " " + super.drawRight(line);
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }

    @Override
    public TilesTypeView getType() {
        return TilesTypeView.LIFE_SUPPORT_BROWN;
    }

    @Override
    public LifeSupportBrownView clone() {
        LifeSupportBrownView copy = new LifeSupportBrownView(this.getID(), this.getConnectors(), this.getClockWise());
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
