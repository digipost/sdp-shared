package no.digipost.api;

import no.digipost.api.representations.EbmsAktoer;
import no.digipost.api.representations.Organisasjonsnummer;
import org.junit.Test;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;


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