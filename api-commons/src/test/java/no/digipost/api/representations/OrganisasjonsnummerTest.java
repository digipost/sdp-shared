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

import static org.fest.assertions.api.Assertions.assertThat;

public class OrganisasjonsnummerTest {

	@Test
	public void fra_string_initializes_organisasjonsnummer() {
		String nummer = "984661185";
		Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of(nummer);

		assertThat(organisasjonsnummer.toString()).isEqualTo(nummer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructor_throws_exception_if_not_valid() {
		String nummer = "98466118522222";
		Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of(nummer);
	}

	@Test
	public void med_landkode_returns_organisasjosnummer_with9908_prefix() {
		String expected = "9908:984661185";
		Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of("984661185");

		String actual = organisasjonsnummer.getOrganisasjonsnummerMedLandkode();

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void uten_landkode_returns_organisasjosnummer_without_prefix() {
		String expected = "984661185";
		Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of(expected);

		String actual = organisasjonsnummer.getOrganisasjonsnummerUtenLandkode();

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void forfrem_til_avsender(){
		Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of("984661185");
		AvsenderOrganisasjonsnummer avsenderOrganisasjonsnummer = organisasjonsnummer.forfremTilAvsender();
	}

	@Test
	public void forfrem_til_databehandler(){
		Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of("984661185");
		DatabehandlerOrganisasjonsnummer databehandlerOrganisasjonsnummer = organisasjonsnummer.forfremTilDatabehandler();
	}


}