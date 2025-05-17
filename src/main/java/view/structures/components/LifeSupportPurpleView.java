package view.structures.components;

public class LifeSupportPurpleView extends ComponentView {
    private String purple = "\033[35m";
    private String reset = "\033[0m";

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
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + " " + purple + "LifeS" + reset + " " + super.drawRight(line);
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }

}
