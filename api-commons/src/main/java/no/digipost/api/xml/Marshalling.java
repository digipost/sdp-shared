package no.digipost.api.xml;

import no.difi.begrep.sdp.schema_v10.SDPKvittering;
import no.digipost.org.oasis_open.docs.ebxml_bp.ebbp_signals_2.NonRepudiationInformation;
import no.digipost.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import no.digipost.org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.etsi.uri._01903.v1_3.QualifyingProperties;
import org.etsi.uri._2918.v1_2.XAdESSignatures;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapHeaderElement;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xmlsoap.schemas.soap.envelope.Envelope;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

    public static void marshal(JaxbMarshaller jaxb2Marshaller, SoapHeaderElement header, QName qName, Object element) {
        marshal(jaxb2Marshaller, wrap(qName, element), header.getResult());
    }

    public static void marshal(JaxbMarshaller jaxb2Marshaller, SoapHeaderElement header, Object element) {
        marshal(jaxb2Marshaller, element, header.getResult());
    }

    public static void marshal(JaxbMarshaller jaxb2Marshaller, SoapBody body, Object element) {
        marshal(jaxb2Marshaller, element, body.getPayloadResult());
    }

    public static void marshal(JaxbMarshaller jaxb2Marshaller, Object element, Result payloadResult) {
        try {
            JAXBSource jaxbSource = new JAXBSource(jaxb2Marshaller.getJaxbContext().createMarshaller(), element);
            TransformerUtil.transform(jaxbSource, payloadResult);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static void marshal(Document doc, Result payloadResult) {
        DOMSource source = new DOMSource(doc);
        TransformerUtil.transform(source, payloadResult);
    }

    public static <T> T unmarshal(JaxbMarshaller jaxb2Marshaller, Node node, Class<T> clazz) {
        try {
            JAXBElement<T> jaxbElement = jaxb2Marshaller.getJaxbContext().createUnmarshaller().unmarshal(node, clazz);
            return jaxbElement.getValue();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T unmarshal(JaxbMarshaller jaxb2Marshaller, SoapHeaderElement header, Class<T> clazz) {
        try {
            JAXBElement<T> jaxbElement = jaxb2Marshaller.getJaxbContext().createUnmarshaller().unmarshal(header.getSource(), clazz);
            return jaxbElement.getValue();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T unmarshal(JaxbMarshaller jaxb2Marshaller, SoapBody body, Class<T> clazz) {
        try {
            JAXBElement<T> jaxbElement = jaxb2Marshaller.getJaxbContext().createUnmarshaller().unmarshal(body.getPayloadSource(), clazz);
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

    @SuppressWarnings("unchecked")
    public static <T> JAXBElement<T> wrap(QName qName, T object) {
        return new JAXBElement<T>(qName, (Class<T>) object.getClass(), object);
    }

    public static void trimNamespaces(Document doc) {
        NamedNodeMap attributes = doc.getDocumentElement().getAttributes();
        List<Attr> attrsToRemove = new ArrayList<Attr>();
        for (int i = 0; i < attributes.getLength(); i++) {
            if (doc.getElementsByTagNameNS(attributes.item(i).getNodeValue(), "*").getLength() == 0) {
                attrsToRemove.add((Attr) attributes.item(i));
            }
        }
        for (Attr a : attrsToRemove) {
            doc.getDocumentElement().removeAttributeNode(a);
        }
    }

    private static class FullyInitializedMarshaller {
        private static final JaxbMarshaller instance = createNewMarshaller();
    }
}
