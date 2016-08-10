package no.digipost.api.representations;

import no.difi.begrep.sdp.schema_v10.SDPDigitalPost;
import no.difi.begrep.sdp.schema_v10.SDPDigitalPostInfo;
import no.difi.begrep.sdp.schema_v10.SDPFlyttetDigitalPost;
import no.difi.begrep.sdp.schema_v10.SDPFysiskPostInfo;
import no.digipost.api.representations.SimpleStandardBusinessDocument.SimpleDigitalPostformidling;
import no.digipost.api.representations.SimpleStandardBusinessDocument.SimpleDigitalPostformidling.Type;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static no.digipost.api.representations.SimpleStandardBusinessDocument.SimpleDigitalPostformidling.Type.NY_POST;
import static no.digipost.api.representations.SimpleStandardBusinessDocument.SimpleDigitalPostformidling.defaultTidEtterMidnatt;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class SimpleDigitalPostTest {

    private final SimpleDigitalPostformidling nyPost = new SimpleDigitalPostformidling(new SDPDigitalPost());
    private final SimpleDigitalPostformidling tilFlytting = new SimpleDigitalPostformidling(new SDPFlyttetDigitalPost());
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void kanIkkeHenteUtFlyttetDigitalPostNaarTypeErNY_POST() {
        assertThat(nyPost.type, is(Type.NY_POST));
        expectedException.expect(IllegalArgumentException.class);
        nyPost.getFlyttetDigitalPost();
    }

    @Test
    public void kanIkkeHenteUtDigitalPostNaarTypeErFLYTTET() {
        assertThat(tilFlytting.type, is(Type.FLYTTET));
        expectedException.expect(IllegalArgumentException.class);
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

        SimpleDigitalPostformidling digitalPostformidling = new SimpleDigitalPostformidling(
                new SDPDigitalPost()
                        .withDigitalPostInfo(new SDPDigitalPostInfo()
                                .withVirkningsdato(leveringsdato)));

        assertThat(digitalPostformidling.getLeveringstidspunkt(), is(leveringsdato.toDateTimeAtStartOfDay().plus(defaultTidEtterMidnatt)));
    }

    @Test
    public void leveringsdatoSkalIkkeSettesNaasIkkeSattOgTypeErNY_POST() {
        SimpleDigitalPostformidling digitalPostformidling = new SimpleDigitalPostformidling(
                new SDPDigitalPost()
                        .withDigitalPostInfo(new SDPDigitalPostInfo()));

        assertThat(digitalPostformidling.getLeveringstidspunkt(), is(nullValue()));
    }

    @Test
    public void leveringsdatoSkalSettesNaarSattOgTypeErFLYTTET() {
        LocalDate virkningsdato = LocalDate.now().plusDays(7);

        SimpleDigitalPostformidling digitalPostformidling = new SimpleDigitalPostformidling(
                new SDPFlyttetDigitalPost()
                        .withMottaksdato(virkningsdato.minusDays(1))
                        .withDigitalPostInfo(new SDPDigitalPostInfo().withVirkningsdato(virkningsdato)));

        assertThat(digitalPostformidling.getLeveringstidspunkt(), is(virkningsdato.toDateTimeAtStartOfDay().plus(defaultTidEtterMidnatt)));
    }

    @Test
    public void leveringstidspunktErAlltidMottakstidspunktHvisVirkningsdatoIkkeErSattOgTypeErFLYTTET() {
        LocalDate mottaksdato = LocalDate.now().minusDays(7);
        SimpleDigitalPostformidling digitalPostformidling = new SimpleDigitalPostformidling(
                new SDPFlyttetDigitalPost()
                        .withMottaksdato(mottaksdato)
                        .withDigitalPostInfo(new SDPDigitalPostInfo()));

        assertThat(digitalPostformidling.getLeveringstidspunkt(), is(mottaksdato.toDateTimeAtStartOfDay().plus(defaultTidEtterMidnatt)));
    }

    @Test
    public void leveringstidspunktErVirkningsdatoDersomDenErSenereEnnMottakstidspunktOgTypeErFLYTTET() {
        LocalDate virkningsdato = LocalDate.now().minusDays(7);
        SimpleDigitalPostformidling digitalPostformidling = new SimpleDigitalPostformidling(
                new SDPFlyttetDigitalPost()
                        .withMottaksdato(virkningsdato.minusDays(1))
                        .withDigitalPostInfo(new SDPDigitalPostInfo().withVirkningsdato(virkningsdato)));

        assertThat(digitalPostformidling.getLeveringstidspunkt(), is(virkningsdato.toDateTimeAtStartOfDay().plus(defaultTidEtterMidnatt)));
    }

    @Test
    public void leveringsdatoSkalVaereMottattDatoHvisSatt() {
        LocalDate mottaksdato = LocalDate.now().minusDays(10);

        SimpleDigitalPostformidling digitalPostformidling = new SimpleDigitalPostformidling(
                new SDPFlyttetDigitalPost()
                        .withDigitalPostInfo(new SDPDigitalPostInfo())
                        .withMottaksdato(mottaksdato));

        assertThat(digitalPostformidling.getLeveringstidspunkt(), is(mottaksdato.toDateTimeAtStartOfDay().plus(defaultTidEtterMidnatt)));
    }


    @Test
    public void leveringsdatoSkalVaereMottattDatoHvisMottattDatoErEtterVirkningsdato() {
        LocalDate mottaksdato = LocalDate.now().plusDays(8);
        LocalDate virkningsdato = LocalDate.now().plusDays(7);

        SimpleDigitalPostformidling digitalPostformidling = new SimpleDigitalPostformidling(
                new SDPFlyttetDigitalPost()
                        .withDigitalPostInfo(new SDPDigitalPostInfo()
                                .withVirkningsdato(virkningsdato))
                        .withMottaksdato(mottaksdato));

        assertThat(digitalPostformidling.getLeveringstidspunkt(), is(mottaksdato.toDateTimeAtStartOfDay().plus(defaultTidEtterMidnatt)));
    }

    @Test
    public void leveringsdatoSkalVaereVirkningsDatoHvisVirkningsdatoEtterMottattDato() {
        LocalDate mottaksdato = LocalDate.now();
        LocalDate virkningsdato = LocalDate.now().plusDays(7);

        SimpleDigitalPostformidling digitalPostformidling = new SimpleDigitalPostformidling(
                new SDPFlyttetDigitalPost()
                        .withDigitalPostInfo(new SDPDigitalPostInfo()
                                .withVirkningsdato(virkningsdato))
                        .withMottaksdato(mottaksdato));

        assertThat(digitalPostformidling.getLeveringstidspunkt(), is(virkningsdato.toDateTimeAtStartOfDay().plus(defaultTidEtterMidnatt)));
    }

    @Test
    public void vanligDigitalPostErAldriAapnet() {
        assertFalse(nyPost.erAlleredeAapnet());
    }

    @Test
    public void flyttetPostKanVaereAapnetEllerUaapnet() {
        assertFalse(tilFlytting.erAlleredeAapnet());
        assertTrue(new SimpleDigitalPostformidling(new SDPFlyttetDigitalPost().withAapnet(true)).erAlleredeAapnet());
    }

    @Test
    public void erIkkeFysiskPostDersomFysiskPostInfoIkkeErSatt() {
        assertFalse(nyPost.erDigitalPostTilFysiskLevering());
        assertFalse(tilFlytting.erDigitalPostTilFysiskLevering());
    }

    @Test
    public void gjenkjennerDigitalPostTilFysiskLevering() {
        SimpleDigitalPostformidling fysiskPost = new SimpleDigitalPostformidling(new SDPDigitalPost().withFysiskPostInfo(new SDPFysiskPostInfo()));
        assertTrue(fysiskPost.erDigitalPostTilFysiskLevering());
        assertTrue(fysiskPost.type == NY_POST);
    }

}
