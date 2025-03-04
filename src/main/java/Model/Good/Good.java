package Model.Good;

public class Good {
    private GoodType color;

    Good(GoodType color) {
        this.color = color;
    }

    public GoodType getColor() {
        return color;
    }

    public int getValue() {
        return color.getValue();
    }
}