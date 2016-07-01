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
package no.digipost.api;

import no.digipost.api.representations.EbmsAktoer;
import no.digipost.api.representations.Organisasjonsnummer;
import org.junit.Test;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class EbmsEndpointUriBuilderTest {

	@Test
	public void meldingsformidlerUriLeggerTilDatabehandlerOgAvsenderOrgnrTilBaseUri() {
	    Organisasjonsnummer databehandler = Organisasjonsnummer.of("984661185");
	    Organisasjonsnummer avsender = Organisasjonsnummer.of("988015814");
	    List<String> identicalMfUris = Stream.of("http://mf.example.com/ebms", "http://mf.example.com/ebms/")
	            .map(EbmsEndpointUriBuilder::meldingsformidlerUri)
	            .map(builder -> builder.build(EbmsAktoer.avsender(databehandler), EbmsAktoer.avsender(avsender)))
	            .map(Object::toString)
	            .collect(toList());

	    assertThat(identicalMfUris, everyItem(is("http://mf.example.com/ebms/" + databehandler.getOrganisasjonsnummerMedLandkode() + "/" + avsender.getOrganisasjonsnummerMedLandkode())));
	    assertThat(identicalMfUris, hasSize(2));
	}

	@Test
    public void statiskUriVilAlltidReturnereUrienDenErInitialisertMed() {
        Organisasjonsnummer databehandler = Organisasjonsnummer.of("984661185");
        Organisasjonsnummer avsender = Organisasjonsnummer.of("988015814");
        URI uri = EbmsEndpointUriBuilder.statiskUri("http://postkasse.example.com/ebms").build(EbmsAktoer.avsender(databehandler), EbmsAktoer.avsender(avsender));

        assertThat(uri, is(URI.create("http://postkasse.example.com/ebms")));
    }
}