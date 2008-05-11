package org.remast.gui.util;

import java.awt.Color;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

/**
 * @author remast
 */
public interface Constants {

    // ------------------------------------------------
    // Date Formats
    // ------------------------------------------------
    public static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
    public static final SimpleDateFormat hhMMFormat = new SimpleDateFormat("HH:mm"); //$NON-NLS-1$

    
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
