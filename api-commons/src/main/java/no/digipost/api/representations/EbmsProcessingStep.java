package no.digipost.api.representations;

import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;

public interface EbmsProcessingStep {

    void apply(EbmsContext ebmsContext, SoapHeaderElement ebmsMessaging, SoapMessage soapMessage);

}
