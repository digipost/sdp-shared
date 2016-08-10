
package no.digipost.api.representations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.w3.xmldsig.Reference;
import org.w3c.dom.Document;

public class EbmsContext {

	private static final String PROPERTY_NAME = "no.posten.dpost.ebms.context";
	public final List<EbmsProcessingStep> responseProcessingSteps = new ArrayList<EbmsProcessingStep>();
	public final List<EbmsProcessingStep> requestProcessingSteps = new ArrayList<EbmsProcessingStep>();

	public UserMessage userMessage = null;
	public List<SignalMessage> receipts = new ArrayList<SignalMessage>();
	public SignalMessage pullSignal = null;
	public Map<String, Reference> incomingReferences = new HashMap<String, Reference>();

	public SimpleStandardBusinessDocument sbd = null;
	public Map<String, String> mpcMap = new HashMap<String, String>();
	public Messaging incomingMessaging;
	public Optional<Organisasjonsnummer> remoteParty = Optional.empty();
	public Exception referencesValidationException;
	public Error warning;
	public Document domSbd;

	public static EbmsContext from(final MessageContext messageContext) {
		EbmsContext context = (EbmsContext) messageContext.getProperty(PROPERTY_NAME);
		if (context == null) {
			context = new EbmsContext();
			messageContext.setProperty(PROPERTY_NAME, context);
		}
		return context;
	}

	public void addResponseStep(final EbmsProcessingStep strategy) {
		responseProcessingSteps.add(strategy);
	}

	public void addRequestStep(final EbmsProcessingStep strategy) {
		requestProcessingSteps.add(strategy);
	}

	public void processResponse(final EbmsContext ebmsContext, final SoapHeaderElement ebmsMessaging, final SoapMessage soapMessage) {
		for (EbmsProcessingStep r : responseProcessingSteps) {
			r.apply(ebmsContext, ebmsMessaging, soapMessage);
		}

	}

	public void processRequest(final EbmsContext ebmsContext, final SoapHeaderElement ebmsMessaging, final SoapMessage soapMessage) {
		for (EbmsProcessingStep r : requestProcessingSteps) {
			r.apply(ebmsContext, ebmsMessaging, soapMessage);
		}
	}

}
