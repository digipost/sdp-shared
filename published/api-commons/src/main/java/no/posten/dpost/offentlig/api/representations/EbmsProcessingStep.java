package no.posten.dpost.offentlig.api.representations;

import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

public interface EbmsProcessingStep {

	void apply(EbmsContext ebmsContext, SoapHeaderElement ebmsMessaging, SaajSoapMessage soapMessage);

}
