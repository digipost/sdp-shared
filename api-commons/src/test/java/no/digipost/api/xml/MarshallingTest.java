package no.digipost.api.xml;

import no.difi.begrep.sdp.schema_v10.SDPFeil;
import no.digipost.org.unece.cefact.namespaces.standardbusinessdocumentheader.BusinessScope;
import no.digipost.org.unece.cefact.namespaces.standardbusinessdocumentheader.DocumentIdentification;
import no.digipost.org.unece.cefact.namespaces.standardbusinessdocumentheader.Partner;
import no.digipost.org.unece.cefact.namespaces.standardbusinessdocumentheader.PartnerIdentification;
import no.digipost.org.unece.cefact.namespaces.standardbusinessdocumentheader.Scope;
import no.digipost.org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import no.digipost.org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;
import no.digipost.org.w3.xmldsig.CanonicalizationMethod;
import no.digipost.org.w3.xmldsig.DigestMethod;
import no.digipost.org.w3.xmldsig.Reference;
import no.digipost.org.w3.xmldsig.Signature;
import no.digipost.org.w3.xmldsig.SignatureMethod;
import no.digipost.org.w3.xmldsig.SignatureValue;
import no.digipost.org.w3.xmldsig.SignedInfo;
import no.digipost.org.w3.xmldsig.Transform;
import no.digipost.org.w3.xmldsig.Transforms;
import org.junit.jupiter.api.Test;

import javax.xml.transform.stream.StreamResult;

import java.io.StringWriter;
import java.time.ZonedDateTime;

import static no.difi.begrep.sdp.schema_v10.SDPFeiltype.KLIENT;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MarshallingTest {

    private final JaxbMarshaller marshaller = Marshalling.getMarshallerSingleton();

    @Test
    public void marshalling_av_gyldig_SBD_skal_ikke_feile() {
        StringWriter outWriter = new StringWriter();
        StreamResult result = new StreamResult(outWriter);
        SDPFeil sdpFeil = new SDPFeil()
                .withSignature(new Signature()
                        .withSignedInfo(
                                new SignedInfo()
                                        .withCanonicalizationMethod(
                                                new CanonicalizationMethod()
                                                        .withAlgorithm("algo")
                                        )
                                        .withSignatureMethod(
                                                new SignatureMethod()
                                                        .withAlgorithm("algo")
                                        )
                                        .withReferences(
                                                new Reference()
                                                        .withTransforms(
                                                                new Transforms()
                                                                        .withTransforms(
                                                                                new Transform()
                                                                                        .withAlgorithm("algo")
                                                                        )
                                                        )
                                                        .withDigestMethod(
                                                                new DigestMethod().withAlgorithm("algo"))
                                                        .withDigestValue(new byte[0])
                                        )
                        )
                        .withSignatureValue(
                                new SignatureValue()))
                .withDetaljer("Detaljer")
                .withFeiltype(KLIENT)
                .withTidspunkt(ZonedDateTime.now());

        marshaller.marshal(createValidStandardBusinessDocument(sdpFeil), result);

    }

    @Test
    public void marshalling_av_ugyldig_SBD_skal_feile() {
        StringWriter outWriter = new StringWriter();
        StreamResult result = new StreamResult(outWriter);
        StandardBusinessDocument sbd = createInvalidStandardBusinessDocument();
        assertThrows(MarshallingException.class, () -> marshaller.marshal(sbd, result));
    }


    private StandardBusinessDocument createValidStandardBusinessDocument(java.lang.Object any) {
        Partner partner = new Partner().withIdentifier(new PartnerIdentification("parner", "authority"));

        return new StandardBusinessDocument()
                .withStandardBusinessDocumentHeader(
                        new StandardBusinessDocumentHeader()
                                .withHeaderVersion("1.0")
                                .withSenders(partner)
                                .withReceivers(partner)
                                .withDocumentIdentification(
                                        new DocumentIdentification()
                                                .withStandard("standard")
                                                .withTypeVersion("typeVersion")
                                                .withInstanceIdentifier("instanceIdentifier")
                                                .withType("type")
                                                .withCreationDateAndTime(ZonedDateTime.now())
                                )
                                .withBusinessScope(new BusinessScope()
                                        .withScopes(new Scope()
                                                .withType("type")
                                                .withInstanceIdentifier("instanceIdentifier").withIdentifier("identifier")
                                                .withIdentifier("identifier")
                                        ))
                ).withAny(any);
    }

    private StandardBusinessDocument createInvalidStandardBusinessDocument() {
        Partner partner = new Partner().withIdentifier(new PartnerIdentification("parner", "authority"));

        return new StandardBusinessDocument()
                .withStandardBusinessDocumentHeader(
                        new StandardBusinessDocumentHeader()
                                .withHeaderVersion("1.0")
                                .withSenders(partner)
                                .withReceivers(partner)
                                .withDocumentIdentification(
                                        new DocumentIdentification()
                                                .withStandard("standard")
                                                .withTypeVersion("typeVersion")
                                )
                );
    }

}
