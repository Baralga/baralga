package org.remast.baralga.model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.remast.baralga.gui.Settings;
import org.remast.baralga.model.filter.Filter;

public abstract class FilterUtils {

    public static Filter restoreFromSettings() {
        Filter filter = new Filter();
        
        String selectedMonth = Settings.instance().getSelectedMonth();
        if (StringUtils.isNotBlank(selectedMonth)) {
            Date month = new Date();
            try {
                month.setMonth(Integer.parseInt(selectedMonth) - 1);
                filter.setMonth(month);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        String selectedYear = Settings.instance().getSelectedYear();
        if (StringUtils.isNotBlank(selectedYear)) {
            try {
                Calendar cal = GregorianCalendar.getInstance();
                cal.set(Calendar.YEAR, Integer.parseInt(selectedYear));
                filter.setYear(cal.getTime());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        return filter;
    }

}
