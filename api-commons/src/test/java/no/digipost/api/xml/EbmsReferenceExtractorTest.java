package no.digipost.api.xml;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPMessage;
import no.digipost.api.EbmsReferenceExtractor;
import no.digipost.org.w3.xmldsig.DigestMethod;
import no.digipost.org.w3.xmldsig.Reference;
import org.junit.jupiter.api.Test;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static co.unruly.matchers.Java8Matchers.where;
import static jakarta.xml.soap.SOAPConstants.SOAP_1_2_PROTOCOL;
import static javax.xml.crypto.dsig.DigestMethod.SHA256;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class EbmsReferenceExtractorTest {

    private static final byte[] FAKE_EBMS; static {
        try {
            FAKE_EBMS = Files.readAllBytes(Paths.get(EbmsReferenceExtractorTest.class.getResource("/fake-ebms.xml").toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    private final EbmsReferenceExtractor referenceExtractor = new EbmsReferenceExtractor(Marshalling.getMarshallerSingleton());

    @Test
    public void extractReferencesFromEbmsSoapMessage() throws Exception {
        SOAPMessage message = MessageFactory.newInstance(SOAP_1_2_PROTOCOL).createMessage(null, new ByteArrayInputStream(FAKE_EBMS));
        Map<String, Reference> references = referenceExtractor.getReferences(new SaajSoapMessage(message));
        assertThat(references.keySet(), containsInAnyOrder("body", "attachment"));

        Reference soapBodyReference = references.get("body");
        assertThat(soapBodyReference, where(Reference::getURI, is("#soapBody")));
        assertThat(soapBodyReference, where(Reference::getDigestMethod, where(DigestMethod::getAlgorithm, is(SHA256))));

        Reference dokumentpakkeReference = references.get("attachment");
        assertThat(dokumentpakkeReference, where(Reference::getURI, containsString("meldingsformidler.sdp.difi.no")));
        assertThat(dokumentpakkeReference, where(Reference::getDigestMethod, where(DigestMethod::getAlgorithm, is(SHA256))));
    }
}
