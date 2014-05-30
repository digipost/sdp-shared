package no.posten.dpost.offentlig.jaxb;

import org.joda.time.LocalDate;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class XSDateAdapter extends XmlAdapter<String, LocalDate> {

	@Override
	public LocalDate unmarshal(final String value) {
		return (XSDateCustomBinder.parseDate(value));
	}

	@Override
	public String marshal(final LocalDate value) {
		return (XSDateCustomBinder.printDate(value));
	}

}
