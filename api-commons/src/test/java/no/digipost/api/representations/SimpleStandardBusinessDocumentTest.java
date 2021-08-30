package no.digipost.api.representations;

import no.difi.begrep.sdp.schema_v10.SDPDigitalPost;
import no.difi.begrep.sdp.schema_v10.SDPDigitalPostInfo;
import no.difi.begrep.sdp.schema_v10.SDPFlyttetDigitalPost;
import no.digipost.api.representations.SimpleStandardBusinessDocument.SimpleDigitalPostformidling;
import no.digipost.api.representations.SimpleStandardBusinessDocument.SimpleDigitalPostformidling.Type;
import no.digipost.xsd.types.DigitalPostformidling;
import org.junit.Test;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import java.time.LocalDate;
import java.time.ZoneId;

import static co.unruly.matchers.Java8Matchers.where;
import static no.digipost.api.representations.SimpleStandardBusinessDocument.SimpleDigitalPostformidling.defaultTidEtterMidnatt;
import static org.apache.commons.lang3.StringUtils.join;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class SimpleStandardBusinessDocumentTest {

    private static final ZoneId zoneId = ZoneId.systemDefault();
    private final LocalDate virkningsdato = LocalDate.of(2014, 6, 24);
    private final LocalDate mottaksdato = LocalDate.of(2013, 2, 17);
    private final SDPDigitalPost sdpPost = new SDPDigitalPost().withDigitalPostInfo(new SDPDigitalPostInfo().withVirkningsdato(virkningsdato));
    private final SimpleDigitalPostformidling nyPost = new SimpleStandardBusinessDocument(new StandardBusinessDocument().withAny(sdpPost)).getDigitalPostformidling();
    private final SDPFlyttetDigitalPost sdpFlyttetPost = new SDPFlyttetDigitalPost().withMottaksdato(mottaksdato);
    private final SimpleDigitalPostformidling flyttetPost = new SimpleStandardBusinessDocument(new StandardBusinessDocument().withAny(sdpFlyttetPost)).getDigitalPostformidling();

    @Test
    public void girDigitalPostformidlingMedTypeNY_POST() {
        assertThat(nyPost.type, is(Type.NY_POST));
        assertThat(nyPost.getDigitalPost(), instanceOf(SDPDigitalPost.class));
        assertFalse(nyPost.erAlleredeAapnet());
        assertThat(nyPost.getLeveringstidspunkt(), is(virkningsdato.atStartOfDay(zoneId).plus(defaultTidEtterMidnatt)));
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
        assertThat(flyttetPost.getLeveringstidspunkt(), is(virkningsdato.atStartOfDay(zoneId).plus(defaultTidEtterMidnatt)));

        LocalDate senereMottaksdato = virkningsdato.plusDays(1);
        sdpFlyttetPost.setMottaksdato(senereMottaksdato);
        assertThat(flyttetPost.getLeveringstidspunkt(), is(senereMottaksdato.atStartOfDay(zoneId).plus(defaultTidEtterMidnatt)));

        sdpFlyttetPost.setMottaksdato(virkningsdato.minusDays(1));
        assertThat(flyttetPost.getLeveringstidspunkt(), is(virkningsdato.atStartOfDay(zoneId).plus(defaultTidEtterMidnatt)));
    }


    @Test
    public void girDigitalPostformidlingMedTypeFLYTTET() {
        assertThat(flyttetPost.type, is(Type.FLYTTET));
        assertThat(flyttetPost.getFlyttetDigitalPost(), instanceOf(SDPFlyttetDigitalPost.class));
        assertThat(flyttetPost.getLeveringstidspunkt(), is(mottaksdato.atStartOfDay(zoneId).plus(defaultTidEtterMidnatt)));
        assertFalse(flyttetPost.erAlleredeAapnet());
        sdpFlyttetPost.setAapnet(true);
        assertTrue(flyttetPost.erAlleredeAapnet());
    }

    @Test
    public void feilerDersomUkjentTypeDigitalPostMelding() {
        SimpleStandardBusinessDocument sbd = new SimpleStandardBusinessDocument(new StandardBusinessDocument().withAny(mock(DigitalPostformidling.class)));
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, sbd::getDigitalPostformidling);
        assertThat(thrown, where(Exception::getMessage, containsString("ikke gjenkjent")));
        assertThat(thrown, where(Exception::getMessage, containsString(join(SimpleDigitalPostformidling.Type.values(), ", "))));
    }

    @Test
    public void feilerDersomManHenterUtFlyttetDigitalPostFraDigitalPostformidlingTypeNY_POST() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, nyPost::getFlyttetDigitalPost);
        assertThat(thrown, where(Exception::getMessage, containsString("ikke av forventet type " + Type.FLYTTET)));
    }

    @Test
    public void feilerDersomManHenterUtDigitalPostFraDigitalPostformidlingTypeFLYTTET() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, flyttetPost::getDigitalPost);
        assertThat(thrown, where(Exception::getMessage, containsString("ikke av forventet type " + Type.NY_POST)));
    }
}
