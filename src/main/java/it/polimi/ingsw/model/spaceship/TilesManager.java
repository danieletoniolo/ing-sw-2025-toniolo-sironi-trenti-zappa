package it.polimi.ingsw.model.spaceship;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.player.PlayerColor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Manager class for handling spaceship tiles and components.
 * This class loads tiles from a JSON file and provides methods to access and manipulate them.
 * @author Lorenzo Trenti
 */
public class TilesManager {
    /** Input stream for reading the tiles JSON file from resources */
    private static final InputStream inputStream = TilesManager.class.getResourceAsStream("/json/Tiles.json");

    /** JSON content as string loaded from the input stream */
    private static final String json;

    /**
     * Static initializer block that loads the JSON content from the tiles file.
     * Reads the entire content of the Tiles.json file from resources and stores it as a string.
     *
     * @throws IllegalArgumentException if the tiles JSON file is not found in resources
     */
    static {
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found!");
        }
        json = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();
    }

    /** Object mapper for JSON deserialization */
    static ObjectMapper objectMapper = new ObjectMapper();

    /** Array containing all tiles loaded from JSON (including main cabins) */
    private static final Component[] allTiles;

    /** Array containing regular tiles (excluding main cabins) */
    private static final Component[] tiles;

    /** Array containing the 4 main cabins */
    private static final Cabin[] mainCabins;

    /**
     * Static initializer block that processes the loaded JSON data.
     * Deserializes the JSON string into Component objects and separates them into:
     * - Regular tiles (first n-4 components)
     * - Main cabins (last 4 components, cast to Cabin type)
     *
     * The separation assumes that the JSON file contains regular tiles first,
     * followed by exactly 4 main cabin components at the end.
     *
     * @throws RuntimeException if JSON deserialization fails or casting to Cabin fails
     */
    static {
        try {
            allTiles = objectMapper.readValue(json, Component[].class);
            tiles = new Component[allTiles.length - 4];
            mainCabins = new Cabin[4];

            int cont = 0;
            for (int i = 0; i < allTiles.length; i++) {
                if (i >= allTiles.length - 4) {
                    mainCabins[cont] = (Cabin) allTiles[i];
                    cont++;
                } else {
                    tiles[i] = allTiles[i];
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get 152 random sorted tiles of the spaceship (Main cabins are not included)
     * @return an array of tiles
     */
    public static ArrayList<Component> getTiles() {
        ArrayList<Component> copy = new ArrayList<>(tiles.length);
        Random random = new Random();

        for(Component tile : tiles) {
            copy.add(deepClone(tile));
        }

        Collections.shuffle(copy);
        for (Component tile : copy) {
            int randomIndex = random.nextInt(4);
            for (int j = 0; j < randomIndex; j++) {
                tile.rotateClockwise();
            }
        }

        return copy;
    }

    /**
     * Get a tile by its ID
     * @param tileID the ID of the tile
     * @return a Component object representing the tile
     */
    public static Component getTile(int tileID) {
        if (tileID < 0 || tileID >= tiles.length) {
            throw new IndexOutOfBoundsException("Tile ID is out of bounds");
        }
        return deepClone(tiles[tileID]);
    }

    /**
     * Get the main cabin of the same color
     * @param color color of the player
     * @return a Cabin object
     */
    public static Cabin getMainCabin(PlayerColor color) {
        if (color == null) {
            throw new NullPointerException("color is null");
        }
        return switch (color) {
            case BLUE -> deepClone(mainCabins[0]);
            case GREEN -> deepClone(mainCabins[1]);
            case RED -> deepClone(mainCabins[2]);
            case YELLOW -> deepClone(mainCabins[3]);
        };
    }

    /**
     * Duplicate an object
     * @param object the object to duplicate
     * @return the duplicated object
     * @param <T> the type of the object
     */
    public static <T extends Serializable> T deepClone(T object) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(object);

            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bis);
            return (T) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
