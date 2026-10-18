package gooeypainter;

import java.util.HashMap;
import java.util.Map;

/**
 * The 140 web color names that Code.org's Neighborhood accepts, with their RGB values.
 *
 * <p>The list of names is copied from Code.org's ColorHelpers class, so any color
 * that works on Code.org works here. The RGB values are the standard CSS values for
 * each name. Names are stored in lowercase; {@link Neighborhood#parseColor(String)}
 * lowercases the student's text before looking it up.</p>
 */
final class WebColors {

    /** Not used: this class only holds the color table. */
    private WebColors() {}

    /** Maps each lowercase color name to its RGB value, such as "gold" to 0xFFD700. */
    static final Map<String, Integer> COLORS;

    static {
        Map<String, Integer> m = new HashMap<>();
        m.put("white", 0xFFFFFF);
        m.put("silver", 0xC0C0C0);
        m.put("gray", 0x808080);
        m.put("black", 0x000000);
        m.put("red", 0xFF0000);
        m.put("maroon", 0x800000);
        m.put("yellow", 0xFFFF00);
        m.put("olive", 0x808000);
        m.put("lime", 0x00FF00);
        m.put("green", 0x008000);
        m.put("aqua", 0x00FFFF);
        m.put("teal", 0x008080);
        m.put("blue", 0x0000FF);
        m.put("navy", 0x000080);
        m.put("fuchsia", 0xFF00FF);
        m.put("purple", 0x800080);
        m.put("mediumvioletred", 0xC71585);
        m.put("deeppink", 0xFF1493);
        m.put("palevioletred", 0xDB7093);
        m.put("hotpink", 0xFF69B4);
        m.put("lightpink", 0xFFB6C1);
        m.put("pink", 0xFFC0CB);
        m.put("darkred", 0x8B0000);
        m.put("firebrick", 0xB22222);
        m.put("crimson", 0xDC143C);
        m.put("indianred", 0xCD5C5C);
        m.put("lightcoral", 0xF08080);
        m.put("salmon", 0xFA8072);
        m.put("darksalmon", 0xE9967A);
        m.put("lightsalmon", 0xFFA07A);
        m.put("orangered", 0xFF4500);
        m.put("tomato", 0xFF6347);
        m.put("darkorange", 0xFF8C00);
        m.put("coral", 0xFF7F50);
        m.put("orange", 0xFFA500);
        m.put("darkkhaki", 0xBDB76B);
        m.put("gold", 0xFFD700);
        m.put("khaki", 0xF0E68C);
        m.put("peachpuff", 0xFFDAB9);
        m.put("palegoldenrod", 0xEEE8AA);
        m.put("moccasin", 0xFFE4B5);
        m.put("papayawhip", 0xFFEFD5);
        m.put("lightgoldenrodyellow", 0xFAFAD2);
        m.put("lemonchiffon", 0xFFFACD);
        m.put("lightyellow", 0xFFFFE0);
        m.put("brown", 0xA52A2A);
        m.put("saddlebrown", 0x8B4513);
        m.put("sienna", 0xA0522D);
        m.put("chocolate", 0xD2691E);
        m.put("darkgoldenrod", 0xB8860B);
        m.put("peru", 0xCD853F);
        m.put("rosybrown", 0xBC8F8F);
        m.put("goldenrod", 0xDAA520);
        m.put("sandybrown", 0xF4A460);
        m.put("tan", 0xD2B48C);
        m.put("burlywood", 0xDEB887);
        m.put("wheat", 0xF5DEB3);
        m.put("navajowhite", 0xFFDEAD);
        m.put("bisque", 0xFFE4C4);
        m.put("blanchedalmond", 0xFFEBCD);
        m.put("cornsilk", 0xFFF8DC);
        m.put("darkgreen", 0x006400);
        m.put("darkolivegreen", 0x556B2F);
        m.put("forestgreen", 0x228B22);
        m.put("seagreen", 0x2E8B57);
        m.put("olivedrab", 0x6B8E23);
        m.put("mediumseagreen", 0x3CB371);
        m.put("limegreen", 0x32CD32);
        m.put("springgreen", 0x00FF7F);
        m.put("mediumspringgreen", 0x00FA9A);
        m.put("darkseagreen", 0x8FBC8F);
        m.put("mediumaquamarine", 0x66CDAA);
        m.put("yellowgreen", 0x9ACD32);
        m.put("lawngreen", 0x7CFC00);
        m.put("chartreuse", 0x7FFF00);
        m.put("lightgreen", 0x90EE90);
        m.put("greenyellow", 0xADFF2F);
        m.put("palegreen", 0x98FB98);
        m.put("darkcyan", 0x008B8B);
        m.put("lightseagreen", 0x20B2AA);
        m.put("cadetblue", 0x5F9EA0);
        m.put("darkturquoise", 0x00CED1);
        m.put("mediumturquoise", 0x48D1CC);
        m.put("turquoise", 0x40E0D0);
        m.put("cyan", 0x00FFFF);
        m.put("aquamarine", 0x7FFFD4);
        m.put("paleturquoise", 0xAFEEEE);
        m.put("lightcyan", 0xE0FFFF);
        m.put("darkblue", 0x00008B);
        m.put("mediumblue", 0x0000CD);
        m.put("midnightblue", 0x191970);
        m.put("royalblue", 0x4169E1);
        m.put("steelblue", 0x4682B4);
        m.put("dodgerblue", 0x1E90FF);
        m.put("deepskyblue", 0x00BFFF);
        m.put("cornflowerblue", 0x6495ED);
        m.put("skyblue", 0x87CEEB);
        m.put("lightskyblue", 0x87CEFA);
        m.put("lightsteelblue", 0xB0C4DE);
        m.put("lightblue", 0xADD8E6);
        m.put("powderblue", 0xB0E0E6);
        m.put("indigo", 0x4B0082);
        m.put("darkmagenta", 0x8B008B);
        m.put("darkviolet", 0x9400D3);
        m.put("darkslateblue", 0x483D8B);
        m.put("blueviolet", 0x8A2BE2);
        m.put("darkorchid", 0x9932CC);
        m.put("magenta", 0xFF00FF);
        m.put("slateblue", 0x6A5ACD);
        m.put("mediumslateblue", 0x7B68EE);
        m.put("mediumorchid", 0xBA55D3);
        m.put("mediumpurple", 0x9370DB);
        m.put("orchid", 0xDA70D6);
        m.put("violet", 0xEE82EE);
        m.put("plum", 0xDDA0DD);
        m.put("thistle", 0xD8BFD8);
        m.put("lavender", 0xE6E6FA);
        m.put("mistyrose", 0xFFE4E1);
        m.put("antiquewhite", 0xFAEBD7);
        m.put("linen", 0xFAF0E6);
        m.put("beige", 0xF5F5DC);
        m.put("whitesmoke", 0xF5F5F5);
        m.put("lavenderblush", 0xFFF0F5);
        m.put("oldlace", 0xFDF5E6);
        m.put("aliceblue", 0xF0F8FF);
        m.put("seashell", 0xFFF5EE);
        m.put("ghostwhite", 0xF8F8FF);
        m.put("honeydew", 0xF0FFF0);
        m.put("floralwhite", 0xFFFAF0);
        m.put("azure", 0xF0FFFF);
        m.put("mintcream", 0xF5FFFA);
        m.put("snow", 0xFFFAFA);
        m.put("ivory", 0xFFFFF0);
        m.put("darkslategray", 0x2F4F4F);
        m.put("dimgray", 0x696969);
        m.put("slategray", 0x708090);
        m.put("lightslategray", 0x778899);
        m.put("darkgray", 0xA9A9A9);
        m.put("lightgray", 0xD3D3D3);
        m.put("gainsboro", 0xDCDCDC);
        COLORS = m;
    }
}
