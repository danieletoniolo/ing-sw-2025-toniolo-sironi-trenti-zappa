package view.miniModel.components;

public class GenericComponentView extends ComponentView {
    public GenericComponentView() {
        super(-1, new int[]{0, 0, 0, 0});
    }

    @Override
    public void drawGui() {
        //TODO: Implement the GUI drawing logic for the Generic component here
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
    public TilesTypeView getType() {
        return TilesTypeView.GENERIC;
    }
}
