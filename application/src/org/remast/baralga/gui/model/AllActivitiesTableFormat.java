package org.remast.baralga.gui.model;


import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.remast.baralga.Messages;
import org.remast.baralga.gui.utils.Constants;
import org.remast.baralga.model.PresentationModel;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.utils.ProTrackUtils;

import ca.odell.glazedlists.gui.WritableTableFormat;

/**
 * Format for table containing all tracked project activities.
 * 
 * @author Jan Stamer
 *
 */
public class AllActivitiesTableFormat implements WritableTableFormat<ProjectActivity> {

    private PresentationModel model;

    public AllActivitiesTableFormat(PresentationModel model) {
        this.model = model;
    }

    /**
     * Project | Date | Start | End | Duration
     */
    public int getColumnCount() {
        return 5;
    }

    public String getColumnName(int col) {
        switch (col) {
        case 0:
            return Messages.getString("AllActivitiesTableFormat.ProjectHeading"); //$NON-NLS-1$
        case 1:
            return Messages.getString("AllActivitiesTableFormat.DateHeading"); //$NON-NLS-1$
        case 2:
            return Messages.getString("AllActivitiesTableFormat.StartHeading"); //$NON-NLS-1$
        case 3:
            return Messages.getString("AllActivitiesTableFormat.EndHeading"); //$NON-NLS-1$
        case 4:
            return Messages.getString("AllActivitiesTableFormat.DurationHeading"); //$NON-NLS-1$
        default:
            return ""; //$NON-NLS-1$
        }
    }

    public Object getColumnValue(ProjectActivity activity, int col) {
        switch (col) {
        case 0:
            return activity.getProject();
        case 1:
            return Constants.dayMonthFormat.format(activity.getStart());
        case 2:
            return Constants.hhMMFormat.format(activity.getStart());
        case 3:
            return Constants.hhMMFormat.format(activity.getEnd());
        case 4:
            return Constants.durationFormat.format(ProTrackUtils.calculateDuration(activity));
        default:
            return ""; //$NON-NLS-1$
        }
    }

    public boolean isEditable(ProjectActivity baseObject, int column) {
        // All columns except the Duration are editable
        return column != 4;
    }

    public ProjectActivity setColumnValue(ProjectActivity baseObject, Object editedValue, int column) {
        // Project
        if(column == 0) {
            baseObject.setProject((Project) editedValue);
            
            // Fire event
            model.fireProTrackActivityChangedEvent(baseObject);
        }
        // Day and month
        else if(column == 1) {
            try {
                Date newDate = Constants.dayMonthFormat.parse((String) editedValue);
                
                Calendar newCal = Calendar.getInstance();
                newCal.setTime(newDate);
                
                // Copy date to start
                Calendar startCal = Calendar.getInstance();
                startCal.setTime(baseObject.getStart());
                startCal.set(Calendar.DAY_OF_YEAR,  newCal.get(Calendar.DAY_OF_YEAR));
                baseObject.setStart(startCal.getTime());
                
                // Copy date to start
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(baseObject.getEnd());
                endCal.set(Calendar.DAY_OF_YEAR,  newCal.get(Calendar.DAY_OF_YEAR));
                baseObject.setEnd(endCal.getTime());
                
                // Fire event
                model.fireProTrackActivityChangedEvent(baseObject);
            } catch (ParseException e) {
                // Ignore parse error and just don't save value
            }
        }
        // Start time
        else if(column == 2) {
            try {
                Date newStart = Constants.hhMMFormat.parse((String) editedValue);
                baseObject.getStart().setHours(newStart.getHours());
                baseObject.getStart().setMinutes(newStart.getMinutes());

                // Fire event
                model.fireProTrackActivityChangedEvent(baseObject);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        // End time
        else if(column == 3) {
            try {
                Date newEnd = Constants.hhMMFormat.parse((String) editedValue);
                baseObject.getEnd().setHours(newEnd.getHours());
                baseObject.getEnd().setMinutes(newEnd.getMinutes());
                
                // Fire event
                model.fireProTrackActivityChangedEvent(baseObject);
            } catch (ParseException e) {
                // Ignore parse error and just don't save value
            }
        }
        return baseObject;
    }

}
