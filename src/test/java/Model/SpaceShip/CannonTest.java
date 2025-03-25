package Model.SpaceShip;

import Model.Game.Board.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CannonTest {
    Cannon component;
    ConnectorType[] connectors;

    @BeforeEach
    void setUp() {
        connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE , ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        component = new Cannon(0, connectors, 1);
        assertNotNull(component, "Component not initialized correctly");
    }

    @RepeatedTest(5)
    void getIDTest(){
        assertEquals(0, component.getID());

        Random rand = new Random();
        int id = rand.nextInt(4,9);
        Cannon cannon = new Cannon(id, connectors, 1);

        assertEquals(id, cannon.getID());
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

        Cannon cannon = new Cannon(1, connectorArray, 1);

        for(int k = 0; k < 4; k++){
            assertEquals(cannon.getConnection(k), check[k]);
        }
    }

    @RepeatedTest(5)
    void isValidTest(){
        //TODO: METODO NON FINITO
        //TODO: Da controllare ma è sbagliato il risultato - sbaglia quando è true add, quindi è sbagliato il metodo - chiedi a toni
        //Potrei fare un test con un componente davanti e uno senza, ma in modo randomico aggiungere un componente davanti
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Storage storage = new Storage(1, connectors, true, 1);
        ship.placeComponent(storage, 8, 7);
        Random rand = new Random();
        int i = rand.nextInt(4) + 1;
        for(int j = 0; j < i; j++) {
            Cannon cannon = new Cannon(j, connectors, 1);
            ship.placeComponent(cannon, 8, 8 + j);

            boolean add = rand.nextBoolean();
            System.out.println(add);
            if(add){
                //If true, add a component in front of the cannon
                Cannon cannon1 = new Cannon(5 + j, connectors, 1);
                ship.placeComponent(cannon1, 7, 8 + j);
            }

            assertNotEquals(add, cannon.isValid());
        }
    }

    //TODO: finire il metodo quando implementano il metodo di spostare i componenti
    //Test for the methods isFixed and fix
    @RepeatedTest(5)
    void isFixedTest(){
        assertFalse(component.isFixed());

        Random rand = new Random();
        int count = rand.nextInt(4) + 1;
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Cannon[] cs = new Cannon[count];
        int i = 0;
        int j;
        for(j = 0; j < count; j++){
            cs[j] = new Cannon(j, connectors, 2);
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

        Cannon cannon = new Cannon(1, connectorArray, 1);
        ship.placeComponent(cannon, 6, 7);
        if(cannon.getConnection(2) != ConnectorType.EMPTY){
            exposed--;
        }
        System.out.println(exposed);
        assertEquals(exposed, cannon.getExposedConnectors());

        Cannon cannon1 = new Cannon(2, connectorArray, 1);
        ship.placeComponent(cannon1, 6, 8);
        if(cannon1.getConnection(3) != ConnectorType.EMPTY){
            exposed--;
        }
        System.out.println(exposed);
        assertEquals(exposed, cannon.getExposedConnectors());
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

            Cannon cannon = new Cannon(1, connector, 1);

            int r = rand.nextInt(4) + 1;
            for(int p = 0; p < r; p++){
                cannon.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            ship.placeComponent(cannon, 6, 7 + j);

            assertTrue(cannon.isConnected(6, 7 + j));
        }
    }

    @RepeatedTest(5)
    void getClockwiseRotation() {
        assertEquals(0, component.getClockwiseRotation());

        Random rand = new Random();
        int r = rand.nextInt(4);

        for(int k = 0; k < r; k++){
            component.rotateClockwise();
        }
        assertEquals(r, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateClockwise() {
        Random rand = new Random();
        int r = rand.nextInt(4);

        int a = component.getClockwiseRotation() + r;

        for(int k = 0; k < r; k++){
            component.rotateClockwise();
        }

        int b = component.getClockwiseRotation();
        assertEquals(a, b);
    }

    @RepeatedTest(5)
    void getCannonStrength() {
        assertEquals(1, component.getCannonStrength());

        Random rand = new Random();
        int r = rand.nextInt(4);
        int power = rand.nextInt(1,3);
        Cannon cannon = new Cannon(0, connectors, power);
        for(int k = 0; k < r; k++){
            cannon.rotateClockwise();
        }

        if(cannon.getClockwiseRotation() != 0){
            float ris = (float) power / 2;
            System.out.println(ris);
            assertEquals(ris, cannon.getCannonStrength());
        } else {
            assertEquals(power, cannon.getCannonStrength());
        }

    }

    @RepeatedTest(10)
    void getComponentType() {
        Random rand = new Random();
        int i = rand.nextInt(2) + 1;

        component = new Cannon(0, connectors, i);
        ComponentType type = component.getComponentType();
        System.out.println(type);

        if(type == ComponentType.SINGLE_CANNON) {
            assertEquals(1, i);
        } else {
            assertEquals(2, i);
        }
    }
}