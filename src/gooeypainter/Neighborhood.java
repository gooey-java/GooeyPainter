package gooeypainter;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * The grid of squares that every {@link Painter} lives on, plus the parts of the
 * library that are shared by all Painters: loading grid.txt, animation timing,
 * console messages, and stopping the program on a {@link GooeyPainterException}.
 *
 * <p>Students never use this class directly. There is exactly one Neighborhood per
 * program, created by {@link #get()} the first time any Painter is constructed.</p>
 *
 * <h2>Threads and locking</h2>
 *
 * <p>Student code runs on the main thread, while the window is drawn on Swing's
 * event dispatch thread. Every change to the grid or to a Painter happens inside
 * {@code synchronized (world)}, and the window holds the same lock while it draws,
 * so the window never sees a half-finished change. Pauses for animation happen
 * <em>outside</em> the lock so the window can redraw during them.</p>
 *
 * <h2>Arrays</h2>
 *
 * <p>The grid arrays are indexed {@code [x][y]} (column first), matching Code.org's
 * own Grid class and the (x, y) order students use.</p>
 */
final class Neighborhood {

    /** Width and height of the blank grid used when there is no grid.txt. */
    static final int DEFAULT_SIZE = 16;

    /** The one Neighborhood for this program, or null before the first Painter. */
    private static Neighborhood instance;

    /** Number of columns. */
    final int width;

    /** Number of rows. */
    final int height;

    /** {@code walls[x][y]} is true if that square is an obstacle. */
    final boolean[][] walls;

    /** {@code buckets[x][y]} is the paint left in the bucket there; 0 means no bucket. */
    final int[][] buckets;

    /** {@code paint[x][y]} is the color painted there, or null for grass. */
    final Color[][] paint;

    /** {@code colorNames[x][y]} is the color text the student used there, for {@link Painter#getColor()}. */
    final String[][] colorNames;

    /** Every Painter created so far, in the order they were made (later ones are drawn on top). */
    final List<Painter> painters = new ArrayList<>();

    /** Describes where the grid came from, shown in the window's title. */
    final String source;

    /** False after {@link Painter#hideBuckets()}. */
    volatile boolean bucketsVisible = true;

    /** Milliseconds to pause after each action. Set by the window's speed slider. */
    volatile int delayMs = 250;

    /** The text shown in the window's status line. */
    volatile String status = "Ready";

    /** True if the status line should be shown in red. */
    volatile boolean statusIsError = false;

    /** True once a GooeyPainterException has stopped the program. */
    volatile boolean stopped = false;

    /** The window, or null if this Java can't open windows. */
    private NeighborhoodWindow window;

    /**
     * Creates an all-grass grid with no buckets, obstacles, or paint.
     *
     * @param width  number of columns
     * @param height number of rows
     * @param source description of where the grid came from, for the window title
     */
    private Neighborhood(int width, int height, String source) {
        this.width = width;
        this.height = height;
        this.source = source;
        walls = new boolean[width][height];
        buckets = new int[width][height];
        paint = new Color[width][height];
        colorNames = new String[width][height];
    }

    /**
     * Returns the program's Neighborhood, creating it the first time: installs the
     * exception handler, loads grid.txt, and opens the window.
     *
     * @return the one Neighborhood for this program
     * @throws GooeyPainterException if grid.txt has a mistake in it
     */
    static synchronized Neighborhood get() {
        if (instance == null) {
            installExceptionHandler();
            instance = load();
            instance.openWindow();
        }
        return instance;
    }

    // ================================================================== grid queries

    /**
     * Checks whether a location is on the grid at all.
     *
     * @param x a column
     * @param y a row
     * @return true if (x, y) is on the grid, whether or not it is an obstacle
     */
    boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    /**
     * Same rule as Code.org's Grid.validLocation: a Painter may stand on (x, y).
     *
     * @param x a column
     * @param y a row
     * @return true if (x, y) is on the grid and isn't an obstacle
     */
    boolean isValidLocation(int x, int y) {
        return inBounds(x, y) && !walls[x][y];
    }

    // ================================================================== output

    /**
     * Records an action a Painter took: shows it in the status line, redraws the
     * window, then pauses for the speed slider's delay so the action is visible.
     * Must be called without holding the lock, so the window can draw during the pause.
     *
     * @param status a short description of the action, such as "Painter #1 moved east to (2, 0)."
     */
    void step(String status) {
        this.status = status;
        this.statusIsError = false;
        refreshWindow();
        int d = delayMs;
        if (d > 0) {
            try {
                Thread.sleep(d);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Prints one of Code.org's console messages (such as "There is no more paint in
     * the painter's bucket") and shows it in red in the status line. Unlike
     * {@link #step(String)}, there is no animation pause, because nothing changed on
     * the grid; Code.org doesn't animate these either.
     *
     * @param message the exact text to print
     */
    void consoleMessage(String message) {
        System.out.println(message);
        status = message;
        statusIsError = true;
        refreshWindow();
    }

    /** Asks the window, if there is one, to redraw. */
    private void refreshWindow() {
        if (window != null) window.refresh();
    }

    /**
     * Makes an uncaught {@link GooeyPainterException} behave like Code.org: print only
     * {@code [EXCEPTION] message} (no Java stack trace) and stop the student's code.
     *
     * <p>This works by setting Java's default uncaught-exception handler, which runs
     * when an exception escapes {@code main}. The main thread then ends, but the
     * window's thread keeps running, so the window stays open showing where the
     * Painter was. To close the window too, add {@code System.exit(1);} after the
     * message is printed.</p>
     *
     * <p>Any other kind of exception (for example a NullPointerException in the
     * student's own code) is passed along unchanged, so it prints the normal Java
     * stack trace.</p>
     */
    private static void installExceptionHandler() {
        Thread.UncaughtExceptionHandler previous = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, e) -> {
            if (e instanceof GooeyPainterException) {
                String message = "[EXCEPTION] " + e.getMessage();
                System.out.println(message);
                System.out.flush();
                Neighborhood n = instance;
                if (n != null) {
                    n.stopped = true;
                    n.status = message;
                    n.statusIsError = true;
                    n.refreshWindow();
                } else {
                    // The grid never loaded (a grid.txt problem), so there's no window to keep open.
                    System.exit(1);
                }
            } else if (previous != null) {
                previous.uncaughtException(thread, e);
            } else {
                System.err.print("Exception in thread \"" + thread.getName() + "\" ");
                e.printStackTrace();
            }
        });
    }

    /**
     * Opens the window on Swing's thread and waits for it to appear. "Headless" Java
     * installs can't show windows at all, so in that case a warning is printed and
     * the program runs without one.
     */
    private void openWindow() {
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("[GooeyPainter] This Java install can't open windows (it is a \"headless\" JDK), "
                    + "so the grid can't be shown. Install a full JDK, such as Eclipse Temurin.");
            return;
        }
        try {
            SwingUtilities.invokeAndWait(() -> window = new NeighborhoodWindow(this));
            Thread.sleep(400); // a short beat so the window is visible before the first action
        } catch (Exception e) {
            System.err.println("[GooeyPainter] Couldn't open the window: " + e);
        }
    }

    // ================================================================== colors

    /**
     * Converts a color the way Code.org accepts it: one of the web color names in
     * {@link WebColors} (any capitalization), or a hex code {@code #RGB} or {@code #RRGGBB}.
     *
     * @param s the color text from the student
     * @return the matching Color, or null if it isn't a recognized color
     */
    static Color parseColor(String s) {
        if (s == null) return null;
        String t = s.trim().toLowerCase();
        Integer rgb = WebColors.COLORS.get(t);
        if (rgb != null) return new Color(rgb);
        if (t.matches("#[0-9a-f]{3}")) {
            // #F80 is shorthand for #FF8800
            t = "#" + t.charAt(1) + t.charAt(1) + t.charAt(2) + t.charAt(2) + t.charAt(3) + t.charAt(3);
        }
        if (t.matches("#[0-9a-f]{6}")) return new Color(Integer.parseInt(t.substring(1), 16));
        return null;
    }

    // ================================================================== grid.txt

    /**
     * Looks for grid.txt in the folder the program runs from (the project folder,
     * when running from VS Code), then in its src folder.
     *
     * @return the grid file, or null if there isn't one
     */
    private static Path findGridFile() {
        for (String c : new String[]{"grid.txt", "src/grid.txt"}) {
            Path p = Paths.get(c);
            if (Files.isRegularFile(p)) return p;
        }
        return null;
    }

    /**
     * Builds the grid from grid.txt, or a blank {@value #DEFAULT_SIZE} x
     * {@value #DEFAULT_SIZE} grid if there is no grid.txt.
     *
     * <p>grid.txt is drawn square by square, one row per line (y = 0 on the first
     * row), with squares separated by spaces:</p>
     * <ul>
     *   <li>{@code .} open grass</li>
     *   <li>{@code X} obstacle</li>
     *   <li>{@code B5} paint bucket holding 5 paint (any whole number)</li>
     * </ul>
     * <p>Anything after {@code //} on a line is a comment, and blank lines are
     * skipped. Every row must have the same number of squares. Unlike Code.org's
     * grid files, the grid doesn't have to be square.</p>
     *
     * @return the new Neighborhood
     * @throws GooeyPainterException if the file can't be read or has a mistake in it
     */
    private static Neighborhood load() {
        Path file = findGridFile();
        if (file == null) {
            System.out.println("[GooeyPainter] No grid.txt found in " + Paths.get("").toAbsolutePath()
                    + " - using a blank " + DEFAULT_SIZE + " x " + DEFAULT_SIZE + " grid.");
            return new Neighborhood(DEFAULT_SIZE, DEFAULT_SIZE, "blank " + DEFAULT_SIZE + " x " + DEFAULT_SIZE);
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new GooeyPainterException("Couldn't read grid.txt: " + e.getMessage());
        }

        // First pass: split each non-comment line into squares and check the row lengths.
        List<String[]> rows = new ArrayList<>();
        List<Integer> rowLineNumbers = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int comment = line.indexOf("//");
            if (comment >= 0) line = line.substring(0, comment);
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] tokens = line.split("\\s+");
            if (!rows.isEmpty() && tokens.length != rows.get(0).length) {
                gridError(i + 1, "this row has " + tokens.length + " squares, but the first row has "
                        + rows.get(0).length + ". Every row needs the same number of squares.");
            }
            rows.add(tokens);
            rowLineNumbers.add(i + 1);
        }
        if (rows.isEmpty()) gridError(0, "the file doesn't have any rows of squares.");

        // Second pass: turn each square's text into grass, an obstacle, or a bucket.
        int w = rows.get(0).length;
        int h = rows.size();
        Neighborhood n = new Neighborhood(w, h, "grid.txt (" + w + " x " + h + ")");
        for (int y = 0; y < h; y++) {
            String[] row = rows.get(y);
            for (int x = 0; x < w; x++) {
                String tok = row[x];
                if (tok.equals(".")) {
                    // open grass: nothing to record
                } else if (tok.equalsIgnoreCase("X")) {
                    n.walls[x][y] = true;
                } else if ((tok.charAt(0) == 'B' || tok.charAt(0) == 'b') && tok.substring(1).matches("\\d+")) {
                    n.buckets[x][y] = Integer.parseInt(tok.substring(1));
                } else {
                    gridError(rowLineNumbers.get(y), "\"" + tok + "\" isn't a square. Use . for grass, "
                            + "X for an obstacle, or B followed by a number for a paint bucket (like B5).");
                }
            }
        }
        return n;
    }

    /**
     * Stops the program with a message pointing to the problem in grid.txt.
     *
     * @param lineNo the line number in grid.txt (counting comments and blank lines), or 0 for the whole file
     * @param msg    what is wrong
     * @throws GooeyPainterException always
     */
    private static void gridError(int lineNo, String msg) {
        throw new GooeyPainterException("Problem in grid.txt" + (lineNo > 0 ? " on line " + lineNo : "") + ": " + msg);
    }
}
