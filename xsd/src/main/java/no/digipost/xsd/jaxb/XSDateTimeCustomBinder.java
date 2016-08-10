
package no.digipost.xsd.jaxb;

import org.joda.time.DateTime;

import javax.xml.bind.DatatypeConverter;

public class XSDateTimeCustomBinder {

	public static DateTime parseDateTime(final String s) {
		return new DateTime(DatatypeConverter.parseDate(s).getTime());
	}

	public static String printDateTime(final DateTime dt) {
		return dt == null ? null : dt.toString();
	}

}
