package Model.SpaceShip;

import Model.Game.Board.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ShieldTest {
    Shield s;
    ConnectorType[] connectors;

    @BeforeEach
    void setUp() {
        connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        s = new Shield(0, connectors);
        assertNotNull(s, "Component not initialized correctly");
    }

    @RepeatedTest(10)
    void getComponentType() {
        s = new Shield(0, connectors);
        ComponentType type = s.getComponentType();
        System.out.println(type);

        assertEquals(ComponentType.SHIELD, type);
    }

    @RepeatedTest(5)
    void getIDTest(){
        assertEquals(0, s.getID());

        Random rand = new Random();
        int id = rand.nextInt(4,9);
        Shield s = new Shield(id, connectors);

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

        Shield s = new Shield(1, connectorArray);

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

        Shield s = new Shield(1, connectorArray);
        ship.placeComponent(s, 6, 7);
        if(s.getConnection(2) != ConnectorType.EMPTY){
            exposed--;
        }
        System.out.println(exposed);
        assertEquals(exposed, s.getExposedConnectors());

        Shield s1 = new Shield(2, connectorArray);
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

            Shield s = new Shield(1, connector);

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
        Shield[] ss = new Shield[count];
        int i = 0;
        int j;
        for(j = 0; j < count; j++){
            ss[j] = new Shield(j, connectors);
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

        Shield shield1 = new Shield(1, connectors1);
        Shield shield2 = new Shield(2, connectors2);

        ship.placeComponent(shield1, 6, 7);
        System.out.println(shield1.isValid());

        if(shield1.getConnection(2) != ConnectorType.EMPTY){
            assertTrue(shield1.isValid());
        } else {
            assertFalse(shield1.isValid());
            return;
        }

        ship.placeComponent(shield2, 6, 8);
        System.out.println(shield1.isValid());

        if((shield1.getConnection(3) == ConnectorType.EMPTY && shield2.getConnection(1) != ConnectorType.EMPTY) ||
                (shield1.getConnection(3) == ConnectorType.SINGLE && shield2.getConnection(1) != ConnectorType.SINGLE &&
                        shield2.getConnection(1) != ConnectorType.TRIPLE) ||
                (shield1.getConnection(3) == ConnectorType.DOUBLE && shield2.getConnection(1) != ConnectorType.DOUBLE &&
                        shield2.getConnection(1) != ConnectorType.TRIPLE) ||
                (shield1.getConnection(3) == ConnectorType.TRIPLE && shield2.getConnection(1) == ConnectorType.EMPTY)){
            assertFalse(shield1.isValid());
        } else {
            assertTrue(shield1.isValid());
        }
    }

    //Reminder: we rotate in sense counterclockwise, so in 0 (N) we protect 0 (N) and 3 (E), in 1 (W) we protect 0 (N) and 1 (W), in 2 (S) we protect 1 (W) and 2 (S), in 3 (E) we protect 2 (S) and 3 (E)
    @RepeatedTest(500)
    void canShield() {
        assertTrue(s.canShield(0));
        assertFalse(s.canShield(1));
        assertFalse(s.canShield(2));
        assertTrue(s.canShield(3));

        Random rand = new Random();
        int r = rand.nextInt(4);
        for(int i = 0; i < r; i++){
            s.rotateClockwise();
        }
        System.out.println("Rotation: " + r%4);

        if(r == 0){
            assertTrue(s.canShield(0));
            assertFalse(s.canShield(1));
            assertFalse(s.canShield(2));
            assertTrue(s.canShield(3));
        } else if(r == 1){
            assertTrue(s.canShield(0));
            assertTrue(s.canShield(1));
            assertFalse(s.canShield(2));
            assertFalse(s.canShield(3));
        } else if(r == 2){
            assertFalse(s.canShield(0));
            assertTrue(s.canShield(1));
            assertTrue(s.canShield(2));
            assertFalse(s.canShield(3));
        } else {
            assertFalse(s.canShield(0));
            assertFalse(s.canShield(1));
            assertTrue(s.canShield(2));
            assertTrue(s.canShield(3));
        }
    }
}