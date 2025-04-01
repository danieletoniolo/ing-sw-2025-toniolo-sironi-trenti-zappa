package Model.State.interfaces;

import org.javatuples.Pair;

import java.util.ArrayList;

public interface RemovableCrew {
    public void setCrewLoss(ArrayList<Pair<Integer, Integer>> cabinsID) throws IllegalStateException;
}
