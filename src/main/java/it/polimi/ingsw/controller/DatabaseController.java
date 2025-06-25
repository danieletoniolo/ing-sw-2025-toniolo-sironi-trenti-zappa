package it.polimi.ingsw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.polimi.ingsw.utils.Launcher;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * DatabaseController class to manage the game state files and player to map file.
 * <p>
 * This class implements the Singleton pattern to ensure that only one instance of the database controller exists.
 * It provides methods to save, load, and delete game states, as well as register players to their respective game controllers.
 * </p>
 * The database controller creates a new folder in the user's home directory to store game state files and a
 * JSON file to map players to their game controllers.
 */
public class DatabaseController {
    /**
     * {@link DatabaseController} static instance to implement the Singleton pattern.
     */
    private static DatabaseController instance;

    /**
     * {@link Path} to the folder where game states are stored.
     */
    private final Path gamesFolder;

    /**
     * Static string to define the index folder.
     */
    private static final String indexFolferPath = "index";

    /**
     * Static string to define the games' folder.
     */
    private static final String gamesFolderPath = "games";

    /**
     * Static string to define the player to map file path.
     */
    private static final String playerToMapFilePath = "playerToMap.json";

    /**
     * Static integer to define the maximum number of hours before a game state file is considered old.
     */
    private static final int maximumHour = 12;

    /**
     * {@link File} to store the player to map file.
     */
    private final File playerToMapFile;

    /**
     * {@link Map} to store the mapping of players to their game controllers.
     * The key is the player's nickname and the value is the game controller's {@link UUID} which is
     * also the game state file name.
     */
    private final Map<String, UUID> playerToMap;

    /**
     * {@link ObjectMapper} to handle JSON serialization and deserialization.
     */
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Private constructor to prevent instantiation and ensure Singleton pattern.
     * It initializes the games folder, index folder, and player to map file.
     * @throws IOException if an I/O error occurs
     */
    private DatabaseController() throws IOException {
        Path folder = Launcher.getDataFolder();

        // Create the games folder if it doesn't exist
        gamesFolder = Paths.get(folder.toString(), gamesFolderPath);
        Files.createDirectories(gamesFolder);

        // Clean up old game state files
        cleanUp();

        // Create the index folder if it doesn't exist
        Path indexFolder = Paths.get(folder.toString(), indexFolferPath);
        Files.createDirectories(indexFolder);

        // Create the Index JSON file if it doesn't exist
        Path indexFile = Paths.get(indexFolder.toString(), playerToMapFilePath);
        if (!Files.exists(indexFile)) {
            Files.createFile(indexFile);
            playerToMapFile = indexFile.toFile();
            playerToMap = new HashMap<>();
        } else {
            // Load the player to map file (JSON)
            playerToMapFile = indexFile.toFile();
            playerToMap = mapper.readValue(playerToMapFile, HashMap.class);
        }
    }

    /**
     * Static method to get the instance of the {@link DatabaseController}.
     * It creates a new instance if it doesn't exist.
     * @return the instance of the {@link DatabaseController}
     * @throws IOException if an I/O error occurs while setting up the files structure.
     */
    public static DatabaseController getInstance() throws IOException {
        if (instance == null) {
            instance = new DatabaseController();
        }
        return instance;
    }

    /**
     * Private method to clean up the metadata map.
     * It iterates over the metadata map and deletes the game state file if the game is older
     * than {@link DatabaseController#maximumHour} hours.
     */
    private void cleanUp() throws IOException {
        // Get the current time
        LocalDateTime now = LocalDateTime.now();

        // Iterate over the game state files in the games folder
        Files.list(gamesFolder)
                .filter(file -> file.toString().endsWith(".dat"))
                .forEach(file -> {
                    try {
                        // Get the last modified time of the file
                        LocalDateTime lastModifiedTime = LocalDateTime.ofInstant(
                            Files.getLastModifiedTime(file).toInstant(),
                            java.time.ZoneId.systemDefault()
                        );
                        // Check if the file is older than maximumHours
                        if (lastModifiedTime.isBefore(now.minusHours(maximumHour))) {
                            // Delete the file
                            Files.delete(file);
                        }
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }

    /**
     * Serialize the game controller to a file and save it in the /saves/games folder.
     * @param gameController the {@link GameController} to save (it contains the game state).
     * @throws IOException if an I/O error occurs while writing to the file.
     */
    public void saveGame(GameController gameController) throws IOException {
        // Get the path from the instance variable
        Path filePath = gamesFolder.resolve(gameController.getUUID().toString() + ".dat");

        // Serialize the state object to a file
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filePath.toFile()));
        outputStream.writeObject(gameController);
        outputStream.flush();
        outputStream.close();
    }

    /**
     * Load the game controller from a file and return it.
     * @param playerNickname the nickname of the player who wants to load the game controller of his game.
     * @return the {@link GameController} loaded from the file.
     * @throws IOException if an I/O error occurs while reading from the file.
     * @throws IllegalStateException if the player is not found in the player to map.
     */
    public GameController loadGame(String playerNickname) throws IOException, IllegalStateException {
        if (!playerToMap.containsKey(playerNickname)) {
            throw new IllegalStateException("Player not found: " + playerNickname);
        }
        UUID uuid = playerToMap.get(playerNickname);

        // Get the path from the instance variable
        Path filePath = gamesFolder.resolve(uuid.toString() + ".dat");

        // Deserialize the state object from a file
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filePath.toFile()));
        GameController gameController;
        try {
            gameController = (GameController) inputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to find class " + e.getMessage());
        }
        inputStream.close();

        return gameController;
    }

    /**
     * Delete the game state file and remove the entry from the playerToMap.
     * @param uuid the {@link UUID} of the game controller to delete.
     * @throws IOException if an I/O error occurs while deleting the file.
     */
    public void deleteGame(UUID uuid) throws IOException {
        // Get the path from the instance variable
        Path filePath = gamesFolder.resolve(uuid.toString() + ".dat");

        // Delete the game state file
        Files.delete(filePath);

        // Remove the entry from the playerToMap
        for (Map.Entry<String, UUID> entry : playerToMap.entrySet()) {
            if (entry.getValue().equals(uuid)) {
                // Remove the entry from the player to map
                playerToMap.remove(entry.getKey());
            }
        }
    }

    /**
     * Link a player to the game controller of his game controller (game state).
     * @param playerNickname the nickname of the player to link to the game controller.
     * @param gameControllerUUID the {@link UUID} of the game controller to link the player to.
     * @throws IOException if an I/O error occurs while writing to the file.
     */
    public void registerPlayer(String playerNickname, UUID gameControllerUUID) throws IOException {
        // Register the player to the game it.polimi.ingsw.controller
        playerToMap.put(playerNickname, gameControllerUUID);
        // Save the player to map file (JSON)
        mapper.writeValue(playerToMapFile, playerToMap);
    }
}
