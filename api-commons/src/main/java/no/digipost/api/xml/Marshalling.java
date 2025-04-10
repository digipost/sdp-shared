package no.digipost.api.xml;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import no.difi.begrep.sdp.schema_v10.SDPKvittering;
import no.digipost.org.oasis_open.docs.ebxml_bp.ebbp_signals_2.NonRepudiationInformation;
import no.digipost.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import no.digipost.org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.etsi.uri._01903.v1_3.QualifyingProperties;
import org.etsi.uri._2918.v1_2.XAdESSignatures;
import org.w3c.dom.Node;
import org.xmlsoap.schemas.soap.envelope.Envelope;

import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

import static java.util.Arrays.asList;

public class Marshalling {

    // Use when not spring managed
    public static JaxbMarshaller getMarshallerSingleton() {
        return FullyInitializedMarshaller.instance;
    }

    public static JaxbMarshaller createNewMarshaller() {
        return JaxbMarshaller.validatingMarshallerForPackages(
                asList(
                    StandardBusinessDocument.class.getPackage(),
                    Envelope.class.getPackage(),
                    no.digipost.org.w3.soap.Envelope.class.getPackage(),
                    no.digipost.org.w3.xmldsig.Reference.class.getPackage(),
                    Messaging.class.getPackage(),
                    NonRepudiationInformation.class.getPackage(),
                    SDPKvittering.class.getPackage(),
                    XAdESSignatures.class.getPackage(),
                    org.etsi.uri._02918.v1_2.XAdESSignatures.class.getPackage(),
                    QualifyingProperties.class.getPackage()
                ),
                SchemaResources.all()
                );
    }
    
    public static <T> T unmarshal(JaxbMarshaller jaxb2Marshaller, Node node, Class<T> clazz) {
        try {
            JAXBElement<T> jaxbElement = jaxb2Marshaller.getJaxbContext().createUnmarshaller().unmarshal(node, clazz);
            return jaxbElement.getValue();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static <T> T unmarshal(JaxbMarshaller jaxb2Marshaller, InputStream is, Class<T> clazz) {
        try {
            JAXBElement<T> jaxbElement = jaxb2Marshaller.getJaxbContext().createUnmarshaller().unmarshal(new StreamSource(is), clazz);
            return jaxbElement.getValue();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private static class FullyInitializedMarshaller {
        private static final JaxbMarshaller instance = createNewMarshaller();
    }
}
