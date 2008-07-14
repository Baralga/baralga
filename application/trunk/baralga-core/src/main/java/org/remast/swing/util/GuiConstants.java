package org.remast.swing.util;

import java.awt.Color;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.remast.text.SmartTimeFormat;

/**
 * Misc constants for the application.
 * @author remast
 */
public interface GuiConstants {

    // ------------------------------------------------
    // Date Formats
    // ------------------------------------------------
    public static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
    public static final DateFormat timeFormat = new SmartTimeFormat(); //$NON-NLS-1$

    
    // ------------------------------------------------
    // Number Formats
    // ------------------------------------------------

    public static final NumberFormat durationFormat = new DecimalFormat("#0.00"); //$NON-NLS-1$

    
    // ------------------------------------------------
    // Colors
    // ------------------------------------------------

    public static final Color BEIGE = new Color(245, 245, 220);

    public static final Color VERY_LIGHT_GREY = new Color(240, 240, 240);

    public static final Color DARK_BLUE = new Color(64, 64, 128);

    public static final Highlighter[] HIGHLIGHTERS = new Highlighter[] { 
        HighlighterFactory.createSimpleStriping(BEIGE),
        new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.DARK_GRAY, Color.WHITE)
    };

}
