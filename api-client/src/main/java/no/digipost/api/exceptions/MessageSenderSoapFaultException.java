package no.digipost.api.exceptions;

import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapMessage;

public class MessageSenderSoapFaultException extends MessageSenderException {

    private final SoapMessage soapMessage;
    private final SoapFault soapFault;

    public MessageSenderSoapFaultException(final SoapMessage soapMessage) {
        super(soapMessage.getFaultReason());
        this.soapMessage = soapMessage;
        this.soapFault = getSoapFault(soapMessage);
    }

    protected MessageSenderSoapFaultException(final String message, final SoapMessage soapMessage) {
        super(message);
        this.soapMessage = soapMessage;
        this.soapFault = getSoapFault(soapMessage);
    }

    private SoapFault getSoapFault(final SoapMessage soapMessage) {
        SoapBody body = soapMessage.getSoapBody();
        return body != null ? body.getFault() : null;
    }

    public SoapMessage getSoapMessage() {
        return this.soapMessage;
    }

    public SoapFault getSoapFault() {
        return this.soapFault;
    }

}
