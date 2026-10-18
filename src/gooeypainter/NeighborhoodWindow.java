package gooeypainter;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

/**
 * The window that shows the grid and everything on it. It is created automatically
 * by {@link Neighborhood} when the first {@link Painter} is made; students never
 * create it themselves.
 *
 * <p>The window has three parts: the grid itself ({@link GridPanel}), a status line
 * describing the last action, and a row of controls (Show coordinates and the speed
 * slider). Closing the window ends the program.</p>
 *
 * <p>All methods except {@link #refresh()} run on Swing's event dispatch thread.
 * {@code refresh()} is called from the student's thread and hands the work over to
 * Swing's thread.</p>
 */
final class NeighborhoodWindow {

    /**
     * Milliseconds to pause after each action, one entry per speed slider position,
     * from slowest (left) to fastest (right). The last entry, 0, means no pause.
     */
    private static final int[] DELAYS = {1000, 700, 500, 350, 250, 150, 80, 40, 10, 0};

    /** The slider position the window starts at (1 = slowest). Position 5 is 250 ms per action. */
    private static final int START_SPEED = 5;

    /** The grid being shown. */
    private final Neighborhood world;

    /** Draws the grid. */
    private final GridPanel panel;

    /** Shows the last action, or an error in red. */
    private final JLabel statusLabel = new JLabel(" ");

    /** The window itself; its title changes to say STOPPED after an exception. */
    private final JFrame frame;

    /**
     * True while a redraw is already waiting to run on Swing's thread. Fast programs
     * can call {@link #refresh()} thousands of times a second; this flag folds those
     * calls into one redraw instead of flooding Swing with requests.
     */
    private final AtomicBoolean refreshPending = new AtomicBoolean(false);

    /**
     * Builds and shows the window. Must be called on Swing's event dispatch thread.
     *
     * @param world the grid to show
     */
    NeighborhoodWindow(Neighborhood world) {
        this.world = world;
        panel = new GridPanel(world);

        frame = new JFrame("GooeyPainter - " + world.source);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);

        JSlider speed = new JSlider(1, DELAYS.length, START_SPEED);
        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        labels.put(1, new JLabel("slow"));
        labels.put(DELAYS.length, new JLabel("fast"));
        speed.setLabelTable(labels);
        speed.setPaintLabels(true);
        speed.setPreferredSize(new Dimension(180, speed.getPreferredSize().height));
        speed.addChangeListener(e -> world.delayMs = DELAYS[speed.getValue() - 1]);
        world.delayMs = DELAYS[speed.getValue() - 1];

        JCheckBox coords = new JCheckBox("Show coordinates");
        coords.addActionListener(e -> {
            panel.showCoords = coords.isSelected();
            panel.repaint();
        });

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 2));
        controls.add(coords);
        controls.add(new JLabel("Speed:"));
        controls.add(speed);

        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 13f));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(statusLabel, BorderLayout.NORTH);
        bottom.add(controls, BorderLayout.SOUTH);
        frame.add(bottom, BorderLayout.SOUTH);

        frame.pack();
        frame.setMinimumSize(new Dimension(520, 300));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Updates the status line and title and redraws the grid. Safe to call from any
     * thread; the actual work happens later on Swing's thread.
     */
    void refresh() {
        if (refreshPending.compareAndSet(false, true)) {
            SwingUtilities.invokeLater(() -> {
                refreshPending.set(false);
                statusLabel.setText(world.status);
                statusLabel.setForeground(world.statusIsError ? new Color(0xC62828) : Color.DARK_GRAY);
                if (world.stopped) frame.setTitle("GooeyPainter - " + world.source + " - STOPPED");
                panel.repaint();
            });
        }
    }

    // =================================================================== drawing

    /**
     * The part of the window that draws the grid: grass, paint, obstacles, buckets,
     * and Painters, plus row and column numbers when Show coordinates is checked.
     *
     * <p>Squares stay square: the grid is scaled to fit the panel and centered, so
     * resizing the window makes the squares bigger or smaller.</p>
     */
    static final class GridPanel extends JPanel {

        /** Version number for Java serialization, which GooeyPainter doesn't use. */
        private static final long serialVersionUID = 1L;

        /** Biggest square size, in pixels, when the window first opens. */
        private static final int MAX_START_SQUARE = 48;

        /** Smallest square size, in pixels, when the window first opens. */
        private static final int MIN_START_SQUARE = 8;

        /** Screen height, in pixels, left over for the title bar, status line, and controls. */
        private static final int ROOM_FOR_CONTROLS = 190;

        /** The grid being drawn. */
        private final transient Neighborhood world;

        /** True while the Show coordinates box is checked. */
        boolean showCoords = false;

        /**
         * Picks a starting size so the whole window fits on the screen, including the
         * title bar and the controls under the grid.
         *
         * @param world the grid to draw
         */
        GridPanel(Neighborhood world) {
            this.world = world;
            // Fit the grid on screen, leaving room for the title bar and the controls below it.
            java.awt.Rectangle screen = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getMaximumWindowBounds();
            int maxW = (int) (screen.width * 0.85);
            int maxH = screen.height - ROOM_FOR_CONTROLS;
            int s = Math.min(MAX_START_SQUARE, Math.min(maxW / world.width, maxH / world.height));
            s = Math.max(MIN_START_SQUARE, s);
            setPreferredSize(new Dimension(world.width * s, world.height * s));
            setBackground(new Color(0x2E3B2A));
        }

        /**
         * Draws the grid whenever Swing asks.
         *
         * @param g0 the graphics to draw with
         */
        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            render((Graphics2D) g0, getWidth(), getHeight(), world, showCoords);
        }

        /**
         * Draws the whole grid into an area of the given size. Kept separate from
         * {@link #paintComponent(Graphics)} so it can also draw into an image (handy
         * for testing without a screen).
         *
         * <p>Layers, bottom to top: each square's grass or paint, obstacles and
         * buckets, faint grid lines, the Painters (in creation order), then the
         * coordinate labels.</p>
         *
         * @param g          the graphics to draw with
         * @param panelW     width of the area, in pixels
         * @param panelH     height of the area, in pixels
         * @param world      the grid to draw
         * @param showCoords true to label the rows and columns
         */
        static void render(Graphics2D g, int panelW, int panelH, Neighborhood world, boolean showCoords) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int margin = showCoords ? 24 : 0;
            int s = Math.max(1, Math.min((panelW - margin) / world.width, (panelH - margin) / world.height));
            int ox = margin + (panelW - margin - s * world.width) / 2;
            int oy = margin + (panelH - margin - s * world.height) / 2;

            synchronized (world) {
                for (int x = 0; x < world.width; x++) {
                    for (int y = 0; y < world.height; y++) {
                        int cx = ox + x * s, cy = oy + y * s;
                        Color p = world.paint[x][y];
                        if (p != null) {
                            g.setColor(p);
                            g.fillRect(cx, cy, s, s);
                        } else {
                            g.setColor(((x + y) & 1) == 0 ? new Color(0x8BC66A) : new Color(0x80BC5F));
                            g.fillRect(cx, cy, s, s);
                        }
                        if (world.walls[x][y]) drawObstacle(g, cx, cy, s);
                        if (world.bucketsVisible && world.buckets[x][y] > 0) drawBucket(g, cx, cy, s, world.buckets[x][y]);
                    }
                }
                // grid lines
                if (s >= 6) {
                    g.setColor(new Color(0, 0, 0, 28));
                    for (int x = 0; x <= world.width; x++) g.drawLine(ox + x * s, oy, ox + x * s, oy + world.height * s);
                    for (int y = 0; y <= world.height; y++) g.drawLine(ox, oy + y * s, ox + world.width * s, oy + y * s);
                }
                for (Painter p : world.painters) {
                    if (p.visible()) drawPainter(g, p, ox + p.xRaw() * s, oy + p.yRaw() * s, s);
                }
            }

            if (showCoords) drawCoords(g, world, ox, oy, s);
        }

        /**
         * Writes column numbers above the grid and row numbers to its left. On small
         * squares only every 5th (or 10th) number is written so they don't overlap.
         *
         * @param g     the graphics to draw with
         * @param world the grid being drawn
         * @param ox    x pixel of the grid's left edge
         * @param oy    y pixel of the grid's top edge
         * @param s     size of one square, in pixels
         */
        private static void drawCoords(Graphics2D g, Neighborhood world, int ox, int oy, int s) {
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, Math.max(8, Math.min(12, s / 2))));
            FontMetrics fm = g.getFontMetrics();
            g.setColor(new Color(0xE8F0E0));
            int every = s >= 16 ? 1 : s >= 8 ? 5 : 10;
            for (int x = 0; x < world.width; x++) {
                if (x % every != 0) continue;
                String t = String.valueOf(x);
                g.drawString(t, ox + x * s + (s - fm.stringWidth(t)) / 2, oy - 6);
            }
            for (int y = 0; y < world.height; y++) {
                if (y % every != 0) continue;
                String t = String.valueOf(y);
                g.drawString(t, ox - 6 - fm.stringWidth(t), oy + y * s + (s + fm.getAscent()) / 2 - 2);
            }
        }

        /**
         * Draws an obstacle as a gray stone filling most of the square.
         *
         * @param g the graphics to draw with
         * @param x x pixel of the square's left edge
         * @param y y pixel of the square's top edge
         * @param s size of the square, in pixels
         */
        private static void drawObstacle(Graphics2D g, int x, int y, int s) {
            double p = s * 0.12;
            g.setColor(new Color(0x5B6670));
            g.fill(new RoundRectangle2D.Double(x + p, y + p * 1.6, s - 2 * p, s - 2.4 * p, s * 0.3, s * 0.3));
            g.setColor(new Color(0x8A96A0));
            g.fill(new RoundRectangle2D.Double(x + p * 1.6, y + p * 1.6, s - 3.6 * p, s * 0.32, s * 0.25, s * 0.25));
        }

        /**
         * Draws a paint bucket with the amount of paint left written on it (when the
         * square is big enough to read).
         *
         * @param g      the graphics to draw with
         * @param x      x pixel of the square's left edge
         * @param y      y pixel of the square's top edge
         * @param s      size of the square, in pixels
         * @param amount units of paint left in the bucket
         */
        private static void drawBucket(Graphics2D g, int x, int y, int s, int amount) {
            double top = y + s * 0.30, bottom = y + s * 0.80;
            double lt = x + s * 0.22, rt = x + s * 0.78, lb = x + s * 0.30, rb = x + s * 0.70;
            Path2D body = new Path2D.Double();
            body.moveTo(lt, top);
            body.lineTo(rt, top);
            body.lineTo(rb, bottom);
            body.lineTo(lb, bottom);
            body.closePath();
            g.setStroke(new BasicStroke((float) Math.max(1, s * 0.04)));
            g.setColor(new Color(0x37474F));
            g.draw(new java.awt.geom.Arc2D.Double(lt, y + s * 0.12, rt - lt, s * 0.36, 0, 180, java.awt.geom.Arc2D.OPEN));
            g.setColor(new Color(0xB0BEC5));
            g.fill(body);
            g.setColor(new Color(0x37474F));
            g.draw(body);
            g.setColor(new Color(0x546E7A));
            g.fill(new Ellipse2D.Double(lt, top - s * 0.05, rt - lt, s * 0.10));
            if (s >= 16) {
                String t = String.valueOf(amount);
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(8, (int) (s * 0.26))));
                FontMetrics fm = g.getFontMetrics();
                g.setColor(new Color(0x263238));
                g.drawString(t, (float) (x + (s - fm.stringWidth(t)) / 2.0), (float) (top + (bottom - top) * 0.5 + fm.getAscent() * 0.45));
            }
        }

        /** Smock colors, one per Painter in creation order, so several Painters can be told apart. */
        private static final Color[] SHIRTS = {
            new Color(0x1E88E5), new Color(0xE53935), new Color(0x8E24AA),
            new Color(0xFB8C00), new Color(0x00897B), new Color(0x6D4C41)
        };

        /**
         * Draws a Painter: an artist in a red beret and a smock, holding a brush toward
         * the direction it faces. The brush tip and the dab on the apron show the last
         * color it painted (gold before it has painted). Eyes show which way it faces;
         * facing north, you see the back of its head. On squares 30 pixels or bigger, a
         * badge in the corner shows how much paint it has (an infinity sign for
         * unlimited paint). On squares under 20 pixels, it is drawn as a simple dot
         * with an arrow so it stays readable.
         *
         * @param g  the graphics to draw with
         * @param p  the Painter to draw
         * @param ox x pixel of its square's left edge
         * @param oy y pixel of its square's top edge
         * @param s  size of the square, in pixels
         */
        private static void drawPainter(Graphics2D g, Painter p, int ox, int oy, int s) {
            String dir = p.directionRaw();
            int dx = Painter.dx(dir), dy = Painter.dy(dir);
            Color shirt = SHIRTS[(p.id() - 1) % SHIRTS.length];
            double cx = ox + s / 2.0;

            if (s < 20) { // tiny cells: simple dot with a pointer
                g.setColor(shirt);
                g.fill(new Ellipse2D.Double(ox + s * 0.18, oy + s * 0.18, s * 0.64, s * 0.64));
                drawPointer(g, ox, oy, s, dx, dy);
                return;
            }

            // shadow
            g.setColor(new Color(0, 0, 0, 45));
            g.fill(new Ellipse2D.Double(cx - s * 0.26, oy + s * 0.82, s * 0.52, s * 0.10));

            if (dir.equals("north")) drawBrush(g, p, ox, oy, s, dir);

            // smock
            g.setColor(shirt);
            g.fill(new RoundRectangle2D.Double(cx - s * 0.21, oy + s * 0.50, s * 0.42, s * 0.36, s * 0.18, s * 0.18));
            // apron
            g.setColor(new Color(255, 255, 255, 215));
            g.fill(new RoundRectangle2D.Double(cx - s * 0.11, oy + s * 0.58, s * 0.22, s * 0.26, s * 0.06, s * 0.06));
            Color dab = p.lastColor() != null ? p.lastColor() : new Color(0xFFB300);
            g.setColor(dab);
            g.fill(new Ellipse2D.Double(cx - s * 0.04, oy + s * 0.66, s * 0.07, s * 0.07));

            // head
            g.setColor(new Color(0xF2C29B));
            g.fill(new Ellipse2D.Double(cx - s * 0.16, oy + s * 0.22, s * 0.32, s * 0.32));

            // eyes (none when facing away)
            g.setColor(new Color(0x2B2B2B));
            double ey = oy + s * 0.37, er = s * 0.045;
            double[] eyeX;
            switch (dir) {
                case "east":  eyeX = new double[]{cx + s * 0.03, cx + s * 0.10}; break;
                case "west":  eyeX = new double[]{cx - s * 0.10, cx - s * 0.03}; break;
                case "south": eyeX = new double[]{cx - s * 0.065, cx + s * 0.065}; break;
                default:      eyeX = new double[0]; break;
            }
            for (double ex : eyeX) g.fill(new Ellipse2D.Double(ex - er / 2, ey - er / 2, er, er));

            // beret
            double tilt = dx * s * 0.03;
            g.setColor(new Color(0xC62828));
            g.fill(new Ellipse2D.Double(cx - s * 0.20 + tilt, oy + s * 0.15, s * 0.40, s * 0.15));
            g.fill(new Ellipse2D.Double(cx - s * 0.025 + tilt, oy + s * 0.10, s * 0.05, s * 0.07));

            if (!dir.equals("north")) drawBrush(g, p, ox, oy, s, dir);

            drawPointer(g, ox, oy, s, dx, dy);

            // paint count badge
            if (s >= 30) {
                String t = p.infinitePaint() ? "\u221E" : String.valueOf(p.paintRaw());
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(9, (int) (s * 0.20))));
                FontMetrics fm = g.getFontMetrics();
                double bw = fm.stringWidth(t) + s * 0.10, bh = fm.getAscent() + s * 0.02;
                double bx = ox + s * 0.03, by = oy + s * 0.03;
                g.setColor(new Color(255, 255, 255, 225));
                g.fill(new RoundRectangle2D.Double(bx, by, bw, bh, bh * 0.6, bh * 0.6));
                g.setColor(new Color(0x37474F));
                g.drawString(t, (float) (bx + s * 0.05), (float) (by + fm.getAscent() - s * 0.01));
            }
        }

        /**
         * Draws the Painter's brush pointing in its direction: a wooden handle with a
         * tip in the last color painted, and a hand where it is held. When facing
         * north it is drawn before the body, so it looks like it is behind the Painter.
         *
         * @param g   the graphics to draw with
         * @param p   the Painter holding the brush
         * @param ox  x pixel of its square's left edge
         * @param oy  y pixel of its square's top edge
         * @param s   size of the square, in pixels
         * @param dir the direction the Painter faces
         */
        private static void drawBrush(Graphics2D g, Painter p, int ox, int oy, int s, String dir) {
            double ax, ay, tx, ty;
            switch (dir) {
                case "east":  ax = ox + s * 0.62; ay = oy + s * 0.66; tx = ox + s * 0.95; ty = oy + s * 0.56; break;
                case "west":  ax = ox + s * 0.38; ay = oy + s * 0.66; tx = ox + s * 0.05; ty = oy + s * 0.56; break;
                case "north": ax = ox + s * 0.74; ay = oy + s * 0.66; tx = ox + s * 0.80; ty = oy + s * 0.08; break;
                default:      ax = ox + s * 0.72; ay = oy + s * 0.58; tx = ox + s * 0.80; ty = oy + s * 0.96; break;
            }
            double mx = ax + (tx - ax) * 0.68, my = ay + (ty - ay) * 0.68;
            g.setStroke(new BasicStroke((float) (s * 0.055), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(0x8D6E63));
            g.draw(new Line2D.Double(ax, ay, mx, my));
            g.setStroke(new BasicStroke((float) (s * 0.10), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(p.lastColor() != null ? p.lastColor() : new Color(0xFFB300));
            g.draw(new Line2D.Double(mx, my, tx, ty));
            g.setStroke(new BasicStroke((float) Math.max(1, s * 0.02)));
            g.setColor(new Color(0, 0, 0, 90));
            g.draw(new Line2D.Double(mx, my, tx, ty));
            // hand
            g.setColor(new Color(0xF2C29B));
            g.fill(new Ellipse2D.Double(ax - s * 0.05, ay - s * 0.05, s * 0.10, s * 0.10));
        }

        /**
         * Draws a small white arrow at the edge of the square showing which way the
         * Painter faces, so its direction is clear even at a glance.
         *
         * @param g  the graphics to draw with
         * @param ox x pixel of the square's left edge
         * @param oy y pixel of the square's top edge
         * @param s  size of the square, in pixels
         * @param dx how x changes moving forward (-1, 0, or 1)
         * @param dy how y changes moving forward (-1, 0, or 1)
         */
        private static void drawPointer(Graphics2D g, int ox, int oy, int s, int dx, int dy) {
            double cx = ox + s / 2.0, cy = oy + s / 2.0;
            double tipX = cx + dx * s * 0.49, tipY = cy + dy * s * 0.49;
            double baseX = cx + dx * s * 0.36, baseY = cy + dy * s * 0.36;
            double half = s * 0.10;
            Path2D tri = new Path2D.Double();
            tri.moveTo(tipX, tipY);
            tri.lineTo(baseX + dy * half, baseY + dx * half);
            tri.lineTo(baseX - dy * half, baseY - dx * half);
            tri.closePath();
            g.setColor(new Color(255, 255, 255, 235));
            g.fill(tri);
            g.setStroke(new BasicStroke((float) Math.max(1, s * 0.02)));
            g.setColor(new Color(0x263238));
            g.draw(tri);
        }
    }
}
