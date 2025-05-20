package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class DatabaseController {
    private final String folderPath;
    private static final String indexFolferPath = "/index";
    private static final String gamesFolderPath = "/games";
    private static final String playerToMapFilePath = "/playerToMap.json";
    private static final String metadataFilePath = "/metadata.json";
    private static final int maximumAttempts = 10;
    private final int maximumHours;

    private final File playerToMapFile;
    private final File metadataFile;

    private final Map<UUID, UUID> playerToMap;
    private final Map<UUID, LocalDateTime> metadata;

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Private method to create a file if it doesn't exist.
     * It tries to create the file multiple times if it fails based on the {@link DatabaseController#maximumHours}.
     * @param path a {@link String} representing the path to the file
     * @return a {@link File} object representing the file
     */
    private File createFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                int saveAttempts = 0;
                while (!file.createNewFile()) {
                    if (saveAttempts > maximumAttempts) {
                        throw new RuntimeException("Unable to create file " + path);
                    }
                    saveAttempts++;
                }
            } catch (Exception e) {
                // TODO: handle exception and log it
            }
        }
        return file;
    }

    /**
     * Private method to create a folder if it doesn't exist.
     * It tries to create the folder multiple times if it fails based on the {@link DatabaseController#maximumHours}.
     * @param path a {@link String} representing the path to the folder
     * @return a {@link File} object representing the folder
     */
    private File createFolder(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            try {
                int saveAttempts = 0;
                while (!folder.mkdirs()) {
                    if (saveAttempts > maximumAttempts) {
                        throw new RuntimeException("Unable to create folder " + path);
                    }
                    saveAttempts++;
                }
            } catch (Exception e) {
                // TODO: handle exception and log it
            }
        }
        return folder;
    }

    /**
     * Private method to load the player to map file (JSON).
     * It uses the {@link ObjectMapper} to deserialize the JSON file into a {@link Map} object.
     * @return a {@link Map} object representing the player to map
     */
    private Map<UUID, UUID> loadPlayerToMap() {
        // Load the player to map file (JSON)
        try {
            return mapper.readValue(playerToMapFile, HashMap.class);
        } catch (IOException e) {
            // TODO: handle exception and log it
        }
        return new HashMap<>();
    }

    /**
     * Private method to load the metadata file (JSON).
     * It uses the {@link ObjectMapper} to deserialize the JSON file into a {@link Map} object.
     * @return a {@link Map} object representing the metadata
     */
    private Map<UUID, LocalDateTime> loadMetadata() {
        // Load the metadata file (JSON)
        try {
            return mapper.readValue(metadataFile, HashMap.class);
        } catch (IOException e) {
            // TODO: handle exception and log it
        }
        return new HashMap<>();
    }

    /**
     * Private method to save the player to map file (JSON).
     * It uses the {@link ObjectMapper} to serialize the {@link Map} object into a JSON file.
     */
    private void savePlayerToMap() {
        // Save the player to map file (JSON)
        try {
            mapper.writeValue(playerToMapFile, playerToMap);
        } catch (IOException e) {
            // TODO: handle exception and log it
        }
    }

    /**
     * Private method to save the metadata file (JSON).
     * It uses the {@link ObjectMapper} to serialize the {@link Map} object into a JSON file.
     */
    private void saveMetadata() {
        // Save the metadata file (JSON)
        try {
            mapper.writeValue(metadataFile, metadata);
        } catch (IOException e) {
            // TODO: handle exception and log it
        }
    }

    /**
     * Private method to clean up the metadata map.
     * It iterates over the metadata map and deletes the game state file if the game is older
     * than {@link DatabaseController#maximumHours} hours.
     */
    private void cleanUp() {
        // Get the current time
        LocalDateTime now = LocalDateTime.now();

        // Iterate over the metadata map
        Iterator<Map.Entry<UUID, LocalDateTime>> iterator = metadata.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, LocalDateTime> entry = iterator.next();
            // Check if the game is older than 1 hour
            if (entry.getValue().isBefore(now.minusHours(maximumHours))) {
                // Delete the game state file
                deleteGame(entry.getKey());
                // Remove the entry from the metadata map
                iterator.remove();
            }
        }
    }

    /**
     * Create a new database controller object.
     * This constructor initializes the folder path, maximum hours and tries to create the folder and files used
     * to store the game state
     * <p>
     * It also loads the player to map and metadata files (JSON) into memory.
     * <p>
     * In the end, it cleans up the metadata map by deleting the game state files that are older than
     * {@link DatabaseController#maximumHours} hours.
     * @param maximumHours the maximum hours of inactivity before a game state is deleted
     * @param folderPath the path to the folder where the game state will be saved
     */
    public DatabaseController(String folderPath, int maximumHours) {
        // Set the folder path
        this.folderPath = folderPath;
        // Set the maximum hours
        this.maximumHours = maximumHours;

        // Create the folder if it doesn't exist
        createFolder(folderPath);

        // Check if the games folder exists
        createFolder(folderPath + gamesFolderPath);

        // Check if the index folder exists
        createFolder(folderPath + indexFolferPath);

        // Check if the index folder exists
        this.playerToMapFile = createFile(folderPath + indexFolferPath + playerToMapFilePath);
        // Load the player to map file (JSON)
        this.playerToMap = loadPlayerToMap();

        // Create the metadata file
        this.metadataFile = createFile(folderPath + indexFolferPath + metadataFilePath);
        // Load the metadata file (JSON)
        this.metadata = loadMetadata();

        // Clean up the metadata map
        cleanUp();
    }

    /**
     * Save the game state to a file
     * @param gameController the game controller to save
     */
    public void saveGame(GameController gameController) {
        // Get the folder path from the instance variable
        String filePath = folderPath + "/" + gameController.toString() + ".dat";
        // Serialize the state object to a file
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(gameController);
        } catch (Exception e) {
            // TODO: handle exception and log it
        }
        // Update the metadata file
        metadata.merge(gameController.getUUID(), LocalDateTime.now(), (oldValue, newValue) -> newValue);
        saveMetadata();
    }

    /**
     * Load the game state from a file
     * @param uuid the {@link UUID} of the game controller to load
     * @return the {@link GameController} object representing the game state with the given uuid.
     */
    public GameController loadGame(UUID uuid) {
        // Get the folder path from the instance variable
        String filePath = folderPath + "/" + uuid.toString() +  ".dat";
        // Deserialize the state object from a file
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (GameController) ois.readObject();
        } catch (Exception e) {
            // TODO: handle exception and log it
            return null;
        }
    }

    /**
     * Delete the game state from a file and update the index file:
     * <ul>
     *     <li>Remove the game state file</li>
     *     <li>Remove the entry from the metadata map and save it to the file</li>
     *     <li>Remove the player from the player to map and save it to the file</li>
 *     </ul>
     * @param uuid the uuid of the game controller to delete
     */
    public void deleteGame(UUID uuid) {
        // Get the folder path from the instance variable
        String filePath = folderPath + "/" + uuid.toString() + ".dat";
        // Delete the file
        try {
            int deleteAttempts = 0;
            while (!new File(filePath).delete()) {
                if (deleteAttempts > maximumAttempts) {
                    throw new RuntimeException("Unable to delete file " + filePath);
                }
                deleteAttempts++;
            }
        } catch (Exception e) {
            // TODO: handle exception and log it
        }
        // Remove the player from the player to map
        for (Map.Entry<UUID, UUID> playerEntry : playerToMap.entrySet()) {
            if (playerEntry.getValue().equals(uuid)) {
                playerToMap.remove(playerEntry.getKey());
            }
        }
        // Save the player to map file (JSON)
        savePlayerToMap();
        // Remove the entry from the metadata map
        metadata.remove(uuid);
        // Save the metadata file (JSON)
        saveMetadata();
    }

    /**
     * Link a player to the game controller of his game controller (game state).
     * @param playerUUID the {@link UUID} of the player to link to the game controller.
     * @param gameControllerUUID the {@link UUID} of the game controller to link the player to.
     */
    public void registerPlayer(UUID playerUUID, UUID gameControllerUUID) {
        // Register the player to the game controller
        playerToMap.put(playerUUID, gameControllerUUID);
        // Save the player to map file (JSON)
        savePlayerToMap();
    }
}
