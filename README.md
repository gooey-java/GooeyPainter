# GooeyPainter

A Painter that moves around a grid and paints squares, made to work just like the
Neighborhood from Code.org's CSA curriculum, but in a real IDE such as VS Code.
Part of the [Gooey Java](https://gooey-java.github.io/) libraries for AP Computer
Science A.

**Website:** https://gooey-java.github.io/GooeyPainter/

**Docs:** https://gooey-java.github.io/GooeyPainter/docs/

**Download:** [GooeyPainter.jar](https://gooey-java.github.io/GooeyPainter/GooeyPainter.jar) and [GooeyPainter-sources.jar](https://gooey-java.github.io/GooeyPainter/GooeyPainter-sources.jar)

## Quick start

1. Put both jars in your project's `lib` folder. VS Code shows the docs when you
   hover over GooeyPainter code.
2. Optionally, add a `grid.txt` file to your project folder to set up the grid
   (without one, you get a blank 16 x 16 grid).
3. Import, write your code, and hit Run:

```java
import gooeypainter.*;

public class MyNeighborhood {
    public static void main(String[] args) {
        Painter artist = new Painter(0, 0, "east", 10);
        artist.paint("red");
        artist.move();
        artist.paint("#FFA500");
        artist.hidePainter();
    }
}
```

A `grid.txt` is drawn one row per line, with spaces between squares:

```
// . is grass, X is an obstacle, B5 is a paint bucket holding 5 paint
.  .  .  .  .  .
.  B5 .  X  .  .
.  .  .  X  .  B3
.  .  .  .  .  .
```

## Coming from Code.org

Code written for Code.org's Neighborhood runs unchanged except for the import:
use `import gooeypainter.*;` instead of `import org.code.neighborhood.*;`.
Painter has the same methods, the same console messages, and the same
`[EXCEPTION]` errors, because its behavior was checked against Code.org's own
source code ([code-dot-org/javabuilder](https://github.com/code-dot-org/javabuilder)).

GooeyPainter adds a few things Code.org doesn't have: a simpler `grid.txt`
format that doesn't need to be square, a speed slider (including instant), and
a Show coordinates option that labels every row and column.

GooeyPainter is not made by or affiliated with Code.org.

## What's in this repository

| Path | What it is |
|---|---|
| `src/gooeypainter/` | The library's source code |
| `GooeyPainter.jar` | The compiled library |
| `GooeyPainter-sources.jar` | The source code as a jar, for hover docs in VS Code |
| `site/` | The showcase page (the website's home page) |
| `build.sh`, `build.bat` | Build the jars, the Javadoc, and the website |
| `javadoc-options.txt` | Settings for the docs (title, footer, Java version) |
| `.github/workflows/pages.yml` | Publishes the website after every push |
| `LICENSE` | The MIT License |

Inside `src/gooeypainter/`, `Painter.java` and `GooeyPainterException.java` are
the classes students use, and `package-info.java` is the docs' front page.
`Neighborhood.java` (the grid and grid.txt), `NeighborhoodWindow.java` (the
window and drawing), and `WebColors.java` (color names) are internal.

## Building and publishing

You need a JDK 17 or newer. Run `./build.sh` (Mac/Linux) or `build.bat`
(Windows). That rebuilds `GooeyPainter.jar` and `GooeyPainter-sources.jar` in this folder
(compiled to run on Java 11 or newer) and builds the whole website in
`_site/`: the showcase page, the Javadoc in `_site/docs/`, and both jars. Open
`_site/index.html` to preview it. (`_site/` isn't committed; GitHub builds its own.)

Publishing is automatic: after every push to `main`, GitHub runs `build.sh` and
publishes `_site/` to https://gooey-java.github.io/GooeyPainter/. The **Actions** tab
on GitHub shows each run. This needs a one-time setting: **Settings > Pages >
Source: GitHub Actions**.

The showcase page is `site/index.html`. Its styles come from
`https://gooey-java.github.io/gooey.css` in the gooey-java.github.io repo, which
every Gooey Java page shares.

## License

GooeyPainter is released under the [MIT License](LICENSE): anyone may use, copy,
change, and share it, as long as the copyright notice and license are kept with it.
