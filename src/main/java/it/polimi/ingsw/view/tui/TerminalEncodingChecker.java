package it.polimi.ingsw.view.tui;

/**
 * Utility class to check if the terminal supports UTF-8 encoding.
 */
public class TerminalEncodingChecker {

    /**
     * Checks if the terminal environment is configured to use UTF-8 encoding.
     *
     * @return true if the terminal encoding, LANG, or LC_CTYPE environment variables indicate UTF-8; false otherwise
     */
    public static boolean isUtf8Terminal() {
        String fileEncoding = System.getProperty("file.encoding");
        String lang = System.getenv("LANG");
        String lcCtype = System.getenv("LC_CTYPE");

        boolean encodingIsUtf8 = fileEncoding != null && fileEncoding.toUpperCase().contains("UTF-8");
        boolean langIsUtf8 = lang != null && lang.toUpperCase().contains("UTF-8");
        boolean ctypeIsUtf8 = lcCtype != null && lcCtype.toUpperCase().contains("UTF-8");

        return encodingIsUtf8 || langIsUtf8 || ctypeIsUtf8;
    }

    /**
     * Main method for testing terminal encoding detection.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        if (isUtf8Terminal()) {
            System.out.println("UTF-8 detected: using full Unicode output.");
            System.out.println("Test: こんにちは世界"); // "Hello World" in Japanese
        } else {
            System.out.println("Non-UTF-8 terminal: falling back to ASCII-compatible output.");
        }
    }
}