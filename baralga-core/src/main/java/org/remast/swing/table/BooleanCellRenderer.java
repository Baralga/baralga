package org.remast.swing.table;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * Imlementation of TableCellRenderer.
 * <br> Alone method returns renderer for boolean if <tt>value</tt> is not-null
 * instance of Boolean class, or default table cell renderer in the another case
 *
 * @author Aleksandr
 */
@SuppressWarnings("serial")
public class BooleanCellRenderer extends DefaultTableCellRenderer {

    /**
     * Renderer for true not-null Boolean
     */
    private static final BooleanRenderer booleanRenderer = new BooleanRenderer();

    /**
     * Returns the table cell renderer. If <tt>value</tt> is not-null instance of
     * Boolean class, returns renderer for boolean. Else returns default table
     * cell renderer
     *
     * @param table  the <code>JTable</code>
     * @param value  the value to assign to the cell at
     *          <code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus true if cell has focus
     * @param row  the row of the cell to render
     * @param column the column of the cell to render
     * @return the default table cell renderer
     * @see javax.swing.JComponent#isPaintingForPrint()
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                          boolean isSelected, boolean hasFocus, int row, int column) {

        // if value is null - cell mustn't contain any CheckBoxes
        if ( value == null )
            return super.getTableCellRendererComponent(table,
                    "", isSelected, hasFocus, row, column);

        return booleanRenderer.getTableCellRendererComponent(table,
            value, isSelected, hasFocus, row, column);
    }


    /**
     * True Boolean renderer
     */
    private static class BooleanRenderer extends JCheckBox implements TableCellRenderer {
    	
    	private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    	public BooleanRenderer() {
    		super();
    		setHorizontalAlignment(JLabel.CENTER);
    		setBorderPainted(true);
    	}

    	public Component getTableCellRendererComponent(JTable table, Object value,
    			boolean isSelected, boolean hasFocus, int row, int column) {
    		if (isSelected) {
    			setForeground(table.getSelectionForeground());
    			super.setBackground(table.getSelectionBackground());
    		}
    		else {
    			setForeground(table.getForeground());
    			setBackground(table.getBackground());
    		}
    		setSelected((value != null && ((Boolean)value).booleanValue()));

    		if (hasFocus) {
    			setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
    		} else {
    			setBorder(noFocusBorder);
    		}

    		return this;
    	}
    }

}
