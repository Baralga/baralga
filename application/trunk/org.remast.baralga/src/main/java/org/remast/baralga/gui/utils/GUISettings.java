package org.remast.baralga.gui.utils;

import java.awt.Color;

import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.RolloverHighlighter;

/**
 * @author remast
 */
public interface GUISettings {

    public static Highlighter[] HIGHLIGHTERS = new Highlighter[] { AlternateRowHighlighter.beige,
            new RolloverHighlighter(Color.DARK_GRAY, Color.WHITE) };
}
