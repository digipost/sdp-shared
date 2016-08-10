package no.digipost.api.exceptions;

import no.digipost.api.xml.Marshalling;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.FaultMessageResolver;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static no.digipost.api.exceptions.MessageSenderOtherEbmsErrorException.isOtherError;
import static no.digipost.api.xml.Constants.MESSAGING_QNAME;

public class MessageSenderFaultMessageResolver implements FaultMessageResolver {

    private static final Logger LOG = LoggerFactory.getLogger(MessageSenderFaultMessageResolver.class);

    private Jaxb2Marshaller marshaller;

    public MessageSenderFaultMessageResolver(Jaxb2Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    @Override
    public void resolveFault(WebServiceMessage message) throws IOException {

        SoapMessage soapMessage = (SoapMessage) message;

        Iterator<SoapHeaderElement> soapHeaderElementIterator = soapMessage.getSoapHeader().examineHeaderElements(MESSAGING_QNAME);
        if (!soapHeaderElementIterator.hasNext()) {
            // SOAP fault without ebMS header.
            throw new MessageSenderSoapFaultException(soapMessage);
        }

        Messaging messaging = Marshalling.unmarshal(marshaller, soapHeaderElementIterator.next(), Messaging.class);
        List<Error> errors = new ArrayList<Error>();
        for (SignalMessage signalMessage : messaging.getSignalMessages()) {
            errors.addAll(signalMessage.getErrors());
        }

        if (errors.isEmpty()) {
            LOG.warn("Got no ebMS error in SOAP fault response that contains ebMS Messaging header.");
            throw new MessageSenderSoapFaultException(soapMessage);
        }

        if (errors.size() > 1) {
            LOG.warn("Got more than one ebMS error in response. Throwing exception with the first one.");
        }

        Error error = errors.get(0);

        if (isOtherError(error)) {
            throw new MessageSenderOtherEbmsErrorException(soapMessage, error);
        } else {
            throw new MessageSenderEbmsErrorException(soapMessage, error);
        }

    }


}
