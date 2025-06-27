package it.polimi.ingsw.view.miniModel.components;

public class GenericComponentView extends ComponentView {
    public GenericComponentView(int row, int col) {
        super(-1, new int[]{0, 0, 0, 0}, 0);
        this.setRow(row);
        this.setCol(col);
    }

    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + "     " + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }

    @Override
    public ComponentTypeView getType() {
        return ComponentTypeView.GENERIC;
    }

    @Override
    public GenericComponentView clone() {
        GenericComponentView copy = new GenericComponentView(this.getRow() - 1, this.getCol() - 1);
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
