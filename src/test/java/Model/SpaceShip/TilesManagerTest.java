package Model.SpaceShip;

import Model.Player.PlayerColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TilesManagerTest {

    @Test
    void getTiles() {
        ArrayList<Integer> IDs = new ArrayList<>();

        // Test if the method returns an array of tiles
        ArrayList<Component> tiles = TilesManager.getTiles();
        assertNotNull(tiles);
        assertEquals(152, tiles.size());

        // Test if the tiles are not null
        for (Component tile : tiles) {
            assertNotNull(tile);
        }

        // Test if the tiles are of the correct type
        for (Component tile : tiles) {
            assertInstanceOf(Component.class, tile);
            IDs.add(tile.getID());
        }

        for (int i = 0; i < 156; i++) {
            if (i == 152 || i == 153 || i == 154 || i == 155) assertFalse(IDs.contains(i));
            else assertTrue(IDs.contains(i));
        }

        ArrayList<Component> tiles2 = TilesManager.getTiles();
        for (int i = 0; i < tiles.size(); i++) {
            assertNotSame(tiles.get(i), tiles2.get(i));
        }
    }

    @ParameterizedTest
    @EnumSource(PlayerColor.class)
    void getMainCabin(PlayerColor color) {
        Cabin mainCabin = TilesManager.getMainCabin(color);
        Cabin mainCabin2 = TilesManager.getMainCabin(color);

        // Test if the method returns a main cabin of the correct color
        assertNotNull(mainCabin);
        assertNotNull(mainCabin2);

        assertInstanceOf(Cabin.class, mainCabin);
        assertInstanceOf(Cabin.class, mainCabin2);

        assertNotSame(mainCabin, mainCabin2);

        switch (color) {
            case BLUE -> assertEquals(152, mainCabin.getID());
            case GREEN -> assertEquals(153, mainCabin.getID());
            case RED -> assertEquals(154, mainCabin.getID());
            case YELLOW -> assertEquals(155, mainCabin.getID());
        }
    }

    @Test
    void deepClone() {
        for (int i = 0; i < 152; i++) {
            // Test if the method returns a deep clone of the object
            Component tile = TilesManager.getTiles().get(i);
            Component tile2 = TilesManager.deepClone(tile);
            assertNotNull(tile2);

            // Test if the deep clone is of the correct type
            assertInstanceOf(Component.class, tile2);

            // Test if the deep clone has the same ID as the original object
            assertEquals(tile.getID(), tile2.getID());

            // Test if the deep clone is not the same object as the original object
            assertNotSame(tile, tile2);
        }
    }
}