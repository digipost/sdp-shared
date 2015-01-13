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
import no.digipost.api.representations.SimpleStandardBusinessDocument.SimpleDigitalPostformidling;
import no.digipost.api.representations.SimpleStandardBusinessDocument.SimpleDigitalPostformidling.Type;
import no.digipost.xsd.types.DigitalPostformidling;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import static org.apache.commons.lang3.StringUtils.join;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class SimpleStandardBusinessDocumentTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private final LocalDate virkningsdato = new LocalDate(2014, 6, 24);
	private final LocalDate mottaksdato = new LocalDate(2013, 2, 17);

	private final SDPDigitalPost sdpPost = new SDPDigitalPost().withDigitalPostInfo(new SDPDigitalPostInfo().withVirkningsdato(virkningsdato));
	private final SimpleDigitalPostformidling nyPost = new SimpleStandardBusinessDocument(new StandardBusinessDocument().withAny(sdpPost)).getDigitalPostformidling();

	private final SDPFlyttetDigitalPost sdpFlyttetPost = new SDPFlyttetDigitalPost().withMottaksdato(mottaksdato);
	private final SimpleDigitalPostformidling flyttetPost = new SimpleStandardBusinessDocument(new StandardBusinessDocument().withAny(sdpFlyttetPost)).getDigitalPostformidling();

	@Test
    public void girDigitalPostformidlingMedTypeNY_POST() {
		assertThat(nyPost.type, is(Type.NY_POST));
		assertThat(nyPost.getDigitalPost(), instanceOf(SDPDigitalPost.class));
		assertFalse(nyPost.erAlleredeAapnet());
		assertThat(nyPost.getLeveringstidspunkt(), is(virkningsdato.toDateTimeAtStartOfDay()));
		assertFalse(nyPost.kreverAapningsKvittering());

		sdpPost.getDigitalPostInfo().setAapningskvittering(false);
		assertFalse(nyPost.kreverAapningsKvittering());

		sdpPost.getDigitalPostInfo().setAapningskvittering(true);
		assertTrue(nyPost.kreverAapningsKvittering());

		sdpPost.setDigitalPostInfo(null);
		assertFalse(nyPost.kreverAapningsKvittering());
    }

	@Test
    public void leveringstidspunktErSenesteTidspunktAvMottaksdatoOgVirkningsdato() {
		sdpFlyttetPost.setDigitalPostInfo(new SDPDigitalPostInfo().withVirkningsdato(virkningsdato));
		assertThat(flyttetPost.getLeveringstidspunkt(), is(virkningsdato.toDateTimeAtStartOfDay()));

		LocalDate senereMottaksdato = virkningsdato.plusDays(1);
		sdpFlyttetPost.setMottaksdato(senereMottaksdato);
		assertThat(flyttetPost.getLeveringstidspunkt(), is(senereMottaksdato.toDateTimeAtStartOfDay()));

		sdpFlyttetPost.setMottaksdato(virkningsdato.minusDays(1));
		assertThat(flyttetPost.getLeveringstidspunkt(), is(virkningsdato.toDateTimeAtStartOfDay()));
    }


	@Test
    public void girDigitalPostformidlingMedTypeFLYTTET() {
		assertThat(flyttetPost.type, is(Type.FLYTTET));
		assertThat(flyttetPost.getFlyttetDigitalPost(), instanceOf(SDPFlyttetDigitalPost.class));
		assertThat(flyttetPost.getLeveringstidspunkt(), is(mottaksdato.toDateTimeAtStartOfDay()));
		assertFalse(flyttetPost.erAlleredeAapnet());
		sdpFlyttetPost.setAapnet(true);
		assertTrue(flyttetPost.erAlleredeAapnet());
    }

	@Test
	public void feilerDersomUkjentTypeDigitalPostMelding() {
		SimpleStandardBusinessDocument sbd = new SimpleStandardBusinessDocument(new StandardBusinessDocument().withAny(mock(DigitalPostformidling.class)));
		expectedException.expectMessage("ikke gjenkjent");
		expectedException.expectMessage(join(SimpleDigitalPostformidling.Type.values(), ", "));
		sbd.getDigitalPostformidling();
	}

	@Test
    public void feilerDersomManHenterUtFlyttetDigitalPostFraDigitalPostformidlingTypeNY_POST() {
		expectedException.expectMessage("ikke av forventet type " + Type.FLYTTET);
		nyPost.getFlyttetDigitalPost();
    }

	@Test
	public void feilerDersomManHenterUtDigitalPostFraDigitalPostformidlingTypeFLYTTET() {
		expectedException.expectMessage("ikke av forventet type " + Type.NY_POST);
		flyttetPost.getDigitalPost();
	}
}
