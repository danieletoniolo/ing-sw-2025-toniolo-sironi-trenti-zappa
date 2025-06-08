package it.polimi.ingsw.view.tui.input;

import it.polimi.ingsw.utils.Logger;
import org.javatuples.Pair;
import org.jline.terminal.Terminal;
import it.polimi.ingsw.view.tui.TerminalUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Predicate;

public class Parser {
    private int selected;
    private final Terminal terminal;
    private Thread inputThread;
    private final Queue<Integer> keyQueue;
    private final Queue<String> stringsQueue;

    /**
     * Constructor for the Parser class, which initializes the terminal.
     *
     * @param terminal The terminal to be used for input and output operations.
     */
    public Parser(Terminal terminal) {
        this.terminal = terminal;
        this.keyQueue = new LinkedList<>();
        this.stringsQueue = new LinkedList<>();
    }

    /**
     * This method displays a menu with the given options and allows the user to navigate through them using 'w' and 's' keys
     * or the arrow keys. The user can select an option by pressing 'Enter'.
     *
     * @param options The list of options to display in the menu.
     * @param menuStartRow The row where the menu starts on the terminal.
     * @return The index of the selected option, or -1 if the menu must be closed due to an external event that changed the screen.
     * @throws InterruptedException If the poll for user input is interrupted. This should only happen if we have an
     * unexpected shutdown.
     */
    public int getCommand(ArrayList<String> options, int menuStartRow) throws InterruptedException {
        terminal.enterRawMode();
        var reader = terminal.reader();
        var writer = terminal.writer();
        selected = 0;

        synchronized (keyQueue) {
            this.keyQueue.clear();
        }

        this.inputThread = new Thread(() -> {
            try {
                while (true) {
                    int ch = reader.read();
                    if (ch == 27) {
                        if (reader.read() == 91) {
                            int arrow = reader.read();
                            synchronized (keyQueue) {
                                switch (arrow) {
                                    case 'A' -> this.keyQueue.add((int) 'w');
                                    case 'B' -> this.keyQueue.add((int) 's');
                                }
                                this.keyQueue.notifyAll();
                            }
                        }
                    } else {
                        synchronized (keyQueue) {
                            this.keyQueue.add(ch);
                            keyQueue.notifyAll();
                        }
                    }
                }
            } catch (Exception ignored) {
                Thread.currentThread().interrupt();
            }
        });
        this.inputThread.start();

        renderMenu(writer, options, menuStartRow);

        while (true) {
            int key;
            synchronized (keyQueue) {
                while (keyQueue.isEmpty()) {
                    keyQueue.wait();
                }
                key = this.keyQueue.poll();
            }
            switch (key) {
                case (int) 'w' -> selected = (selected - 1 + options.size()) % options.size();
                case (int) 's' -> selected = (selected + 1) % options.size();
                case 10, 13 -> {
                    terminal.flush();
                    inputThread.interrupt();
                    return selected;
                }
                case -1 -> {
                    inputThread.interrupt();
                    return -1;
                }
            }
            renderMenu(writer, options, menuStartRow);
        }
    }

    /**
     * Changes the current screen by interrupting the input thread and signaling it to stop waiting for input.
     * This method is used when we have to switch screens by an external event and not by user input.
     */
    public void changeScreen() {
        if (inputThread != null && inputThread.isAlive()) {
            inputThread.interrupt();
        }
        synchronized (keyQueue) {
            keyQueue.add(-1); // Use -1 to signal the end of input
            keyQueue.notifyAll();
        }
        synchronized (stringsQueue) {
            stringsQueue.add(null); // Use null to signal the end of input
            stringsQueue.notifyAll();
        }
    }

    /**
     * Reads a string input from the user, validating it with the provided predicate.
     * The input is displayed at the specified row in the terminal.
     *
     * @param prompt The prompt to display to the user.
     * @param menuStartRow The row where the input should be displayed.
     * @param validator A predicate to validate the input.
     * @param errorMessage The message to display if validation fails.
     * @return The validated string input from the user, or null if interrupted.
     */
    private String getString(String prompt, int menuStartRow, Predicate<String> validator, String errorMessage) {
        var writer = terminal.writer();
        var reader = terminal.reader();

        TerminalUtils.restoreRawMode(terminal);

        synchronized (stringsQueue) {
            stringsQueue.clear();
        }

        inputThread = new Thread(() -> {
            try {
                while (true) {
                    TerminalUtils.printLine(writer, prompt, menuStartRow);

                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        int ch = reader.read();
                        if (ch == 10 || ch == 13) { // Enter key pressed
                            String entered = sb.toString().trim();
                            if (validator.test(entered)) {
                                TerminalUtils.printLine(writer, "", menuStartRow + 1);
                                TerminalUtils.restoreRawMode(terminal);
                                synchronized (stringsQueue) {
                                    stringsQueue.add(entered);
                                    stringsQueue.notifyAll();
                                }
                            } else {
                                TerminalUtils.printLine(writer, errorMessage, menuStartRow + 1);
                                break;
                            }

                        } else if (ch == 127 || ch == 8) { // Handle backspace
                            if (!sb.isEmpty()) {
                                sb.deleteCharAt(sb.length() - 1);
                                TerminalUtils.printLine(writer, prompt + sb, menuStartRow);
                            }
                        } else if (ch >= 32 && ch < 127) { // Printable characters
                            sb.append((char) ch);
                            TerminalUtils.printLine(writer, prompt + sb, menuStartRow);
                        }
                    }
                }
            } catch (IOException ignored) {
                Thread.currentThread().interrupt();
            }
        });
        inputThread.start();

        String input;
        synchronized (stringsQueue) {
            while (stringsQueue.isEmpty()) {
                try {
                    stringsQueue.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    Logger.getInstance().logError("Parser thread stopped while waiting for input", false);
                }
            }
            input = stringsQueue.poll();
        }
        TerminalUtils.restoreRawMode(terminal);
        inputThread.interrupt();
        return input;
    }


    /**
     * Reads a pair of integers from the user, expecting input in the format "row col".
     * The is taken using the {@link #getString} method, which validates the input format.
     *
     * @param prompt The prompt to display to the user.
     * @param menuStartRow The row where the input should be displayed.
     * @return A Pair containing the row and column integers, or null if interrupted or invalid input.
     */
    public Pair<Integer, Integer> getRowAndCol(String prompt, int menuStartRow) {
        String input = getString(prompt, menuStartRow, s -> {
            String[] parts = s.trim().split("\\s+");
            if (parts.length != 2) return false;
            try {
                Integer.parseInt(parts[0]);
                Integer.parseInt(parts[1]);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
            }, "Input is not valid, type 'row col' (ex: 10 5)");

        if (input != null) {
            String[] parts = input.trim().split("\\s+");
            return new Pair<>(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
        return null;
    }

    /**
     * Reads a nickname from the user, validating that it contains only letters and numbers.
     * The input is taken using the {@link #getString} method, which validates the input format.
     *
     * @param prompt The prompt to display to the user.
     * @param menuStartRow The row where the input should be displayed.
     * @return The validated nickname string, or null if interrupted or invalid input.
     */
    public String readNickname(String prompt, int menuStartRow) {
        return getString(prompt, menuStartRow,
                s -> s.matches("^[a-zA-Z0-9]+$"),
                "Invalid input. Use only letters and numbers, no spaces.");
    }

    /**
     * Prints the menu options to the terminal at the specified starting row, with the specified options
     * and highlights the selected option.
     *
     * @param writer The PrintWriter to write the menu to the terminal.
     * @param options The list of options to display in the menu.
     * @param menuStartRow The row where the menu starts on the terminal.
     */
    private void renderMenu(PrintWriter writer, ArrayList<String> options, int menuStartRow) {
        for (int i = 0; i < options.size(); i++) {
            int row = menuStartRow + i;
            String prefix = (i == selected) ? "> " : "  ";
            TerminalUtils.printLine(writer, prefix + options.get(i), row);
        }
        writer.flush();
    }
}
