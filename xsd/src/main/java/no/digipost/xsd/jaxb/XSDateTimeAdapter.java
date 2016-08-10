
package no.digipost.xsd.jaxb;

import org.joda.time.DateTime;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class XSDateTimeAdapter extends XmlAdapter<String, DateTime> {

	@Override
	public DateTime unmarshal(final String value) {
		return (XSDateTimeCustomBinder.parseDateTime(value));
	}

	@Override
	public String marshal(final DateTime value) {
		return (XSDateTimeCustomBinder.printDateTime(value));
	}

}