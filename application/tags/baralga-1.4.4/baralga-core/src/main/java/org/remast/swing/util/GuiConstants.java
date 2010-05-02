package org.remast.swing.util;

import java.awt.Color;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * Misc constants for the application.
 * @author remast
 */
public abstract class GuiConstants {
    
    /** Hide constructor in utility class. */
    private GuiConstants() { }

    public static final Color BEIGE = new Color(245, 245, 220);

    public static final Color VERY_LIGHT_GREY = new Color(240, 240, 240);

    public static final Color DARK_BLUE = new Color(64, 64, 128);

    @SuppressWarnings(value = "MS_MUTABLE_ARRAY", justification = "We trust all callers")
    public static final Highlighter[] HIGHLIGHTERS = new Highlighter[] { 
        HighlighterFactory.createSimpleStriping(BEIGE),
        new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.DARK_GRAY, Color.WHITE)
    };

}
