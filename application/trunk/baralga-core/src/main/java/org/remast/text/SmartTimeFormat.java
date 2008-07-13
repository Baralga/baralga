package org.remast.text;

import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * Smart time format based on the simple date format.
 * <h3>Examples</h3>
 * <ul>
 *  <li>12    -> 12:00</li>
 *  <li>12,5  -> 12:30</li>
 *  <li>12,50 -> 12:30</li>
 * </ul>
 * @author remast
 */
@SuppressWarnings("serial")
public class SmartTimeFormat extends SimpleDateFormat {
    
    /**
     * Definition of the time format to parse.
     */
    private static final String HHMM_FORMAT = "HH:mm"; //$NON-NLS-1$
    
    public SmartTimeFormat() {
        super(HHMM_FORMAT);
    }

    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        return super.format(date, toAppendTo, fieldPosition);
    }

    @Override
    public Date parse(final String source, final ParsePosition pos) {
        String time = source;
        time = StringUtils.trimToEmpty(time);
        
        if (StringUtils.isBlank(time)) {
            return super.parse(time, pos);
        }
        
        if (time.contains(",50")) { //$NON-NLS-1$
            time = time.replace(",50", ":30"); //$NON-NLS-1$ // $NON-NLS-2$
        }

        if (time.contains(",5")) { //$NON-NLS-1$
            time = time.replace(",5", ":30"); //$NON-NLS-1$ // $NON-NLS-2$
        }
        
        if (!time.contains(":")) { //$NON-NLS-1$
            time = time + ":00"; //$NON-NLS-1$
        }
        
        if (time.length() == 4) {
            time = "0" + time; //$NON-NLS-1$
        }
        
        return super.parse(time, pos);
    }

}
