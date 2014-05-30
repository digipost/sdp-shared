package no.posten.dpost.offentlig.api.exceptions.ebms.custom;

import no.posten.dpost.offentlig.api.exceptions.ebms.Origin;

public class Constants {

	public static final String customSecurityErrorCode(Origin origin, String suffix) {
		if (suffix.length() != 2) {
			throw new IllegalArgumentException("Illegal error code suffix");
		}
		switch (origin) {
			case security:
				return "11" + suffix;
			case ebMS:
				return "10" + suffix;
			case reliability:
				return "12" + suffix;
			default:
				throw new IllegalArgumentException("Illegal error code origin");
		}
	}

}
