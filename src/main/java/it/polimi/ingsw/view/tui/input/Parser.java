package it.polimi.ingsw.view.tui.input;

import it.polimi.ingsw.utils.Logger;
import org.javatuples.Pair;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import it.polimi.ingsw.view.tui.TerminalUtils;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class Parser {
    private int selected;
    private final Terminal terminal;
    private final LineReader reader;
    private Thread inputThread;
    private ExecutorService executor = null;
    private BlockingQueue<Integer> keyQueue;

    public Parser(Terminal terminal) {
        this.terminal = terminal;
        this.reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();

    }

    public int getCommand(ArrayList<String> options, int menuStartRow, Supplier<Boolean> isStillCurrentScreen) throws Exception {
        terminal.enterRawMode();
        var reader = terminal.reader();
        var writer = terminal.writer();
        selected = 0;

        this.keyQueue = new LinkedBlockingQueue<>();

        this.inputThread = new Thread(() -> {
            try {
                while (true) {
                    int ch = reader.read();
                    if (ch == 27) {
                        if (reader.read() == 91) {
                            int arrow = reader.read();
                            switch (arrow) {
                                case 'A' -> this.keyQueue.put((int) 'w');
                                case 'B' -> this.keyQueue.put((int) 's');
                            }
                        }
                    } else {
                        this.keyQueue.put(ch);
                    }
                }
            } catch (Exception ignored) {}
        });
        this.inputThread.start();

        renderMenu(writer, options, menuStartRow);

        while (true) {
            int key = this.keyQueue.take();
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
        if (keyQueue != null) {
            try {
                // Signal the getCommand method to stop waiting for input
                keyQueue.put(-1);
            } catch (InterruptedException e) {
                Logger.getInstance().logError("Unable to signal input thread to stop: " + e.getMessage(), true);
            }
        }
    }

    public Pair<Integer, Integer> getRowAndCol(String prompt, int menuStartRow, Supplier<Boolean> isStillCurrentScreen) {
        var writer = terminal.writer();
        executor = Executors.newSingleThreadExecutor();

        while (true) {
            if (!isStillCurrentScreen.get()) {
                executor.shutdownNow();
                return null;
            }

            writer.printf("\033[%d;0H", menuStartRow);
            writer.print("\033[2K");
            writer.print(prompt);
            writer.flush();

            Future<String> inputFuture = executor.submit(() -> reader.readLine(""));

            try {
                while (true) {
                    try {
                        String line = inputFuture.get(100, TimeUnit.MILLISECONDS);
                        String[] parts = line.trim().split("\\s+");
                        if (parts.length != 2) {
                            TerminalUtils.printLine(writer, "Input is not valid, type 'row col' (ex: 10 5)", menuStartRow + 1);
                            break;
                        }

                        int x = Integer.parseInt(parts[0]);
                        int y = Integer.parseInt(parts[1]);

                        TerminalUtils.printLine(writer, "", menuStartRow + 1);
                        TerminalUtils.restoreRawMode(terminal);
                        executor.shutdownNow();
                        return new Pair<>(x, y);
                    } catch (TimeoutException e) {
                        if (!isStillCurrentScreen.get()) {
                            inputFuture.cancel(true);
                            executor.shutdownNow();
                            return null;
                        }
                    }
                }
            } catch (Exception e) {
                TerminalUtils.printLine(writer, "Input was interrupted.", menuStartRow + 1);
                executor.shutdownNow();
                return null;
            }
        }
    }

    public String readNickname(String prompt, int menuStartRow, Supplier<Boolean> isStillCurrentScreen) {
        var writer = terminal.writer();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        while (true) {
            if (!isStillCurrentScreen.get()) {
                executor.shutdownNow();
                return null;
            }

            writer.printf("\033[%d;0H", menuStartRow);
            writer.print("\033[2K");
            writer.print(prompt);
            writer.flush();

            Future<String> inputFuture = executor.submit(() -> reader.readLine(""));

            while (true) {
                try {
                    String input = inputFuture.get(100, TimeUnit.MILLISECONDS).trim();
                    if (!input.matches("^[a-zA-Z0-9]+$")) {
                        TerminalUtils.printLine(writer, "Invalid input. Use only letters and numbers, no spaces.", menuStartRow + 1);
                        break;
                    }
                    TerminalUtils.printLine(writer, "", menuStartRow + 1);
                    TerminalUtils.restoreRawMode(terminal);
                    executor.shutdownNow();
                    return input;
                } catch (TimeoutException e) {
                    if (!isStillCurrentScreen.get()) {
                        inputFuture.cancel(true);
                        executor.shutdownNow();
                        return null;
                    }
                } catch (Exception e) {
                    TerminalUtils.printLine(writer, "Input aborted.", menuStartRow + 1);
                    executor.shutdownNow();
                    return null;
                }
            }
        }
    }

    private void renderMenu(PrintWriter writer, ArrayList<String> options, int menuStartRow) {
        for (int i = 0; i < options.size(); i++) {
            int row = menuStartRow + i;
            String prefix = (i == selected) ? "> " : "  ";
            TerminalUtils.printLine(writer, prefix + options.get(i), row);
        }
        writer.flush();
    }

    public void shutdown() {
        if (inputThread != null && inputThread.isAlive()) {
            inputThread.interrupt();
        }
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
        TerminalUtils.restoreRawMode(terminal);
    }
}
