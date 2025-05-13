package Model.State;

import Model.Game.Board.Level;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.*;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FightHandlerTest {
    FightHandler fh;

    @BeforeEach
    void setUp() {
        fh = new FightHandler();
        assertNotNull(fh);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, Integer.MAX_VALUE})
    void initialize(int startIndex) {
        FightHandler fightHandler = new FightHandler();
        assertDoesNotThrow(() -> fightHandler.initialize(startIndex));
        assertEquals(FightHandlerInternalState.CAN_PROTECT, fh.getInternalState());
        assertNull(fh.getProtect());
        assertNull(fh.getBatteryID());
        assertNull(fh.getFragmentChoice());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, Integer.MAX_VALUE})
    void transitionHit(int initialHitIndex) {
        fh.setHitIndex(initialHitIndex);
        fh.setInternalState(FightHandlerInternalState.PROTECTION);
        fh.transitionHit();
        assertEquals(initialHitIndex + 1, fh.getHitIndex());
        assertEquals(FightHandlerInternalState.CAN_PROTECT, fh.getInternalState());
        assertNull(fh.getProtect());
        assertNull(fh.getBatteryID());
        assertNull(fh.getFragmentChoice());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10})
    void setFragmentChoice_validState(int fragmentChoice) {
        fh.setInternalState(FightHandlerInternalState.DESTROY_FRAGMENT);
        assertDoesNotThrow(() -> fh.setFragmentChoice(fragmentChoice));
        assertEquals(fragmentChoice, fh.getFragmentChoice());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10})
    void setFragmentChoice_invalidState(int fragmentChoice) {
        fh.setInternalState(FightHandlerInternalState.CAN_PROTECT);
        assertThrows(IllegalStateException.class, () -> fh.setFragmentChoice(fragmentChoice));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void setProtect_validState(boolean protect) {
        fh.setInternalState(FightHandlerInternalState.PROTECTION);
        Integer batteryID = protect ? 1 : null;
        assertDoesNotThrow(() -> fh.setProtect(protect, batteryID));
        assertEquals(protect, fh.getProtect());
        assertEquals(batteryID, fh.getBatteryID());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void setProtect_invalidState(boolean protect) {
        fh.setInternalState(FightHandlerInternalState.CAN_PROTECT);
        Integer batteryID = protect ? 1 : null;
        assertThrows(IllegalStateException.class, () -> fh.setProtect(protect, batteryID));
    }

    @Test
    void setProtect_protectTrueWithNullBatteryID() {
        fh.setInternalState(FightHandlerInternalState.PROTECTION);
        assertThrows(IllegalArgumentException.class, () -> fh.setProtect(true, null));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 6, 10})
    void setDice_validState(int dice) {
        fh.setInternalState(FightHandlerInternalState.CAN_PROTECT);
        assertDoesNotThrow(() -> fh.setDice(dice));
        assertEquals(dice, fh.getDice());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 6, 10})
    void setDice_invalidState(int dice) {
        fh.setInternalState(FightHandlerInternalState.PROTECTION);
        assertThrows(IllegalStateException.class, () -> fh.setDice(dice));
    }

    @Test
    void getFragments_afterSettingFragments_returnsFragments() {
        ArrayList<ArrayList<Pair<Integer, Integer>>> fragments = new ArrayList<>();
        fragments.add(new ArrayList<>());
        fh.setFragments(fragments);
        assertEquals(fragments, fh.getFragments());
    }

    @Test
    void executeProtection_protectTrue() {
        ConnectorType[] c = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        SpaceShip spaceShip = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Battery component = new Battery(2, c, 3);
        fh.setInternalState(FightHandlerInternalState.PROTECTION);
        fh.setProtect(true, 2);
        fh.setProtectionResult(Pair.with(component, 0));

        spaceShip.placeComponent(component, 6, 7);
        PlayerData p1 = new PlayerData("123e4567-e89b-12d3-a456-426614174002", PlayerColor.RED, spaceShip);
        fh.executeProtection(p1);

        assertEquals(2, spaceShip.getEnergyNumber());
        assertEquals(FightHandlerInternalState.CAN_PROTECT, fh.getInternalState());
        assertEquals(1, fh.getHitIndex());
    }

    @Test
    void executeProtection_protectFalse() {
        ConnectorType[] c = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        SpaceShip spaceShip = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Battery component = new Battery(2, c, 3);
        fh.setInternalState(FightHandlerInternalState.PROTECTION);
        fh.setProtect(false, null);
        fh.setProtectionResult(Pair.with(component, 0));

        spaceShip.placeComponent(component, 6, 7);
        PlayerData p1 = new PlayerData("123e4567-e89b-12d3-a456-426614174002", PlayerColor.RED, spaceShip);
        fh.executeProtection(p1);

        assertNull(spaceShip.getComponent(6, 7));
        assertEquals(FightHandlerInternalState.CAN_PROTECT, fh.getInternalState());
        assertEquals(1, fh.getHitIndex());
    }

    //TODO: Capire
    /*
    @Test
    void executeProtection_protectFalseMultipleFragments() {
        boolean[][] vs = new boolean[12][12];
        for (boolean[] v : vs) {
            Arrays.fill(v, true);
        }
        ConnectorType[] c = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        SpaceShip spaceShip = new SpaceShip(Level.SECOND, vs);
        Battery component = new Battery(2, c, 3);
        ArrayList<ArrayList<Pair<Integer, Integer>>> fragments = new ArrayList<>();
        fragments.add(new ArrayList<>());
        fragments.add(new ArrayList<>());

        spaceShip.placeComponent(component, 6, 7);
        spaceShip.setDisconnectedComponents(fragments);

        fh.setInternalState(FightHandlerInternalState.PROTECTION);
        fh.setProtect(false, null);
        fh.setProtectionResult(Pair.with(component, 0));

        fh.executeProtection(spaceShip);

        assertNull(spaceShip.getComponent(6, 7));
        assertEquals(FightHandlerInternalState.CAN_PROTECT, fh.getInternalState());
        assertEquals(fragments, fh.getFragments());
    }

     */

    @Test
    void executeProtection_withProtection1() {
        ConnectorType[] c = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        SpaceShip spaceShip = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Battery component = new Battery(2, c, 3);
        fh.setInternalState(FightHandlerInternalState.PROTECTION);
        fh.setProtect(false, null);
        fh.setProtectionResult(Pair.with(component, 1));

        PlayerData p1 = new PlayerData("123e4567-e89b-12d3-a456-426614174002", PlayerColor.RED, spaceShip);
        fh.executeProtection(p1);

        assertEquals(FightHandlerInternalState.CAN_PROTECT, fh.getInternalState());
        assertEquals(1, fh.getHitIndex());
    }
}