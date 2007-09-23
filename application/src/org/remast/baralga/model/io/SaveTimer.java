package org.remast.baralga.model.io;

import java.util.TimerTask;

import org.remast.baralga.model.PresentationModel;

public class SaveTimer extends TimerTask {

    private PresentationModel model;

    public SaveTimer(final PresentationModel model) {
        this.model = model;
    }

    @Override
    public void run() {
        try {
            this.model.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
