package Model.SpaceShip;

import Model.Cards.Hits.Direction;
import Model.Cards.Hits.Hit;
import Model.Cards.Hits.HitType;
import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.GenerateRandomShip;
import Model.Good.Good;
import Model.Good.GoodType;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SpaceShipTest {
    SpaceShip ship;
    boolean[][] spots;
    ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};

    @BeforeEach
    void setUp() {
        spots = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                spots[i][j] = true;
            }
        }
        ship = new SpaceShip(Level.SECOND, spots);
        assertNotNull(ship, "Ship not initialized correctly");
    }

    @RepeatedTest(5)
    void getNumberOfComponents_initialState() {
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        assertEquals(1, ship.getNumberOfComponents());
    }

    @RepeatedTest(5)
    void getNumberOfComponents_afterAddingComponents() {
        boolean[][] vs = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                vs[i][j] = true;
            }
        }
        SpaceShip ship = new SpaceShip(Level.SECOND, vs);
        ship.placeComponent(new Storage(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}, true, 2), 8, 7);
        ship.placeComponent(new Battery(3, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}, 3), 8, 8);
        assertEquals(3, ship.getNumberOfComponents());
    }

    @RepeatedTest(5)
    void getNumberOfComponents_afterRemovingComponents() {
        boolean[][] vs = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                vs[i][j] = true;
            }
        }
        SpaceShip ship = new SpaceShip(Level.SECOND, vs);
        ship.placeComponent(new Storage(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}, true, 2), 6, 7);
        ship.placeComponent(new Battery(3, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}, 3), 6, 8);
        ship.destroyComponent(6, 7);
        assertEquals(2, ship.getNumberOfComponents());
    }

    @RepeatedTest(5)
    void getNumberOfComponents_afterAddingAndRemovingComponents() {
        boolean[][] vs = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                vs[i][j] = true;
            }
        }
        SpaceShip ship = new SpaceShip(Level.SECOND, vs);
        ship.placeComponent(new Storage(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}, true, 2), 6, 7);
        ship.placeComponent(new Battery(3, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}, 3), 6, 8);
        ship.destroyComponent(6, 7);
        ship.placeComponent(new Cannon(4, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}, 1), 6, 9);
        assertEquals(3, ship.getNumberOfComponents());
    }

    @RepeatedTest(5)
    void singleEnginesStrength_initialState() {
        assertEquals(0, ship.getSingleEnginesStrength());
    }

    @RepeatedTest(5)
    void singleEnginesStrength_afterAddingSingleEngine() {
        Engine engine = new Engine(1, connectors, 1);
        ship.placeComponent(engine, 6, 7);
        assertEquals(1, ship.getSingleEnginesStrength());
    }

    @RepeatedTest(5)
    void singleEnginesStrength_afterAddingMultipleSingleEngines() {
        Engine engine1 = new Engine(1, connectors, 1);
        Engine engine2 = new Engine(2, connectors, 1);
        ship.placeComponent(engine1, 6, 7);
        ship.placeComponent(engine2, 6, 8);
        assertEquals(2, ship.getSingleEnginesStrength());
    }

    @RepeatedTest(5)
    void singleEnginesStrength_afterRemovingSingleEngine() {
        Engine engine = new Engine(1, connectors, 1);
        ship.placeComponent(engine, 6, 7);
        ship.destroyComponent(6, 7);
        assertEquals(0, ship.getSingleEnginesStrength());
    }

    @RepeatedTest(5)
    void singleEnginesStrength_afterAddingAndRemovingSingleEngines() {
        Engine engine1 = new Engine(1, connectors, 1);
        Engine engine2 = new Engine(2, connectors, 1);
        ship.placeComponent(engine1, 6, 7);
        ship.placeComponent(engine2, 6, 8);
        ship.destroyComponent(6, 7);
        assertEquals(1, ship.getSingleEnginesStrength());
    }

    @RepeatedTest(5)
    void doubleEnginesStrength_initialState() {
        assertEquals(0, ship.getDoubleEnginesStrength());
    }

    @RepeatedTest(5)
    void doubleEnginesStrength_afterAddingDoubleEngine() {
        Engine engine = new Engine(2, connectors, 2);
        ship.placeComponent(engine, 6, 7);
        assertEquals(2, ship.getDoubleEnginesStrength());
    }

    @RepeatedTest(5)
    void doubleEnginesStrength_afterAddingMultipleDoubleEngines() {
        Engine engine1 = new Engine(2, connectors, 2);
        Engine engine2 = new Engine(3, connectors, 2);
        ship.placeComponent(engine1, 6, 7);
        ship.placeComponent(engine2, 6, 8);
        assertEquals(4, ship.getDoubleEnginesStrength());
    }

    @RepeatedTest(5)
    void doubleEnginesStrength_afterRemovingDoubleEngine() {
        Engine engine = new Engine(2, connectors, 2);
        ship.placeComponent(engine, 6, 7);
        ship.destroyComponent(6, 7);
        assertEquals(0, ship.getDoubleEnginesStrength());
    }

    @RepeatedTest(5)
    void doubleEnginesStrength_afterAddingAndRemovingDoubleEngines() {
        Engine engine1 = new Engine(2, connectors, 2);
        Engine engine2 = new Engine(3, connectors, 2);
        ship.placeComponent(engine1, 6, 7);
        ship.placeComponent(engine2, 6, 8);
        ship.destroyComponent(6, 7);
        assertEquals(2, ship.getDoubleEnginesStrength());
    }

    @RepeatedTest(5)
    void getRows() {
        assertEquals(12, SpaceShip.getRows());
    }

    @RepeatedTest(5)
    void getCols() {
        assertEquals(12, SpaceShip.getCols());
    }

    @RepeatedTest(5)
    void singleCannonsStrength_initialState() {
        assertEquals(0, ship.getSingleCannonsStrength());
    }

    @RepeatedTest(5)
    void singleCannonsStrength_afterAddingSingleCannon() {
        Cannon cannon = new Cannon(1, connectors, 1);
        ship.placeComponent(cannon, 6, 7);
        assertEquals(1, ship.getSingleCannonsStrength());
    }

    @RepeatedTest(5)
    void singleCannonsStrength_afterAddingMultipleSingleCannons() {
        Cannon cannon1 = new Cannon(1, connectors, 1);
        Cannon cannon2 = new Cannon(2, connectors, 1);
        ship.placeComponent(cannon1, 6, 7);
        ship.placeComponent(cannon2, 6, 8);
        assertEquals(2, ship.getSingleCannonsStrength());
    }

    @RepeatedTest(5)
    void singleCannonsStrength_afterRemovingSingleCannon() {
        Cannon cannon = new Cannon(1, connectors, 1);
        ship.placeComponent(cannon, 6, 7);
        ship.destroyComponent(6, 7);
        assertEquals(0, ship.getSingleCannonsStrength());
    }

    @RepeatedTest(5)
    void singleCannonsStrength_afterAddingAndRemovingSingleCannons() {
        Cannon cannon1 = new Cannon(1, connectors, 1);
        Cannon cannon2 = new Cannon(2, connectors, 1);
        ship.placeComponent(cannon1, 6, 7);
        ship.placeComponent(cannon2, 6, 8);
        ship.destroyComponent(6, 7);
        assertEquals(1, ship.getSingleCannonsStrength());
    }

    @RepeatedTest(5)
    void doubleCannonsStrength_initialState() {
        assertEquals(0, ship.getDoubleCannonsStrength());
    }

    @RepeatedTest(5)
    void doubleCannonsStrength_afterAddingDoubleCannon() {
        Cannon cannon = new Cannon(1, connectors, 2);
        ship.placeComponent(cannon, 6, 7);
        assertEquals(2, ship.getDoubleCannonsStrength());
    }

    @RepeatedTest(5)
    void doubleCannonsStrength_afterAddingMultipleDoubleCannons() {
        Cannon cannon1 = new Cannon(1, connectors, 2);
        Cannon cannon2 = new Cannon(2, connectors, 2);
        ship.placeComponent(cannon1, 6, 7);
        ship.placeComponent(cannon2, 6, 8);
        assertEquals(4, ship.getDoubleCannonsStrength());
    }

    @RepeatedTest(5)
    void doubleCannonsStrength_afterRemovingDoubleCannon() {
        Cannon cannon = new Cannon(1, connectors, 2);
        ship.placeComponent(cannon, 6, 7);
        ship.destroyComponent(6, 7);
        assertEquals(0, ship.getDoubleCannonsStrength());
    }

    @RepeatedTest(5)
    void doubleCannonsStrength_afterAddingAndRemovingDoubleCannons() {
        Cannon cannon1 = new Cannon(1, connectors, 2);
        Cannon cannon2 = new Cannon(2, connectors, 2);
        ship.placeComponent(cannon1, 6, 7);
        ship.placeComponent(cannon2, 6, 8);
        ship.destroyComponent(6, 7);
        assertEquals(2, ship.getDoubleCannonsStrength());
    }

    @RepeatedTest(5)
    void doubleCannonsNumber_initialState() {
        assertEquals(0, ship.getDoubleCannonsNumber());
    }

    @RepeatedTest(5)
    void doubleCannonsNumber_afterAddingDoubleCannon() {
        Cannon cannon = new Cannon(1, connectors, 2);
        ship.placeComponent(cannon, 6, 7);
        assertEquals(1, ship.getDoubleCannonsNumber());
    }

    @RepeatedTest(5)
    void doubleCannonsNumber_afterAddingMultipleDoubleCannons() {
        Cannon cannon1 = new Cannon(1, connectors, 2);
        Cannon cannon2 = new Cannon(2, connectors, 2);
        ship.placeComponent(cannon1, 6, 7);
        ship.placeComponent(cannon2, 6, 8);
        assertEquals(2, ship.getDoubleCannonsNumber());
    }

    @RepeatedTest(5)
    void doubleCannonsNumber_afterRemovingDoubleCannon() {
        Cannon cannon = new Cannon(1, connectors, 2);
        ship.placeComponent(cannon, 6, 7);
        ship.destroyComponent(6, 7);
        assertEquals(0, ship.getDoubleCannonsNumber());
    }

    @RepeatedTest(5)
    void doubleCannonsNumber_afterAddingAndRemovingDoubleCannons() {
        Cannon cannon1 = new Cannon(1, connectors, 2);
        Cannon cannon2 = new Cannon(2, connectors, 2);
        ship.placeComponent(cannon1, 6, 7);
        ship.placeComponent(cannon2, 6, 8);
        ship.destroyComponent(6, 7);
        assertEquals(1, ship.getDoubleCannonsNumber());
    }

    @RepeatedTest(5)
    void energyNumber_initialState() {
        assertEquals(0, ship.getEnergyNumber());
    }

    @RepeatedTest(5)
    void energyNumber_afterAddingEnergy() {
        Battery battery = new Battery(1, connectors, 3);
        ship.placeComponent(battery, 6, 7);
        assertEquals(3, ship.getEnergyNumber());
    }

    @RepeatedTest(5)
    void energyNumber_afterAddingMultipleEnergyComponents() {
        Battery battery1 = new Battery(1, connectors, 3);
        Battery battery2 = new Battery(2, connectors, 2);
        ship.placeComponent(battery1, 6, 7);
        ship.placeComponent(battery2, 6, 8);
        assertEquals(5, ship.getEnergyNumber());
    }

    @RepeatedTest(5)
    void energyNumber_afterRemovingEnergyComponent() {
        Battery battery = new Battery(1, connectors, 3);
        ship.placeComponent(battery, 6, 7);
        ship.destroyComponent(6, 7);
        assertEquals(0, ship.getEnergyNumber());
    }

    @RepeatedTest(5)
    void energyNumber_afterAddingAndRemovingEnergyComponents() {
        Battery battery1 = new Battery(1, connectors, 3);
        Battery battery2 = new Battery(2, connectors, 2);
        ship.placeComponent(battery1, 6, 7);
        ship.placeComponent(battery2, 6, 8);
        ship.destroyComponent(6, 7);
        assertEquals(2, ship.getEnergyNumber());
    }

    @RepeatedTest(5)
    void goodsValue_initialState() {
        assertEquals(0, ship.getGoodsValue());
    }

    @RepeatedTest(5)
    void goodsValue_afterAddingGoods() {
        Good good = new Good(GoodType.RED);
        Storage s1 = new Storage(1, connectors, true, 2);
        ship.placeComponent(s1, 6, 7);
        ArrayList<Good> goods = new ArrayList<>();
        goods.add(good);
        ship.exchangeGood(goods, null, s1.getID());
        assertEquals(4, ship.getGoodsValue());
    }

    @RepeatedTest(5)
    void goodsValue_afterAddingMultipleGoods() {
        Good good1 = new Good(GoodType.RED);
        Good good2 = new Good(GoodType.BLUE);
        Storage s1 = new Storage(1, connectors, true, 2);
        ship.placeComponent(s1, 6, 7);
        ArrayList<Good> goods = new ArrayList<>();
        goods.add(good1);
        Storage s2 = new Storage(2, connectors, true, 2);
        ship.placeComponent(s2, 6, 8);
        ArrayList<Good> goods2 = new ArrayList<>();
        goods2.add(good2);
        ship.exchangeGood(goods, null, s1.getID());
        ship.exchangeGood(goods2, null, s2.getID());

        assertEquals(5, ship.getGoodsValue());
    }

    @RepeatedTest(5)
    void goodsValue_afterRemovingGoods() {
        Good good = new Good(GoodType.YELLOW);
        Storage s1 = new Storage(1, connectors, true, 2);
        ship.placeComponent(s1, 6, 7);
        ArrayList<Good> goods = new ArrayList<>();
        goods.add(good);
        ship.exchangeGood(goods, null, s1.getID());
        ship.exchangeGood(null, goods, s1.getID());
        assertEquals(0, ship.getGoodsValue());
    }

    @RepeatedTest(5)
    void goodsValue_afterAddingAndRemovingGoods() {
        Good good1 = new Good(GoodType.YELLOW);
        Good good2 = new Good(GoodType.GREEN);
        Storage s1 = new Storage(1, connectors, true, 2);
        ship.placeComponent(s1, 6, 7);
        ArrayList<Good> goods = new ArrayList<>();
        goods.add(good1);
        ship.exchangeGood(goods, null, s1.getID());
        Storage s2 = new Storage(2, connectors, true, 2);
        ship.placeComponent(s2, 6, 8);
        ArrayList<Good> goods2 = new ArrayList<>();
        goods2.add(good2);
        ship.exchangeGood(goods2, null, s2.getID());

        ship.exchangeGood(null, goods2, s2.getID());
        assertEquals(3, ship.getGoodsValue());
    }

    @RepeatedTest(5)
    void crewNumber_initialState() {
        assertEquals(0, ship.getCrewNumber());
    }

    @RepeatedTest(5)
    void crewNumber_afterAddingCrewMember() {
        Cabin crewMember = new Cabin(1, connectors);
        ship.placeComponent(crewMember, 6, 7);
        ship.addCrewMember(crewMember.getID(), false, false);
        assertEquals(2, ship.getCrewNumber());
    }

    @RepeatedTest(5)
    void crewNumber_afterAddingMultipleCrewMembers() {
        Cabin crewMember1 = new Cabin(1, connectors);
        Cabin crewMember2 = new Cabin(2, connectors);
        ship.placeComponent(crewMember1, 6, 7);
        ship.placeComponent(crewMember2, 6, 8);
        ship.addCrewMember(crewMember1.getID(), false, false);
        ship.addCrewMember(crewMember2.getID(), false, false);

        assertEquals(4, ship.getCrewNumber());
    }

    @RepeatedTest(5)
    void crewNumber_afterRemovingCrewMember() {
        Cabin crewMember = new Cabin(1, connectors);
        ship.placeComponent(crewMember, 6, 7);
        ship.addCrewMember(crewMember.getID(), false, false);
        ship.destroyComponent(6, 7);
        assertEquals(0, ship.getCrewNumber());
    }

    @RepeatedTest(5)
    void crewNumber_afterAddingAndRemovingCrewMembers() {
        Cabin crewMember1 = new Cabin(1, connectors);
        Cabin crewMember2 = new Cabin(2, connectors);
        ship.placeComponent(crewMember1, 6, 7);
        ship.placeComponent(crewMember2, 6, 8);
        ship.addCrewMember(crewMember1.getID(), false, false);
        ship.addCrewMember(crewMember2.getID(), false, false);
        ship.destroyComponent(6, 7);

        assertEquals(2, ship.getCrewNumber());
    }

    @RepeatedTest(5)
    void purpleAlien_initialState() {
        assertFalse(ship.hasPurpleAlien());
    }

    @RepeatedTest(5)
    void purpleAlien_afterAddingPurpleAlien() {
        Cabin cabin1 = new Cabin(1, connectors);
        ship.placeComponent(cabin1, 6, 7);
        LifeSupportPurple lsb = new LifeSupportPurple(2, connectors);
        ship.placeComponent(lsb, 5, 7);
        ship.getCabin(1).isValid();
        ship.addCrewMember(cabin1.getID(), false, true);

        assertTrue(ship.hasPurpleAlien());
    }

    @RepeatedTest(5)
    void purpleAlien_afterRemovingPurpleAlien() {
        Cabin cabin1 = new Cabin(1, connectors);
        ship.placeComponent(cabin1, 6, 7);
        LifeSupportPurple lsb = new LifeSupportPurple(2, connectors);
        ship.placeComponent(lsb, 5, 7);
        ship.getCabin(1).isValid();
        ship.addCrewMember(cabin1.getID(), false, true);

        ship.removeCrewMember(cabin1.getID(), 1);
        assertFalse(ship.hasPurpleAlien());
    }

    @RepeatedTest(5)
    void purpleAlien_afterAddingAndRemovingPurpleAlien() {
        Cabin cabin1 = new Cabin(1, connectors);
        ship.placeComponent(cabin1, 6, 7);
        LifeSupportPurple lsb = new LifeSupportPurple(2, connectors);
        ship.placeComponent(lsb, 5, 7);
        ship.getCabin(1).isValid();
        ship.addCrewMember(cabin1.getID(), false, true);

        ship.removeCrewMember(cabin1.getID(), 1);
        assertFalse(ship.hasPurpleAlien());
        ship.addCrewMember(cabin1.getID(), false, true);
        assertTrue(ship.hasPurpleAlien());
    }

    @RepeatedTest(5)
    void brownAlien_initialState() {
        assertFalse(ship.hasBrownAlien());
    }

    @RepeatedTest(5)
    void brownAlien_afterAddingBrownAlien() {
        Cabin cabin1 = new Cabin(1, connectors);
        ship.placeComponent(cabin1, 6, 7);
        LifeSupportBrown lsb = new LifeSupportBrown(2, connectors);
        ship.placeComponent(lsb, 5, 7);
        ship.getCabin(1).isValid();
        ship.addCrewMember(cabin1.getID(), true, false);

        assertTrue(ship.hasBrownAlien());
    }

    @RepeatedTest(5)
    void brownAlien_afterRemovingBrownAlien() {
        Cabin cabin1 = new Cabin(1, connectors);
        ship.placeComponent(cabin1, 6, 7);
        LifeSupportBrown lsb = new LifeSupportBrown(2, connectors);
        ship.placeComponent(lsb, 5, 7);
        ship.getCabin(1).isValid();
        ship.addCrewMember(cabin1.getID(), true, false);

        ship.removeCrewMember(cabin1.getID(), 1);
        assertFalse(ship.hasBrownAlien());
    }

    @RepeatedTest(5)
    void brownAlien_afterAddingAndRemovingBrownAlien() {
        Cabin cabin1 = new Cabin(1, connectors);
        ship.placeComponent(cabin1, 6, 7);
        LifeSupportBrown lsb = new LifeSupportBrown(2, connectors);
        ship.placeComponent(lsb, 5, 7);
        ship.getCabin(1).isValid();
        ship.addCrewMember(cabin1.getID(), true, false);

        ship.removeCrewMember(cabin1.getID(), 1);
        assertFalse(ship.hasBrownAlien());
        ship.addCrewMember(cabin1.getID(), true, false);
        assertTrue(ship.hasBrownAlien());
    }

    @RepeatedTest(5)
    void reservedComponents_initialState() {
        assertTrue(ship.getReservedComponents().isEmpty());
    }

    @RepeatedTest(5)
    void reservedComponents_afterReservingComponent() {
        Component component = new Storage(1, connectors, true, 2);
        ship.reserveComponent(component);
        assertEquals(1, ship.getReservedComponents().size());
        assertEquals(component, ship.getReservedComponents().getFirst());
    }

    @RepeatedTest(5)
    void reservedComponents_afterReservingMultipleComponents() {
        Component component1 = new Storage(1, connectors, true, 2);
        Component component2 = new Battery(2, connectors, 3);
        ship.reserveComponent(component1);
        ship.reserveComponent(component2);
        assertEquals(2, ship.getReservedComponents().size());
        assertEquals(component1, ship.getReservedComponents().get(0));
        assertEquals(component1.getComponentType(), ship.getReservedComponents().get(0).getComponentType());
        assertEquals(component2, ship.getReservedComponents().get(1));
        assertEquals(component2.getComponentType(), ship.getReservedComponents().get(1).getComponentType());
    }

    @RepeatedTest(5)
    void reservedComponents_afterUnreservingComponent() {
        Component component = new Storage(1, connectors, true, 2);
        ship.reserveComponent(component);
        ship.unreserveComponent(component);
        assertTrue(ship.getReservedComponents().isEmpty());
    }

    @RepeatedTest(5)
    void reservedComponents_afterUnreservingOneOfMultipleComponents() {
        Component component1 = new Storage(1, connectors, true, 2);
        Component component2 = new Battery(2, connectors, 3);
        ship.reserveComponent(component1);
        ship.reserveComponent(component2);
        ship.unreserveComponent(component1);
        assertEquals(1, ship.getReservedComponents().size());
        assertEquals(component2, ship.getReservedComponents().getFirst());
        assertEquals(component2.getComponentType(), ship.getReservedComponents().getFirst().getComponentType());
    }

    @RepeatedTest(5)
    void lostComponents_initialState() {
        assertTrue(ship.getLostComponents().isEmpty());
    }

    @RepeatedTest(5)
    void lostComponents_afterDestroyingComponent() {
        Component component = new Storage(1, connectors, true, 2);
        ship.placeComponent(component, 6, 7);
        ship.destroyComponent(6, 7);
        assertEquals(1, ship.getLostComponents().size());
        assertEquals(component, ship.getLostComponents().getFirst());
        assertEquals(component.getComponentType(), ship.getLostComponents().getFirst().getComponentType());
    }

    @RepeatedTest(5)
    void lostComponents_afterDestroyingMultipleComponents() {
        Component component1 = new Storage(1, connectors, true, 2);
        Component component2 = new Battery(2, connectors, 3);
        ship.placeComponent(component1, 6, 7);
        ship.placeComponent(component2, 6, 8);
        ship.destroyComponent(6, 7);
        ship.destroyComponent(6, 8);
        assertEquals(2, ship.getLostComponents().size());
        assertEquals(component1, ship.getLostComponents().get(0));
        assertEquals(component1.getComponentType(), ship.getLostComponents().get(0).getComponentType());
        assertEquals(component2, ship.getLostComponents().get(1));
        assertEquals(component2.getComponentType(), ship.getLostComponents().get(1).getComponentType());
    }

    @RepeatedTest(5)
    void lostComponents_afterDestroyingAndRestoringComponent() {
        Component component = new Storage(1, connectors, true, 2);
        ship.placeComponent(component, 6, 7);
        ship.destroyComponent(6, 7);
        ship.placeComponent(component, 6, 7);
        assertFalse(ship.getLostComponents().isEmpty());
    }

    @RepeatedTest(5)
    void goods_initialState() {
        assertTrue(ship.getGoods().isEmpty());
    }

    @RepeatedTest(5)
    void goods_afterAddingSingleGood() {
        Good good = new Good(GoodType.RED);
        ship.getGoods().add(good);
        assertEquals(1, ship.getGoods().size());
        assertEquals(good, ship.getGoods().peek());
    }

    @RepeatedTest(5)
    void goods_afterAddingMultipleGoods() {
        Good good1 = new Good(GoodType.RED);
        Good good2 = new Good(GoodType.BLUE);
        ship.getGoods().add(good1);
        ship.getGoods().add(good2);
        assertEquals(2, ship.getGoods().size());
        assertEquals(good1, ship.getGoods().peek());
    }

    @RepeatedTest(5)
    void goods_afterRemovingGood() {
        Good good = new Good(GoodType.RED);
        ship.getGoods().add(good);
        ship.getGoods().poll();
        assertTrue(ship.getGoods().isEmpty());
    }

    @RepeatedTest(5)
    void goods_afterAddingAndRemovingGoods() {
        Good good1 = new Good(GoodType.RED);
        Good good2 = new Good(GoodType.BLUE);
        ship.getGoods().add(good1);
        ship.getGoods().add(good2);
        ship.getGoods().poll();
        assertEquals(1, ship.getGoods().size());
        assertEquals(good2, ship.getGoods().peek());
    }

    @RepeatedTest(5)
    void exchangeGood_addSingleGood() {
        Good good = new Good(GoodType.RED);
        Storage storage = new Storage(1, connectors, true, 2);
        ship.placeComponent(storage, 6, 7);
        ArrayList<Good> goodsToAdd = new ArrayList<>();
        goodsToAdd.add(good);
        ship.exchangeGood(goodsToAdd, null, storage.getID());
        assertEquals(4, ship.getGoodsValue());
    }

    @RepeatedTest(5)
    void exchangeGood_addMultipleGoods() {
        Good good1 = new Good(GoodType.RED);
        Good good2 = new Good(GoodType.BLUE);
        Storage storage = new Storage(1, connectors, true, 2);
        ship.placeComponent(storage, 6, 7);
        ArrayList<Good> goodsToAdd = new ArrayList<>();
        goodsToAdd.add(good1);
        goodsToAdd.add(good2);
        ship.exchangeGood(goodsToAdd, null, storage.getID());
        assertEquals(5, ship.getGoodsValue());
    }

    @RepeatedTest(5)
    void exchangeGood_removeSingleGood() {
        Good good = new Good(GoodType.RED);
        Storage storage = new Storage(1, connectors, true, 2);
        ship.placeComponent(storage, 6, 7);
        ArrayList<Good> goodsToAdd = new ArrayList<>();
        goodsToAdd.add(good);
        ship.exchangeGood(goodsToAdd, null, storage.getID());
        ArrayList<Good> goodsToRemove = new ArrayList<>();
        goodsToRemove.add(good);
        ship.exchangeGood(null, goodsToRemove, storage.getID());
        assertEquals(0, ship.getGoodsValue());
    }

    @RepeatedTest(5)
    void exchangeGood_removeMultipleGoods() {
        Good good1 = new Good(GoodType.RED);
        Good good2 = new Good(GoodType.BLUE);
        Storage storage = new Storage(1, connectors, true, 2);
        ship.placeComponent(storage, 6, 7);
        ArrayList<Good> goodsToAdd = new ArrayList<>();
        goodsToAdd.add(good1);
        goodsToAdd.add(good2);
        ship.exchangeGood(goodsToAdd, null, storage.getID());
        ArrayList<Good> goodsToRemove = new ArrayList<>();
        goodsToRemove.add(good1);
        goodsToRemove.add(good2);
        ship.exchangeGood(null, goodsToRemove, storage.getID());
        assertEquals(0, ship.getGoodsValue());
    }

    @RepeatedTest(5)
    void exchangeGood_addAndRemoveGoods() {
        Good good1 = new Good(GoodType.RED);
        Good good2 = new Good(GoodType.BLUE);
        Storage storage = new Storage(1, connectors, true, 2);
        ship.placeComponent(storage, 6, 7);
        ArrayList<Good> goodsToAdd = new ArrayList<>();
        goodsToAdd.add(good1);
        goodsToAdd.add(good2);
        ship.exchangeGood(goodsToAdd, null, storage.getID());
        ArrayList<Good> goodsToRemove = new ArrayList<>();
        goodsToRemove.add(good1);
        ship.exchangeGood(null, goodsToRemove, storage.getID());
        assertEquals(1, ship.getGoodsValue());
    }

    @RepeatedTest(5)
    void exchangeGood_invalidStorageID() {
        Good good = new Good(GoodType.RED);
        ArrayList<Good> goodsToAdd = new ArrayList<>();
        goodsToAdd.add(good);
        assertThrows(IllegalArgumentException.class, () -> ship.exchangeGood(goodsToAdd, null, 999));
    }

    @RepeatedTest(5)
    void exchangeGood_removeNonExistentGood() {
        Good good = new Good(GoodType.RED);
        Storage storage = new Storage(1, connectors, true, 2);
        ship.placeComponent(storage, 6, 7);
        ArrayList<Good> goodsToRemove = new ArrayList<>();
        goodsToRemove.add(good);
        assertThrows(IllegalStateException.class, () -> ship.exchangeGood(null, goodsToRemove, storage.getID()));
    }

    @RepeatedTest(5)
    void exchangeGood_addBeyondCapacity() {
        Good good1 = new Good(GoodType.RED);
        Good good2 = new Good(GoodType.BLUE);
        Good good3 = new Good(GoodType.YELLOW);
        Storage storage = new Storage(1, connectors, true, 2);
        ship.placeComponent(storage, 6, 7);
        ArrayList<Good> goodsToAdd = new ArrayList<>();
        goodsToAdd.add(good1);
        goodsToAdd.add(good2);
        goodsToAdd.add(good3);
        assertThrows(IllegalStateException.class, () -> ship.exchangeGood(goodsToAdd, null, storage.getID()));
    }

    @RepeatedTest(5)
    void addCrewMember_addSingleCrewMember() {
        Cabin cabin = new Cabin(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.addCrewMember(cabin.getID(), false, false);
        assertEquals(2, ship.getCrewNumber());
    }

    @RepeatedTest(5)
    void addCrewMember_addMultipleCrewMembers() {
        Cabin cabin1 = new Cabin(1, connectors);
        Cabin cabin2 = new Cabin(2, connectors);
        ship.placeComponent(cabin1, 6, 7);
        ship.placeComponent(cabin2, 6, 8);
        ship.addCrewMember(cabin1.getID(), false, false);
        ship.addCrewMember(cabin2.getID(), false, false);
        assertEquals(4, ship.getCrewNumber());
    }

    @RepeatedTest(5)
    void addCrewMember_addBrownAlien() {
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportBrown lsb = new LifeSupportBrown(2, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lsb, 6, 8);
        cabin.isValid();
        ship.addCrewMember(cabin.getID(), true, false);
        assertTrue(ship.hasBrownAlien());
        assertEquals(1, ship.getCrewNumber());
    }

    @RepeatedTest(5)
    void addCrewMember_addPurpleAlien() {
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportPurple lsb = new LifeSupportPurple(2, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lsb, 6, 8);
        cabin.isValid();
        ship.addCrewMember(cabin.getID(), false, true);
        assertTrue(ship.hasPurpleAlien());
        assertEquals(1, ship.getCrewNumber());
    }

    @RepeatedTest(5)
    void addCrewMember_addBothAliensThrowsException() {
        Cabin cabin = new Cabin(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        assertThrows(IllegalArgumentException.class, () -> ship.addCrewMember(cabin.getID(), true, true));
    }

    @RepeatedTest(5)
    void addCrewMember_addCrewMemberToNonExistentCabinThrowsException() {
        assertThrows(NullPointerException.class, () -> ship.addCrewMember(999, false, false));
    }

    @RepeatedTest(5)
    void removeCrewMember_removeSingleCrewMember() {
        Cabin cabin = new Cabin(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.addCrewMember(cabin.getID(), false, false);
        ship.removeCrewMember(cabin.getID(), 1);
        assertEquals(1, ship.getCrewNumber());
    }

    @RepeatedTest(5)
    void removeCrewMember_removeMultipleCrewMembers() {
        Cabin cabin = new Cabin(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.addCrewMember(cabin.getID(), false, false);
        ship.removeCrewMember(cabin.getID(), 2);
        assertEquals(0, ship.getCrewNumber());
    }

    @RepeatedTest(5)
    void removeCrewMember_removeBrownAlien() {
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportBrown lsb = new LifeSupportBrown(2, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lsb, 6, 8);
        cabin.isValid();
        ship.addCrewMember(cabin.getID(), true, false);
        ship.removeCrewMember(cabin.getID(), 1);
        assertFalse(ship.hasBrownAlien());
    }

    @RepeatedTest(5)
    void removeCrewMember_removePurpleAlien() {
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportPurple lsb = new LifeSupportPurple(2, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lsb, 6, 8);
        cabin.isValid();
        ship.addCrewMember(cabin.getID(), false, true);
        ship.removeCrewMember(cabin.getID(), 1);
        assertFalse(ship.hasPurpleAlien());
    }

    @RepeatedTest(5)
    void removeCrewMember_removeMoreThanExistingThrowsException() {
        Cabin cabin = new Cabin(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.addCrewMember(cabin.getID(), false, false);
        assertThrows(IllegalStateException.class, () -> ship.removeCrewMember(cabin.getID(), 3));
    }

    @RepeatedTest(5)
    void removeCrewMember_removeFromNonExistentCabinThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ship.removeCrewMember(999, 1));
    }

    @RepeatedTest(5)
    void canProtect_validShield() {
        Shield shield = new Shield(1, connectors);
        ship.placeComponent(shield, 6, 7);

        for(int i = 0; i < 4; i++){
            Direction[] values = Direction.values();
            Direction direction = values[i];
            Hit hit = new Hit(HitType.SMALLMETEOR, direction);
            Pair<Component, Integer> result;
            if(direction.getValue() % 2 == 0){
                result = ship.canProtect(shield.column, hit);
            } else {
                result = ship.canProtect(shield.row, hit);
            }
            assertNotNull(result);
            assertEquals(0, result.getValue1());

            Hit hit1 = new Hit(HitType.LIGHTFIRE, direction);
            Pair<Component, Integer> result1;
            if(direction.getValue() % 2 == 0){
                result1 = ship.canProtect(shield.column, hit1);
            } else {
                result1 = ship.canProtect(shield.row, hit1);
            }
            assertNotNull(result1);
            assertEquals(0, result1.getValue1());

            shield.rotateClockwise();
        }
    }

    @RepeatedTest(5)
    void canProtect_noShield() {
        Hit hit = new Hit(HitType.SMALLMETEOR, Direction.NORTH);
        Pair<Component, Integer> result = ship.canProtect(7, hit);
        assertNotNull(result.getValue0());
        assertEquals(-1, result.getValue1());

        Cabin c1 = new Cabin(2, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE});
        ship.placeComponent(c1, 6, 7);
        Pair<Component, Integer> result1 = ship.canProtect(7, hit);
        assertNull(result1.getValue0());
        assertEquals(1, result1.getValue1());
    }

    @RepeatedTest(5)
    void canProtect_largeMeteorWithBattery() {
        Component c1 = new Engine(1, connectors, 1);
        ship.placeComponent(c1, 6, 7);
        Cannon c2 = new Cannon(2, connectors, 2);
        ship.placeComponent(c2, 6, 8);

        Hit hit = new Hit(HitType.LARGEMETEOR, Direction.NORTH);
        Pair<Component, Integer> result = ship.canProtect(7, hit);
        assertNotNull(result);
        assertEquals(c1, result.getValue0());
        assertEquals(-1, result.getValue1());

        Hit hit1 = new Hit(HitType.LARGEMETEOR, Direction.NORTH);
        Pair<Component, Integer> result1 = ship.canProtect(8, hit1);
        assertNotNull(result1);
        assertEquals(1, result1.getValue1());
    }

    @RepeatedTest(5)
    void useEnergy_validBattery() {
        Battery battery = new Battery(1, connectors, 3);
        ship.placeComponent(battery, 6, 7);
        assertTrue(ship.useEnergy(1));
        assertEquals(2, battery.getEnergyNumber());
    }

    //TODO: Complete when method is implemented
    @RepeatedTest(5)
    void useEnergy_invalidBatteryThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ship.useEnergy(1));
    }

    @RepeatedTest(5)
    void useEnergy_emptyBatteryThrowsException() {
        Battery battery = new Battery(1, connectors, 1);
        ship.placeComponent(battery, 6, 7);
        ship.useEnergy(1);
        //assertThrows(NullPointerException.class, () -> ship.useEnergy(1));
        assertFalse(ship.useEnergy(1));
    }

    @RepeatedTest(5)
    void getInvalidComponents_noInvalidComponents() {
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        assertTrue(ship.getInvalidComponents().isEmpty());
    }

    @RepeatedTest(5)
    void getInvalidComponents_withInvalidComponents() {
        ship.placeComponent(new Cabin(1, connectors), 6, 7);
        ship.placeComponent(new Storage(2, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY}, true, 2), 6, 8);
        assertFalse(ship.getInvalidComponents().isEmpty());
    }

    @RepeatedTest(5)
    void getInvalidComponents_someComponentsInvalid() {
        ship.placeComponent(new Cabin(1, connectors), 6, 7);
        ship.placeComponent(new Storage(2, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY}, true, 2), 6, 8);
        ship.placeComponent(new Battery(3, connectors, 3), 5, 7);
        assertEquals(2, ship.getInvalidComponents().size());
    }

    @RepeatedTest(5)
    void ExposedConnectors_noComponents() {
        ship.refreshExposedConnectors();
        assertEquals(4, ship.getExposedConnectors());
    }

    @RepeatedTest(5)
    void ExposedConnectors_singleComponent() {
        Cabin cabin = new Cabin(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.refreshExposedConnectors();
        assertEquals(6, ship.getExposedConnectors());
    }

    @RepeatedTest(5)
    void ExposedConnectors_multipleComponents() {
        Cabin cabin = new Cabin(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        Battery battery = new Battery(2, connectors, 3);
        ship.placeComponent(battery, 6, 8);
        ship.refreshExposedConnectors();
        assertEquals(8, ship.getExposedConnectors());
    }

    @RepeatedTest(5)
    void ExposedConnectors_allConnectorsCovered() {
        Cabin cabin = new Cabin(1, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE});
        ship.placeComponent(cabin, 6, 7);
        Battery battery = new Battery(2, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.TRIPLE, ConnectorType.EMPTY, ConnectorType.EMPTY}, 3);
        ship.placeComponent(battery, 6, 8);
        Shield shield = new Shield(3, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.TRIPLE});
        ship.placeComponent(shield, 6, 6);
        ship.refreshExposedConnectors();
        assertEquals(3, ship.getExposedConnectors());
    }

    @RepeatedTest(5)
    void ExposedConnectors_someConnectorsCovered() {
        Cabin cabin = new Cabin(1, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE});
        ship.placeComponent(cabin, 6, 7);
        Battery battery = new Battery(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.EMPTY, ConnectorType.TRIPLE}, 3);
        ship.placeComponent(battery, 6, 8);
        Shield shield = new Shield(3, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.TRIPLE, ConnectorType.EMPTY, ConnectorType.TRIPLE});
        ship.placeComponent(shield, 6, 6);
        ship.refreshExposedConnectors();
        assertEquals(6, ship.getExposedConnectors());
    }

    @RepeatedTest(5)
    void getComponent_validCoordinates() {
        Cabin cabin = new Cabin(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        assertEquals(cabin, ship.getComponent(6, 7));
    }

    @RepeatedTest(5)
    void getComponent_invalidCoordinates() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> ship.getComponent(12, 12));
    }

    @RepeatedTest(5)
    void getComponent_noComponentAtCoordinates() {
        assertNull(ship.getComponent(6, 7));
    }

    @RepeatedTest(5)
    void getComponents_initialState() {
        Component[][] components = ship.getComponents();
        assertNotNull(components);
        assertEquals(12, components.length);
        assertEquals(12, components[0].length);
    }

    @RepeatedTest(5)
    void getComponents_afterAddingComponent() {
        Cabin cabin = new Cabin(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        Component[][] components = ship.getComponents();
        assertEquals(cabin, components[6][7]);
    }

    @RepeatedTest(5)
    void getComponents_afterRemovingComponent() {
        Cabin cabin = new Cabin(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.destroyComponent(6, 7);
        Component[][] components = ship.getComponents();
        assertNull(components[6][7]);
    }

    @RepeatedTest(5)
    void getSurroundingComponents_validCoordinates() {
        Cabin cabin = new Cabin(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        ArrayList<Component> surroundingComponents = ship.getSurroundingComponents(6, 7);
        assertEquals(4, surroundingComponents.size());
        assertNull(surroundingComponents.get(0)); // North
        assertNull(surroundingComponents.get(1)); // West
        assertNotNull(surroundingComponents.get(2)); // South
        assertEquals(ComponentType.CABIN, surroundingComponents.get(2).getComponentType());
        assertNull(surroundingComponents.get(3)); // East
    }

    @RepeatedTest(5)
    void getSurroundingComponents_withSurroundingComponents() {
        Cabin cabin = new Cabin(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        Battery battery = new Battery(2, connectors, 3);
        ship.placeComponent(battery, 5, 7); // North
        Storage storage = new Storage(3, connectors, true, 2);
        ship.placeComponent(storage, 6, 6); // West
        Shield shield = new Shield(5, connectors);
        ship.placeComponent(shield, 6, 8); // East

        ArrayList<Component> surroundingComponents = ship.getSurroundingComponents(6, 7);
        assertEquals(4, surroundingComponents.size());
        assertEquals(battery, surroundingComponents.get(0)); // North
        assertEquals(storage, surroundingComponents.get(1)); // West
        assertEquals(ComponentType.CABIN, surroundingComponents.get(2).getComponentType()); // South
        assertEquals(shield, surroundingComponents.get(3)); // East
    }

    @RepeatedTest(5)
    void reserveComponent_validComponent() {
        Component component = new Storage(1, connectors, true, 2);
        ship.reserveComponent(component);
        assertEquals(1, ship.getReservedComponents().size());
        assertEquals(component, ship.getReservedComponents().getFirst());
    }

    @RepeatedTest(5)
    void reserveComponent_multipleValidComponents() {
        Component component1 = new Storage(1, connectors, true, 2);
        Component component2 = new Battery(2, connectors, 3);
        ship.reserveComponent(component1);
        ship.reserveComponent(component2);
        assertEquals(2, ship.getReservedComponents().size());
        assertEquals(component1, ship.getReservedComponents().get(0));
        assertEquals(component2, ship.getReservedComponents().get(1));
    }

    @RepeatedTest(5)
    void reserveComponent_exceedingLimitThrowsException() {
        Component component1 = new Storage(1, connectors, true, 2);
        Component component2 = new Battery(2, connectors, 3);
        Component component3 = new Cannon(3, connectors, 1);
        ship.reserveComponent(component1);
        ship.reserveComponent(component2);
        assertThrows(IllegalStateException.class, () -> ship.reserveComponent(component3));
    }

    @RepeatedTest(5)
    void unreserveComponent_validComponent() {
        Component component = new Storage(1, connectors, true, 2);
        ship.reserveComponent(component);
        ship.unreserveComponent(component);
        assertTrue(ship.getReservedComponents().isEmpty());
    }

    @RepeatedTest(5)
    void unreserveComponent_multipleComponents() {
        Component component1 = new Storage(1, connectors, true, 2);
        Component component2 = new Battery(2, connectors, 3);
        ship.reserveComponent(component1);
        ship.reserveComponent(component2);
        ship.unreserveComponent(component1);
        assertEquals(1, ship.getReservedComponents().size());
        assertEquals(component2, ship.getReservedComponents().getFirst());
    }

    @RepeatedTest(5)
    void unreserveComponent_nonReservedComponent() {
        Component component = new Storage(1, connectors, true, 2);
        ship.unreserveComponent(component);
        assertTrue(ship.getReservedComponents().isEmpty());
    }

    @RepeatedTest(5)
    void placeComponent_validComponent() {
        Component component = new Storage(1, connectors, true, 2);
        ship.placeComponent(component, 6, 7);
        assertEquals(component, ship.getComponent(6, 7));
        assertEquals(2, ship.getNumberOfComponents());
    }

    @RepeatedTest(5)
    void placeComponent_invalidSpotThrowsException() {
        Component component = new Storage(1, connectors, true, 2);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> ship.placeComponent(component, 0, 0));
    }

    ///TODO: Implementare
    @RepeatedTest(5)
    void placeComponent_alreadyOccupiedSpotThrowsException() {
        Component component1 = new Storage(1, connectors, true, 2);
        Component component2 = new Battery(2, connectors, 3);
        ship.placeComponent(component1, 6, 7);
        assertThrows(IllegalStateException.class, () -> ship.placeComponent(component2, 6, 7));
    }

    @RepeatedTest(5)
    void placeComponent_exceedingShipBoundsThrowsException() {
        Component component = new Storage(1, connectors, true, 2);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> ship.placeComponent(component, 12, 12));
    }

    @RepeatedTest(5)
    void placeComponent_validComponentUpdatesStats() {
        Component component = new Battery(1, connectors, 3);
        ship.placeComponent(component, 6, 7);
        assertEquals(3, ship.getEnergyNumber());
    }

    @RepeatedTest(5)
    void getCabin_validID() {
        Cabin cabin = new Cabin(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        assertEquals(cabin, ship.getCabin(1));
    }

    @RepeatedTest(5)
    void getCabin_invalidIDThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ship.getCabin(999));
    }

    @RepeatedTest(5)
    void getCabin_afterRemovingCabinThrowsException() {
        Cabin cabin = new Cabin(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.destroyComponent(6, 7);
        assertThrows(IllegalArgumentException.class, () -> ship.getCabin(1));
    }

    @RepeatedTest(5)
    void getCabins_initialState() {
        assertFalse(ship.getCabins().isEmpty());
    }

    @RepeatedTest(5)
    void getCabins_afterAddingSingleCabin() {
        Cabin cabin = new Cabin(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        assertEquals(1, ship.getCabins().size());
        assertEquals(cabin, ship.getCabins().get(1));
    }

    @RepeatedTest(5)
    void getCabins_afterAddingMultipleCabins() {
        Cabin cabin1 = new Cabin(1, connectors);
        Cabin cabin2 = new Cabin(2, connectors);
        ship.placeComponent(cabin1, 6, 7);
        ship.placeComponent(cabin2, 6, 8);
        assertEquals(2, ship.getCabins().size());
        assertEquals(cabin1, ship.getCabins().get(1));
        assertEquals(cabin2, ship.getCabins().get(2));
    }

    @RepeatedTest(5)
    void getCabins_afterRemovingCabin() {
        Cabin cabin = new Cabin(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.destroyComponent(6, 7);
        assertTrue(ship.getCabins().isEmpty());
    }

    @RepeatedTest(5)
    void getStorage_validID() {
        Storage storage = new Storage(1, connectors, true, 2);
        ship.placeComponent(storage, 6, 7);
        assertEquals(storage, ship.getStorage(1));
    }

    @RepeatedTest(5)
    void getStorage_invalidIDThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ship.getStorage(999));
    }

    @RepeatedTest(5)
    void getStorage_afterRemovingStorageThrowsException() {
        Storage storage = new Storage(1, connectors, true, 2);
        ship.placeComponent(storage, 6, 7);
        ship.destroyComponent(6, 7);
        assertThrows(IllegalArgumentException.class, () -> ship.getStorage(1));
    }

    @RepeatedTest(5)
    void getStorages_initialState() {
        assertTrue(ship.getStorages().isEmpty());
    }

    @RepeatedTest(5)
    void getStorages_afterAddingSingleStorage() {
        Storage storage = new Storage(1, connectors, true, 2);
        ship.placeComponent(storage, 6, 7);
        assertEquals(1, ship.getStorages().size());
        assertEquals(storage, ship.getStorages().get(1));
    }

    @RepeatedTest(5)
    void getStorages_afterAddingMultipleStorages() {
        Storage storage1 = new Storage(1, connectors, true, 2);
        Storage storage2 = new Storage(2, connectors, true, 2);
        ship.placeComponent(storage1, 6, 7);
        ship.placeComponent(storage2, 6, 8);
        assertEquals(2, ship.getStorages().size());
        assertEquals(storage1, ship.getStorages().get(1));
        assertEquals(storage2, ship.getStorages().get(2));
    }

    @RepeatedTest(5)
    void getStorages_afterRemovingStorage() {
        Storage storage = new Storage(1, connectors, true, 2);
        ship.placeComponent(storage, 6, 7);
        ship.destroyComponent(6, 7);
        assertTrue(ship.getStorages().isEmpty());
    }

    @RepeatedTest(5)
    void getBattery_validID() {
        Battery battery = new Battery(1, connectors, 3);
        ship.placeComponent(battery, 6, 7);
        assertEquals(battery, ship.getBattery(1));
    }

    @RepeatedTest(5)
    void getBattery_invalidIDThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ship.getBattery(999));
    }

    @RepeatedTest(5)
    void getBattery_afterRemovingBatteryThrowsException() {
        Battery battery = new Battery(1, connectors, 3);
        ship.placeComponent(battery, 6, 7);
        ship.destroyComponent(6, 7);
        assertThrows(IllegalArgumentException.class, () -> ship.getBattery(1));
    }

    @RepeatedTest(5)
    void getBatteries_initialState() {
        assertTrue(ship.getBatteries().isEmpty());
    }

    @RepeatedTest(5)
    void getBatteries_afterAddingSingleBattery() {
        Battery battery = new Battery(1, connectors, 3);
        ship.placeComponent(battery, 6, 7);
        assertEquals(1, ship.getBatteries().size());
        assertEquals(battery, ship.getBatteries().get(1));
    }

    @RepeatedTest(5)
    void getBatteries_afterAddingMultipleBatteries() {
        Battery battery1 = new Battery(1, connectors, 3);
        Battery battery2 = new Battery(2, connectors, 3);
        ship.placeComponent(battery1, 6, 7);
        ship.placeComponent(battery2, 6, 8);
        assertEquals(2, ship.getBatteries().size());
        assertEquals(battery1, ship.getBatteries().get(1));
        assertEquals(battery2, ship.getBatteries().get(2));
    }

    @RepeatedTest(5)
    void getBatteries_afterRemovingBattery() {
        Battery battery = new Battery(1, connectors, 3);
        ship.placeComponent(battery, 6, 7);
        ship.destroyComponent(6, 7);
        assertTrue(ship.getBatteries().isEmpty());
    }

    @RepeatedTest(5)
    void getCannon_validID() {
        Cannon cannon = new Cannon(1, connectors, 1);
        ship.placeComponent(cannon, 6, 7);
        assertEquals(cannon, ship.getCannon(1));
    }

    @RepeatedTest(5)
    void getCannon_invalidIDThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ship.getCannon(999));
    }

    //TODO: Manca remove per cannons in destroy
    @RepeatedTest(5)
    void getCannon_afterRemovingCannonThrowsException() {
        Cannon cannon = new Cannon(1, connectors, 1);
        ship.placeComponent(cannon, 6, 7);
        ship.destroyComponent(6, 7);
        assertThrows(IllegalArgumentException.class, () -> ship.getCannon(1));
    }

    @RepeatedTest(5)
    void getCannons_initialState() {
        assertTrue(ship.getCannons().isEmpty());
    }

    @RepeatedTest(5)
    void getCannons_afterAddingSingleCannon() {
        Cannon cannon = new Cannon(1, connectors, 1);
        ship.placeComponent(cannon, 6, 7);
        assertEquals(1, ship.getCannons().size());
        assertEquals(cannon, ship.getCannons().get(1));
    }

    @RepeatedTest(5)
    void getCannons_afterAddingMultipleCannons() {
        Cannon cannon1 = new Cannon(1, connectors, 1);
        Cannon cannon2 = new Cannon(2, connectors, 1);
        ship.placeComponent(cannon1, 6, 7);
        ship.placeComponent(cannon2, 6, 8);
        assertEquals(2, ship.getCannons().size());
        assertEquals(cannon1, ship.getCannons().get(1));
        assertEquals(cannon2, ship.getCannons().get(2));
    }

    //TODO: Manca remove per cannons in destroy
    @RepeatedTest(5)
    void getCannons_afterRemovingCannon() {
        Cannon cannon = new Cannon(1, connectors, 1);
        ship.placeComponent(cannon, 6, 7);
        ship.destroyComponent(6, 7);
        assertTrue(ship.getCannons().isEmpty());
    }

    @RepeatedTest(5)
    void destroyComponent_validComponent() {
        Cannon cannon = new Cannon(1, connectors, 1);
        ship.placeComponent(cannon, 6, 7);
        ship.destroyComponent(6, 7);
        assertNull(ship.getComponent(6, 7));
        assertTrue(ship.getCannons().isEmpty());
    }

    @RepeatedTest(5)
    void destroyComponent_noComponentAtCoordinates() {
        assertNull(ship.getComponent(6, 7));
        assertThrows(IllegalArgumentException.class, () -> ship.destroyComponent(6, 7));
    }

    @RepeatedTest(5)
    void destroyComponent_updatesStats() {
        Cannon cannon = new Cannon(1, connectors, 1);
        ship.placeComponent(cannon, 6, 7);
        ship.destroyComponent(6, 7);
        assertEquals(0, ship.getSingleCannonsStrength());
    }

    //TODO: Controllare metodo con qualcuno
    @RepeatedTest(5)
    void getDisconnectedComponents_noComponents() {
        assertTrue(ship.getDisconnectedComponents().isEmpty());
    }

    @RepeatedTest(5)
    void getDisconnectedComponents_singleComponent() {
        Cabin cabin = new Cabin(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        assertTrue(ship.getDisconnectedComponents().isEmpty());
    }

    @RepeatedTest(5)
    void getDisconnectedComponents_multipleConnectedComponents() {
        Cabin cabin = new Cabin(1, connectors);
        Battery battery = new Battery(2, connectors, 3);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(battery, 6, 8);
        assertTrue(ship.getDisconnectedComponents().isEmpty());
    }

    @RepeatedTest(5)
    void getDisconnectedComponents_multipleDisconnectedComponents() {
        Cabin cabin = new Cabin(1, connectors);
        Battery battery = new Battery(2, connectors, 3);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(battery, 8, 8);
        assertFalse(ship.getDisconnectedComponents().isEmpty());
        assertEquals(2, ship.getDisconnectedComponents().size());
    }

    @RepeatedTest(5)
    void getDisconnectedComponents_afterDestroyingComponent() {
        Cabin cabin = new Cabin(1, connectors);
        Battery battery = new Battery(2, connectors, 3);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(battery, 6, 8);
        ship.destroyComponent(6, 8);
        assertFalse(ship.getDisconnectedComponents().isEmpty());
        assertEquals(1, ship.getDisconnectedComponents().size());
    }



/*

    @RepeatedTest(5)
    void getNumberOfComponents() {
        assertEquals(1, ship.getNumberOfComponents());


        //Faccio un inserimento e controllo che sia 1 il risultato, poi creo un metodo che mi inserisca tot volte un componente e il risultato ha caso limite 27


        Random rand = new Random();
        int count = rand.nextInt(1, 4);
        for(int i = 0; i < count; i++){
            Storage storage = new Storage(i, connectors, true, 2);
            ship.placeComponent(storage, 6, 7 + i);
        }
        assertEquals(count + 1, ship.getNumberOfComponents());
    }

    @RepeatedTest(5)
    void getSingleEnginesStrength() {
        assertEquals(0, ship.getSingleEnginesStrength());

        Random rand = new Random();
        int j;
        int i, power = 0;
        int index = 0;
        int limit = rand.nextInt(3) + 1;
        System.out.println("There are " + limit + " engine");
        for(j = 0; j < limit; j++){
            i = rand.nextInt(2) + 1;
            System.out.println(" i " + i);
            Engine engine = new Engine(j, connectors, i);

            int r = rand.nextInt(4) + 1;
            for(int k = 0; k < r; k++){
                engine.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            if(engine.getClockwiseRotation() != 0){
                System.out.println("Engine " + j + " cannot be added");
            } else {
                ship.placeComponent(engine, 8,7 + index);
                index++;
                if(engine.getComponentType() == ComponentType.SINGLE_ENGINE){
                    power += i;
                }
            }

            System.out.println(" ");
        }

        assertEquals(power, ship.getSingleEnginesStrength());
    }

    @RepeatedTest(5)
    void getDoubleEnginesStrength() {
        assertEquals(0, ship.getDoubleEnginesStrength());

        Random rand = new Random();
        int j;
        int i, power = 0;
        int index = 0;
        int limit = rand.nextInt(3) + 1;
        System.out.println("There are " + limit + " engine");
        for(j = 0; j < limit; j++){
            i = rand.nextInt(2) + 1;
            System.out.println(" i " + i);
            Engine engine = new Engine(j, connectors, i);

            int r = rand.nextInt(4) + 1;
            for(int k = 0; k < r; k++){
                engine.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            if(engine.getClockwiseRotation() != 0){
                System.out.println("Engine " + j + " cannot be added");
            } else {
                ship.placeComponent(engine, 8,7 + index);
                index++;
                if(engine.getComponentType() == ComponentType.DOUBLE_ENGINE){
                    power += 2;
                }
            }

            System.out.println(" ");
        }

        assertEquals(power, ship.getDoubleEnginesStrength());
    }

    @RepeatedTest(500)
    void checkTotalEngineStrength() {
        assertEquals(0, ship.getSingleEnginesStrength() + ship.getDoubleEnginesStrength());

        Random rand = new Random();
        int j;
        int i, power = 0;
        for(j = 0; j < rand.nextInt(5); j++){
            i = rand.nextInt(2) + 1;
            System.out.println(" i " + i);
            Engine engine = new Engine(0, connectors, i);
            power = power + i;
            ship.placeComponent(engine, 8,7 + j);
        }
        System.out.println(" power " + power);
        assertEquals(power, ship.getSingleEnginesStrength() + ship.getDoubleEnginesStrength());
    }

    @RepeatedTest(5)
    void getSingleCannonsStrength() {
        assertEquals(0, ship.getSingleCannonsStrength());

        Random rand = new Random();
        int j;
        int i;
        float power = 0f;
        int index = 0;
        int limit = rand.nextInt(3) + 1;
        System.out.println("There are " + limit + " cannon");
        for(j = 0; j < limit; j++){
            i = rand.nextInt(2) + 1;
            System.out.println(" i " + i);
            Cannon cannon = new Cannon(j, connectors, i);

            int r = rand.nextInt(4) + 1;
            for(int k = 0; k < r; k++){
                cannon.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            ship.placeComponent(cannon, 6,7 + index);
            if(cannon.getComponentType() == ComponentType.SINGLE_CANNON){
                System.out.println("Cannon " + j + " is good");
                if(cannon.getClockwiseRotation() != 0){
                    power += 0.5f;
                } else {
                    power += 1f;
                }
            }
            index++;

            System.out.println(" ");
        }
        System.out.println(" power : " + power);
        assertEquals(power, ship.getSingleCannonsStrength());
    }

    @RepeatedTest(5)
    void getDoubleCannonsStrength() {
        assertEquals(0, ship.getDoubleCannonsStrength());

        Random rand = new Random();
        int j;
        int i, power = 0;
        int index = 0;
        int limit = rand.nextInt(3) + 1;
        System.out.println("There are " + limit + " cannon");
        for(j = 0; j < limit; j++){
            i = rand.nextInt(2) + 1;
            System.out.println(" i " + i);
            Cannon cannon = new Cannon(j, connectors, i);

            int r = rand.nextInt(4) + 1;
            for(int k = 0; k < r; k++){
                cannon.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            ship.placeComponent(cannon, 6,7 + index);
            if(cannon.getComponentType() == ComponentType.DOUBLE_CANNON){
                System.out.println("Cannon " + j + " is good");
                if(cannon.getClockwiseRotation() != 0){
                    power += 1;
                } else {
                    power += 2;
                }
            }
            index++;

            System.out.println(" ");
        }
        System.out.println(" power : " + power);
        assertEquals(power, ship.getDoubleCannonsStrength());
    }

    @RepeatedTest(5)
    void getDoubleCannonsNumber() {
        assertEquals(0, ship.getDoubleCannonsNumber());

        Random rand = new Random();
        int j;
        int i;
        int power = 0;
        int index = 0;
        int number = 0;
        int limit = rand.nextInt(3) + 1;
        System.out.println("There are " + limit + " cannon");
        for(j = 0; j < limit; j++){
            i = rand.nextInt(2) + 1;
            System.out.println(" i " + i);
            Cannon cannon = new Cannon(j, connectors, i);

            int r = rand.nextInt(4) + 1;
            for(int k = 0; k < r; k++){
                cannon.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            ship.placeComponent(cannon, 6,7 + index);
            if(cannon.getComponentType() == ComponentType.DOUBLE_CANNON){
                System.out.println("Cannon " + j + " is good");
                if(cannon.getClockwiseRotation() != 0){
                    power += 1;
                } else {
                    power += 2;
                }
                number++;
            }
            index++;

            System.out.println(" ");
        }
        System.out.println(" number : " + number);
        assertEquals(number, ship.getDoubleCannonsNumber());
    }

    @RepeatedTest(5)
    void checkTotalCannonsStrength() {
        assertEquals(0, ship.getDoubleCannonsStrength() + ship.getSingleCannonsStrength());

        Random rand = new Random();
        int j;
        int i, power = 0;
        for(j = 0; j < rand.nextInt(5); j++){
            i = rand.nextInt(2) + 1;
            Cannon cannon = new Cannon(j, connectors, i);
            power += i;
            ship.placeComponent(cannon, 6,7 + j);
        }

        assertEquals(power, ship.getDoubleCannonsStrength() + ship.getSingleCannonsStrength());
    }

    @RepeatedTest(5)
    void getEnergyNumber() {
        assertEquals(0, ship.getEnergyNumber());

        Random rand = new Random();
        int limit = rand.nextInt(3);
        int index = 0;
        int total = 0;
        for(int i = 0; i < limit; i++){
            int j = rand.nextInt(2,4);
            Battery battery = new Battery(i, connectors, j);

            int r = rand.nextInt(4) + 1;
            for(int k = 0; k < r; k++){
                battery.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            ship.placeComponent(battery, 6,7 + index);
            index++;
            total += j;
        }

        assertEquals(total, ship.getEnergyNumber());
    }

    @RepeatedTest(5)
    void exchangeGood() {
        assertEquals(0, ship.getGoodsValue());

        Random rand = new Random();
        int limit = rand.nextInt(4);
        int index = 0;
        int total = 0;
        for(int i = 0; i < limit; i++){
            System.out.println("i : " + i + "\n");
            boolean bool = rand.nextBoolean();
            Storage storage = new Storage(i, connectors, bool, 3);

            ship.placeComponent(storage, 6,7 + index);
            index++;

            //Creation of storage
            ArrayList<Good> gToAdd = new ArrayList<>();
            for(int k = 0; k < 3; k++){
                GoodType[] values = GoodType.values();
                GoodType randomType = values[rand.nextInt(values.length)];
                System.out.println(randomType);
                Good good = new Good(randomType);

                if(storage.isDangerous()){
                    gToAdd.add(good);
                    total += good.getValue();
                } else {
                    if(good.getColor() != GoodType.RED){
                        gToAdd.add(good);
                        total += good.getValue();
                    } else {
                        System.out.println("Red good not added");
                        // k--; If we want to add another good
                    }
                }
            }
            ship.exchangeGood(gToAdd, null, storage.getID());

            System.out.println(" ");

            //Add
            ArrayList<Good> addGoods = new ArrayList<>();
            int toAdd = rand.nextInt(1, storage.getGoodsCapacity());
            for(int k = 0; k < toAdd; k++){
                GoodType[] values = GoodType.values();
                GoodType randomType = values[rand.nextInt(values.length)];
                System.out.println(randomType);
                Good good = new Good(randomType);

                if(storage.isDangerous()){
                    addGoods.add(good);
                    total += good.getValue();
                } else {
                    if(good.getColor() != GoodType.RED){
                        addGoods.add(good);
                        total += good.getValue();
                    } else {
                        System.out.println("Red good not added");
                        k--;
                    }
                }
            }

            System.out.println(" ");

            //Remove
            ArrayList<Good> removeGoods = new ArrayList<>();
            for(int k = 0; k < toAdd; k++){
                Good good = storage.getGoods().get(k);
                System.out.println(good.getColor());
                removeGoods.add(good);
                total -= good.getValue();
            }

            ship.exchangeGood(addGoods, removeGoods, storage.getID());
        }
        System.out.println(total);
        assertEquals(total, ship.getGoodsValue());
    }

    //Test for CrewMember and Aliens
    @RepeatedTest(50)
    void crewNumber(){
        assertEquals(0, ship.getCrewNumber());

        Random rand = new Random();
        int limit = rand.nextInt(1, 4);
        System.out.println("How many: " + limit);
        int total = 0;
        for(int i = 0; i < limit; i++){
            Cabin cabin = new Cabin(i + 4, connectors);
            System.out.println(cabin);
            assertThrows((IllegalArgumentException.class), () -> ship.addCrewMember(cabin.getID(), false, false), "N1");

            ship.placeComponent(cabin, 6,7 + i);

            assertThrows((IllegalArgumentException.class), () -> ship.addCrewMember(cabin.getID(), true, true));

            int flag = rand.nextInt(1, 3);
            System.out.println("Flag: " + flag);
            if(flag == 1 && (!ship.hasPurpleAlien() || !ship.hasBrownAlien())) {
                if (!ship.hasBrownAlien()) {
                    LifeSupportBrown lsb = new LifeSupportBrown(i + 10, connectors);
                    ship.placeComponent(lsb, 5,7 + i);

                    ship.getCabin(i + 4).isValid();
                    ship.addCrewMember(cabin.getID(), true, false);
                    total += 1;
                    System.out.println("BA");

                    assertTrue(ship.hasBrownAlien());
                    assertEquals(1, cabin.getCrewNumber());
                } else {
                    LifeSupportPurple lsp = new LifeSupportPurple(i + 11, connectors);
                    ship.placeComponent(lsp, 5,7 + i);

                    ship.getCabin(i + 4).isValid();
                    ship.addCrewMember(cabin.getID(), false, true);
                    total += 1;
                    System.out.println("PA");

                    assertTrue(ship.hasPurpleAlien());
                    assertEquals(1, cabin.getCrewNumber());
                }
            } else {
                ship.addCrewMember(cabin.getID(), false, false);
                total += 2;
                System.out.println("CM");
                assertEquals(2, cabin.getCrewNumber());
            }
        }

        System.out.println("Total : " + total +"\n");
        assertEquals(total, ship.getCrewNumber());

        for(int j = 0; j < limit; j++){
            boolean flag = rand.nextBoolean();
            System.out.println("Flag : " + flag + "    ID: " + (j));
            if(flag){           //I need to remove
                if (ship.getCabin(j + 4).hasBrownAlien()) {
                    System.out.println("BA.1");

                    ship.removeCrewMember(j + 4, 1);
                    assertFalse(ship.hasBrownAlien());
                    assertEquals(0, ship.getCabin(j + 4).getCrewNumber());

                    total -= 1;
                } else if(ship.getCabin(j + 4).hasPurpleAlien()) {
                    System.out.println("PA.1");

                    ship.removeCrewMember(j + 4, 1);
                    assertFalse(ship.hasPurpleAlien());
                    assertEquals(0, ship.getCabin(j + 4).getCrewNumber());

                    total -= 1;
                } else {
                    System.out.println("CM.1");

                    ship.removeCrewMember(j + 4, 2);
                    assertEquals(0, ship.getCabin(j + 4).getCrewNumber());

                    total -= 2;
                }
            }
        }

        System.out.println("Total final : " + total + "\n");
        assertEquals(total, ship.getCrewNumber());
    }

    @RepeatedTest(500)
    void canProtect(){
        Random rand = new Random();
        int numberHit = rand.nextInt(3, 6);
        Pair<Component, Integer> pair;

        Shield shield = new Shield(10, connectors);
        for(int j = 0; j < rand.nextInt(4); j++){
            shield.rotateClockwise();
        }
        System.out.println("Rotation shield: " + shield.getClockwiseRotation());
        ship.placeComponent(shield, 6,7);

        Battery battery = new Battery(11, connectors, 3);
        ship.placeComponent(battery, 6,8);
        Battery battery1 = new Battery(12, connectors, 3);
        ship.placeComponent(battery1, 8,7);
        ConnectorType[] connector1 = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.EMPTY, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Battery battery2 = new Battery(13, connector1, 3);
        ship.placeComponent(battery2, 8,6);
        Battery battery3 = new Battery(12, connectors, 3);
        ship.placeComponent(battery3, 9,7);

        Cannon cannon = new Cannon(12, connectors, 1);
        for(int j = 0; j < rand.nextInt(4); j++){
            cannon.rotateClockwise();
        }
        System.out.println("Rotation cannon: " + cannon.getClockwiseRotation());
        ship.placeComponent(cannon, 6,9);

        for(int j = 0; j < numberHit; j++){
            int dice = rand.nextInt(6,10);
            System.out.println("\nDice : " + dice);

            HitType[] values = HitType.values();
            HitType randomType = values[rand.nextInt(values.length)];
            System.out.println(randomType);

            Direction[] values2 = Direction.values();
            Direction randomDirection = values2[rand.nextInt(values2.length)];
            System.out.println(randomDirection);

            Hit hit = new Hit(randomType, randomDirection);

            pair = ship.canProtect(dice, hit);
            System.out.println(pair.getValue0() + "  " + pair.getValue1());

            if(hit.getType() == HitType.SMALLMETEOR){
                Component component = null;
                Component[][] components = ship.getComponents();
                switch (hit.getDirection()) {
                    case NORTH:
                        for (int i = 0; i < 12 && component == null; i++) {
                            component = components[i][dice];
                        }
                        break;
                    case WEST:
                        for (int i = 0; i < 12 && component == null; i++) {
                            component = components[dice][i];
                        }
                        break;
                    case SOUTH:
                        for (int i = 12 - 1; i >= 0 && component == null; i--) {
                            component = components[i][dice];
                        }
                        break;
                    case EAST:
                        for (int i = 12 - 1; i >= 0 && component == null; i--) {
                            component = components[dice][i];
                        }
                        break;
                }
                if (component != null) {
                    if (component.getConnection(hit.getDirection().getValue()) == ConnectorType.EMPTY) {
                        assertEquals(1, pair.getValue1());
                    }
                } else if(hit.getDirection().getValue() == shield.getClockwiseRotation() || hit.getDirection().getValue() == (shield.getClockwiseRotation() + 3) % 4){
                    assertEquals(0, pair.getValue1());
                } else {
                    assertEquals(-1, pair.getValue1());
                }
            } else if(hit.getType() == HitType.LARGEMETEOR){
                if(hit.getDirection().getValue() == cannon.getClockwiseRotation() && pair.getValue0() != null){
                    if(hit.getDirection().getValue() % 2 == 0 && dice == cannon.getColumn()){
                        assertEquals(1, pair.getValue1());
                    } else if(hit.getDirection().getValue() % 2 != 0 && dice == cannon.getRow()){
                        assertEquals(1, pair.getValue1());
                    } else {
                        assertEquals(-1, pair.getValue1());
                    }
                } else {
                    if(pair.getValue0() == null){
                        assertEquals(1, pair.getValue1());
                    } else {
                        assertEquals(-1, pair.getValue1());
                    }
                }
            } else if(hit.getType() == HitType.LIGHTFIRE){
                if(hit.getDirection().getValue() == shield.getClockwiseRotation() || hit.getDirection().getValue() == (shield.getClockwiseRotation() + 3) % 4){
                    assertEquals(0, pair.getValue1());
                } else {
                    assertEquals(-1, pair.getValue1());
                }
            } else {
                assertEquals(-1, pair.getValue1());
            }

        }
    }

    //TODO: Control if in the model they put false or they throws an exception
    @RepeatedTest(5)
    void useEnergy() {
        Random rand = new Random();
        Map<Integer, Battery> batteries;
        int count = rand.nextInt(3) + 1;
        for(int i = 0; i < count; i++){
            int energy = rand.nextInt(2, 4);
            System.out.println("Battery " + i + " : " + energy);
            Battery battery = new Battery(i + 4, connectors, energy);
            for(int j = 0; j < rand.nextInt(4); j++){
                battery.rotateClockwise();
            }
            ship.placeComponent(battery, 6,7 + i);
        }

        batteries = ship.getBatteries();
        assertEquals(count, batteries.size());

        for(int i = 0; i < count; i++){
            Battery batterySingle = batteries.get(i + 4);
            int energy = batterySingle.getEnergyNumber();
            System.out.println(energy);
            if(energy > 0){
                assertTrue(ship.useEnergy(batterySingle.ID));
                System.out.println(batterySingle.getEnergyNumber());
                assertEquals(energy - 1, batterySingle.getEnergyNumber());
            } else {
                assertFalse(ship.useEnergy(batterySingle.ID));
            }
        }

    }

    @RepeatedTest(5)
    void getInvalidComponents() {
        Random rand = new Random();
        ConnectorType[] values = ConnectorType.values();
        List<Pair<Integer, Integer>> invalidComponents;
        boolean shipOk = true;

        int count = rand.nextInt(1, 4);
        for(int j = 0; j < count; j++){
            ConnectorType[] connector = new ConnectorType[4];
            for(int i = 0; i < 4; i++){
                ConnectorType randomType = values[rand.nextInt(values.length)];
                System.out.println(randomType);
                connector[i] = randomType;
            }

            Storage storage = new Storage(j + 4, connector, true, 2);
            System.out.println(storage + "\n");
            ship.placeComponent(storage, 6, 7 + j);

            if(!storage.isValid()){
                shipOk = false;
            }
        }
        System.out.println(shipOk);

        invalidComponents = ship.getInvalidComponents();
        for (Pair<Integer, Integer> invalidComponent : invalidComponents) {
            System.out.println(ship.getComponent(invalidComponent.getValue0(), invalidComponent.getValue1()));
        }
        System.out.println();

        if(!invalidComponents.isEmpty()){
            System.out.println(" No " + invalidComponents);
            assertFalse(shipOk);
        } else {
            System.out.println(" Si ");
            assertTrue(shipOk);
        }
        System.out.println();

    }

    @RepeatedTest(5)
    void refreshExposedConnectors() {
        ship.refreshExposedConnectors();
        Component c = ship.getComponent(7, 7);
        System.out.println(c.getExposedConnectors());
        assertEquals(4, ship.getExposedConnectors());

        Random rand = new Random();
        int count = rand.nextInt(1, 4);
        int ris = 4;
        Storage[] storage = new Storage[count];
        for(int i = 0; i < count; i++){
            ris = 3; //Because when I add the first component, the exposed connectors are 3 for the central component
            storage[i] = new Storage(i, connectors, true, 2);
            ship.placeComponent(storage[i], 6, 7 + i);
            ship.refreshExposedConnectors();
            for(int j = 0; j <= i; j++){
                ris += storage[j].getExposedConnectors();
            }
            System.out.println(ris);
        }
        assertEquals(ris, ship.getExposedConnectors());
    }

    //All the components are in direction 0
    @RepeatedTest(5)
    void getComponent() throws JsonProcessingException {
        Random rand = new Random();
        ComponentType[] values = ComponentType.values();
        ComponentType randomType = values[rand.nextInt(values.length)];

        GenerateRandomShip randShip = new GenerateRandomShip();
        ship = randShip.getShip();
        boolean[][] valid = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                valid[i][j] = true;
            }
        }

        Integer[] number = new Integer[12]; //Last one for total components
        for(int i = 0; i < 12; i++){
            number[i] = 0;
        }

        for(int i = 0; i < 12; i++){
            for(int j = 0; j < 12; j++){
                if(valid[i][j]){
                    if(ship.getComponent(i,j) != null){
                        switch (ship.getComponent(i , j).getComponentType()) {
                            case CABIN:
                                assertEquals(ComponentType.CABIN, ship.getComponent(i, j).getComponentType());
                                number[0] += 1;
                                break;
                            case CENTER_CABIN:
                                assertEquals(ComponentType.CENTER_CABIN, ship.getComponent(i, j).getComponentType());
                                number[0] += 1;
                                break;
                            case STORAGE:
                                assertEquals(ComponentType.STORAGE, ship.getComponent(i, j).getComponentType());
                                number[1] += 1;
                                break;
                            case BATTERY:
                                assertEquals(ComponentType.BATTERY, ship.getComponent(i, j).getComponentType());
                                number[2] += 1;
                                break;
                            case SINGLE_ENGINE:
                                assertEquals(ComponentType.SINGLE_ENGINE, ship.getComponent(i, j).getComponentType());
                                number[3] += 1;
                                break;
                            case DOUBLE_ENGINE:
                                assertEquals(ComponentType.DOUBLE_ENGINE, ship.getComponent(i, j).getComponentType());
                                number[4] += 1;
                                break;
                            case SINGLE_CANNON:
                                assertEquals(ComponentType.SINGLE_CANNON, ship.getComponent(i, j).getComponentType());
                                number[5] += 1;
                                break;
                            case DOUBLE_CANNON:
                                assertEquals(ComponentType.DOUBLE_CANNON, ship.getComponent(i, j).getComponentType());
                                number[6] += 1;
                                break;
                            case SHIELD:
                                assertEquals(ComponentType.SHIELD, ship.getComponent(i, j).getComponentType());
                                number[7] += 1;
                                break;
                            case BROWN_LIFE_SUPPORT:
                                assertEquals(ComponentType.BROWN_LIFE_SUPPORT, ship.getComponent(i, j).getComponentType());
                                number[8] += 1;
                                break;
                            case PURPLE_LIFE_SUPPORT:
                                assertEquals(ComponentType.PURPLE_LIFE_SUPPORT, ship.getComponent(i, j).getComponentType());
                                number[9] += 1;
                                break;
                            case CONNECTORS:
                                assertEquals(ComponentType.CONNECTORS, ship.getComponent(i, j).getComponentType());
                                number[10] += 1;
                                break;
                            default:
                                break;
                        }
                        number[11] += 1;
                    }
                }
            }
        }

        assertEquals(number[0], ship.getCabins().size());
        assertEquals(number[1], ship.getStorages().size());
        assertEquals(number[2], ship.getBatteries().size());

        assertEquals(number[3], ship.getSingleEnginesStrength());
        assertEquals(number[4], ship.getDoubleEnginesStrength() / 2);

        assertEquals(number[5] + number[6], ship.getCannons().size());
        assertEquals(number[6], ship.getDoubleCannonsNumber());

        boolean notCabinWithLifeSupport = true;
        boolean check = false;
        for(Map.Entry<Integer, Cabin> c : ship.getCabins().entrySet()){
            ArrayList<Component> surrounding = ship.getSurroundingComponents(c.getValue().getRow(), c.getValue().getColumn());
            for(Component comp : surrounding){
                if(comp != null) {
                    if (comp.getComponentType() == ComponentType.BROWN_LIFE_SUPPORT) {
                        c.getValue().isValid();
                        ship.addCrewMember(c.getValue().getID(), true, false);
                        notCabinWithLifeSupport = false;
                        ship.removeCrewMember(c.getValue().getID(), 1);
                        check = true;
                        break;
                    }
                }
            }
            break;
        }
        if(check){
            assertTrue(number[8] > 0);
        } else {
            assertTrue(notCabinWithLifeSupport);
        }

        notCabinWithLifeSupport = true;
        check = false;
        for(Map.Entry<Integer, Cabin> c : ship.getCabins().entrySet()){
            ArrayList<Component> surrounding = ship.getSurroundingComponents(c.getValue().getRow(), c.getValue().getColumn());
            for(Component comp : surrounding){
                if(comp != null) {
                    if (comp.getComponentType() == ComponentType.PURPLE_LIFE_SUPPORT) {
                        c.getValue().isValid();
                        ship.addCrewMember(c.getValue().getID(), false, true);
                        notCabinWithLifeSupport = false;
                        check = true;
                        break;
                    }
                }
            }
            break;
        }
        if(check){
            assertTrue(number[9] > 0);
        } else {
            assertTrue(notCabinWithLifeSupport);
        }

        int remaining = number[11] - number[0] - number[1] - number[2] - number[3] - number[4] - number[5] - number[6] - number[8] - number[9];
        assertEquals(remaining, number[7] + number[10]); //Number of shields and connectors

        assertEquals(number[11], ship.getNumberOfComponents());
    }

    @RepeatedTest(5)
    void getSurroundingComponents() throws JsonProcessingException {
        Random rand = new Random();
        int count = rand.nextInt(1, 4);

        GenerateRandomShip s = new GenerateRandomShip();
        SpaceShip ship = s.getShip();

        boolean[][] vs = new boolean[12][12];
        for(int i = 0; i < 12; i++) {
            for(int j = 0; j < 12; j++) {
                vs[i][j] = false;
            }
        }
        vs[5][6] = true;
        vs[5][8] = true;
        for(int j = 5; j < 10; j++) {
            vs[6][j] = true;
        }
        for(int i = 4; i < 11; i++) {
            vs[7][i] = true;
            vs[8][i] = true;
        }
        for(int j = 4; j < 7; j++) {
            vs[9][j] = true;
        }
        for(int j = 8; j < 11; j++) {
            vs[9][j] = true;
        }

        for(int i = 1; i < 11; i++){
            for(int j = 1; j < 11; j++){
                if(vs[i][j]){
                    ArrayList<Component> result = new ArrayList<>();
                    result = ship.getSurroundingComponents(i, j);
                    System.out.println(result);
                    assertEquals(ship.getComponent(i - 1, j), result.get(0));
                    assertEquals(ship.getComponent(i, j - 1), result.get(1));
                    assertEquals(ship.getComponent(i + 1, j), result.get(2));
                    assertEquals(ship.getComponent(i, j + 1), result.get(3));
                }
            }
        }
    }

    @RepeatedTest(5)
    void reserveComponent() {
        Component c1, c2, c3;

        c1 = new Storage(0, connectors, true, 2);
        assertDoesNotThrow(() -> ship.reserveComponent(c1));
        assertEquals(1, ship.getReservedComponents().size());
        assertEquals(c1, ship.getReservedComponents().getFirst());

        c2 = new Storage(1, connectors, true, 2);
        assertDoesNotThrow(() -> ship.reserveComponent(c2));
        assertEquals(2, ship.getReservedComponents().size());
        assertEquals(c2, ship.getReservedComponents().get(1));

        c3 = new Storage(2, connectors, true, 2);
        assertThrows(IllegalStateException.class, () -> ship.reserveComponent(c3));
    }

    //Return list of all the lost components
    @RepeatedTest(5)
    void getLostComponent() {
        Random rand = new Random();
        List<Component> lostComponents;
        int count = rand.nextInt(1, 4);
        for(int i = 0; i < count; i++){
            Storage storage = new Storage(i, connectors, true, 2);
            System.out.println(storage);
            ship.placeComponent(storage, 6, 7 + i);
        }
        ship.destroyComponent(6, 7 + count - 1);
        lostComponents = ship.getLostComponents();
        assertEquals(1, lostComponents.size());
    }

    @RepeatedTest(5)
    void unreserveComponent() {
        Component c1, c2;

        c1 = new Storage(0, connectors, true, 2);
        ship.reserveComponent(c1);
        c2 = new Storage(1, connectors, true, 2);
        ship.reserveComponent(c2);

        ship.unreserveComponent(c1);
        assertEquals(1, ship.getReservedComponents().size());
        assertEquals(c2, ship.getReservedComponents().getFirst());
        ship.unreserveComponent(c2);
        assertEquals(0, ship.getReservedComponents().size());
    }

    @RepeatedTest(500)
    void destroyComponent() throws JsonProcessingException {
        Random rand = new Random();
        int number = rand.nextInt(1, 7);
        GenerateRandomShip s = new GenerateRandomShip();
        s.addElementsShip();
        ship = s.getShip();

        boolean[][] vs = new boolean[12][12];
        for(int i = 0; i < 12; i++) {
            for(int j = 0; j < 12; j++) {
                vs[i][j] = false;
            }
        }
        vs[5][6] = true;
        vs[5][8] = true;
        for(int j = 5; j < 10; j++) {
            vs[6][j] = true;
        }
        for(int i = 4; i < 11; i++) {
            vs[7][i] = true;
            vs[8][i] = true;
        }
        for(int j = 4; j < 7; j++) {
            vs[9][j] = true;
        }
        for(int j = 8; j < 11; j++) {
            vs[9][j] = true;
        }

        for(int k = 0; k < number; k++){
            int i, j;
            Component c = null;
            int[] checkValues = new int[8];
            checkValues[0] = ship.getCrewNumber();
            checkValues[1] = ship.getGoodsValue();
            checkValues[2] = ship.getEnergyNumber();
            checkValues[3] = ship.getSingleEnginesStrength();
            checkValues[4] = ship.getDoubleEnginesStrength();
            checkValues[5] = (int) ship.getSingleCannonsStrength();
            checkValues[6] = ship.getDoubleCannonsStrength();
            checkValues[7] = ship.getDoubleCannonsNumber();

            do {
                do {
                    i = rand.nextInt(4, 9);
                    j = rand.nextInt(3, 10);
                } while (!vs[i][j]);

                if (ship.getComponent(i, j) != null) {
                    c = ship.getComponent(i, j);
                    ship.destroyComponent(i, j);
                }
            } while (c == null);

            switch (c.getComponentType()) {
                case CABIN, CENTER_CABIN:
                    Cabin c1 = (Cabin) c;
                    if(c1.hasPurpleAlien() || c1.hasBrownAlien()){
                        assertEquals(ship.getCrewNumber() + 1, checkValues[0]);
                    } else if(c1.getCrewNumber() > 0){
                        assertEquals(ship.getCrewNumber() + 2, checkValues[0]);
                    }
                    break;
                case STORAGE:
                    Storage s1 = (Storage) c;
                    assertEquals(ship.getGoodsValue() + s1.getGoodsValue(), checkValues[1]);
                    break;
                case BATTERY:
                    Battery b = (Battery) c;
                    assertEquals(ship.getEnergyNumber() + b.getEnergyNumber(), checkValues[2]);
                    break;
                case SINGLE_ENGINE:
                    Engine e2 = (Engine) c;
                    assertEquals(ship.getSingleEnginesStrength() + e2.getEngineStrength(), checkValues[3]);
                    break;
                case DOUBLE_ENGINE:
                    Engine e1 = (Engine) c;
                    assertEquals(ship.getDoubleEnginesStrength() + e1.getEngineStrength(), checkValues[4]);
                    break;
                case SINGLE_CANNON:
                    Cannon c3 = (Cannon) c;
                    assertEquals(ship.getSingleCannonsStrength() + c3.getCannonStrength(), checkValues[5]);
                    break;
                case DOUBLE_CANNON:
                    Cannon c2 = (Cannon) c;
                    assertEquals(ship.getDoubleCannonsStrength() + c2.getCannonStrength(), checkValues[6]);
                    assertEquals(ship.getDoubleCannonsNumber() + 1, checkValues[7]);
                    break;
                case BROWN_LIFE_SUPPORT:
                    if(ship.getCabins().isEmpty()){
                        assertFalse(ship.hasBrownAlien());
                    } else {
                        for(Cabin cabin : ship.getCabins().values()){
                            if(cabin.hasBrownAlien()){
                                assertTrue(ship.hasBrownAlien());
                            }
                        }
                    }
                    break;
                case PURPLE_LIFE_SUPPORT:
                    if(ship.getCabins().isEmpty()){
                        assertFalse(ship.hasPurpleAlien());
                    } else {
                        for(Cabin cabin : ship.getCabins().values()){
                            if(cabin.hasPurpleAlien()){
                                assertTrue(ship.hasPurpleAlien());
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @RepeatedTest(5)
    void placeComponent() throws JsonProcessingException {
        Random rand = new Random();
        Board b = new Board(Level.SECOND, null, null, null, null);
        boolean[][] vs = new boolean[12][12];
        for(int i = 0; i < 12; i++) {
            for(int j = 0; j < 12; j++) {
                vs[i][j] = true;
            }
        }
        SpaceShip s = new SpaceShip(Level.SECOND, vs);

        for(int i = 0; i < 4; i++){
            Component c = null;
            c = b.getTile(rand.nextInt(0, 156));
            ComponentType type = c.getComponentType();
            s.placeComponent(c, 6, 7 + i);

            assertEquals(type, s.getComponent(6, 7 + i).getComponentType());
            assertEquals(1 + 1 + i, s.getNumberOfComponents());
            assertEquals(c.getID(), s.getComponent(6, 7 + i).getID());
            assertEquals(c.getRow(), s.getComponent(6, 7 + i).getRow());
            assertEquals(c.getColumn(), s.getComponent(6, 7 + i).getColumn());
            assertEquals(c.getExposedConnectors(), s.getComponent(6, 7 + i).getExposedConnectors());
            assertEquals(c.getClockwiseRotation(), s.getComponent(6, 7 + i).getClockwiseRotation());

            switch (type) {
                case CABIN, CENTER_CABIN:
                    assertFalse(s.getCabins().isEmpty());
                    break;
                case STORAGE:
                    assertFalse(s.getStorages().isEmpty());
                    break;
                case BATTERY:
                    assertFalse(s.getBatteries().isEmpty());
                    break;
                case SINGLE_CANNON, DOUBLE_CANNON:
                    assertFalse(s.getCannons().isEmpty());
                    break;
                default:
                    break;
            }
        }
    }

    @RepeatedTest(5)
    void getCabin() {
        Random rand = new Random();
        int count = rand.nextInt(1, 4);
        for(int i = 0; i < count; i++){
            Cabin cabin = new Cabin(i + 4, connectors);
            ship.placeComponent(cabin, 6, 7 + i);
        }
        assertEquals(count + 1, ship.getCabins().size());
        for(int i = 0; i < count; i++){
            assertEquals(i + 4, ship.getCabin(i + 4).getID());
            assertEquals(ComponentType.CABIN, ship.getCabin(i + 4).getComponentType());
            assertEquals(ComponentType.CABIN, ship.getCabins().get(i + 4).getComponentType());
        }
        assertThrows((IllegalArgumentException.class), () -> ship.getCabin(100));
    }

    @RepeatedTest(5)
    void getStorage(){
        Random rand = new Random();
        int count = rand.nextInt(1, 4);
        for(int i = 0; i < count; i++){
            Storage storage = new Storage(i + 4, connectors, true, 2);
            ship.placeComponent(storage, 6, 7 + i);
        }
        assertEquals(count, ship.getStorages().size());
        for(int i = 0; i < count + 1; i++){
            if(i == count){
                assertThrows((IllegalArgumentException.class), () -> ship.getStorage(count));
                break;
            }
            assertEquals(i + 4, ship.getStorage(i + 4).getID());
            assertEquals(ComponentType.STORAGE, ship.getStorage(i + 4).getComponentType());
            assertEquals(ComponentType.STORAGE, ship.getStorages().get(i + 4).getComponentType());
        }
    }

    @RepeatedTest(5)
    void getBattery(){
        Random rand = new Random();
        int count = rand.nextInt(1, 4);
        for(int i = 0; i < count; i++){
            Battery battery = new Battery(i + 4, connectors, 3);
            ship.placeComponent(battery, 6, 7 + i);
        }
        assertEquals(count, ship.getBatteries().size());
        for(int i = 0; i < count + 1; i++){
            if(i == count){
                assertThrows((IllegalArgumentException.class), () -> ship.getBattery(count));
                break;
            }
            assertEquals(i + 4, ship.getBattery(i + 4).getID());
            assertEquals(ComponentType.BATTERY, ship.getBattery(i + 4).getComponentType());
            assertEquals(ComponentType.BATTERY, ship.getBatteries().get(i + 4).getComponentType());
        }
    }

    //TODO: Da capire come funziona il metodo e come testarlo (usare debugger)
    //TODO: creare classe per generare ship che restituisce il numero di connessioni non valide
    @RepeatedTest(5)
    void getDisconnectedComponentsTest() throws JsonProcessingException {
        //Restituisce i pezzi disconnessi

        /*
        Prendo una nave, la massacro di colpi e vedo i pezzi distrutti
        Restituisce anche i pezzi non validi ( non connessi correttamente )

        1. Colpiti
        2. Connessi male
        3. Staccati


        Random rand = new Random();

        GenerateRandomShip rs = new GenerateRandomShip();
        rs.addElementsShip();
        ship = rs.getShip();

        for(int i = 0; i < 5; i++){
            HitType hitType = HitType.HEAVYFIRE;
            int dice = rand.nextInt(6, 10);
            Direction[] values = Direction.values();
            Direction randomDirection = values[rand.nextInt(values.length)];
            Hit hit = new Hit(hitType, randomDirection);

            Pair<Component, Integer> resultHit = ship.canProtect(dice, hit);

            ship.destroyComponent(resultHit.getValue0().getRow(), resultHit.getValue0().getColumn());

            ArrayList<ArrayList<Pair<Integer, Integer>>> result = ship.getDisconnectedComponents();
        }

        assertEquals(5, ship.getDisconnectedComponents().size());
    }
*/
}