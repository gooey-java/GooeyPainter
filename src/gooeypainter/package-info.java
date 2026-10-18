/**
 * GooeyPainter: a Painter that moves around a grid and paints squares, made to
 * work just like the Neighborhood from Code.org's CSA curriculum, but in a real
 * IDE such as VS Code.
 *
 * <p>Add {@code import gooeypainter.*;} to the top of your file and create a
 * {@link gooeypainter.Painter}. A window showing the grid opens automatically the
 * first time a Painter is made, and every action the Painter takes is animated in
 * it. There is nothing else to set up: no window to create and nothing to start.</p>
 *
 * <pre>{@code
 * import gooeypainter.*;
 *
 * public class MyNeighborhood {
 *     public static void main(String[] args) {
 *         // Start at (0, 0), facing east, with 10 units of paint
 *         Painter artist = new Painter(0, 0, "east", 10);
 *
 *         artist.paint("red");
 *         artist.move();
 *         artist.paint("#FFA500");   // hex codes work too, like ones from pixilart.com
 *         artist.move();
 *         artist.paint("gold");
 *
 *         artist.hidePainter();      // get out of the way so the art shows
 *     }
 * }
 * }</pre>
 *
 * <h2>Coordinates</h2>
 *
 * <p>Each square has an (x, y) location. (0, 0) is the <b>top-left</b> square.
 * x increases going right (east) and y increases going <b>down</b> (south), the
 * same as on Code.org and in most computer graphics.</p>
 *
 * <pre>
 *            x = 0     x = 1     x = 2
 *   y = 0  (0, 0)    (1, 0)    (2, 0)        north is up
 *   y = 1  (0, 1)    (1, 1)    (2, 1)        south is down
 *   y = 2  (0, 2)    (1, 2)    (2, 2)
 * </pre>
 *
 * <h2>Making your own Painter class</h2>
 *
 * <p>A Painter can only turn left. To teach it new tricks, write a class that
 * {@code extends Painter} and add your own methods, just like PainterPlus on Code.org:</p>
 *
 * <pre>{@code
 * import gooeypainter.*;
 *
 * public class PainterPlus extends Painter {
 *
 *     public PainterPlus(int x, int y, String direction, int paint) {
 *         super(x, y, direction, paint);
 *     }
 *
 *     public void turnRight() {
 *         turnLeft();
 *         turnLeft();
 *         turnLeft();
 *     }
 * }
 * }</pre>
 *
 * <h2>Setting up the grid with grid.txt</h2>
 *
 * <p>The grid is read from a file named {@code grid.txt} in your project folder
 * (the folder you opened in VS Code). If there is no grid.txt, you get a blank
 * 16 x 16 grid. Draw the grid one row per line, with y = 0 on the first row, and
 * put spaces between the squares:</p>
 *
 * <ul>
 *   <li>{@code .} is open grass</li>
 *   <li>{@code X} is an obstacle the Painter can't move into</li>
 *   <li>{@code B} followed by a number is a paint bucket holding that much paint,
 *       such as {@code B5}</li>
 * </ul>
 *
 * <p>Lines that start with {@code //} are comments. Every row must have the same
 * number of squares. This grid.txt makes a 6 x 4 grid with two buckets and two
 * obstacles:</p>
 *
 * <pre>
 * // My neighborhood
 * .  .  .  .  .  .
 * .  B5 .  X  .  .
 * .  .  .  X  .  B3
 * .  .  .  .  .  .
 * </pre>
 *
 * <h2>Colors</h2>
 *
 * <p>{@link gooeypainter.Painter#paint(String)} accepts the same colors as Code.org:
 * any of the 140 standard web color names, such as {@code "red"}, {@code "skyblue"},
 * {@code "darkgreen"}, or {@code "gold"} (capital letters don't matter), or a hex
 * code such as {@code "#FF8800"} or {@code "#F80"}. Hex codes make it easy to use
 * exact colors from a pixel art editor like pixilart.com.</p>
 *
 * <h2>The window</h2>
 *
 * <p>The speed slider at the bottom of the window controls how fast the Painter
 * moves; all the way to the right is instant, which is handy for big pixel art.
 * The <b>Show coordinates</b> box labels every row and column. The status line
 * describes the last thing that happened.</p>
 *
 * <h2>Errors</h2>
 *
 * <p>Some mistakes stop your program, like on Code.org. When that happens, a line
 * starting with {@code [EXCEPTION]} is printed, the window's title says STOPPED,
 * and the window stays open so you can see where the Painter was. These mistakes
 * stop the program by throwing a {@link gooeypainter.GooeyPainterException}:</p>
 *
 * <ul>
 *   <li>moving off the grid or into an obstacle</li>
 *   <li>painting on a square that has a paint bucket</li>
 *   <li>using a color or direction that isn't recognized</li>
 *   <li>creating a Painter at a location that isn't on the grid</li>
 *   <li>a problem in grid.txt</li>
 * </ul>
 *
 * <p>Other mistakes just print a message, and the program keeps going:</p>
 *
 * <ul>
 *   <li>painting with no paint: "There is no more paint in the painter's bucket"</li>
 *   <li>taking paint where there is no bucket: "There is no paint to collect here"</li>
 *   <li>scraping a square with no paint: "There's no paint to remove here"</li>
 * </ul>
 *
 * <p>To avoid these, check first with methods like
 * {@link gooeypainter.Painter#canMove()}, {@link gooeypainter.Painter#hasPaint()},
 * and {@link gooeypainter.Painter#isOnBucket()}.</p>
 */
package gooeypainter;
