package org.remast.baralga.gui.panels.report;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.jdesktop.swingx.table.DatePickerCellEditor;
import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.BaralgaMain;
import org.remast.baralga.gui.dialogs.AddOrEditActivityDialog;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.panels.table.AllActivitiesTableFormat;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.swing.util.AWTUtils;
import org.remast.swing.util.GuiConstants;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.swing.EventComboBoxModel;
import ca.odell.glazedlists.swing.EventTableModel;

import com.jidesoft.swing.JideScrollPane;
import com.jidesoft.utils.BasicTransferable;

/**
 * @author remast
 */
@SuppressWarnings("serial")//$NON-NLS-1$
public class AllActitvitiesPanel extends JPanel implements Observer {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(BaralgaMain.class);

    /** The model. */
    private final PresentationModel model;

    private EventTableModel<ProjectActivity> tableModel;

    /**
     * Create a panel showing all activities of the given model.
     * 
     * @param model
     *            the model
     */
    public AllActitvitiesPanel(final PresentationModel model) {
        this.model = model;
        this.model.addObserver(this);

        this.setLayout(new BorderLayout());

        initialize();
    }


    /**
     * Set up GUI components.
     */
    private void initialize() {
        tableModel = new EventTableModel<ProjectActivity>(
                model.getActivitiesList(),
                new AllActivitiesTableFormat(model)
        );
        final JXTable table = new JXTable(tableModel);

        // :INFO: This corrupts the initial sorting. Would be nice though...
//        EventListJXTableSorting.install(table, model.getActivitiesList());
        table.setSortable(false);

        table.getColumn(1).setCellRenderer(
                new DefaultTableRenderer(new FormatStringValue(DateFormat.getDateInstance()))
        );
        table.getColumn(1).setCellEditor(
                new DatePickerCellEditor()
        );

        table.getColumn(2).setCellRenderer(
                new DefaultTableRenderer(new FormatStringValue(FormatUtils.createTimeFormat()))
        );
        table.getColumn(3).setCellRenderer(
                new DefaultTableRenderer(new FormatStringValue(FormatUtils.createTimeFormat()))
        );
        table.getColumn(4).setCellRenderer(
                new DefaultTableRenderer(new FormatStringValue(FormatUtils.durationFormat))
        );

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(final ListSelectionEvent event) {
                if (table.getSelectedRows() == null) {
                    table.setToolTipText(null);
                }

                double duration = 0;

                for (int i : table.getSelectedRows()) {
                    //                    int modelIndex = table.convertRowIndexToModel(i);
                	try {
                		duration += model.getActivitiesList().get(i).getDuration();
					} catch (IndexOutOfBoundsException e) {
						// Exception occurs when user has selected entries and then the filter changes.
						// Therefore we can safely ignore the exception and keep going.
					}
                }

                table.setToolTipText(textBundle.textFor("AllActivitiesPanel.tooltipDuration", FormatUtils.durationFormat.format(duration))); //$NON-NLS-1$
            }

        });

        final JPopupMenu contextMenu = new JPopupMenu();
        final Action editAction = new AbstractAction(textBundle.textFor("AllActitvitiesPanel.Edit"), new ImageIcon(getClass().getResource("/icons/gtk-edit.png"))) { //$NON-NLS-1$

            public void actionPerformed(final ActionEvent event) {
                // 1. Get selected activities
                int[] selectionIndices = table.getSelectedRows();

                // 2. Remove all selected activities
                if (selectionIndices.length == 0) {
                    return;
                }

                final AddOrEditActivityDialog editActivityDialog = new AddOrEditActivityDialog(
                        AWTUtils.getFrame(AllActitvitiesPanel.this), 
                        model, 
                        model.getActivitiesList().get(selectionIndices[0])
                );
                editActivityDialog.setVisible(true);
            }

        };
        editAction.setEnabled(false);
        contextMenu.add(editAction);

        contextMenu.add(new AbstractAction(textBundle.textFor("AllActitvitiesPanel.Delete"), new ImageIcon(getClass().getResource("/icons/gtk-delete.png"))) { //$NON-NLS-1$

            public void actionPerformed(final ActionEvent event) {

                // 1. Get selected activities
                final int[] selectionIndices = table.getSelectedRows();

                // 2. Remove all selected activities
                final List<ProjectActivity> selectedActivities = new ArrayList<ProjectActivity>(selectionIndices.length);
                for (int selectionIndex : selectionIndices) {
                    selectedActivities.add(
                            model.getActivitiesList().get(selectionIndex)
                    );
                }
                model.removeActivities(selectedActivities, this);
            }

        });

        final Action copyDescriptionAction = new AbstractAction(textBundle.textFor("AllActitvitiesPanel.CopyDescription"), new ImageIcon(getClass().getResource("/icons/gtk-copy.png"))) { //$NON-NLS-1$

            public void actionPerformed(final ActionEvent event) {
            	// 1. Get selected activities
            	final int[] selectionIndices = table.getSelectedRows();

            	if (selectionIndices.length == 0) {
            		return;
            	}

            	final ProjectActivity activity = model.getActivitiesList().get(selectionIndices[0]);

            	// Copy description to clipboard.
            	final Transferable transferable  = new BasicTransferable(org.remast.util.StringUtils.stripXmlTags(activity.getDescription()), activity.getDescription());
            	final Clipboard  clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            	clipboard.setContents(transferable, null);
            }

        };
        contextMenu.add(copyDescriptionAction);
        copyDescriptionAction.setEnabled(false);
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(final MouseEvent e) {
                checkForPopup(e);
            }

            @Override
            public void mouseClicked(final MouseEvent e) {
                checkForPopup(e);
            }

            @Override
            public void mousePressed(final MouseEvent e) {
                checkForPopup(e);
            }

            private void checkForPopup(final MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JTable table = (JTable) e.getSource();
                    int[] selectionIndices = table.getSelectedRows();
                    if (selectionIndices.length == 0) {
                        // select cell under mouse
                        int row = table.rowAtPoint(e.getPoint());
                        int column = table.columnAtPoint(e.getPoint());
                        table.changeSelection(row, column, false, false);
                    }

                    if (selectionIndices.length > 1) {
                        // edit action works only on a single cell
                        editAction.setEnabled(false);
                        copyDescriptionAction.setEnabled(false);
                    } else {
                        editAction.setEnabled(true);
                        copyDescriptionAction.setEnabled(true);
                    }
                    contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        table.setPreferredScrollableViewportSize(table.getPreferredSize());

        table.setHighlighters(GuiConstants.HIGHLIGHTERS);
        table.setCellEditor(new JXTable.GenericEditor());

        final TableColumn projectColumn = table.getColumn(0);
        final TableCellEditor cellEditor = new ComboBoxCellEditor(
                new JComboBox(
                        new EventComboBoxModel<Project>(model.getProjectList())
                )
        );
        projectColumn.setCellEditor(cellEditor);

        final JideScrollPane tableScrollPane = new JideScrollPane(table);
        this.add(tableScrollPane);
    }

    /**
     * {@inheritDoc}
     */
    public void update(final Observable source, final Object eventObject) {
        if (source == null || !(eventObject instanceof BaralgaEvent)) {
            return;
        }

        final BaralgaEvent event = (BaralgaEvent) eventObject;

        switch (event.getType()) {
        case BaralgaEvent.PROJECT_ACTIVITY_CHANGED:
            tableModel.fireTableDataChanged();
            break;

        case BaralgaEvent.PROJECT_ACTIVITY_ADDED:
        case BaralgaEvent.PROJECT_ACTIVITY_REMOVED:
            tableModel.fireTableDataChanged();
            break;

        case BaralgaEvent.PROJECT_CHANGED:
            tableModel.fireTableDataChanged();
            break;
        }
    }

}
