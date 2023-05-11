package no.digipost.api.interceptors;

import no.digipost.api.EbmsReferenceExtractor;
import no.digipost.api.interceptors.steps.ReferenceValidatorStep;
import no.digipost.api.representations.EbmsContext;
import no.digipost.api.xml.JaxbMarshaller;
import no.digipost.org.w3.xmldsig.Reference;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapMessage;

import java.util.Map;

public class EbmsReferenceValidatorInterceptor implements ClientInterceptor {

    private final EbmsReferenceExtractor extractor;
    private final JaxbMarshaller jaxbMarshaller;

    public EbmsReferenceValidatorInterceptor(final JaxbMarshaller jaxbMarshaller) {
        this.jaxbMarshaller = jaxbMarshaller;
        this.extractor = new EbmsReferenceExtractor(jaxbMarshaller);
    }

    @Override
    public boolean handleRequest(final MessageContext messageContext) throws WebServiceClientException {
        EbmsContext context = EbmsContext.from(messageContext);
        Map<String, Reference> references = extractor.getReferences((SoapMessage) messageContext.getRequest());
        if (references.size() > 0) {
            context.addResponseStep(new ReferenceValidatorStep(jaxbMarshaller, references.values()));
            context.incomingReferences = references;
        }
        return true;
    }

    @Override
    public boolean handleResponse(final MessageContext messageContext) throws WebServiceClientException {
        EbmsContext context = EbmsContext.from(messageContext);
        Map<String, Reference> references = extractor.getReferences((SoapMessage) messageContext.getResponse());
        if (references.size() > 0) {
            context.incomingReferences = references;
        }
        return true;
    }

    @Override
    public boolean handleFault(final MessageContext messageContext) throws WebServiceClientException {
        return true;
    }

    @Override
    public void afterCompletion(final MessageContext messageContext, final Exception ex) throws WebServiceClientException {

    }

}
