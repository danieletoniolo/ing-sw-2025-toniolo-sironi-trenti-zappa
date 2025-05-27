package it.polimi.ingsw.view.miniModel.components;

public class CannonView extends ComponentView {
    private float power;
    private String purple = "\033[35m";
    private String reset = "\033[0m";
    private int arrowRotation;
    private boolean doubleCannon;

    public CannonView(int ID, int[] connectors, float power, int arrowRotation) {
        super(ID, connectors);
        this.power = power;
        this.arrowRotation = arrowRotation;
        if ((arrowRotation == 0 && power == 2) || (arrowRotation != 0 && power == 1)) {
            this.doubleCannon = true;
        } else {
            this.doubleCannon = false;
        }
    }

    public void setArrowRotation(int arrowRotation) {
        this.arrowRotation = arrowRotation;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    @Override
    public void drawGui() {
        //TODO: Implement the GUI drawing logic for the Cannon component here
    }

    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + " " + (doubleCannon ? drawDoubleCannon() : drawCannon()) + " " + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }

    private String drawCannon(){
        return switch (arrowRotation) {
            case 0 -> " " + purple + ArrowUp + reset + " ";
            case 1 -> " " + purple + ArrowRight + reset + " ";
            case 2 -> " " + purple + ArrowDown + reset + " ";
            case 3 -> " " + purple + ArrowLeft + reset + " ";
            default -> throw new IllegalStateException("Unexpected value: " + arrowRotation);
        };
    }

    private String drawDoubleCannon(){
        return switch (arrowRotation) {
            case 0 -> purple + ArrowUp + reset + " " + purple + ArrowUp + reset;
            case 1 -> purple + ArrowRight + reset + " " + purple + ArrowRight + reset;
            case 2 -> purple + ArrowDown + reset + " " + purple + ArrowDown + reset;
            case 3 -> purple + ArrowLeft + reset + " " + purple + ArrowLeft + reset;
            default -> throw new IllegalStateException("Unexpected value: " + arrowRotation);
        };
    }

    @Override
    public TilesTypeView getType() {
        return doubleCannon ? TilesTypeView.DOUBLE_CANNON : TilesTypeView.SINGLE_CANNON;
    }
}
