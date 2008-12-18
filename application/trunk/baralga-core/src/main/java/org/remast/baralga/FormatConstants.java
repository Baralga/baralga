package org.remast.baralga;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.remast.text.SmartTimeFormat;

/** Hide constructor in utility class. */
public abstract class FormatConstants {

    // ------------------------------------------------
    // Date Formats
    // ------------------------------------------------
    public static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);

    public static final DateFormat timeFormat = new SmartTimeFormat(); //$NON-NLS-1$
    
    // ------------------------------------------------
    // Number Formats
    // ------------------------------------------------
    public static final NumberFormat durationFormat = new DecimalFormat("#0.00"); //$NON-NLS-1$

}
