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
package no.posten.dpost.offentlig.api.handlers;

import no.posten.dpost.offentlig.api.interceptors.steps.AddReferencesStep;
import no.posten.dpost.offentlig.api.representations.EbmsApplikasjonsKvittering;
import no.posten.dpost.offentlig.api.representations.EbmsContext;
import no.posten.dpost.offentlig.api.representations.EbmsProcessingStep;
import no.posten.dpost.offentlig.api.representations.EbmsPullRequest;
import no.posten.dpost.offentlig.api.representations.Mpc;
import no.posten.dpost.offentlig.xml.Constants;
import no.posten.dpost.offentlig.xml.Marshalling;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PullRequest;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;

import javax.xml.transform.TransformerException;
import java.io.IOException;

public class PullRequestSender extends EbmsContextAware implements WebServiceMessageCallback {

	private final EbmsPullRequest pullRequest;
	private final Jaxb2Marshaller marshaller;
	private final EbmsApplikasjonsKvittering tidligereKvitteringSomSkalBekreftes;

	public PullRequestSender(final EbmsPullRequest pullRequest, final Jaxb2Marshaller marshaller, final EbmsApplikasjonsKvittering tidligereKvitteringSomSkalBekreftes) {
		this.pullRequest = pullRequest;
		this.marshaller = marshaller;
		this.tidligereKvitteringSomSkalBekreftes = tidligereKvitteringSomSkalBekreftes;
	}

	@Override
	public void doWithMessage(final WebServiceMessage message) throws IOException, TransformerException {
		if (tidligereKvitteringSomSkalBekreftes != null) {
			ebmsContext.addRequestStep(new AddReferencesStep(marshaller, tidligereKvitteringSomSkalBekreftes.messageId, tidligereKvitteringSomSkalBekreftes.references));
		}

		ebmsContext.addRequestStep(new EbmsProcessingStep() {

			@Override
			public void apply(final EbmsContext ebmsContext, final SoapHeaderElement ebmsMessaging, final SoapMessage soapMessage) {
				Mpc mpc = new Mpc(pullRequest.prioritet, null);
				SignalMessage signalMessage = new SignalMessage()
						.withMessageInfo(pullRequest.createMessageInfo())
						.withPullRequest(new PullRequest()
										.withMpc(mpc.toString())
						);
				Marshalling.marshal(marshaller, ebmsMessaging, Constants.SIGNAL_MESSAGE_QNAME, signalMessage);
			}

		});
	}

}