package no.digipost.api.representations;

import org.junit.jupiter.api.Test;
import no.digipost.org.w3.xmldsig.Reference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class KvitteringsReferanseTest {

    @Test
    public void testBuilder_FromReference() throws Exception {
        Reference referenceToOriginalMessage = ObjectMother.getReference();

        KvitteringsReferanse kvitteringsReferanse = KvitteringsReferanse.builder(referenceToOriginalMessage).build();

        assertThat(kvitteringsReferanse.getMarshalled(), notNullValue());
    }

    @Test
    public void testBuilder_FrommarshalledReference() throws Exception {
        String marshalledReferenceToOriginalMessage = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns5:Reference xmlns:ns5=\"http://www.w3.org/2000/09/xmldsig#\" URI=\"#id-f2ecf3b2-101e-433b-a30d-65a9b6779b5a\" xmlns:ns2=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns3=\"http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader\" xmlns:ns4=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ns6=\"http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/\" xmlns:ns7=\"http://docs.oasis-open.org/ebxml-bp/ebbp-signals-2.0\" xmlns:ns8=\"http://www.w3.org/1999/xlink\" xmlns:ns9=\"http://begrep.difi.no/sdp/schema_v10\" xmlns:ns10=\"http://uri.etsi.org/2918/v1.2.1#\" xmlns:ns11=\"http://uri.etsi.org/01903/v1.3.2#\"><ns5:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/><ns5:DigestValue>eFFiS1V0dUVHU3JzZ1pzU0FUNXJGKy95ZmxyK2hsMmNVQzRjS3lpTXhSTT0=</ns5:DigestValue></ns5:Reference>";

        KvitteringsReferanse kvitteringsReferanse = KvitteringsReferanse.builder(marshalledReferenceToOriginalMessage).build();

        assertThat(kvitteringsReferanse.getMarshalled(), is(marshalledReferenceToOriginalMessage));
    }

    @Test
    public void testGetUnmarshalled() {
        Reference referenceToOriginalMessage = ObjectMother.getReference();

        KvitteringsReferanse kvitteringsReferanse = KvitteringsReferanse.builder(referenceToOriginalMessage).build();

        assertThat(kvitteringsReferanse.getUnmarshalled(), is(referenceToOriginalMessage));
    }
}
