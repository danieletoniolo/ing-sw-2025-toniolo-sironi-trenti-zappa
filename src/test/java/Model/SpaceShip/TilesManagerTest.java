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
        Component[] tiles = TilesManager.getTiles();
        assertNotNull(tiles);
        assertEquals(152, tiles.length);

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
            if (i == 32 || i == 33 || i == 51 || i == 60) assertFalse(IDs.contains(i));
            else assertTrue(IDs.contains(i));
        }

        Component[] tiles2 = TilesManager.getTiles();
        for (int i = 0; i < tiles.length; i++) {
            assertNotSame(tiles[i], tiles2[i]);
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
            case RED -> assertEquals(51, mainCabin.getID());
            case BLUE -> assertEquals(32, mainCabin.getID());
            case YELLOW -> assertEquals(60, mainCabin.getID());
            case GREEN -> assertEquals(33, mainCabin.getID());
        }
    }

    @Test
    void getAllTiles() {
        ArrayList<Integer> IDs = new ArrayList<>();

        // Test if the method returns an array of tiles
        Component[] allTiles = TilesManager.getTiles();
        assertNotNull(allTiles);
        assertEquals(156, allTiles.length);

        // Test if the tiles are not null
        for (Component tile : allTiles) {
            assertNotNull(tile);
        }

        // Test if the tiles are of the correct type
        for (Component tile : allTiles) {
            assertInstanceOf(Component.class, tile);
            IDs.add(tile.getID());
        }

        for (int i = 0; i < 156; i++) {
            assertTrue(IDs.contains(i));
        }

        Component[] allTiles2 = TilesManager.getTiles();
        for (int i = 0; i < allTiles.length; i++) {
            assertNotSame(allTiles[i], allTiles2[i]);
        }
    }

    @Test
    void deepClone() {
        // Test if the method returns a deep clone of the object
        Component tile = TilesManager.getTiles()[0];
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