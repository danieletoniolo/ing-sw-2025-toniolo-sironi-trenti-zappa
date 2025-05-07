package view.structures.components;

public class ConnectorsView extends ComponentView {

    public ConnectorsView(int ID, int[] connectors) {
        super(ID, connectors);
    }

    @Override
    public void drawComponentGui() {
        //TODO: Implement the GUI drawing logic for the Connectors component here
    }

    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 1, 3, 4 -> super.drawLineTui(line);
            case 2 -> super.drawLeft(line) + "   Pipes   " + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }
}
