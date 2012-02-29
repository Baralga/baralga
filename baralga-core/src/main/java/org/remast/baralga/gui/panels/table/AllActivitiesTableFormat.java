package org.remast.baralga.gui.panels.table;

import java.beans.PropertyChangeEvent;
import java.text.ParseException;
import java.util.Date;

import org.joda.time.DateTime;
import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.BaralgaMain;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.text.SmartTimeFormat;
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
    public String getColumnName(final int column) {
        switch (column) {
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

    public Object getColumnValue(final ProjectActivity activity, final int column) {
        switch (column) {
        case 0:
            return activity.getProject();
        case 1:
            return activity.getStart().toDate();
        case 2:
            return FormatUtils.formatTime(activity.getStart());
        case 3:
            return FormatUtils.formatTime(activity.getEnd());
        case 4:
            return (Double) activity.getDuration();
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
            // Ignore null project
            if (editedValue == null) {
                return activity;
            }

            final Project oldProject = activity.getProject();
            activity.setProject((Project) editedValue);

            // Fire event
            final PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(activity, ProjectActivity.PROPERTY_PROJECT, oldProject, editedValue);
            model.fireProjectActivityChangedEvent(activity, propertyChangeEvent);
        } else if (column == 1) {
            // Day and month
            final Date oldDate = activity.getEnd().toDate();
            Date newDate = (Date) editedValue;

            activity.setDay(new DateTime(newDate));

            // Fire event
            final PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(activity, ProjectActivity.PROPERTY_DATE, oldDate, newDate);
            model.fireProjectActivityChangedEvent(activity, propertyChangeEvent);
        } else if (column == 2) {
            // Start time
            try {
                final Date oldStart = activity.getStart().toDate();

                int[] hoursMinutes = SmartTimeFormat.parseToHourAndMinutes((String) editedValue);
                activity.setStartTime(hoursMinutes[0], hoursMinutes[1]);

                // Fire event
                final PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(activity, ProjectActivity.PROPERTY_START,
                        oldStart, activity.getStart().toDate());
                model.fireProjectActivityChangedEvent(activity, propertyChangeEvent);
            } catch (IllegalArgumentException e) {
                // Ignore and don't save changes to model.
            } catch (ParseException e) {
                // Ignore and don't save changes to model.
            }
        } else if (column == 3) {
            // End time
            try {
                final Date oldEnd = activity.getEnd().toDate();

                int[] hoursMinutes = SmartTimeFormat.parseToHourAndMinutes((String) editedValue);
                activity.setEndTime(hoursMinutes[0], hoursMinutes[1]);

                // Fire event
                final PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(activity, ProjectActivity.PROPERTY_END,
                        oldEnd, activity.getEnd().toDate());
                model.fireProjectActivityChangedEvent(activity, propertyChangeEvent);
            } catch (IllegalArgumentException e) {
                // Ignore and don't save changes to model.
            } catch (ParseException e) {
                // Ignore and don't save changes to model.
            }
        }
        return activity;
    }

    /**
    public ProjectActivity setColumnValue(final ProjectActivity activity, final Object editedValue, final int column) {
        // Project
        if (column == 0) {
            ProjectActivity newActivity = activity.withProject((Project) editedValue);

            model.replaceActivity(activity, newActivity, this);
        }
        // Day and month
        else if (column == 1) {
            ProjectActivity newActivity = activity.withDay(new DateTime((Date) editedValue));

            model.replaceActivity(activity, newActivity, this);
        }
        // Start time
        else if (column == 2) {
            try {
//                // If nothing changed we're done
//                if (StringUtils.equals(FormatUtils.formatTime(activity.getStart()), (String) editedValue)) {
//                    return activity;
//                }

                int[] hoursMinutes = SmartTimeFormat.parseToHourAndMinutes((String) editedValue);
                ProjectActivity newActivity = activity.withStartTime(hoursMinutes[0], hoursMinutes[1]);

                model.replaceActivity(activity, newActivity, this);
            } catch( IllegalArgumentException e ) {
                // Ignore and don't save changes to model.
            } catch( ParseException e ) {
                // Ignore and don't save changes to model.
            }
        }
        // End time
        else if (column == 3) {
            try {
//                // If nothing changed we're done
//                if (StringUtils.equals(FormatUtils.formatTime(activity.getEnd()), (String) editedValue)) {
//                    return activity;
//                }

                int[] hoursMinutes = SmartTimeFormat.parseToHourAndMinutes((String) editedValue);
                ProjectActivity newActivity = activity.withEndTime(hoursMinutes[0], hoursMinutes[1]);

                model.replaceActivity(activity, newActivity, this);
            } catch( IllegalArgumentException e ) {
                // Ignore and don't save changes to model.
            } catch (ParseException e) {
                // Ignore and don't save changes to model.
            }
        }
        return activity;
    }
     */
}
