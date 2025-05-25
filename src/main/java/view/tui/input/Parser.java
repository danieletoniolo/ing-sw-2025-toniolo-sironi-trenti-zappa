package view.tui.input;

import org.javatuples.Pair;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import view.tui.TerminalUtils;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class Parser {
    private int selected;
    private Terminal terminal;
    private LineReader reader;

    public Parser(Terminal terminal) {
        this.terminal = terminal;
        this.reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();
    }

    /**
     * Displays a menu and waits for user input.
     *
     * @param options       The list of options to display.
     * @param menuStartRow  The starting row for the menu display.
     * @return A Command object representing the selected option and its arguments.
     * @throws Exception If an error occurs during input handling.
     */
    public int getCommand(ArrayList<String> options, int menuStartRow, Supplier<Boolean> isStillCurrentScreen) throws Exception {
        terminal.reader().skip(terminal.reader().available());
        terminal.enterRawMode();
        var reader = terminal.reader();
        var writer = terminal.writer();
        selected = 0;

        boolean reading = true;
        while (reading) {
            if (!isStillCurrentScreen.get()) return -1;

            renderMenu(writer, options, menuStartRow);

            int key = -1;
            if (reader.ready()) {
                key = reader.read();
            } else {
                Thread.sleep(100);
                continue;
            }

            switch (key) {
                case 'w':
                    selected = (selected - 1 + options.size()) % options.size();
                    break;
                case 's':
                    selected = (selected + 1) % options.size();
                    break;
                case 10, 13:
                    terminal.flush();
                    reading = false;
                    break;
            }
        }

        return selected;
    }

    public Pair<Integer, Integer> getRowAndCol(String prompt, int menuStartRow, Supplier<Boolean> isStillCurrentScreen) {
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

            try {
                while (!inputFuture.isDone()) {
                    if (!isStillCurrentScreen.get()) {
                        inputFuture.cancel(true);
                        executor.shutdownNow();
                        return null;
                    }
                    Thread.sleep(100);
                }

                String line = inputFuture.get();
                String[] parts = line.trim().split("\\s+");
                if (parts.length != 2) {
                    TerminalUtils.printLine(writer, "Input is not valid, type 'row col' (ex: 10 5)", menuStartRow + 1);
                    continue;
                }

                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);

                TerminalUtils.printLine(writer, "", menuStartRow + 1);
                restoreRawMode();
                executor.shutdownNow();
                return new Pair<>(x, y);

            } catch (InterruptedException | ExecutionException | CancellationException e) {
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

            while (!inputFuture.isDone()) {
                if (!isStillCurrentScreen.get()) {
                    inputFuture.cancel(true);
                    executor.shutdownNow();
                    return null;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
            }

            try {
                String input = inputFuture.get().trim();
                if (!input.matches("^[a-zA-Z0-9]+$")) {
                    TerminalUtils.printLine(writer, "Invalid input. Use only letters and numbers, no spaces.", menuStartRow + 1);
                    continue;
                }
                TerminalUtils.printLine(writer, "", menuStartRow + 1);
                restoreRawMode();
                executor.shutdownNow();
                return input;
            } catch (Exception e) {
                TerminalUtils.printLine(writer, "Input aborted.", menuStartRow + 1);
            }
        }
    }


    private void restoreRawMode() {
        try {
            terminal.enterRawMode();
            terminal.writer().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Renders the menu options to the terminal.
     *
     * @param writer        The PrintWriter to write to the terminal.
     * @param options       The list of options to display.
     * @param menuStartRow  The starting row for the menu display.
     */
    private void renderMenu(java.io.PrintWriter writer, ArrayList<String> options, int menuStartRow) {
        for (int i = 0; i < options.size(); i++) {
            int row = menuStartRow + i;
            if (i == selected) {
                TerminalUtils.printLine(writer, "> " + options.get(i), row);
            } else {
                TerminalUtils.printLine(writer, "  " + options.get(i), row);
            }
        }
        writer.flush();
    }
}
