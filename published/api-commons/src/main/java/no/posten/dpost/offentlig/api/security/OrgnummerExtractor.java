package no.posten.dpost.offentlig.api.security;

import no.posten.dpost.offentlig.api.representations.Organisasjonsnummer;

import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class OrgnummerExtractor {
	private static final Pattern CN_PATTERN = Pattern.compile("CN=([0-9]{9})([^0-9].*)?$");
	private static final Pattern BUYPASS_PATTERN = Pattern.compile("SERIALNUMBER=([0-9]{9})", CASE_INSENSITIVE);

	public Organisasjonsnummer tryParse(final X509Certificate cert) {
		String dn = cert.getSubjectDN().getName();
		if (cert.getIssuerDN().getName().toLowerCase().contains("buypass")) {
			Matcher matcher = BUYPASS_PATTERN.matcher(dn);
			if (matcher.find()) {
				return new Organisasjonsnummer(matcher.group(1));
			}
		}
		Matcher matcher = CN_PATTERN.matcher(dn);
		if (matcher.find()) {
			return new Organisasjonsnummer(matcher.group(1));
		}
		return null;

	}

	public Organisasjonsnummer from(final X509Certificate cert) {
		Organisasjonsnummer orgnr = tryParse(cert);
		if (orgnr != null) {
			return orgnr;
		}
		throw new IllegalArgumentException("Fant ikke organisasjonsnummer i [" + cert.getSubjectDN().getName() + "], issuer=[" + cert.getIssuerDN().getName() + "]");
	}
}
