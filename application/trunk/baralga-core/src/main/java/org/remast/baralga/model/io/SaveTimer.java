package org.remast.baralga.model.io;

import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.remast.baralga.gui.model.PresentationModel;

/**
 * A timer to periodically save the model.
 * @author remast
 */
public class SaveTimer extends TimerTask {
    
    /** The logger. */
    private static final Log log = LogFactory.getLog(SaveTimer.class);

    /** The model. */
    private final PresentationModel model;

    /**
     * Create a time which periodically saves the model.
     * @param model the model
     */
    public SaveTimer(final PresentationModel model) {
        this.model = model;
    }

    @Override
    public void run() {
        try {
            this.model.save();
        } catch (Exception e) {
            log.error(e, e);
        }
    }

}
