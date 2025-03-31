package Model.State.interfaces;

import java.util.Map;

public interface RemovableCrew {
    public void setCrewLoss(Map<Integer, Integer> cabinsID) throws IllegalStateException;
}
