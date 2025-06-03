package it.polimi.ingsw.view.tui.input;

import org.javatuples.Pair;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import it.polimi.ingsw.view.tui.TerminalUtils;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class Parser {
    private int selected;
    private final Terminal terminal;
    private final LineReader reader;
    private Thread inputThread = null;
    private ExecutorService executor = null;

    public Parser(Terminal terminal) {
        this.terminal = terminal;
        this.reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();
    }

    public int getCommand(ArrayList<String> options, int menuStartRow, Supplier<Boolean> isStillCurrentScreen) throws Exception {
        terminal.enterRawMode();
        var writer = terminal.writer();
        selected = 0;

        InputStream in = terminal.input();
        BlockingQueue<Integer> keyQueue = new ArrayBlockingQueue<>(10);

        inputThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    if (in.available() > 0) {
                        int ch = in.read();
                        if (ch == 27 && in.available() >= 2) {
                            if (in.read() == 91) {
                                int arrow = in.read();
                                switch (arrow) {
                                    case 'A' -> keyQueue.put((int) 'w');
                                    case 'B' -> keyQueue.put((int) 's');
                                }
                            }
                        } else {
                            keyQueue.put(ch);
                        }
                    } else {
                        Thread.sleep(50);
                    }
                }
            } catch (Exception ignored) {}
        });
        inputThread.start();

        renderMenu(writer, options, menuStartRow);

        while (isStillCurrentScreen.get()) {
            Integer key = keyQueue.poll(50, TimeUnit.MILLISECONDS);
            if (key == null) continue;

            switch (key.intValue()) {
                case 'w' -> selected = (selected - 1 + options.size()) % options.size();
                case 's' -> selected = (selected + 1) % options.size();
                case 10, 13 -> {
                    terminal.flush();
                    inputThread.interrupt();
                    return selected;
                }
            }

            renderMenu(writer, options, menuStartRow);
        }

        inputThread.interrupt();
        return -1;
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
