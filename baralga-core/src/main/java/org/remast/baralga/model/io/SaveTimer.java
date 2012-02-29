package org.remast.baralga.model.io;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.remast.baralga.gui.model.PresentationModel;

/**
 * A timer to periodically save the model.
 * @author remast
 */
public class SaveTimer extends TimerTask {
    
    /** The logger. */
    private static final Logger log = LoggerFactory.getLogger(SaveTimer.class);

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
        } catch (Exception exception) {
            log.error(exception.getLocalizedMessage(), exception);
        }
    }

}
