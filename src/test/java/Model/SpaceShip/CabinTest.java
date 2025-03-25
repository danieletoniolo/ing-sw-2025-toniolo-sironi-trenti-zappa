package Model.SpaceShip;

import Model.Game.Board.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CabinTest {
    Cabin c;
    ConnectorType[] connectors;

    @BeforeEach
    void setUp() {
        connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        c = new Cabin(0, connectors);
        assertNotNull(c);
    }

    @RepeatedTest(10)
    void getComponentType() {
        c = new Cabin(0,  connectors);
        ComponentType type = c.getComponentType();
        System.out.println(type);

        assertEquals(ComponentType.CABIN, type);
    }

    @RepeatedTest(5)
    void getRowTest(){
        assertEquals(6, c.getRow());

        Random rand = new Random();
        int r = rand.nextInt(4,9);
        Cabin cabin = new Cabin(1, connectors);

        assertEquals(r, cabin.getRow());
    }

    @RepeatedTest(5)
    void getColumnTest(){
        assertEquals(7, c.getColumn());

        Random rand = new Random();
        int c = rand.nextInt(5,9);
        Cabin cabin = new Cabin(1, connectors);

        assertEquals(c, cabin.getColumn());
    }

    @RepeatedTest(5)
    void getIDTest(){
        assertEquals(0, c.getID());

        Random rand = new Random();
        int id = rand.nextInt(4,9);
        Cabin cabin = new Cabin(id, connectors);

        assertEquals(id, cabin.getID());
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

        Cabin cabin = new Cabin(1, connectorArray);

        for(int k = 0; k < 4; k++){
            assertEquals(cabin.getConnection(k), check[k]);
        }
    }

    @RepeatedTest(5)
    void getClockwiseRotation() {
        assertEquals(0, c.getClockwiseRotation());

        Random rand = new Random();
        int r = rand.nextInt(4);

        for(int k = 0; k < r; k++){
            c.rotateClockwise();
        }
        assertEquals(r, c.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateClockwise() {
        Random rand = new Random();
        int r = rand.nextInt(4);

        int a = c.getClockwiseRotation() + r;

        for(int k = 0; k < r; k++){
            c.rotateClockwise();
        }

        int b = c.getClockwiseRotation();
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

        Cabin cabin = new Cabin(1, connectorArray);
        ship.placeComponent(cabin, 6, 7);
        if(cabin.getConnection(2) != ConnectorType.EMPTY){
            exposed--;
        }
        System.out.println(exposed);
        assertEquals(exposed, cabin.getExposedConnectors());

        Cabin cabin1 = new Cabin(2, connectorArray);
        ship.placeComponent(cabin1, 6, 8);
        if(cabin.getConnection(3) != ConnectorType.EMPTY){
            exposed--;
        }
        System.out.println(exposed);
        assertEquals(exposed, cabin.getExposedConnectors());
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

            Cabin cabin = new Cabin(1, connector);

            int r = rand.nextInt(4) + 1;
            for(int p = 0; p < r; p++){
                cabin.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            ship.placeComponent(cabin, 6, 7 + j);

            assertTrue(cabin.isConnected(6, 7 + j));
        }
    }

    //TODO: finire il metodo quando implementano il metodo di spostare i componenti
    //Test for the methods isFixed and fix
    @RepeatedTest(5)
    void isFixedTest(){
        assertFalse(c.isFixed());

        Random rand = new Random();
        int count = rand.nextInt(4) + 1;
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Cabin[] cs = new Cabin[count];
        int i = 0;
        int j;
        for(j = 0; j < count; j++){
            cs[j] = new Cabin(j, connectors);
            System.out.println(cs[j]);

            ship.placeComponent(cs[j], 6, 7 + j);

            if(j > 0){
                cs[i].fix();
                assertTrue(cs[i].isFixed());
                i++;
            }

            assertFalse(cs[j].isFixed());
        }
    }

    @RepeatedTest(1000)
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

        Cabin cabin1 = new Cabin(1,  connectors1);
        Cabin cabin2 = new Cabin(2,  connectors2);

        ship.placeComponent(cabin1, 6, 7);
        System.out.println(cabin1.isValid());

        if(cabin1.getConnection(2) != ConnectorType.EMPTY){
            assertTrue(cabin1.isValid());
        } else {
            assertFalse(cabin1.isValid());
            return;
        }

        ship.placeComponent(cabin2, 6, 8);
        System.out.println(ship.getSurroundingComponents(6, 7));
        System.out.println(ship.getSurroundingComponents(6, 8));
        System.out.println(cabin1.isValid());

        if((cabin1.getConnection(3) == ConnectorType.EMPTY && cabin2.getConnection(1) != ConnectorType.EMPTY) ||
                (cabin1.getConnection(3) == ConnectorType.SINGLE && cabin2.getConnection(1) != ConnectorType.SINGLE &&
                        cabin2.getConnection(1) != ConnectorType.TRIPLE) ||
                (cabin1.getConnection(3) == ConnectorType.DOUBLE && cabin2.getConnection(1) != ConnectorType.DOUBLE &&
                        cabin2.getConnection(1) != ConnectorType.TRIPLE) ||
                (cabin1.getConnection(3) == ConnectorType.TRIPLE && cabin2.getConnection(1) == ConnectorType.EMPTY)){
            assertFalse(cabin1.isValid());
        } else {
            assertTrue(cabin1.isValid());
        }
    }

    @RepeatedTest(5)
    void getCrewNumber() {
        Random rand = new Random();
        int count = rand.nextInt(4) + 1;
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        for(int i = 0; i < count; i++){
            Cabin cabin = new Cabin(i, connectors);
            ship.placeComponent(cabin, 6, 7 + i);

            cabin.addCrewMember();
            assertEquals(2, cabin.getCrewNumber());

            boolean bool = rand.nextBoolean();
            if(bool){
                cabin.removeCrewMember(1);
            } else {
                cabin.removeCrewMember(2);
            }

            if(bool){
                assertEquals(1, cabin.getCrewNumber());
            } else {
                assertEquals(0, cabin.getCrewNumber());
            }
        }

        Cabin cabin1 = new Cabin(1, connectors);
        ship.placeComponent(cabin1, 8, 7);
        cabin1.addPurpleLifeSupport();
        cabin1.addPurpleAlien();
        assertEquals(1, cabin1.getCrewNumber());
        cabin1.removeCrewMember(1);
        assertEquals(0, cabin1.getCrewNumber());
        assertThrows((IllegalStateException.class), () -> cabin1.removeCrewMember(1));
    }

    //TODO: Da controllare che nel model abbiano cambiato il metodo, pk non controllano che ci sia un life support accanto
    @Test
    void hasPurpleLifeSupport() {
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Cabin cabin = new Cabin(1, connectors);
        assertFalse(cabin.hasPurpleLifeSupport());

        LifeSupportPurple c = new LifeSupportPurple(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(c, 6, 8);

        cabin.addPurpleLifeSupport();
        assertTrue(cabin.hasPurpleLifeSupport());
    }

    @Test
    void hasBrownLifeSupport() {
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Cabin cabin = new Cabin(1, connectors);
        assertFalse(cabin.hasBrownLifeSupport());

        LifeSupportBrown c = new LifeSupportBrown(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(c, 6, 8);

        cabin.addBrownLifeSupport();
        assertTrue(cabin.hasBrownLifeSupport());
    }

    @RepeatedTest(5)
    void hasPurpleAlien() {
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Cabin cabin = new Cabin(1, connectors);
        assertFalse(cabin.hasPurpleAlien());

        LifeSupportPurple c = new LifeSupportPurple(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(c, 6, 8);

        cabin.addPurpleLifeSupport();
        cabin.addPurpleAlien();

        assertThrows((IllegalStateException.class), cabin::addCrewMember);
        assertTrue(cabin.hasPurpleAlien());
    }

    @RepeatedTest(5)
    void hasBrownAlien() {
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Cabin cabin = new Cabin(1, connectors);
        assertFalse(cabin.hasBrownAlien());

        LifeSupportBrown c = new LifeSupportBrown(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(c, 6, 8);

        cabin.addBrownLifeSupport();
        cabin.addBrownAlien();

        assertThrows((IllegalStateException.class), cabin::addCrewMember);
        assertTrue(cabin.hasBrownAlien());
    }

    @RepeatedTest(5)
    void addCrewMember() {
        Random rand = new Random();
        int count = rand.nextInt(2) + 1;
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        for(int i = 0; i < count; i++){
            Cabin cabin = new Cabin(i, connectors);
            ship.placeComponent(cabin, 7, 8 + i);

            //Alien
            boolean bool = rand.nextBoolean();
            if(bool){
                boolean bool1 = rand.nextBoolean();
                if(bool1){
                    System.out.println("P");
                    LifeSupportPurple l = new LifeSupportPurple(1, connectors);
                    ship.placeComponent(l, 8, 8 + i);

                    cabin.addPurpleLifeSupport();
                    cabin.addPurpleAlien();
                    assertEquals(1, cabin.getCrewNumber());
                    assertThrows((IllegalStateException.class), cabin::addCrewMember);
                } else {
                    System.out.println("B");
                    LifeSupportBrown l = new LifeSupportBrown(1, connectors);
                    ship.placeComponent(l, 8, 8 + i);

                    cabin.addBrownLifeSupport();
                    cabin.addBrownAlien();
                    assertEquals(1, cabin.getCrewNumber());
                    assertThrows((IllegalStateException.class), cabin::addCrewMember);
                }
            } else {
                System.out.println("N");
                cabin.addCrewMember();
                assertEquals(2, cabin.getCrewNumber());
            }
        }
    }

    @RepeatedTest(5)
    void addPurpleAlien() {
        Random rand = new Random();
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Cabin cabin = new Cabin(1, connectors);
        assertFalse(cabin.hasPurpleAlien());
        assertEquals(0, cabin.getCrewNumber());

        boolean bool = rand.nextBoolean();
        if(bool){
            boolean bool1 = rand.nextBoolean();
            if(bool1){
                System.out.println("P");
                LifeSupportPurple l = new LifeSupportPurple(1, connectors);
                ship.placeComponent(cabin, 6, 7);
                ship.placeComponent(l, 6, 8);

                cabin.addPurpleLifeSupport();
                cabin.addPurpleAlien();
                assertEquals(1, cabin.getCrewNumber());
                assertTrue(cabin.hasPurpleAlien());
            } else {
                System.out.println("B");
                LifeSupportBrown l = new LifeSupportBrown(1, connectors);
                ship.placeComponent(cabin, 6, 7);
                ship.placeComponent(l, 6, 8);

                cabin.addBrownLifeSupport();
                cabin.addBrownAlien();
                assertEquals(1, cabin.getCrewNumber());
                assertThrows((IllegalStateException.class), cabin::addPurpleAlien);
            }
        } else {
            //Not alien
            System.out.println("N");
            assertFalse(cabin.hasPurpleAlien());
            assertThrows((IllegalStateException.class), cabin::addPurpleAlien);
        }
    }

    @RepeatedTest(5)
    void addBrownAlien() {
        Random rand = new Random();
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Cabin cabin = new Cabin(1, connectors);
        assertFalse(cabin.hasBrownAlien());
        assertEquals(0, cabin.getCrewNumber());

        boolean bool = rand.nextBoolean();
        if(bool){
            boolean bool1 = rand.nextBoolean();
            if(bool1){
                System.out.println("P");
                LifeSupportPurple l = new LifeSupportPurple(1, connectors);
                ship.placeComponent(cabin, 6, 7);
                ship.placeComponent(l, 6, 8);

                cabin.addPurpleLifeSupport();
                cabin.addPurpleAlien();
                assertEquals(1, cabin.getCrewNumber());
                assertThrows((IllegalStateException.class), cabin::addBrownAlien);
            } else {
                System.out.println("B");
                LifeSupportBrown l = new LifeSupportBrown(1, connectors);
                ship.placeComponent(cabin, 6, 7);
                ship.placeComponent(l, 6, 8);

                cabin.addBrownLifeSupport();
                cabin.addBrownAlien();
                assertEquals(1, cabin.getCrewNumber());
                assertTrue(cabin.hasBrownAlien());
            }
        } else {
            //Not alien
            System.out.println("N");
            assertFalse(cabin.hasBrownAlien());
            assertThrows((IllegalStateException.class), cabin::addBrownAlien);
        }
    }

    @RepeatedTest(10)
    void removeCrewMember() {
        Random rand = new Random();
        int count = rand.nextInt(2) + 1;
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        for(int i = 0; i < count; i++){
            Cabin cabin = new Cabin(i, connectors);
            ship.placeComponent(cabin, 7, 8 + i);

            //Alien
            boolean bool = rand.nextBoolean();
            if(bool){
                boolean bool1 = rand.nextBoolean();
                if(bool1){
                    System.out.println("P");
                    LifeSupportPurple l = new LifeSupportPurple(i + 5, connectors);
                    ship.placeComponent(l, 8, 8 + i);

                    cabin.addPurpleLifeSupport();
                    cabin.addPurpleAlien();
                    assertEquals(1, cabin.getCrewNumber());
                    cabin.removeCrewMember(1);
                    assertEquals(0, cabin.getCrewNumber());
                    assertThrows((IllegalStateException.class), () -> cabin.removeCrewMember(1));
                } else {
                    System.out.println("B");
                    LifeSupportBrown l = new LifeSupportBrown(i + 5, connectors);
                    ship.placeComponent(l, 8, 8 + i);

                    cabin.addBrownLifeSupport();
                    cabin.addBrownAlien();
                    assertEquals(1, cabin.getCrewNumber());
                    cabin.removeCrewMember(1);
                    assertEquals(0, cabin.getCrewNumber());
                    assertThrows((IllegalStateException.class), () -> cabin.removeCrewMember(1));
                }
            } else {
                System.out.println("N");
                cabin.addCrewMember();
                assertEquals(2, cabin.getCrewNumber());
                cabin.removeCrewMember(1);
                assertEquals(1, cabin.getCrewNumber());
                cabin.removeCrewMember(1);
                assertEquals(0, cabin.getCrewNumber());
                assertThrows((IllegalStateException.class), () -> cabin.removeCrewMember(1));
            }
        }
    }

    @RepeatedTest(5)
    void addPurpleLifeSupport() {
        Random rand = new Random();
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Cabin cabin = new Cabin(1, connectors);
        assertFalse(cabin.hasPurpleLifeSupport());
        ship.placeComponent(cabin, 6, 7);

        boolean bool = rand.nextBoolean();
        if(bool){
            System.out.println("P");
            LifeSupportPurple l = new LifeSupportPurple(1, connectors);
            ship.placeComponent(l, 6, 8);
        }

        ArrayList<Component> a = ship.getSurroundingComponents(6, 7);

        for(int i = 0; i < 4; i++){
            System.out.println(i + " " + a.get(i));
            if(a.get(i) instanceof LifeSupportPurple){
                System.out.println("Y");
                cabin.addPurpleLifeSupport();
                assertTrue(cabin.hasPurpleLifeSupport());
            } else {
                System.out.println("N");
                assertFalse(cabin.hasPurpleLifeSupport());
            }
        }
    }

    @RepeatedTest(5)
    void addBrownLifeSupport() {
        Random rand = new Random();
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Cabin cabin = new Cabin(1, connectors);
        assertFalse(cabin.hasBrownLifeSupport());
        ship.placeComponent(cabin, 6, 7);

        boolean bool = rand.nextBoolean();
        if(bool){
            System.out.println("B");
            LifeSupportBrown l = new LifeSupportBrown(1, connectors);
            ship.placeComponent(l, 6, 8);
        }

        ArrayList<Component> a = ship.getSurroundingComponents(6, 7);

        for(int i = 0; i < 4; i++){
            System.out.println(i + " " + a.get(i));
            if(a.get(i) instanceof LifeSupportBrown){
                System.out.println("Y");
                cabin.addBrownLifeSupport();
                assertTrue(cabin.hasBrownLifeSupport());
            } else {
                System.out.println("N");
                assertFalse(cabin.hasBrownLifeSupport());
            }
        }
    }
}