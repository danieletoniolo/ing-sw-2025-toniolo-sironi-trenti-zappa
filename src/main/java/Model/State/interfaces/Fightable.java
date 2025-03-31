package Model.State.interfaces;

import java.util.Map;

public interface Fightable {
    public void setFragmentChoice(int fragmentChoice) throws IllegalStateException;
    public void setProtect(boolean protect_, Integer batteryID_) throws IllegalStateException, IllegalArgumentException;
    public void setDice(int dice) throws IllegalStateException;
}
