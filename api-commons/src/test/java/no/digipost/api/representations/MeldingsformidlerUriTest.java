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
package no.digipost.api.representations;

import org.junit.Test;

import java.net.URI;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class MeldingsformidlerUriTest {

	@Test
	public void Constructor_InitializesWithBaseUri() {
		URI baseUri = URI.create("http://baseuri.no");
		Organisasjonsnummer databehandlerOrganisasjonsNummer = Organisasjonsnummer.of("984661185");

		MeldingsformidlerUri meldingsformidlerUri = new MeldingsformidlerUri(baseUri, databehandlerOrganisasjonsNummer);

		assertThat(meldingsformidlerUri.baseUri, is(baseUri));
	}

	@Test
	public void getFull_AppendsAvsenderAndDatabehandlerOganisasjonsnummerToBaseUri() {
		URI baseUri = URI.create("http://baseuri.no");
		Organisasjonsnummer databehandlerOrganisasjonsNummer = Organisasjonsnummer.of("984661185");
		Organisasjonsnummer avsenderOrganisasjonsNummer = Organisasjonsnummer.of("988015814");
		MeldingsformidlerUri meldingsformidlerUri = new MeldingsformidlerUri(baseUri, databehandlerOrganisasjonsNummer);

		String actual = meldingsformidlerUri.getFull(avsenderOrganisasjonsNummer).toString();

		assertThat(actual, containsString(avsenderOrganisasjonsNummer.getOrganisasjonsnummerMedLandkode()));
		assertThat(actual, containsString(databehandlerOrganisasjonsNummer.getOrganisasjonsnummerMedLandkode()));
	}
}