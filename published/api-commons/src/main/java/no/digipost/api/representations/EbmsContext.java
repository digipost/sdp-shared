/**
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.digipost.api.representations;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.w3.xmldsig.Reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EbmsContext {

	private static final String PROPERTY_NAME = "no.posten.dpost.ebms.context";
	public final List<EbmsProcessingStep> responseProcessingSteps = new ArrayList<EbmsProcessingStep>();
	public final List<EbmsProcessingStep> requestProcessingSteps = new ArrayList<EbmsProcessingStep>();

	public UserMessage userMessage = null;
	public List<SignalMessage> receipts = new ArrayList<SignalMessage>();
	public SignalMessage pullSignal = null;
	public List<Reference> incomingReferences = new ArrayList<Reference>();

	public SimpleStandardBusinessDocument sbd = null;
	public Map<String, String> mpcMap = new HashMap<String, String>();
	public Messaging incomingMessaging;
	public Organisasjonsnummer remoteParty;
	public Exception referencesValidationException;

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
