//---------------------------------------------------------
// $Id$ 
// 
// (c) 2011 Cellent Finance Solutions AG 
//          Calwer Strasse 33 
//          70173 Stuttgart 
//          www.cellent-fs.de 
//--------------------------------------------------------- 
package org.remast.swing.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * Table with alternating row background color.
 * @author remast
 */
@SuppressWarnings("serial")
public class JHighligthedTable extends JTable {
	
    public static final Color BEIGE = new Color(245, 245, 220);

    public JHighligthedTable(TableModel tableModel) {
    	super(tableModel);
    }

	public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
		Component returnComp = super.prepareRenderer(renderer, row, column);
		if (!returnComp.getBackground().equals(getSelectionBackground())){
			Color backgroundColor = (row % 2 == 0 ? BEIGE : Color.WHITE);
			returnComp.setBackground(backgroundColor);
			backgroundColor = null;
		}
		return returnComp;
	}
}
