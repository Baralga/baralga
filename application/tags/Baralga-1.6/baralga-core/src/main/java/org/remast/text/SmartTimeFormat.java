package org.remast.text;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * Smart time format based on the simple date format.
 * <h3>Examples</h3>
 * <ul>
 *  <li>12    -> 12:00</li>
 *  <li>12,5  -> 12:30</li>
 *  <li>12,50 -> 12:30</li>
 *  <li>12.30 -> 12:30</li>
 * </ul>
 * @author remast
 */
@SuppressWarnings("serial")
public class SmartTimeFormat extends TimeFormat {

    /**
     * {@inheritDoc}
     */
    public final Date parse(final String source, final ParsePosition pos) {
        String time = source;
        time = StringUtils.trimToEmpty(time);
        
        if (StringUtils.isBlank(time)) {
            return super.parse(time, pos);
        }
        
        time = normalize(time);
        return super.parse(time, pos);
    }

    /**
     * Parses hours and minutes from the given time as String.
     * @param timeString the time to be parsed
     * @return the parsed time as array with hours and minutes
     * @throws ParseException on parse errors
     */
    public static int[] parseToHourAndMinutes(final String timeString) throws ParseException {
        String time = timeString;
        time = StringUtils.trimToEmpty(time);
        
        if (StringUtils.isBlank(time)) {
            throw new ParseException("String is empty", 1);
        }
        
        time = normalize(time);
        
        String[] splitted = time.split(":");
        if (splitted.length != 2) {
            throw new ParseException("String '" + timeString + "' has an unsupported format", 1);
        } else {
            int[] result = new int[2];
            for (int i = 0; i < 2; i++) {
                result[i] = Integer.parseInt(splitted[i]);
            }
            return result;
        }
    }
    
    /**
     * Replaces ';' with ',', '.' with ':' and converts some fraction notations
     * into hh:mm (e.g. 12,5 into 12:30).
     * And some more.
     * @param timeString the String to normalize
     * @return the normalized String
     */
    private static String normalize(final String timeString) {
        String time = timeString;
        time = StringUtils.replace(time, ",,", ":");
        time = StringUtils.replaceChars(time, '/', ':');
        time = StringUtils.replaceChars(time, ';', ',');
        time = StringUtils.replaceChars(time, '.', ':');
        
        // Treat 11,25 as 11:15
        // Treat 11,75 as 11:45
        // Treat 11,5 and 11,50 as 11:30
        final String [] splittedTime = time.split(",");
        if (time.contains(",")&& splittedTime.length >= 2) {
        	final String hh = splittedTime[0];
        	String mm = splittedTime[1];
    		if (mm.length() < 2) {
    			mm = mm + "0"; 
    		}

        	try {
        		// Convert to integer value
        		int m = Integer.valueOf(mm);

        		// Convert to float for calculation
        		float fm = m;          

        		// Convert from base100 to base60
        		fm *= 0.6;                     

        		// Round to int
        		m = java.lang.Math.round(fm);  

        		mm = String.valueOf(m);
        		if (mm.length() < 2) {
        			mm = "0" + mm; 
        		}
        		time = hh + ":" + mm;
        	} catch (NumberFormatException e) {
        		// Conversion to int failed so smart format does not apply.
        	}
        }

        // Treat 11 as 11:30
        if (!time.contains(":")) { //$NON-NLS-1$
            time = time + ":00"; //$NON-NLS-1$
        }
        
		if (time.length() < 5) {
			time = "0" + time; 
		}
        return time;
    }
}
