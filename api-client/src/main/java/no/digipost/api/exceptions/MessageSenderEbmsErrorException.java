package no.digipost.api.exceptions;

import no.digipost.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;
import org.springframework.ws.soap.SoapMessage;

public class MessageSenderEbmsErrorException extends MessageSenderSoapFaultException {

    private final Error error;

    public MessageSenderEbmsErrorException(final SoapMessage soapMessage, final Error error) {
        super(error.getDescription() != null ? error.getDescription().getValue() : error.getShortDescription(), soapMessage);
        this.error = error;
    }

    public Error getError() {
        return error;
    }

}
