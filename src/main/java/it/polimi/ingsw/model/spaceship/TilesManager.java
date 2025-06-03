package it.polimi.ingsw.model.spaceship;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.player.PlayerColor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TilesManager {
    private static final ClassLoader classLoader = TilesManager.class.getClassLoader();
    private static final InputStream inputStream = classLoader.getResourceAsStream("json/Tiles.json");
    private static final String json;
    static {
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found!");
        }
        json = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();
    }
    static ObjectMapper objectMapper = new ObjectMapper();

    private static final Component[] allTiles;
    private static final Component[] tiles;
    private static final Cabin[] mainCabins;

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
     * Get 152 tiles of the spaceship (Main cabins are not included)
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
