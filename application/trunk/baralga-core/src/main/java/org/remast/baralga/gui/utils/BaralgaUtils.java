package org.remast.baralga.gui.utils;

import java.io.File;

import org.remast.baralga.gui.Settings;

/**
 * Misc utility methods for Baralga.
 * @author remast
 */
public abstract class BaralgaUtils {
    
    /** Hide constructor. */
    private BaralgaUtils() {}

    /**
     * Checks whether the Baralga directory exists and creates it if necessary.
     */
    public static void checkOrCreateBaralgaDir() {
        final File baralgaDir = Settings.getBaralgaDirectory();
        if (!baralgaDir.exists()) {
            baralgaDir.mkdir();
        }
    }

}
