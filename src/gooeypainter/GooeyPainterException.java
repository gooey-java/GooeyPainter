package gooeypainter;

/**
 * Thrown when a {@link Painter} is asked to do something that isn't allowed, such
 * as moving off the grid or into an obstacle.
 *
 * <p>You normally never write {@code GooeyPainterException} in your own code. When
 * one is thrown and not caught, GooeyPainter stops your program and prints just
 * one line, the same way Code.org does:</p>
 *
 * <pre>
 * [EXCEPTION] Painter tried to move off the grid or into an obstacle.
 * </pre>
 *
 * <p>Unlike most Java exceptions, no stack trace is printed. The window stays open,
 * its title says STOPPED, and the Painter stays where it was, so you can see
 * exactly where your program went wrong.</p>
 *
 * <p>The messages are the same ones Code.org's Neighborhood uses:</p>
 *
 * <table class="striped">
 *   <caption>When each message appears</caption>
 *   <thead><tr><th scope="col">Message</th><th scope="col">Caused by</th></tr></thead>
 *   <tbody>
 *   <tr><td>Painter tried to move off the grid or into an obstacle.</td>
 *       <td>{@link Painter#move()} when {@link Painter#canMove()} is false</td></tr>
 *   <tr><td>Painter tried to paint off the grid or over an obstacle.</td>
 *       <td>{@link Painter#paint(String)} on a square with a paint bucket</td></tr>
 *   <tr><td>The provided color name is not recognized. ...</td>
 *       <td>{@link Painter#paint(String)} with an unknown color</td></tr>
 *   <tr><td>The direction provided is not recognized. ...</td>
 *       <td>a direction other than "north", "east", "south", or "west"</td></tr>
 *   <tr><td>The location specified isn't on the grid.</td>
 *       <td>creating a Painter at an (x, y) that isn't on the grid</td></tr>
 *   <tr><td>The square specified isn't on the grid.</td>
 *       <td>using a Painter that was created on top of an obstacle</td></tr>
 *   <tr><td>Problem in grid.txt ...</td>
 *       <td>a mistake in the grid.txt file; the message says which line</td></tr>
 *   </tbody>
 * </table>
 *
 * <p>Because it is a {@link RuntimeException}, you are never required to catch it.</p>
 */
public class GooeyPainterException extends RuntimeException {

    /** Version number for Java serialization, which GooeyPainter doesn't use. */
    private static final long serialVersionUID = 1L;

    // The messages below are copied from Code.org's English translations
    // (apps/i18n/javalab/en_us.json in github.com/code-dot-org/code-dot-org),
    // with two small typos fixed, so students see the wording they already know.

    /** Moving off the grid or into an obstacle. */
    static final String INVALID_MOVE =
            "Painter tried to move off the grid or into an obstacle.";

    /** Painting on a square that holds a paint bucket. */
    static final String INVALID_PAINT_LOCATION =
            "Painter tried to paint off the grid or over an obstacle.";

    /** A direction other than north, east, south, or west. */
    static final String INVALID_DIRECTION =
            "The direction provided is not recognized. Supported directions are \"north\", \"south\", \"east\", and \"west\".";

    /** A color that isn't a web color name or hex code. */
    static final String INVALID_COLOR =
            "The provided color name is not recognized. Refer to the documentation to see what color names are supported.";

    /** Creating a Painter outside the grid. */
    static final String INVALID_LOCATION =
            "The location specified isn't on the grid.";

    /** Using a square the Painter can't be on (an obstacle). */
    static final String GET_SQUARE_FAILED =
            "The square specified isn't on the grid.";

    /**
     * Creates an exception with the message that will be printed after
     * {@code [EXCEPTION]} if it stops the program.
     *
     * @param message what went wrong, written so a student can understand it
     */
    public GooeyPainterException(String message) {
        super(message);
    }
}
