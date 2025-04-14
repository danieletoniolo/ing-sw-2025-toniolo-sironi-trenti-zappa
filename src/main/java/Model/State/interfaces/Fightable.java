package Model.State.interfaces;

public interface Fightable {
    public void setDice(int dice) throws IllegalStateException;
    public void setProtect(boolean protect_, Integer batteryID_) throws IllegalStateException, IllegalArgumentException;
}
