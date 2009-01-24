package org.remast.baralga.gui.panels.table;

import java.beans.PropertyChangeEvent;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.BaralgaMain;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.gui.WritableTableFormat;

/**
 * Format for table containing all tracked project activities.
 * @author remast
 */
public class AllActivitiesTableFormat implements WritableTableFormat<ProjectActivity> {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(BaralgaMain.class);

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
    public String getColumnName(final int col) {
        switch (col) {
        case 0:
            return textBundle.textFor("AllActivitiesTableFormat.ProjectHeading"); //$NON-NLS-1$
        case 1:
            return textBundle.textFor("AllActivitiesTableFormat.DateHeading"); //$NON-NLS-1$
        case 2:
            return textBundle.textFor("AllActivitiesTableFormat.StartHeading"); //$NON-NLS-1$
        case 3:
            return textBundle.textFor("AllActivitiesTableFormat.EndHeading"); //$NON-NLS-1$
        case 4:
            return textBundle.textFor("AllActivitiesTableFormat.DurationHeading"); //$NON-NLS-1$
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
            synchronized (FormatUtils.timeFormat) {
                return FormatUtils.timeFormat.format(activity.getStart());
            }
        case 3:
            synchronized (FormatUtils.timeFormat) {
                return FormatUtils.timeFormat.format(activity.getEnd());
            }
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

    public ProjectActivity setColumnValue(final ProjectActivity activity, final Object editedValue, final int column) {
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
            startCal.set(Calendar.YEAR, newCal.get(Calendar.YEAR));
            startCal.set(Calendar.DAY_OF_YEAR, newCal.get(Calendar.DAY_OF_YEAR));
            activity.setStart(startCal.getTime());

            // Copy date to end to preserve day in year
            final Calendar endCal = Calendar.getInstance();
            endCal.setTime(activity.getEnd());
            endCal.set(Calendar.YEAR, newCal.get(Calendar.YEAR));
            endCal.set(Calendar.DAY_OF_YEAR, newCal.get(Calendar.DAY_OF_YEAR));
            activity.setEnd(endCal.getTime());

            // Fire event
            final PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(activity, ProjectActivity.PROPERTY_DATE, oldDate, newDate);
            model.fireProjectActivityChangedEvent(activity, propertyChangeEvent);
        }
        // Start time
        else if (column == 2) {
            try {
                final Date newStart = FormatUtils.timeFormat.parse((String) editedValue);

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

                final Date newEnd = FormatUtils.timeFormat.parse((String) editedValue);
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
