package org.remast.baralga.gui.panels.table;

import java.beans.PropertyChangeEvent;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.remast.baralga.Messages;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.gui.util.Constants;
import org.remast.util.DateUtils;

import ca.odell.glazedlists.gui.WritableTableFormat;

/**
 * Format for table containing all tracked project activities.
 * @author remast
 */
public class AllActivitiesTableFormat implements WritableTableFormat<ProjectActivity> {

    /** The model. */
    private PresentationModel model;

    public AllActivitiesTableFormat(final PresentationModel model) {
        this.model = model;
    }

    /**
     * Project | Date | Start | End | Duration
     */
    public int getColumnCount() {
        return 5;
    }

    /**
     * Gets the name of the given column.
     * @param column the number of the column
     */
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

    public Object getColumnValue(final ProjectActivity activity,final int column) {
        switch (column) {
            case 0:
                return activity.getProject();
            case 1:
                return activity.getStart();
            case 2:
                return Constants.HHmmFormat.format(activity.getStart());
            case 3:
                return Constants.HHmmFormat.format(activity.getEnd());
            case 4:
                return activity.getDuration();
            default:
                return ""; //$NON-NLS-1$
        }
    }

    public boolean isEditable(final ProjectActivity baseObject, final int column) {
        // All columns except the duration are editable
        return column != 4;
    }

    public ProjectActivity setColumnValue(ProjectActivity activity, Object editedValue, int column) {
        // Project
        if (column == 0) {
            final Project oldProject = activity.getProject();
            activity.setProject((Project) editedValue);

            // Fire event
            final PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(activity, ProjectActivity.PROPERTY_PROJECT, oldProject, editedValue);
            model.fireProjectActivityChangedEvent(activity, propertyChangeEvent);
        }
        // Day and month
        else if (column == 1) {
            final Date oldDate = activity.getEnd();
            Date newDate = (Date) editedValue;

            final Calendar newCal = Calendar.getInstance();
            newCal.setTime(newDate);

            // Save date to start
            final Calendar startCal = Calendar.getInstance();
            startCal.setTime(activity.getStart());
            startCal.set(Calendar.DAY_OF_YEAR, newCal.get(Calendar.DAY_OF_YEAR));
            activity.setStart(startCal.getTime());

            // Copy date to end to preserve day in year
            final Calendar endCal = Calendar.getInstance();
            endCal.setTime(activity.getEnd());
            endCal.set(Calendar.DAY_OF_YEAR, newCal.get(Calendar.DAY_OF_YEAR));
            activity.setEnd(endCal.getTime());

            // Fire event
            final PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(activity, ProjectActivity.PROPERTY_DATE, oldDate, newDate);
            model.fireProjectActivityChangedEvent(activity, propertyChangeEvent);
        }
        // Start time
        else if (column == 2) {
            try {
                final Date newStart = DateUtils.parseTimeSmart((String) editedValue);

                final Date oldStart = activity.getStart();
                activity.getStart().setHours(newStart.getHours());
                activity.getStart().setMinutes(newStart.getMinutes());

                // Fire event
                final PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(activity, ProjectActivity.PROPERTY_START, oldStart, newStart);
                model.fireProjectActivityChangedEvent(activity, propertyChangeEvent);
            } catch (ParseException e) {
                // Ignore and don't save changes to model.
            }
        }
        // End time
        else if (column == 3) {
            try {
                final Date oldEnd = activity.getEnd();

                final Date newEnd = DateUtils.parseTimeSmart((String) editedValue);
                activity.getEnd().setHours(newEnd.getHours());
                activity.getEnd().setMinutes(newEnd.getMinutes());

                // Fire event
                final PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(activity, ProjectActivity.PROPERTY_END, oldEnd, newEnd);
                model.fireProjectActivityChangedEvent(activity, propertyChangeEvent);
            } catch (ParseException e) {
                // Ignore and don't save changes to model.
            }
        }
        return activity;
    }

}
