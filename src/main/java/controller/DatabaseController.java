package controller;

import java.io.*;
import java.util.UUID;

public class DatabaseController {
    private final String folderPath;
    private static final int maximumAttempts = 10;
    /**
     * Create a new database controller
     * @param folderPath the path to the folder where the game state will be saved
     */
    public DatabaseController(String folderPath) {
        this.folderPath = folderPath;

        // Create the folder if it doesn't exist
        try {
            int saveAttempts = 0;
            while (!new File(folderPath).mkdirs()) {
                if (saveAttempts > maximumAttempts) {
                    throw new RuntimeException("Unable to create folder " + folderPath);
                }
                saveAttempts++;
            }
        } catch (Exception e) {
            // TODO: handle exception and log it
        }
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
    }

    /**
     * Load the game state from a file
     * @param uuid the uuid of the game controller to load
     * @return the game controller
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
     * Delete the game state from a file
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
    }
}
