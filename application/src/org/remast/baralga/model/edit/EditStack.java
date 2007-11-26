/**
 * 
 */
package org.remast.baralga.model.edit;

import java.util.Observable;
import java.util.Observer;

import org.remast.baralga.gui.events.ProTrackEvent;

/**
 * @author remast
 */
public class EditStack implements Observer {

    @Override
    public void update(Observable source, Object eventObject) {
        if (eventObject != null && eventObject instanceof ProTrackEvent) {
            final ProTrackEvent event = (ProTrackEvent) eventObject;

            switch (event.getType()) {

                // :INFO: Start and stop can not be undone.
                // case ProTrackEvent.START:
                // break;
                //
                // case ProTrackEvent.STOP:
                // break;

                case ProTrackEvent.PROJECT_CHANGED:
                    System.out.println("Changed");
                    break;

                case ProTrackEvent.PROJECT_ADDED:
                    System.out.println("Added");
                    break;

                case ProTrackEvent.PROJECT_REMOVED:
                    System.out.println("Removed");
                    break;
            }
        }
    }

}
