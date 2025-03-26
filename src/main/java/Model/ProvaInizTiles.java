package Model;

import Model.SpaceShip.Component;
import Model.SpaceShip.Shield;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.javac.Main;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class ProvaInizTiles {
    public static void main(String[] args) throws JsonProcessingException {
        ClassLoader classLoader = Main.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("Json/Tiles.json");

        if (inputStream == null) {
            throw new IllegalArgumentException("File non trovato!");
        }

        // 📌 Convertire InputStream in Stringa
        String json = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();

        // 📌 Convertire JSON in array di carte
        ObjectMapper objectMapper = new ObjectMapper();
        Component[] tiles = objectMapper.readValue(json, Component[].class);

        // 📌 Stampare le carte per verificarle
        Arrays.stream(tiles).forEach(tile -> System.out.println(tile.getID()));
    }
}
