package org.remast.baralga.gui.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

/**
 * @author Jan Stamer
 */
public interface Constants {

    //------------------------------------------------
    // Date Formats
    //------------------------------------------------
    
    public static final SimpleDateFormat hhMMFormat = new SimpleDateFormat("HH:mm"); //$NON-NLS-1$
    
    public static final SimpleDateFormat dayMonthFormat = new SimpleDateFormat("dd.MM.yyyy"); //$NON-NLS-1$
    
    //------------------------------------------------
    // Number Formats
    //------------------------------------------------

    public static final NumberFormat durationFormat = new DecimalFormat("#0.00"); //$NON-NLS-1$

}
