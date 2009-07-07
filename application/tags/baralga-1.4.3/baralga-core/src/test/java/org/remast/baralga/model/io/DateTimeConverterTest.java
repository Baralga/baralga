package org.remast.baralga.model.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import junit.framework.TestCase;

import org.joda.time.DateTime;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Tests the {@link DateTimeConverter}.
 * @author kutzi
 */
public class DateTimeConverterTest extends TestCase {

    /**
     * Tests that a {@link Date} written by XStream can be read by the {@link DateTimeConverter}.
     * @throws IOException
     */
    public void testDateToDateTime() throws IOException {
        DateTime timeToTest = new DateTime(2009, 3, 15, 12, 0, 0, 0);
        
        ClassWithDate date = new ClassWithDate();
        date.time = timeToTest.toDate();
        
        final XStream xstreamOut = new XStream(new DomDriver(IOConstants.FILE_ENCODING));
        xstreamOut.setMode(XStream.ID_REFERENCES);
        xstreamOut.processAnnotations(
                new Class[] {ClassWithDate.class}
        );
        
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        xstreamOut.toXML(date, baos);
        baos.close();
        
        byte[] bytes = baos.toByteArray();
        
        final XStream xstreamIn = new XStream(new DomDriver(IOConstants.FILE_ENCODING));
        xstreamIn.setMode(XStream.ID_REFERENCES);
        xstreamIn.registerConverter(new DateTimeConverter());
        xstreamIn.processAnnotations(
                new Class[] {ClassWithDateTime.class}
        );
        ClassWithDateTime dateTime = (ClassWithDateTime) xstreamIn.fromXML(new ByteArrayInputStream(bytes));
        
        assertEquals(date.time, dateTime.time.toDate());
    }

    /**
     * Tests that a {@link DateTime} written by the {@link DateTimeConverter} can
     * be read as a {@link Date} with XStream.
     * @throws IOException
     */
    public void testDateTimeToDate() throws IOException {
        DateTime timeToTest = new DateTime(2009, 3, 15, 12, 0, 0, 0);
        
        ClassWithDateTime dateTime = new ClassWithDateTime();
        dateTime.time = timeToTest;
        
        final XStream xstreamOut = new XStream(new DomDriver(IOConstants.FILE_ENCODING));
        xstreamOut.setMode(XStream.ID_REFERENCES);
        xstreamOut.registerConverter(new DateTimeConverter());
        xstreamOut.processAnnotations(
                new Class[] {ClassWithDateTime.class}
        );
        
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        xstreamOut.toXML(dateTime, baos);
        baos.close();
        
        byte[] bytes = baos.toByteArray();
        
        final XStream xstreamIn = new XStream(new DomDriver(IOConstants.FILE_ENCODING));
        xstreamIn.setMode(XStream.ID_REFERENCES);
        xstreamIn.processAnnotations(
                new Class[] {ClassWithDate.class}
        );
        ClassWithDate date = (ClassWithDate) xstreamIn.fromXML(new ByteArrayInputStream(bytes));
        
        assertEquals(dateTime.time.toDate(), date.time);
    }
    
    @XStreamAlias("class")//$NON-NLS-1$
    private static class ClassWithDate {
        Date time;
    }
    
    @XStreamAlias("class")//$NON-NLS-1$
    private static class ClassWithDateTime {
        DateTime time;
    }
}
