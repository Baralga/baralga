package org.remast.baralga.gui.panels.report;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;
import org.jdesktop.swingx.plaf.basic.core.BasicTransferable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.jdesktop.swingx.table.DatePickerCellEditor;
import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.BaralgaMain;
import org.remast.baralga.gui.dialogs.AddOrEditActivityDialog;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.panels.table.AllActivitiesTableFormat;
import org.remast.baralga.gui.panels.table.ProjectActivityTextFilterator;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.swing.JSearchField;
import org.remast.swing.table.JHighligthedTable;
import org.remast.swing.util.AWTUtils;
import org.remast.text.SmartTimeFormat;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventComboBoxModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.google.common.eventbus.Subscribe;

/**
 * @author remast
 */
@SuppressWarnings("serial")//$NON-NLS-1$
public class AllActitvitiesPanel extends JPanel {

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
		this.model.getEventBus().register(this);

		this.setLayout(new BorderLayout());

		initialize();
	}


	/**
	 * Set up GUI components.
	 */
	private void initialize() {
		// Init search field and a list filtered list for the quick search
		final JSearchField searchField = new JSearchField();
		final MatcherEditor<ProjectActivity> textMatcherEditor = new TextComponentMatcherEditor<ProjectActivity>(searchField, new ProjectActivityTextFilterator());
		final FilterList<ProjectActivity> textFilteredIssues = new FilterList<ProjectActivity>(model.getActivitiesList(), textMatcherEditor);

		tableModel = new EventTableModel<ProjectActivity>(
				textFilteredIssues,
				new AllActivitiesTableFormat(model)
				);

		final JTable table = new JHighligthedTable(tableModel);

		TableComparatorChooser.install(
				table, 
				model.getActivitiesList(), 
				TableComparatorChooser.MULTIPLE_COLUMN_MOUSE
				);

		table.getColumn(table.getColumnName(1)).setCellRenderer(
				new DefaultTableRenderer(new FormatStringValue(FormatUtils.DAY_FORMAT))
				);
		table.getColumn(table.getColumnName(1)).setCellEditor(
				new DatePickerCellEditor()
				);

		table.getColumn(table.getColumnName(2)).setCellRenderer(
				new DefaultTableRenderer(new FormatStringValue(new SmartTimeFormat()))
				);
		table.getColumn(table.getColumnName(3)).setCellRenderer(
				new DefaultTableRenderer(new FormatStringValue(new SmartTimeFormat()))
				);
		table.getColumn(table.getColumnName(4)).setCellRenderer(
				new DefaultTableRenderer(new FormatStringValue(FormatUtils.DURATION_FORMAT))
				);

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(final ListSelectionEvent event) {
				if (table.getSelectedRows() == null) {
					table.setToolTipText(null);
				}

				double duration = 0;

				for (int i : table.getSelectedRows()) {
					try {
						duration += model.getActivitiesList().get(i).getDuration();
					} catch (IndexOutOfBoundsException e) {
						// Exception occurs when user has selected entries and then the filter changes.
						// Therefore we can safely ignore the exception and keep going.
					}
				}

				table.setToolTipText(textBundle.textFor("AllActivitiesPanel.tooltipDuration", FormatUtils.DURATION_FORMAT.format(duration))); //$NON-NLS-1$
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

		//        table.setHighlighters(GuiConstants.HIGHLIGHTERS);

		final TableColumn projectColumn = table.getColumn(table.getColumnName(0));
		final TableCellEditor cellEditor = new ComboBoxCellEditor(
				new JComboBox(
						new EventComboBoxModel<Project>(model.getProjectList())
						)
				);
		projectColumn.setCellEditor(cellEditor);

		final JScrollPane tableScrollPane = new JScrollPane(table);

		int border = 5;
		final double[][] size = {
				{ border, TableLayout.FILL, border}, // Columns
				{ border, TableLayout.PREFERRED, border, TableLayout.FILL } }; // Rows
		this.setLayout(new TableLayout(size));

		this.add(searchField, "1, 1");
		this.add(tableScrollPane, "1, 3");
	}

	/**
	 * {@inheritDoc}
	 */
	@Subscribe public void update(final Object eventObject) {
		if (!(eventObject instanceof BaralgaEvent)) {
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
