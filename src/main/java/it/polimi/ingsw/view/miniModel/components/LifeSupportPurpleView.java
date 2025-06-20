package it.polimi.ingsw.view.miniModel.components;

public class LifeSupportPurpleView extends ComponentView {
    private static final String purple = "\033[35m";
    private static final String reset = "\033[0m";

    public LifeSupportPurpleView(int ID, int[] connectors, int clockWise) {
        super(ID, connectors, clockWise);
    }

    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + " " + purple + " * " + reset + " " + super.drawRight(line);
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }

    @Override
    public TilesTypeView getType() {
        return TilesTypeView.LIFE_SUPPORT_PURPLE;
    }

    @Override
    public LifeSupportPurpleView clone() {
        LifeSupportPurpleView copy = new LifeSupportPurpleView(this.getID(), this.getConnectors(), this.getClockWise());
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
