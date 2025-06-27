package it.polimi.ingsw.view.miniModel.components;

/**
 * Represents a generic component view in the mini model.
 * Extends {@link ComponentView} and provides specific drawing and type logic.
 */
public class GenericComponentView extends ComponentView {
    /**
     * Constructs a GenericComponentView at the specified row and column.
     *
     * @param row the row index of the component
     * @param col the column index of the component
     */
    public GenericComponentView(int row, int col) {
        super(-1, new int[]{0, 0, 0, 0}, 0);
        this.setRow(row);
        this.setCol(col);
    }

    /**
     * Draws a specific line of the component for the TUI (Text User Interface).
     *
     * @param line the line number to draw
     * @return the string representation of the line
     * @throws IndexOutOfBoundsException if the line number is not valid
     */
    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + "     " + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }

    /**
     * Gets the type of this component.
     *
     * @return the component type, always {@link ComponentTypeView#GENERIC}
     */
    @Override
    public ComponentTypeView getType() {
        return ComponentTypeView.GENERIC;
    }

    /**
     * Creates and returns a copy of this component.
     *
     * @return a clone of this instance
     */
    @Override
    public GenericComponentView clone() {
        GenericComponentView copy = new GenericComponentView(this.getRow() - 1, this.getCol() - 1);
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
