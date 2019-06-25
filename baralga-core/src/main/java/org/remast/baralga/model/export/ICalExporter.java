//---------------------------------------------------------
// $Id$ 
// 
// (c) 2011 Cellent Finance Solutions AG 
//          Calwer Strasse 33 
//          70173 Stuttgart 
//          www.cellent-fs.de 
//--------------------------------------------------------- 
package org.remast.baralga.model.export;

import java.io.OutputStream;
import java.util.Collection;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;

import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;
import org.remast.util.DateUtils;

public class ICalExporter implements Exporter {

	private static final String DEFAULT_TIME_ZONE_IDENTIFIER = "Europe/Berlin";

	@Override
	public void export(Collection<ProjectActivity> data, Filter filter, OutputStream outputStream) throws Exception { 
		final Calendar calendar = new Calendar();
		calendar.getProperties().add(new ProdId("-//Baralga Activities //iCal4j 1.0//EN"));
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);

		final TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		TimeZone timeZone = registry.getTimeZone(TimeZone.getDefault().getID());
		if (timeZone == null) {
			timeZone = registry.getTimeZone(DEFAULT_TIME_ZONE_IDENTIFIER);
		}
		final VTimeZone vTimeZone = timeZone.getVTimeZone();
		final TzId timeZoneId = vTimeZone.getTimeZoneId();
		 
		for (ProjectActivity activity : data) {
			UidGenerator ug = new RandomUidGenerator();
			Uid uid = ug.generateUid();
			
			final String eventName = activity.getProject().getTitle();
			final DateTime start = new DateTime(DateUtils.convertToDate(activity.getStart()));
			final DateTime end = new DateTime(DateUtils.convertToDate(activity.getEnd()));
			final VEvent event = new VEvent(start, end, eventName);

			event.getProperties().add(timeZoneId);
			event.getProperties().add(new Description(activity.getDescription()));
			event.getProperties().add(uid);
			
			calendar.getComponents().add(event);
		}
		
		final CalendarOutputter outputter = new CalendarOutputter();
		outputter.output(calendar, outputStream);
	}

	@Override
	public boolean isFullExport() {
		return true;
	}

}
