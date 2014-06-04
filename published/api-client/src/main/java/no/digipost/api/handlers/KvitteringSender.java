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
package no.digipost.api.handlers;

import no.digipost.api.SdpMeldingSigner;
import no.digipost.api.api.interceptors.steps.AddUserMessageStep;
import no.digipost.api.api.representations.EbmsAktoer;
import no.digipost.api.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.api.representations.Mpc;
import no.digipost.api.api.representations.SimpleStandardBusinessDocument;
import no.digipost.api.xml.Marshalling;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapMessage;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.IOException;

public class KvitteringSender extends EbmsContextAware implements WebServiceMessageCallback {

	private final EbmsApplikasjonsKvittering appKvittering;
	private final Jaxb2Marshaller marshaller;
	private final EbmsAktoer tekniskAvsender;
	private final EbmsAktoer tekniskMottaker;
	private final SdpMeldingSigner signer;

	public KvitteringSender(final SdpMeldingSigner signer, final EbmsAktoer tekniskAvsender, final EbmsAktoer tekniskMottaker, final EbmsApplikasjonsKvittering appKvittering, final Jaxb2Marshaller marshaller) {
		this.signer = signer;
		this.tekniskAvsender = tekniskAvsender;
		this.tekniskMottaker = tekniskMottaker;
		this.appKvittering = appKvittering;
		this.marshaller = marshaller;
	}

	@Override
	public void doWithMessage(final WebServiceMessage message) throws IOException, TransformerException {
		SoapMessage soapMessage = (SoapMessage) message;
		SimpleStandardBusinessDocument simple = new SimpleStandardBusinessDocument(appKvittering.sbd);
		if (simple.getMelding().getSignature() == null) {
			Document signedDoc = signer.sign(appKvittering.sbd);
			Marshalling.marshal(signedDoc, soapMessage.getEnvelope().getBody().getPayloadResult());
		} else {
			Marshalling.marshal(marshaller, soapMessage.getEnvelope().getBody(), appKvittering.sbd);
		}

		Mpc mpc = new Mpc(appKvittering.prioritet, null);
		ebmsContext.addRequestStep(new AddUserMessageStep(mpc, appKvittering.messageId, null, appKvittering.sbd, tekniskAvsender, tekniskMottaker
				, marshaller));
	}

}
