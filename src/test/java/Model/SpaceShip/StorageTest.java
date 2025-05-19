package Model.SpaceShip;

import Model.Game.Board.Level;
import Model.Good.Good;
import Model.Good.GoodType;
import Model.Player.PlayerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class StorageTest {
    Storage s;
    ConnectorType[] connectors;
    Field goodsField = Storage.class.getDeclaredField("goods");
    Field goodsValueField = Storage.class.getDeclaredField("goodsValue");
    Field dangerousField = Storage.class.getDeclaredField("dangerous");
    Field goodsCapacityField = Storage.class.getDeclaredField("goodsCapacity");
    Field connectorsField = Component.class.getDeclaredField("connectors");
    Field clockwiseRotationField = Component.class.getDeclaredField("clockwiseRotation");
    Field fixedField = Component.class.getDeclaredField("fixed");
    Field shipField = Component.class.getDeclaredField("ship");
    Field IDField = Component.class.getDeclaredField("ID");
    Field rowField = Component.class.getDeclaredField("row");
    Field columnField = Component.class.getDeclaredField("column");

    StorageTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() {
        connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        s = new Storage(0, connectors, true, 3);
        assertNotNull(s);
        goodsField.setAccessible(true);
        goodsValueField.setAccessible(true);
        dangerousField.setAccessible(true);
        goodsCapacityField.setAccessible(true);
        connectorsField.setAccessible(true);
        clockwiseRotationField.setAccessible(true);
        fixedField.setAccessible(true);
        shipField.setAccessible(true);
        IDField.setAccessible(true);
        rowField.setAccessible(true);
        columnField.setAccessible(true);
    }

    @Test
    void testStorageConstructor() throws IllegalAccessException {
        Storage a = new Storage();
        assertNotNull(a);
        assertEquals(0, IDField.get(a));
        assertEquals(0, goodsCapacityField.get(a));
        assertFalse((boolean) dangerousField.get(a));
    }

    @RepeatedTest(5)
    void isDangerous_returnsTrueForDangerousStorage() throws IllegalAccessException {
        Storage storage = new Storage(1, connectors, true, 3);
        assertTrue((boolean) dangerousField.get(storage));
    }

    @RepeatedTest(5)
    void isDangerous_returnsFalseForNonDangerousStorage() throws IllegalAccessException {
        Storage storage = new Storage(1, connectors, false, 3);
        assertFalse((boolean) dangerousField.get(storage));
    }

    @RepeatedTest(5)
    void getGoodsCapacity_returnsCorrectCapacity() throws IllegalAccessException {
        Storage storage = new Storage(1, connectors, true, 3);
        assertEquals(3, goodsCapacityField.get(storage));
    }

    @RepeatedTest(5)
    void addGood_addsGoodToStorage() throws IllegalAccessException {
        Storage storage = new Storage(1, connectors, true, 3);
        Good good = new Good(GoodType.GREEN);
        storage.addGood(good);
        ArrayList<Good> goods = (ArrayList<Good>) goodsField.get(storage);
        assertTrue(goods.contains(good));
    }

    @RepeatedTest(5)
    void addGood_whenStorageIsFull() {
        Storage storage = new Storage(1, connectors, true, 1);
        Good good1 = new Good(GoodType.GREEN);
        Good good2 = new Good(GoodType.YELLOW);
        storage.addGood(good1);
        assertThrows(IllegalStateException.class, () -> storage.addGood(good2));
    }

    @RepeatedTest(5)
    void addGood_whenAddingRedGoodToNonDangerousStorage() {
        Storage storage = new Storage(1, connectors, false, 3);
        Good good = new Good(GoodType.RED);
        assertThrows(IllegalStateException.class, () -> storage.addGood(good));
    }

    @RepeatedTest(5)
    void addGood_increasesGoodsValue() throws IllegalAccessException {
        Storage storage = new Storage(1, connectors, true, 3);
        Good good = new Good(GoodType.GREEN);
        int initialGoodsValue = (int) goodsValueField.get(storage);
        storage.addGood(good);
        assertEquals(initialGoodsValue + good.getValue(), goodsValueField.get(storage));
    }

    @RepeatedTest(5)
    void removeGood_removesGoodFromStorage() throws IllegalAccessException {
        Storage storage = new Storage(1, connectors, true, 3);
        Good good = new Good(GoodType.GREEN);
        storage.addGood(good);
        storage.removeGood(good);
        ArrayList<Good> goods = (ArrayList<Good>) goodsField.get(storage);
        assertFalse(goods.contains(good));
    }

    @RepeatedTest(5)
    void removeGood_whenGoodNotFound() {
        Storage storage = new Storage(1, connectors, true, 3);
        Good good = new Good(GoodType.GREEN);
        assertThrows(IllegalStateException.class, () -> storage.removeGood(good));
    }

    @RepeatedTest(5)
    void removeGood_decreasesGoodsValue() throws IllegalAccessException {
        Storage storage = new Storage(1, connectors, true, 3);
        Good good = new Good(GoodType.GREEN);
        storage.addGood(good);
        int initialGoodsValue = (int) goodsValueField.get(storage);
        storage.removeGood(good);
        assertEquals(initialGoodsValue - good.getValue(), goodsValueField.get(storage));
    }

    @RepeatedTest(5)
    void getGoods_returnsEmptyListWhenNoGoodsAdded() throws IllegalAccessException {
        Storage storage = new Storage(1, connectors, true, 3);
        ArrayList<Good> goods = (ArrayList<Good>) goodsField.get(storage);
        assertTrue(goods.isEmpty());
    }

    @RepeatedTest(5)
    void getGoods_returnsListWithAddedGoods() throws IllegalAccessException {
        Storage storage = new Storage(1, connectors, true, 3);
        Good good1 = new Good(GoodType.GREEN);
        Good good2 = new Good(GoodType.YELLOW);
        storage.addGood(good1);
        storage.addGood(good2);
        ArrayList<Good> goods = (ArrayList<Good>) goodsField.get(storage);
        assertEquals(2, goods.size());
        assertTrue(goods.contains(good1));
        assertTrue(goods.contains(good2));
    }

    @RepeatedTest(5)
    void getGoods_returnsListWithoutRemovedGoods() throws IllegalAccessException {
        Storage storage = new Storage(1, connectors, true, 3);
        Good good1 = new Good(GoodType.GREEN);
        Good good2 = new Good(GoodType.YELLOW);
        storage.addGood(good1);
        storage.addGood(good2);
        storage.removeGood(good1);
        ArrayList<Good> goods = (ArrayList<Good>) goodsField.get(storage);
        assertEquals(1, goods.size());
        assertFalse(goods.contains(good1));
        assertTrue(goods.contains(good2));
    }

    @RepeatedTest(5)
    void getGoodsValue_returnsZeroWhenNoGoodsAdded() throws IllegalAccessException {
        Storage storage = new Storage(1, connectors, true, 3);
        assertEquals(0, goodsValueField.get(storage));
    }

    @RepeatedTest(5)
    void getGoodsValue_returnsCorrectValueAfterAddingGoods() throws IllegalAccessException {
        Storage storage = new Storage(1, connectors, true, 3);
        Good good1 = new Good(GoodType.GREEN);
        Good good2 = new Good(GoodType.YELLOW);
        storage.addGood(good1);
        storage.addGood(good2);
        assertEquals(good1.getValue() + good2.getValue(), goodsValueField.get(storage));
    }

    @RepeatedTest(5)
    void getGoodsValue_returnsCorrectValueAfterRemovingGoods() throws IllegalAccessException {
        Storage storage = new Storage(1, connectors, true, 3);
        Good good1 = new Good(GoodType.GREEN);
        Good good2 = new Good(GoodType.YELLOW);
        storage.addGood(good1);
        storage.addGood(good2);
        storage.removeGood(good1);
        assertEquals(good2.getValue(), goodsValueField.get(storage));
    }

    @RepeatedTest(5)
    void getGoodsValue_returnsZeroAfterRemovingAllGoods() throws IllegalAccessException {
        Storage storage = new Storage(1, connectors, true, 3);
        Good good1 = new Good(GoodType.GREEN);
        Good good2 = new Good(GoodType.YELLOW);
        storage.addGood(good1);
        storage.addGood(good2);
        storage.removeGood(good1);
        storage.removeGood(good2);
        assertEquals(0, goodsValueField.get(storage));
    }

    @RepeatedTest(5)
    void getComponentType_returnsStorage() {
        Storage storage = new Storage(1, connectors, true, 3);
        assertEquals(ComponentType.STORAGE, storage.getComponentType());
    }

    @RepeatedTest(5)
    void getComponentType_withDifferentIDs() {
        Storage storage1 = new Storage(1, connectors, true, 3);
        Storage storage2 = new Storage(2, connectors, false, 2);
        assertEquals(ComponentType.STORAGE, storage1.getComponentType());
        assertEquals(ComponentType.STORAGE, storage2.getComponentType());
    }

    @RepeatedTest(5)
    void getConnection_northFace() {
        ConnectorType[] connectors = {ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY};
        Component component = new Storage(1, connectors, true, 3);
        assertEquals(ConnectorType.SINGLE, component.getConnection(0));
    }

    @RepeatedTest(5)
    void getConnection_westFace() {
        ConnectorType[] connectors = {ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.EMPTY};
        Component component = new Storage(1, connectors, true, 3);
        assertEquals(ConnectorType.SINGLE, component.getConnection(1));
    }

    @RepeatedTest(5)
    void getConnection_southFace() {
        ConnectorType[] connectors = {ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.EMPTY};
        Component component = new Storage(1, connectors, true, 3);
        assertEquals(ConnectorType.SINGLE, component.getConnection(2));
    }

    @RepeatedTest(5)
    void getConnection_eastFace() {
        ConnectorType[] connectors = {ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.SINGLE};
        Component component = new Storage(1, connectors, true, 3);
        assertEquals(ConnectorType.SINGLE, component.getConnection(3));
    }

    @RepeatedTest(5)
    void getConnection_afterRotation() {
        ConnectorType[] connectors1 = {ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.DOUBLE, ConnectorType.EMPTY};
        Component component = new Storage(1, connectors1, true, 3);
        component.rotateClockwise();
        assertEquals(ConnectorType.EMPTY, component.getConnection(0));
        assertEquals(ConnectorType.DOUBLE, component.getConnection(1));
    }

    @RepeatedTest(5)
    void getClockwiseRotation_initialValue() throws IllegalAccessException {
        Component component = new Storage(1, connectors, true, 3);
        assertEquals(0, clockwiseRotationField.get(component));
    }

    @RepeatedTest(5)
    void getClockwiseRotation_afterOneRotation() throws IllegalAccessException {
        Component component = new Storage(1, connectors, true, 3);
        component.rotateClockwise();
        assertEquals(1, clockwiseRotationField.get(component));
    }

    @RepeatedTest(5)
    void getClockwiseRotation_afterMultipleRotations() throws IllegalAccessException {
        Component component = new Storage(1, connectors, true, 3);
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        assertEquals(3, clockwiseRotationField.get(component));
    }

    @RepeatedTest(5)
    void getClockwiseRotation_fullRotation() throws IllegalAccessException {
        Component component = new Storage(1, connectors, true, 3);
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        assertEquals(0, clockwiseRotationField.get(component));
    }

    @RepeatedTest(5)
    void getID_returnsCorrectID() throws IllegalAccessException {
        Component component = new Storage(1, connectors, true, 3);
        assertEquals(1, IDField.get(component));
    }

    @RepeatedTest(5)
    void getID_differentID() throws IllegalAccessException {
        Component component = new Storage(2, connectors, true, 3);
        assertEquals(2, IDField.get(component));
    }

    @RepeatedTest(5)
    void rotateClockwise_multipleFullRotations() throws IllegalAccessException {
        Component component = new Storage(1, connectors, true, 3);
        for (int i = 0; i < 8; i++) {
            component.rotateClockwise();
        }
        assertEquals(0, clockwiseRotationField.get(component));
    }

    @RepeatedTest(5)
    void getExposedConnectors_whenAttachedToShip() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Component component = new Storage(1, connectors, true, 3);
        ship.placeComponent(component, 6, 7);
        assertEquals(3, component.getExposedConnectors());
    }

    @RepeatedTest(5)
    void getExposedConnectors_whenNotAttachedToShip_throwsException() {
        Component component = new Storage(1, connectors, true, 3);
        assertThrows(IllegalStateException.class, component::getExposedConnectors);
    }

    @RepeatedTest(5)
    void getExposedConnectors_withSurroundingComponents() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Component component = new Storage(1, connectors, true, 3);
        Component adjacentComponent = new Storage(2, connectors, true, 3);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertEquals(2, component.getExposedConnectors());
    }

    @RepeatedTest(5)
    void isConnected_withAdjacentComponent() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Component component = new Storage(1, connectors, true, 3);
        Component adjacentComponent = new Storage(2, connectors, true, 3);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertTrue(component.isConnected(6, 7));
    }

    @RepeatedTest(5)
    void isConnected_withMultipleAdjacentComponents() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Component component = new Storage(1, connectors, true, 3);
        Component adjacentComponent1 = new Storage(2, connectors, true, 3);
        Component adjacentComponent2 = new Storage(3, connectors, true, 3);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent1, 6, 8);
        ship.placeComponent(adjacentComponent2, 5, 7);
        assertTrue(component.isConnected(6, 7));
    }

    @RepeatedTest(5)
    void isFixed_initiallyFalse() throws IllegalAccessException {
        Component component = new Storage(1, connectors, true, 3);
        assertFalse((boolean) fixedField.get(component));
    }

    @RepeatedTest(5)
    void isFixed_afterFixing() throws IllegalAccessException {
        Component component = new Storage(1, connectors, true, 3);
        component.fix();
        assertTrue((boolean) fixedField.get(component));
    }

    @RepeatedTest(5)
    void isFixed_afterMultipleFixCalls() throws IllegalAccessException {
        Component component = new Storage(1, connectors, true, 3);
        component.fix();
        component.fix();
        assertTrue((boolean) fixedField.get(component));
    }

    @RepeatedTest(5)
    void fix_setsFixedToTrue() throws IllegalAccessException {
        Component component = new Storage(1, connectors, true, 3);
        component.fix();
        assertTrue((boolean) fixedField.get(component));
    }

    @RepeatedTest(5)
    void fix_doesNotChangeFixedStateIfAlreadyFixed() throws IllegalAccessException {
        Component component = new Storage(1, connectors, true, 3);
        component.fix();
        component.fix();
        assertTrue((boolean) fixedField.get(component));
    }

    @RepeatedTest(5)
    void isValid_withAllValidConnections() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        ConnectorType[] connectors = {ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE};
        Component component = new Storage(1, connectors, true, 3);
        Component adjacentComponent = new Storage(2, connectors, true, 3);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertTrue(component.isValid());
    }

    @RepeatedTest(5)
    void isValid_withInvalidConnections() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        ConnectorType[] connectors = {ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.EMPTY};
        Component component = new Storage(1, connectors, true, 3);
        Component adjacentComponent = new Storage(2, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE}, true, 3);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertFalse(component.isValid());
    }

    @RepeatedTest(5)
    void isValid_withTripleConnector() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        ConnectorType[] connectors = {ConnectorType.TRIPLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE};
        Component component = new Storage(1, connectors, true, 3);
        Component adjacentComponent = new Storage(2, new ConnectorType[]{ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.TRIPLE, ConnectorType.SINGLE}, true, 3);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertTrue(component.isValid());
    }

    @RepeatedTest(5)
    void isValid_withMixedConnections() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        ConnectorType[] connectors = {ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.SINGLE};
        Component component = new Storage(1, connectors, true, 3);
        Component adjacentComponent1 = new Storage(2, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE}, true, 3);
        Component adjacentComponent2 = new Storage(3, new ConnectorType[]{ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE}, true, 2);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent1, 6, 8);
        ship.placeComponent(adjacentComponent2, 5, 7);
        assertFalse(component.isValid());
    }

/*
    @RepeatedTest(10)
    void getComponentType() {
        s = new Storage(0, connectors, true, 2);
        ComponentType type = s.getComponentType();
        System.out.println(type);

        assertEquals(ComponentType.STORAGE, type);
    }

    @RepeatedTest(5)
    void getIDTest(){
        assertEquals(0, s.getID());

        Random rand = new Random();
        int id = rand.nextInt(4,9);
        Storage s = new Storage(id, connectors, true, 2);

        assertEquals(id, s.getID());
    }

    @RepeatedTest(5)
    void getConnectionTest(){
        Random rand = new Random();
        ConnectorType[] values = ConnectorType.values();
        ConnectorType[] connectorArray = new ConnectorType[4];
        ConnectorType[] check = new ConnectorType[4];
        for(int i = 0; i < 4; i++){
            ConnectorType randomType = values[rand.nextInt(values.length)];
            System.out.println(randomType);
            connectorArray[i] = randomType;
            check[i] = randomType;
        }

        Storage s = new Storage(1, connectorArray, true, 2);

        for(int k = 0; k < 4; k++){
            assertEquals(s.getConnection(k), check[k]);
        }
    }

    @RepeatedTest(5)
    void getClockwiseRotation() {
        assertEquals(0, s.getClockwiseRotation());

        Random rand = new Random();
        int r = rand.nextInt(4);

        for(int k = 0; k < r; k++){
            s.rotateClockwise();
        }
        assertEquals(r, s.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateClockwise() {
        Random rand = new Random();
        int r = rand.nextInt(4);

        int a = s.getClockwiseRotation() + r;

        for(int k = 0; k < r; k++){
            s.rotateClockwise();
        }

        int b = s.getClockwiseRotation();
        assertEquals(a, b);
    }

    @RepeatedTest(5)
    void getExposedConnectorsTest(){
        Random rand = new Random();
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        ConnectorType[] values = ConnectorType.values();
        ConnectorType[] connectorArray = new ConnectorType[4];
        int exposed = 0;
        for(int i = 0; i < 4; i++){
            ConnectorType randomType = values[rand.nextInt(values.length)];
            System.out.println(randomType);
            connectorArray[i] = randomType;
            if(randomType != ConnectorType.EMPTY){
                exposed++;
            }
        }

        Storage s = new Storage(1, connectorArray, true, 2);
        ship.placeComponent(s, 6, 7);
        if(s.getConnection(2) != ConnectorType.EMPTY){
            exposed--;
        }
        System.out.println(exposed);
        assertEquals(exposed, s.getExposedConnectors());

        Storage s1 = new Storage(2, connectorArray, true, 3);
        ship.placeComponent(s1, 6, 8);
        if(s.getConnection(3) != ConnectorType.EMPTY){
            exposed--;
        }
        System.out.println(exposed);
        assertEquals(exposed, s.getExposedConnectors());
    }

    @RepeatedTest(5)
    void isConnectedTest(){
        SpaceShip ship;
        boolean[][] spots;
        spots = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                spots[i][j] = true;
            }
        }
        ship = new SpaceShip(Level.SECOND, spots);

        Random rand = new Random();
        int i = rand.nextInt(5);
        for(int j = 0; j < i; j++) {
            ConnectorType[] values = ConnectorType.values();
            ConnectorType[] connector = new ConnectorType[4];
            for(int k = 0; k < 4; k++){
                ConnectorType randomType = values[rand.nextInt(values.length)];
                System.out.println(randomType);
                connector[k] = randomType;
            }

            Storage s = new Storage(1, connector, true, 2);

            int r = rand.nextInt(4) + 1;
            for(int p = 0; p < r; p++){
                s.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            ship.placeComponent(s, 6, 7 + j);

            assertTrue(s.isConnected(6, 7 + j));
        }
    }

    //Test for the methods isFixed and fix
    @RepeatedTest(5)
    void isFixedTest(){
        assertFalse(s.isFixed());

        Random rand = new Random();
        int count = rand.nextInt(4) + 1;
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Storage[] ss = new Storage[count];
        int i = 0;
        int j;
        for(j = 0; j < count; j++){
            ss[j] = new Storage(j, connectors, true, 2);
            System.out.println(ss[j]);

            ship.placeComponent(ss[j], 6, 7 + j);

            if(j > 0){
                ss[i].fix();
                assertTrue(ss[i].isFixed());
                i++;
            }

            assertFalse(ss[j].isFixed());
        }
    }

    @RepeatedTest(500)
    void isValidTest() {
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);

        Random rand = new Random();
        ConnectorType[] values = ConnectorType.values();
        ConnectorType[] connectors1 = new ConnectorType[4];
        ConnectorType[] connectors2 = new ConnectorType[4];
        for(int i = 0; i < 4; i++){
            ConnectorType randomType = values[rand.nextInt(values.length)];
            connectors1[i] = randomType;
            System.out.println(randomType);
        }
        System.out.println();
        for(int i = 0; i < 4; i++){
            ConnectorType randomType = values[rand.nextInt(values.length)];
            connectors2[i] = randomType;
            System.out.println(randomType);
        }

        Storage storage1 = new Storage(1, connectors1, true, 2);
        Storage storage2 = new Storage(2, connectors2, true, 3);

        ship.placeComponent(storage1, 6, 7);
        System.out.println(storage1.isValid());

        if(storage1.getConnection(2) != ConnectorType.EMPTY){
            assertTrue(storage1.isValid());
        } else {
            assertFalse(storage1.isValid());
            return;
        }

        ship.placeComponent(storage2, 6, 8);
        System.out.println(storage1.isValid());

        if((storage1.getConnection(3) == ConnectorType.EMPTY && storage2.getConnection(1) != ConnectorType.EMPTY) ||
                (storage1.getConnection(3) == ConnectorType.SINGLE && storage2.getConnection(1) != ConnectorType.SINGLE &&
                        storage2.getConnection(1) != ConnectorType.TRIPLE) ||
                (storage1.getConnection(3) == ConnectorType.DOUBLE && storage2.getConnection(1) != ConnectorType.DOUBLE &&
                        storage2.getConnection(1) != ConnectorType.TRIPLE) ||
                (storage1.getConnection(3) == ConnectorType.TRIPLE && storage2.getConnection(1) == ConnectorType.EMPTY)){
            assertFalse(storage1.isValid());
        } else {
            assertTrue(storage1.isValid());
        }
    }

    @RepeatedTest(5)
    void isDangerous() {
        Random rand = new Random();
        boolean isDangerous = rand.nextBoolean();
        Storage s = new Storage(1, connectors, isDangerous, 2);
        if(isDangerous){
            assertTrue(s.isDangerous());
        } else {
            assertFalse(s.isDangerous());
        }
    }

    //test also for getGoods and getGoodsValue
    @RepeatedTest(5)
    void addGood() {
        Random rand = new Random();
        int value = 0;
        boolean dangerous = rand.nextBoolean();
        int capacity = rand.nextInt(1, 4);

        ArrayList<Good> check = new ArrayList<>();
        Storage s = new Storage(1, connectors, dangerous, capacity);
        for(int i = 0; i < capacity; i++){
            GoodType[] values = GoodType.values();
            GoodType randType = values[rand.nextInt(values.length)];
            System.out.println(randType);
            Good good = new Good(randType);

            if(dangerous){
                s.addGood(good);
                if(randType == GoodType.RED){
                    value += 4;
                } else if(randType == GoodType.YELLOW){
                    value += 3;
                } else if(randType == GoodType.GREEN){
                    value += 2;
                } else {
                    value += 1;
                }
                check.add(good);
            } else {
                if(good.getColor() != GoodType.RED){
                    s.addGood(good);
                    if(randType == GoodType.RED){
                        value += 4;
                    } else if(randType == GoodType.YELLOW){
                        value += 3;
                    } else if(randType == GoodType.GREEN){
                        value += 2;
                    } else {
                        value += 1;
                    }
                    check.add(good);
                }
            }
        }

        for(int j = 0; j < s.getGoods().size(); j++){
            assertEquals(check.get(j), s.getGoods().get(j));
        }
        assertEquals(value, s.getGoodsValue());
    }

    @RepeatedTest(5)
    void removeGood() {
        Random rand = new Random();
        int value = 0;
        boolean dangerous = rand.nextBoolean();
        System.out.println(dangerous);
        int capacity = rand.nextInt(1, 4);
        Storage s = new Storage(1, connectors, dangerous, capacity);
        for(int i = 0; i < capacity; i++){
            GoodType[] values = GoodType.values();
            GoodType randType = values[rand.nextInt(values.length)];
            System.out.println(randType);
            Good good = new Good(randType);

            if(dangerous){
                s.addGood(good);
                if(randType == GoodType.RED){
                    value += 4;
                } else if(randType == GoodType.YELLOW){
                    value += 3;
                } else if(randType == GoodType.GREEN){
                    value += 2;
                } else {
                    value += 1;
                }
            } else {
                if(good.getColor() != GoodType.RED){
                    s.addGood(good);
                    if(randType == GoodType.YELLOW){
                        value += 3;
                    } else if(randType == GoodType.GREEN){
                        value += 2;
                    } else {
                        value += 1;
                    }
                }
            }
        }
        System.out.println(value);

        int numberGoods = s.getGoods().size();
        System.out.println(numberGoods);

        while(!s.getGoods().isEmpty()){
            if(s.getGoods().getFirst().getColor() == GoodType.RED){
                value -= 4;
            } else if(s.getGoods().getFirst().getColor() == GoodType.YELLOW){
                value -= 3;
            } else if(s.getGoods().getFirst().getColor() == GoodType.GREEN){
                value -= 2;
            } else {
                value -= 1;
            }

            System.out.println();
            System.out.println(value);
            s.removeGood(s.getGoods().getFirst());
            System.out.println(s.getGoods().size());
            assertEquals(value, s.getGoodsValue());
        }
        assertEquals(0, s.getGoodsValue());
        assertEquals(0, s.getGoods().size());
        assertThrows((IllegalStateException.class), () -> s.removeGood(new Good(GoodType.RED)));
    }

 */
}