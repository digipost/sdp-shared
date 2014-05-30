package no.posten.dpost.offentlig.jaxb;

import org.joda.time.LocalDate;

import javax.xml.bind.DatatypeConverter;

public class XSDateCustomBinder {

	public static LocalDate parseDate(final String s) {
		return new LocalDate(DatatypeConverter.parseDate(s));
	}

	public static String printDate(final LocalDate date) {
		return date.toString();
	}
}
