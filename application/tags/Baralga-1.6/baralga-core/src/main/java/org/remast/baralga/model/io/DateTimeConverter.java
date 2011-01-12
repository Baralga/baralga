package org.remast.baralga.model.io;

import java.util.Date;

import org.joda.time.DateTime;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.basic.DateConverter;

/**
 * XStream converter for {@link DateTime}.
 * 
 * It is compatible with XStream's build-in converter for {@link Date}:
 * {@link DateConverter}.
 * 
 * @author kutzi
 */
public class DateTimeConverter implements SingleValueConverter {

    private final DateConverter delegate;
 
    /**
     * Construct a {@link DateTimeConverter} with standard formats.
     */
    public DateTimeConverter() {
        this.delegate = new DateConverter();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean canConvert(final Class type) {
        return type.equals(DateTime.class);
    }

    @Override
    public Object fromString(final String str) {
        Date date = (Date) this.delegate.fromString(str);
        return new DateTime(date);
    }

    @Override
    public String toString(final Object obj) {
        return this.delegate.toString(((DateTime) obj).toDate());
    }
}
