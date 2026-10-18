package gooeypainter;

import java.awt.Color;

/**
 * A Painter lives on a grid of squares. It can move forward, turn left, pick up
 * paint from paint buckets, and paint the square it is standing on.
 *
 * <p>Painter works the same way as the Painter in Code.org's Neighborhood, so code
 * written on Code.org runs here with only the import changed. The grid comes from
 * the {@code grid.txt} file in your project folder; see the
 * {@linkplain gooeypainter package overview} for its format.</p>
 *
 * <pre>{@code
 * Painter p = new Painter(2, 3, "north", 5);   // at (2, 3), facing north, 5 paint
 * p.paint("blue");                             // paints (2, 3); 4 paint left
 * p.move();                                    // moves up to (2, 2)
 * p.turnLeft();                                // now facing west
 * }</pre>
 *
 * <h2>Location and direction</h2>
 *
 * <p>(0, 0) is the top-left square. x increases going right and y increases going
 * <b>down</b>, so moving north subtracts 1 from y and moving south adds 1. A
 * Painter always faces one of four directions: {@code "north"} (up),
 * {@code "east"} (right), {@code "south"} (down), or {@code "west"} (left).
 * Directions can be written with any capitalization.</p>
 *
 * <h2>Paint</h2>
 *
 * <p>A Painter carries its own supply of paint. Each call to {@link #paint(String)}
 * uses one unit. More paint can be picked up one unit at a time from paint buckets
 * on the grid with {@link #takePaint()}, or set directly with {@link #setPaint(int)}.</p>
 *
 * <h2>No turnRight()</h2>
 *
 * <p>A Painter can only turn left. That's on purpose: you can teach it to turn
 * right (and much more) by writing your own class that {@code extends Painter}.
 * See the {@linkplain gooeypainter package overview} for an example.</p>
 *
 * <h2>Mistakes</h2>
 *
 * <p>Methods that can't do what they're asked either stop the program by throwing
 * a {@link GooeyPainterException} or print a message and keep going. Each method
 * below says which.</p>
 *
 * @see GooeyPainterException
 */
public class Painter {

    /** Like Code.org: {@code new Painter()} gets unlimited paint on grids at least this wide. */
    private static final int LARGE_GRID_SIZE = 20;

    /** The number given to the next Painter created, shown in the window's status line. */
    private static int nextId = 1;

    /** The grid this Painter lives on (there is only one per program). */
    private final Neighborhood world;

    /** This Painter's number: 1 for the first Painter created, 2 for the second, and so on. */
    private final int id;

    /** True if this Painter never runs out of paint. */
    private final boolean hasInfinitePaint;

    /** The column this Painter is in. 0 is the left edge. */
    private int x;

    /** The row this Painter is in. 0 is the top edge. */
    private int y;

    /** "north", "east", "south", or "west", always lowercase. */
    private String direction;

    /** Units of paint this Painter is carrying. Can go below 0 only with unlimited paint. */
    private int paint;

    /** False after hidePainter(), true after showPainter(). */
    private boolean visible = true;

    /** The last color this Painter painted with, used to color its brush in the window. */
    private Color lastColor = null;

    /**
     * Creates a Painter at (0, 0), facing east, with no paint.
     *
     * <p>As on Code.org, a Painter made this way has <b>unlimited paint</b> when the
     * grid is 20 or more squares wide. On smaller grids it starts with no paint, so
     * it must pick some up with {@link #takePaint()} or be given some with
     * {@link #setPaint(int)} before it can paint.</p>
     *
     * <p>If this is the first Painter in the program, the grid is loaded from
     * grid.txt and the window opens.</p>
     *
     * @throws GooeyPainterException if grid.txt has a mistake in it
     */
    public Painter() {
        this(0, 0, "east", 0, true);
    }

    /**
     * Creates a Painter at a location of your choice, facing a direction of your
     * choice, carrying a set amount of paint.
     *
     * <pre>{@code
     * Painter artist = new Painter(0, 0, "east", 100);
     * }</pre>
     *
     * <p>If this is the first Painter in the program, the grid is loaded from
     * grid.txt and the window opens.</p>
     *
     * @param x         the column to start in, from 0 (the left edge) to the grid's width minus 1
     * @param y         the row to start in, from 0 (the top edge) to the grid's height minus 1
     * @param direction the direction to face: {@code "north"}, {@code "east"},
     *                  {@code "south"}, or {@code "west"} (any capitalization)
     * @param paint     how many units of paint the Painter starts with
     * @throws GooeyPainterException if (x, y) isn't on the grid, if the direction
     *                               isn't one of the four above, or if grid.txt has a
     *                               mistake in it
     */
    public Painter(int x, int y, String direction, int paint) {
        this(x, y, direction, paint, false);
    }

    /**
     * Does the work for both public constructors: loads the grid if needed, checks
     * the direction and location, and adds the Painter to the window.
     *
     * @param x                      the starting column
     * @param y                      the starting row
     * @param direction              the starting direction, in any capitalization
     * @param paint                  the starting amount of paint
     * @param couldHaveInfinitePaint true only for {@code new Painter()}, which gets
     *                               unlimited paint on large grids
     */
    private Painter(int x, int y, String direction, int paint, boolean couldHaveInfinitePaint) {
        world = Neighborhood.get();
        String dir = directionFromString(direction);
        synchronized (world) {
            if (!world.inBounds(x, y)) {
                throw new GooeyPainterException(GooeyPainterException.INVALID_LOCATION);
            }
            this.hasInfinitePaint = couldHaveInfinitePaint && world.width >= LARGE_GRID_SIZE;
            this.id = nextId++;
            this.x = x;
            this.y = y;
            this.direction = dir;
            this.paint = paint;
            world.painters.add(this);
        }
        world.step(name() + " appeared at (" + x + ", " + y + ") facing " + dir + ".");
    }

    // ================================================================== actions

    /**
     * Turns the Painter 90 degrees to the left, without moving it.
     *
     * <p>North turns to west, west to south, south to east, and east to north.
     * Turning left three times is the same as turning right once.</p>
     */
    public void turnLeft() {
        synchronized (world) {
            switch (direction) {
                case "north": direction = "west"; break;
                case "west":  direction = "south"; break;
                case "south": direction = "east"; break;
                default:      direction = "north"; break;
            }
        }
        world.step(name() + " turned left and is now facing " + direction + ".");
    }

    /**
     * Moves the Painter one square forward in the direction it is facing.
     *
     * <p>Moving onto a paint bucket or a painted square is fine. Moving off the edge
     * of the grid or into an obstacle is not: it <b>stops the program</b>. Use
     * {@link #canMove()} first if you aren't sure the way is clear:</p>
     *
     * <pre>{@code
     * while (p.canMove()) {
     *     p.move();
     * }
     * }</pre>
     *
     * @throws GooeyPainterException if the square ahead is off the grid or is an obstacle
     */
    public void move() {
        synchronized (world) {
            int nx = x + dx(direction);
            int ny = y + dy(direction);
            if (!world.isValidLocation(nx, ny)) {
                throw new GooeyPainterException(GooeyPainterException.INVALID_MOVE);
            }
            x = nx;
            y = ny;
        }
        world.step(name() + " moved " + direction + " to (" + x + ", " + y + ").");
    }

    /**
     * Paints the square the Painter is standing on, using one unit of paint.
     * Painting a square that is already painted replaces its old color.
     *
     * <pre>{@code
     * p.paint("red");        // a web color name
     * p.paint("SkyBlue");    // capital letters don't matter
     * p.paint("#FF8800");    // a hex code, such as one from pixilart.com
     * }</pre>
     *
     * <p>If the Painter has no paint, nothing is painted, the message
     * "There is no more paint in the painter's bucket" is printed, and the program
     * keeps going. Use {@link #hasPaint()} to check first.</p>
     *
     * @param color one of the 140 web color names (such as {@code "red"},
     *              {@code "gold"}, {@code "skyblue"}, {@code "darkgreen"}), or a hex code
     *              in the form {@code "#RRGGBB"} or {@code "#RGB"}
     * @throws GooeyPainterException if the color isn't recognized, or if the square
     *                               has a paint bucket on it
     */
    public void paint(String color) {
        if (!hasPaint()) {
            world.consoleMessage("There is no more paint in the painter's bucket");
            return;
        }
        Color c = Neighborhood.parseColor(color);
        synchronized (world) {
            checkCurrentSquare();
            if (c == null) {
                throw new GooeyPainterException(GooeyPainterException.INVALID_COLOR);
            }
            if (world.buckets[x][y] > 0) {
                throw new GooeyPainterException(GooeyPainterException.INVALID_PAINT_LOCATION);
            }
            world.paint[x][y] = c;
            world.colorNames[x][y] = color;
            paint--;
            lastColor = c;
        }
        world.step(name() + " painted (" + x + ", " + y + ") " + color + ".");
    }

    /**
     * Removes the paint from the square the Painter is standing on, turning it back
     * into plain grass. This doesn't give the Painter any paint back.
     *
     * <p>If the square has no paint on it, the message "There's no paint to remove
     * here" is printed and the program keeps going. Use {@link #isOnPaint()} to
     * check first.</p>
     */
    public void scrapePaint() {
        boolean hadPaint;
        synchronized (world) {
            checkCurrentSquare();
            hadPaint = world.paint[x][y] != null;
            world.paint[x][y] = null;
            world.colorNames[x][y] = null;
        }
        world.step(name() + " scraped (" + x + ", " + y + ").");
        if (!hadPaint) {
            world.consoleMessage("There's no paint to remove here");
        }
    }

    /**
     * Takes one unit of paint from the paint bucket the Painter is standing on and
     * adds it to the Painter's own supply. The number on the bucket goes down by 1,
     * and when it reaches 0 the bucket disappears.
     *
     * <p>To take all of the paint from a bucket:</p>
     *
     * <pre>{@code
     * while (p.isOnBucket()) {
     *     p.takePaint();
     * }
     * }</pre>
     *
     * <p>If there is no bucket here, the message "There is no paint to collect here"
     * is printed and the program keeps going.</p>
     */
    public void takePaint() {
        boolean took;
        synchronized (world) {
            checkCurrentSquare();
            took = world.buckets[x][y] > 0;
            if (took) {
                world.buckets[x][y]--;
                paint++;
            }
        }
        if (took) {
            world.step(name() + " took 1 paint.");
        } else {
            world.consoleMessage("There is no paint to collect here");
        }
    }

    /**
     * Hides the Painter so the squares under it can be seen. A hidden Painter can
     * still move, turn, and paint; you just can't see it. A good way to end a
     * pixel art program is to hide the Painter so the whole picture shows.
     *
     * @see #showPainter()
     */
    public void hidePainter() {
        synchronized (world) { visible = false; }
        world.step(name() + " is hidden.");
    }

    /**
     * Shows the Painter again after {@link #hidePainter()}.
     */
    public void showPainter() {
        synchronized (world) { visible = true; }
        world.step(name() + " is visible.");
    }

    /**
     * Hides every paint bucket on the grid. The buckets are still there and still
     * hold paint; they just aren't drawn. This affects all buckets, not only the
     * one under this Painter.
     *
     * <p>Code.org's Painter has this method but leaves it out of its documentation.</p>
     *
     * @see #showBuckets()
     */
    public void hideBuckets() {
        world.bucketsVisible = false;
        world.step("Paint buckets are hidden.");
    }

    /**
     * Shows every paint bucket on the grid again after {@link #hideBuckets()}.
     *
     * <p>Code.org's Painter has this method but leaves it out of its documentation.</p>
     */
    public void showBuckets() {
        world.bucketsVisible = true;
        world.step("Paint buckets are visible.");
    }

    /**
     * Sets how much paint the Painter is carrying, replacing whatever it had.
     *
     * <p>If {@code paint} is negative, nothing changes and the message "Paint amount
     * must not be a negative number." is printed. A Painter with unlimited paint
     * (see {@link #Painter()}) ignores this method.</p>
     *
     * @param paint the new number of units of paint, 0 or more
     */
    public void setPaint(int paint) {
        if (paint < 0) {
            System.out.println("Paint amount must not be a negative number.");
            return;
        }
        if (hasInfinitePaint) return;
        synchronized (world) { this.paint = paint; }
    }

    // ================================================================== questions

    /**
     * Returns how many units of paint the Painter is carrying.
     *
     * <p>For a Painter with unlimited paint (see {@link #Painter()}), this counts
     * down below 0 as it paints, the same as on Code.org; use {@link #hasPaint()}
     * to check whether a Painter can paint.</p>
     *
     * @return the number of units of paint in the Painter's bucket
     */
    public int getMyPaint() {
        synchronized (world) { return paint; }
    }

    /**
     * Checks whether the Painter has any paint to paint with.
     *
     * @return {@code true} if the Painter has at least one unit of paint (or unlimited
     *         paint), {@code false} if it has run out
     */
    public boolean hasPaint() {
        if (hasInfinitePaint) return true;
        synchronized (world) { return paint > 0; }
    }

    /**
     * Checks whether the square the Painter is standing on has been painted.
     *
     * @return {@code true} if the square has paint on it, {@code false} if it is
     *         plain grass
     */
    public boolean isOnPaint() {
        synchronized (world) {
            checkCurrentSquare();
            return world.paint[x][y] != null;
        }
    }

    /**
     * Checks whether the Painter is standing on a paint bucket that still has paint
     * in it. (Empty buckets disappear, so this is the same as asking whether
     * {@link #takePaint()} would work here.)
     *
     * @return {@code true} if there is a paint bucket here, {@code false} otherwise
     */
    public boolean isOnBucket() {
        synchronized (world) {
            checkCurrentSquare();
            return world.buckets[x][y] > 0;
        }
    }

    /**
     * Returns the color of the square the Painter is standing on, written exactly
     * the way it was given to {@link #paint(String)}. For example, after
     * {@code paint("Red")} this returns {@code "Red"}, not {@code "red"}.
     *
     * <pre>{@code
     * if (p.isOnPaint() && p.getColor().equals("red")) {
     *     p.scrapePaint();
     * }
     * }</pre>
     *
     * @return the color of this square, or {@code null} if it isn't painted
     */
    public String getColor() {
        synchronized (world) {
            checkCurrentSquare();
            return world.colorNames[x][y];
        }
    }

    /**
     * Checks whether the Painter can move forward one square in the direction it is
     * facing, without actually moving. Call this before {@link #move()} to avoid
     * stopping the program by running into an edge or obstacle.
     *
     * @return {@code true} if the square ahead is on the grid and isn't an obstacle
     */
    public boolean canMove() {
        return canMove(getDirection());
    }

    /**
     * Checks whether the Painter could move one square in the given direction,
     * without turning or moving. For example, {@code canMove("north")} checks the
     * square above the Painter, whichever way it is facing.
     *
     * @param direction the direction to check: {@code "north"}, {@code "east"},
     *                  {@code "south"}, or {@code "west"} (any capitalization)
     * @return {@code true} if the square in that direction is on the grid and isn't
     *         an obstacle
     * @throws GooeyPainterException if the direction isn't one of the four above
     */
    public boolean canMove(String direction) {
        String dir = directionFromString(direction);
        synchronized (world) {
            return world.isValidLocation(x + dx(dir), y + dy(dir));
        }
    }

    /**
     * Checks whether the Painter is facing north (up the screen).
     *
     * @return {@code true} if the Painter is facing north
     */
    public boolean isFacingNorth() { return getDirection().equals("north"); }

    /**
     * Checks whether the Painter is facing east (to the right).
     *
     * @return {@code true} if the Painter is facing east
     */
    public boolean isFacingEast() { return getDirection().equals("east"); }

    /**
     * Checks whether the Painter is facing south (down the screen).
     *
     * @return {@code true} if the Painter is facing south
     */
    public boolean isFacingSouth() { return getDirection().equals("south"); }

    /**
     * Checks whether the Painter is facing west (to the left).
     *
     * @return {@code true} if the Painter is facing west
     */
    public boolean isFacingWest() { return getDirection().equals("west"); }

    /**
     * An older name for {@link #isFacingNorth()}, kept because Code.org still accepts it.
     *
     * @return {@code true} if the Painter is facing north
     * @deprecated use {@link #isFacingNorth()} instead
     */
    @Deprecated
    public boolean facingNorth() { return isFacingNorth(); }

    /**
     * An older name for {@link #isFacingEast()}, kept because Code.org still accepts it.
     *
     * @return {@code true} if the Painter is facing east
     * @deprecated use {@link #isFacingEast()} instead
     */
    @Deprecated
    public boolean facingEast() { return isFacingEast(); }

    /**
     * An older name for {@link #isFacingSouth()}, kept because Code.org still accepts it.
     *
     * @return {@code true} if the Painter is facing south
     * @deprecated use {@link #isFacingSouth()} instead
     */
    @Deprecated
    public boolean facingSouth() { return isFacingSouth(); }

    /**
     * An older name for {@link #isFacingWest()}, kept because Code.org still accepts it.
     *
     * @return {@code true} if the Painter is facing west
     * @deprecated use {@link #isFacingWest()} instead
     */
    @Deprecated
    public boolean facingWest() { return isFacingWest(); }

    /**
     * Returns the column the Painter is in.
     *
     * @return the Painter's x-coordinate, where 0 is the left edge of the grid
     */
    public int getX() {
        synchronized (world) { return x; }
    }

    /**
     * Returns the row the Painter is in.
     *
     * @return the Painter's y-coordinate, where 0 is the top edge of the grid
     */
    public int getY() {
        synchronized (world) { return y; }
    }

    /**
     * Returns the direction the Painter is facing.
     *
     * @return {@code "north"}, {@code "east"}, {@code "south"}, or {@code "west"},
     *         always in lowercase
     */
    public String getDirection() {
        synchronized (world) { return direction; }
    }

    // ================================================================== library internals
    // Package-private: used by the window to draw this Painter without triggering
    // checks or animation steps.

    /**
     * Returns this Painter's number.
     *
     * @return this Painter's number, starting at 1
     */
    int id() { return id; }

    /**
     * Tells the window whether to draw this Painter.
     *
     * @return false while hidden by hidePainter()
     */
    boolean visible() { return visible; }

    /**
     * Tells the window what color to draw this Painter's brush.
     *
     * @return the last color painted, or null if this Painter hasn't painted yet
     */
    Color lastColor() { return lastColor; }

    /**
     * Gives the window this Painter's direction while it already holds the lock.
     *
     * @return the direction
     */
    String directionRaw() { return direction; }

    /**
     * Gives the window this Painter's column while it already holds the lock.
     *
     * @return the column
     */
    int xRaw() { return x; }

    /**
     * Gives the window this Painter's row while it already holds the lock.
     *
     * @return the row
     */
    int yRaw() { return y; }

    /**
     * Gives the window this Painter's paint count while it already holds the lock.
     *
     * @return the paint count
     */
    int paintRaw() { return paint; }

    /**
     * Tells the window whether to show an infinity sign for this Painter's paint.
     *
     * @return true if this Painter never runs out of paint
     */
    boolean infinitePaint() { return hasInfinitePaint; }

    /**
     * Returns the name used for this Painter in the status line.
     *
     * @return a name such as "Painter #1"
     */
    private String name() {
        return "Painter #" + id;
    }

    /**
     * Matches Code.org's Grid.getSquare: the Painter's own square must be on the
     * grid and not an obstacle. This can only fail for a Painter that was created
     * on top of an obstacle, which Code.org also allows.
     *
     * @throws GooeyPainterException if the Painter is standing on an obstacle
     */
    private void checkCurrentSquare() {
        if (!world.isValidLocation(x, y)) {
            throw new GooeyPainterException(GooeyPainterException.GET_SQUARE_FAILED);
        }
    }

    /**
     * Turns a direction typed by a student into the lowercase form used inside the class.
     *
     * @param text a direction in any capitalization
     * @return "north", "east", "south", or "west"
     * @throws GooeyPainterException if the text isn't one of the four directions
     */
    private static String directionFromString(String text) {
        if (text != null) {
            String d = text.toLowerCase();
            if (d.equals("north") || d.equals("east") || d.equals("south") || d.equals("west")) return d;
        }
        throw new GooeyPainterException(GooeyPainterException.INVALID_DIRECTION);
    }

    /**
     * Returns how far x changes when moving one square in a direction.
     *
     * @param dir a lowercase direction
     * @return how x changes when moving that way: 1 for east, -1 for west, otherwise 0
     */
    static int dx(String dir) {
        return dir.equals("east") ? 1 : dir.equals("west") ? -1 : 0;
    }

    /**
     * Returns how far y changes when moving one square in a direction.
     *
     * @param dir a lowercase direction
     * @return how y changes when moving that way: 1 for south (down), -1 for north (up), otherwise 0
     */
    static int dy(String dir) {
        return dir.equals("south") ? 1 : dir.equals("north") ? -1 : 0;
    }
}
