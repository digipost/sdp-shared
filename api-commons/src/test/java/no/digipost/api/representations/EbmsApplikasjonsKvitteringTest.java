package no.digipost.api.representations;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import no.digipost.org.w3.xmldsig.Reference;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class EbmsApplikasjonsKvitteringTest {

    @Test
    public void testGetMeldingsId() throws Exception {
        EbmsApplikasjonsKvittering ebmsApplikasjonskvittering = getEbmsApplikasjonskvittering();

        assertThat(ebmsApplikasjonskvittering.getMeldingsId(), is(ebmsApplikasjonskvittering.messageId));

    }

    @Test
    public void testGetReferanseTilMeldingSomKvitteres() throws Exception {
        KvitteringsReferanse referanseTilMeldingSomKvitteres = getEbmsApplikasjonskvittering().getReferanseTilMeldingSomKvitteres();

        assertThat(referanseTilMeldingSomKvitteres.getMarshalled(), stringLength(greaterThan(500)));
    }

    private Matcher<String> stringLength(Matcher<? super Integer> lengthMatcher) {
        return new CustomTypeSafeMatcher<String>("String with length " + lengthMatcher) {
            @Override
            protected boolean matchesSafely(String s) {
                return lengthMatcher.matches(s.length());
            }
        };
    }

    private EbmsApplikasjonsKvittering getEbmsApplikasjonskvittering() {
        List<Reference> references = new ArrayList<Reference>();
        references.add(ObjectMother.getReference());

        return EbmsApplikasjonsKvittering.create(EbmsAktoer.avsender("984661185"), EbmsAktoer.avsender("988015814"), null)
                .withReferences(references)
                .build();
    }

}
