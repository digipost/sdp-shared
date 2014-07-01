/**
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.digipost.api.security;

import no.digipost.api.representations.Organisasjonsnummer;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class OrgnummerExtractor {

	private static final Pattern CN_PATTERN = Pattern.compile("CN=([0-9]{9})([^0-9].*)?$");
	private static final Pattern BUYPASS_PATTERN = Pattern.compile("SERIALNUMBER=([0-9]{9})", CASE_INSENSITIVE);
	public static final Collection<Pattern> PATTERNS = Arrays.asList(CN_PATTERN, BUYPASS_PATTERN, Pattern.compile(".*"));

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
