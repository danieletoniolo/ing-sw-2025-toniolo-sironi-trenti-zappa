package view.structures.components;

public class LifeSupportBrownView extends ComponentView {

    public LifeSupportBrownView(int ID, int[] conncetors) {
        super(ID, conncetors);
    }

    @Override
    public void drawComponentGui(){
        //TODO: Implement the GUI drawing logic for the LifeSupportBrown component here
    }

    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 4 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + "   Life    " + super.drawRight(line);
            case 2 -> super.drawLeft(line) + "  Support  " + super.drawRight(line);
            case 3 -> super.drawLeft(line) + "   Brown   " + super.drawRight(line);
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }
}
