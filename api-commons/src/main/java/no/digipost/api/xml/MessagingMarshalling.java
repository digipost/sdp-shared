package no.digipost.api.xml;

import no.digipost.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;

import java.util.Iterator;

import static no.digipost.api.xml.Constants.MESSAGING_QNAME;

public class MessagingMarshalling {

    /**
     * Enten returnerer denne et Messaging objekt, eller så kaster den en RuntimeException
     */
    public static Messaging getMessaging(JaxbMarshaller jaxb2Marshaller, WebServiceMessage message) {

        SoapHeader soapHeader = ((SoapMessage) message).getSoapHeader();
        if (soapHeader == null) {
            throw new RuntimeException("The ebMS header is missing (no SOAP header found in SOAP request)");
        }

        Iterator<SoapHeaderElement> soapHeaderElementIterator = soapHeader.examineHeaderElements(MESSAGING_QNAME);
        if (!soapHeaderElementIterator.hasNext()) {
            throw new RuntimeException("The ebMS header is missing in SOAP header");
        }

        SoapHeaderElement incomingSoapHeaderElement = soapHeaderElementIterator.next();
        try {
            return jaxb2Marshaller.unmarshal(incomingSoapHeaderElement.getSource(), Messaging.class);
        } catch (Exception e) {
            throw new RuntimeException("The ebMs header failed to unmarshall", e);
        }

    }

}
