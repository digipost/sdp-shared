package no.posten.dpost.offentlig.api.interceptors;

import no.posten.dpost.offentlig.api.EbmsReferenceExtractor;
import no.posten.dpost.offentlig.api.interceptors.steps.ReferenceValidatorStep;
import no.posten.dpost.offentlig.api.representations.EbmsContext;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3.xmldsig.Reference;

import java.util.List;

public class EbmsReferenceValidatorInterceptor implements ClientInterceptor {

	private final EbmsReferenceExtractor extractor;
	private final Jaxb2Marshaller jaxb2Marshaller;

	public EbmsReferenceValidatorInterceptor(final Jaxb2Marshaller jaxb2Marshaller) {
		this.jaxb2Marshaller = jaxb2Marshaller;
		extractor = new EbmsReferenceExtractor(jaxb2Marshaller);
	}

	@Override
	public boolean handleRequest(final MessageContext messageContext) throws WebServiceClientException {
		EbmsContext context = EbmsContext.from(messageContext);
		List<Reference> references = extractor.getReferences((SaajSoapMessage)messageContext.getRequest());
		if (references.size() > 0) {
			context.addResponseStep(new ReferenceValidatorStep(jaxb2Marshaller, references));
			context.incomingReferences = references;
		}
		return true;
	}

	@Override
	public boolean handleResponse(final MessageContext messageContext) throws WebServiceClientException {
		return true;
	}

	@Override
	public boolean handleFault(final MessageContext messageContext) throws WebServiceClientException {
		return true;
	}

}
