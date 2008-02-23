package org.remast.baralga.model.utils;

import java.io.File;

import org.remast.baralga.gui.Settings;

public class ProTrackUtils {

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
