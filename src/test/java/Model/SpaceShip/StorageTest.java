package Model.SpaceShip;

import Model.Game.Board.Level;
import Model.Good.Good;
import Model.Good.GoodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class StorageTest {
    Storage s;
    ConnectorType[] connectors;

    @BeforeEach
    void setUp() {
        connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        s = new Storage(0, connectors, true, 3);
        assertNotNull(s, "Component not initialized correctly");
    }

    @RepeatedTest(10)
    void getComponentType() {
        s = new Storage(0, connectors, true, 2);
        ComponentType type = s.getComponentType();
        System.out.println(type);

        assertEquals(ComponentType.STORAGE, type);
    }

    @RepeatedTest(5)
    void getRowTest(){
        assertEquals(6, s.getRow());

        Random rand = new Random();
        int r = rand.nextInt(4,9);
        Storage s = new Storage(1, connectors, true, 2);

        assertEquals(r, s.getRow());
    }

    @RepeatedTest(5)
    void getColumnTest(){
        assertEquals(7, s.getColumn());

        Random rand = new Random();
        int c = rand.nextInt(5,9);
        Storage s = new Storage(1, connectors, true, 3);

        assertEquals(c, s.getColumn());
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

    //TODO: finire il metodo quando implementano il metodo di spostare i componenti
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

    @RepeatedTest(5)
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

    @RepeatedTest(5)
    void exchangeGood() {
        Random rand = new Random();
        Storage s = new Storage(1, connectors, true, 4);
        ArrayList<Good> goodsToAdd = new ArrayList<>();
        ArrayList<Good> goodsToRemove = new ArrayList<>();
        Good[] minGood = null;
        for(int i = 0; i < 4; i++){
            GoodType[] values = GoodType.values();
            GoodType randType = values[rand.nextInt(values.length)];
            Good good = new Good(randType);
            System.out.println(good.getColor());
            s.addGood(good);
            if(i == 0){
                minGood = new Good[2];
                minGood[0] = good;
            } else if(i == 1){
                minGood[1] = good;
            } else {
                boolean done = false;
                if(good.getValue() < minGood[0].getValue()){
                    minGood[0] = good;
                    done = true;
                }
                if(good.getValue() < minGood[1].getValue() && !done) {
                    minGood[1] = good;
                }
            }
        }
        System.out.println();

        goodsToRemove.add(minGood[0]);
        goodsToRemove.add(minGood[1]);

        for (int i = 0; i < 2; i++) {
            GoodType[] values = GoodType.values();
            GoodType randType = values[rand.nextInt(values.length)];
            Good good = new Good(randType);
            System.out.println(good.getColor());
            goodsToAdd.add(good);
        }

        System.out.println();
        System.out.println(goodsToAdd.get(0).getColor() + " " + goodsToAdd.get(1).getColor());
        System.out.println(goodsToRemove.get(0).getColor() + " " + goodsToRemove.get(1).getColor());
        System.out.println();

        s.exchangeGood(goodsToAdd, goodsToRemove);

        System.out.println(s.getGoods().get(0).getColor() + " " + s.getGoods().get(1).getColor() + " " + s.getGoods().get(2).getColor() + " " + s.getGoods().get(3).getColor());

        for (Good good : goodsToAdd) {
            assertTrue(s.getGoods().contains(good));
        }
        for (Good good : goodsToRemove) {
            assertFalse(s.getGoods().contains(good));
        }
    }
}