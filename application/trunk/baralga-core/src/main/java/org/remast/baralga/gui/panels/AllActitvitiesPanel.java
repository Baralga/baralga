package org.remast.baralga.gui.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.jdesktop.swingx.table.DatePickerCellEditor;
import org.remast.baralga.Messages;
import org.remast.baralga.gui.events.ProTrackEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.panels.table.AllActivitiesTableFormat;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;
import org.remast.gui.util.Constants;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventComboBoxModel;
import ca.odell.glazedlists.swing.EventTableModel;

/**
 * @author remast
 */
@SuppressWarnings("serial")//$NON-NLS-1$
public class AllActitvitiesPanel extends JXPanel implements Observer {

    /** The model. */
    private final PresentationModel model;

    /** The applied filter. */
    private Filter filter;

    /** The list of activities. */
    private SortedList<ProjectActivity> filteredActivitiesList;

    private EventTableModel<ProjectActivity> tableModel;

    /**
     * Create a panel showing all activities of the given model.
     * 
     * @param model
     *            the model
     */
    public AllActitvitiesPanel(final PresentationModel model) {
        this.model = model;
        this.filteredActivitiesList = new SortedList<ProjectActivity>(new BasicEventList<ProjectActivity>());
        this.model.addObserver(this);
        this.filter = this.model.getFilter();
        this.setLayout(new BorderLayout());

        initialize();
    }

    /**
     * Apply the filter to the list of activities.
     */
    private void applyFilter() {
        // clear filtered activities
        filteredActivitiesList.clear();

        if (filter != null) {
            List<ProjectActivity> filteredResult = filter.applyFilters(model.getActivitiesList());
            filteredActivitiesList.addAll(filteredResult);
        } else {
            filteredActivitiesList.addAll(model.getActivitiesList());
        }
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        applyFilter();

        tableModel = new EventTableModel<ProjectActivity>(this.filteredActivitiesList,
                new AllActivitiesTableFormat(model));
        final JXTable table = new JXTable(tableModel);

        table.getColumn(1).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(DateFormat.getDateInstance()))) ;
        table.getColumn(1).setCellEditor(new DatePickerCellEditor());
        
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (table.getSelectedRows() == null) {
                    table.setToolTipText(null);
                }
                
                double duration = 0;
                for(int i : table.getSelectedRows()) {
                    duration += filteredActivitiesList.get(i).getDuration();
                }
                table.setToolTipText(Messages.getString("AllActivitiesPanel.tooltipDuration") + Constants.durationFormat.format(duration)); //$NON-NLS-1$
                
            }
            
        });
        
        // :INFO: Sorting is done via GlazedLists. We need to disable sorting here to avoid to sort order icons.
        table.setSortable(false);

        final JPopupMenu menu = new JPopupMenu();
        menu.add(new AbstractAction(Messages.getString("AllActitvitiesPanel.Delete"), new ImageIcon(getClass().getResource("/icons/gtk-delete.png"))) { //$NON-NLS-1$

                    public void actionPerformed(ActionEvent event) {
                        // 1. Get selected activities
                        int[] selectionIndices = table.getSelectedRows();

                        // 2. Remove all selected activities
                        for (int selectionIndex : selectionIndices) {
                            model.removeActivity(filteredActivitiesList.get(selectionIndex), this);
                        }
                    }

                });

        table.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JTable source = (JTable) e.getSource();
                    int row = source.rowAtPoint(e.getPoint());
                    int column = source.columnAtPoint(e.getPoint());
                    source.changeSelection(row, column, false, false);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        table.setPreferredScrollableViewportSize(table.getPreferredSize());

        table.setHighlighters(Constants.HIGHLIGHTERS);
        table.setCellEditor(new JXTable.GenericEditor());

        final TableColumn projectColumn = table.getColumn(0);
        final TableCellEditor cellEditor = new ComboBoxCellEditor(new JComboBox(new EventComboBoxModel<Project>(model
                .getProjectList())));
        projectColumn.setCellEditor(cellEditor);

        JScrollPane table_scroll_pane = new JScrollPane(table);
        this.add(table_scroll_pane);
    }

    /**
     * @return the filter
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * Set and apply new filter.
     * 
     * @param filter
     *            the filter to set
     */
    private void setFilter(final Filter filter) {
        this.filter = filter;
        applyFilter();
    }

    /**
     * Update the panel from observed event.
     */
    public void update(Observable source, Object eventObject) {
        if (eventObject != null && eventObject instanceof ProTrackEvent) {
            final ProTrackEvent event = (ProTrackEvent) eventObject;

            switch (event.getType()) {

                case ProTrackEvent.PROJECT_ACTIVITY_ADDED:
                    applyFilter();
                    break;

                case ProTrackEvent.PROJECT_ACTIVITY_REMOVED:
                    applyFilter();
                    break;
                    
                case ProTrackEvent.PROJECT_ACTIVITY_CHANGED:
                    tableModel.fireTableDataChanged();
                    
                case ProTrackEvent.FILTER_CHANGED:
                    final Filter newFilter = (Filter) event.getData();
                    setFilter(newFilter);
                    break;
            }
        }
    }

}
