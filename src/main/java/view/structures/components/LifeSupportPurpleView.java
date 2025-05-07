package view.structures.components;

public class LifeSupportPurpleView extends ComponentView {

    public LifeSupportPurpleView(int ID, int[] connectors) {
        super(ID, connectors);
    }

    @Override
    public void drawComponentGui() {
        //TODO: Implement the GUI drawing logic for the LifeSupportPurple component here
    }

    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 4 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + "   Life    " + super.drawRight(line);
            case 2 -> super.drawLeft(line) + "  Support  " + super.drawRight(line);
            case 3 -> super.drawLeft(line) + "  Purple   " + super.drawRight(line);
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }

}
