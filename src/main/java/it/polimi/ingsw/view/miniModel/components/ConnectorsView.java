package it.polimi.ingsw.view.miniModel.components;

public class ConnectorsView extends ComponentView {

    public ConnectorsView(int ID, int[] connectors, int clockWise) {
        super(ID, connectors, clockWise);
    }

    @Override
    public void drawGui() {
        //TODO: Implement the GUI drawing logic for the Connectors component here
    }

    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2  -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + "  ╬  " + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }

    @Override
    public TilesTypeView getType() {
        return TilesTypeView.CONNECTORS;
    }

    @Override
    public ConnectorsView clone() {
        ConnectorsView copy = new ConnectorsView(this.getID(), this.getConnectors(), this.getClockWise());
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
