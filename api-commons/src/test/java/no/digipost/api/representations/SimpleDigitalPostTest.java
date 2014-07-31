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

import no.difi.begrep.sdp.schema_v10.SDPDigitalPost;
import no.difi.begrep.sdp.schema_v10.SDPDigitalPostInfo;
import no.difi.begrep.sdp.schema_v10.SDPFlyttetDigitalPost;
import no.digipost.api.representations.SimpleStandardBusinessDocument.SimpleDigitalPostleveranse;
import no.digipost.api.representations.SimpleStandardBusinessDocument.SimpleDigitalPostleveranse.Type;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class SimpleDigitalPostTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	SimpleDigitalPostleveranse nyPost = new SimpleDigitalPostleveranse(new SDPDigitalPost());
	SimpleDigitalPostleveranse tilFlytting = new SimpleDigitalPostleveranse(new SDPFlyttetDigitalPost());

	@Test
    public void kanIkkeHenteUtFlyttetDigitalPostNaarTypeErNY_POST() {
		assertEquals(Type.NY_POST, nyPost.type);
		expectedException.expect(IllegalStateException.class);
		nyPost.getFlyttetDigitalPost();
    }

	@Test
	public void kanIkkeHenteUtDigitalPostNaarTypeErFLYTTET() {
		assertEquals(Type.FLYTTET, tilFlytting.type);
		expectedException.expect(IllegalStateException.class);
		tilFlytting.getDigitalPost();
	}

	@Test
    public void kanHenteUtUlikeDigitalPostMeldinger() {
		assertNotNull(nyPost.getDigitalPost());
		assertNotNull(tilFlytting.getFlyttetDigitalPost());
    }

	@Test
	public void leveringsdatoSkalSettesNaarSattOgTypeErNY_POST() {
		LocalDate leveringsdato = LocalDate.now().plusDays(7);

		SimpleDigitalPostleveranse simpleDigitalPostleveranse = new SimpleDigitalPostleveranse(
				new SDPDigitalPost()
						.withDigitalPostInfo(new SDPDigitalPostInfo()
								.withVirkningsdato(leveringsdato)));

		assertThat(simpleDigitalPostleveranse.getLeveringsTidspunkt(), is(leveringsdato.toDateTimeAtStartOfDay()));
	}

	@Test
	public void leveringsdatoSkalIkkeSettesNaasIkkeSattOgTypeErNY_POST() {
		SimpleDigitalPostleveranse simpleDigitalPostleveranse = new SimpleDigitalPostleveranse(
				new SDPDigitalPost()
						.withDigitalPostInfo(new SDPDigitalPostInfo()));

		assertThat(simpleDigitalPostleveranse.getLeveringsTidspunkt(), is(nullValue()));
	}

	@Test
	public void leveringsdatoSkalSettesNaarSattOgTypeErFLYTTET() {
		LocalDate virkningsdato = LocalDate.now().plusDays(7);

		SimpleDigitalPostleveranse simpleDigitalPostleveranse = new SimpleDigitalPostleveranse(
				new SDPFlyttetDigitalPost()
						.withDigitalPostInfo(new SDPDigitalPostInfo()
								.withVirkningsdato(virkningsdato)));

		assertThat(simpleDigitalPostleveranse.getLeveringsTidspunkt(), is(virkningsdato.toDateTimeAtStartOfDay()));
	}

	@Test
	public void leveringsdatoSkalIkkeSettesNaarIkkeSattOgTypeErFLYTTET() {
		SimpleDigitalPostleveranse simpleDigitalPostleveranse = new SimpleDigitalPostleveranse(
				new SDPFlyttetDigitalPost()
						.withDigitalPostInfo(new SDPDigitalPostInfo()));

		assertThat(simpleDigitalPostleveranse.getLeveringsTidspunkt(), is(nullValue()));
	}

	@Test
	public void leveringsdatoSkalVaereMottattDatoHvisSatt() {
		LocalDate mottaksdato = LocalDate.now().minusDays(10);

		SimpleDigitalPostleveranse simpleDigitalPostleveranse = new SimpleDigitalPostleveranse(
				new SDPFlyttetDigitalPost()
						.withDigitalPostInfo(new SDPDigitalPostInfo())
						.withMottaksdato(mottaksdato));

		assertThat(simpleDigitalPostleveranse.getLeveringsTidspunkt(), is(mottaksdato.toDateTimeAtStartOfDay()));
	}


	@Test
	public void leveringsdatoSkalVaereMottattDatoHvisMottattDatoErEtterVirkningsdato() {
		LocalDate mottaksdato = LocalDate.now().plusDays(8);
		LocalDate virkningsdato = LocalDate.now().plusDays(7);

		SimpleDigitalPostleveranse simpleDigitalPostleveranse = new SimpleDigitalPostleveranse(
				new SDPFlyttetDigitalPost()
						.withDigitalPostInfo(new SDPDigitalPostInfo()
								.withVirkningsdato(virkningsdato))
						.withMottaksdato(mottaksdato));

		assertThat(simpleDigitalPostleveranse.getLeveringsTidspunkt(), is(mottaksdato.toDateTimeAtStartOfDay()));
	}

	@Test
	public void leveringsdatoSkalVaereVirkningsDatoHvisVirkningsdatoEtterMottattDato() {
		LocalDate mottaksdato = LocalDate.now();
		LocalDate virkningsdato = LocalDate.now().plusDays(7);

		SimpleDigitalPostleveranse simpleDigitalPostleveranse = new SimpleDigitalPostleveranse(
				new SDPFlyttetDigitalPost()
						.withDigitalPostInfo(new SDPDigitalPostInfo()
								.withVirkningsdato(virkningsdato))
						.withMottaksdato(mottaksdato));

		assertThat(simpleDigitalPostleveranse.getLeveringsTidspunkt(), is(virkningsdato.toDateTimeAtStartOfDay()));
	}
}
