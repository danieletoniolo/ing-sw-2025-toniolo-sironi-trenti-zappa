package it.polimi.ingsw.view.miniModel.components;

/**
 * Represents the view for connectors in the mini model.
 * Extends {@link ComponentView} to provide specific rendering and type information for connectors.
 */
public class ConnectorsView extends ComponentView {

    /**
     * Constructs a new ConnectorsView.
     *
     * @param ID         the unique identifier of the component
     * @param connectors the array representing the connectors
     * @param clockWise  the clockwise orientation
     */
    public ConnectorsView(int ID, int[] connectors, int clockWise) {
        super(ID, connectors, clockWise);
    }

    /**
     * Draws a specific line of the connector for the TUI (Text User Interface).
     *
     * @param line the line number to draw
     * @return the string representation of the line
     * @throws IndexOutOfBoundsException if the line number is not valid
     */
    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2  -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + "  ╬  " + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }

    /**
     * Gets the type of this component.
     *
     * @return the component type as {@link ComponentTypeView}
     */
    @Override
    public ComponentTypeView getType() {
        return ComponentTypeView.CONNECTORS;
    }

    /**
     * Creates and returns a copy of this ConnectorsView.
     *
     * @return a clone of this instance
     */
    @Override
    public ConnectorsView clone() {
        ConnectorsView copy = new ConnectorsView(this.getID(), this.getConnectors(), this.getClockWise());
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
