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

import no.difi.begrep.sdp.schema_v10.SDPDigitalPost;
import no.digipost.api.SdpMeldingSigner;
import no.posten.dpost.offentlig.api.interceptors.steps.AddUserMessageStep;
import no.posten.dpost.offentlig.api.representations.EbmsAktoer;
import no.posten.dpost.offentlig.api.representations.EbmsForsendelse;
import no.posten.dpost.offentlig.api.representations.Mpc;
import no.posten.dpost.offentlig.xml.Marshalling;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapMessage;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.w3.xmldsig.DigestMethod;
import org.w3.xmldsig.Reference;
import org.w3c.dom.Document;

import javax.activation.DataHandler;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.UUID;

public class ForsendelseSender extends EbmsContextAware implements WebServiceMessageCallback {

	private final StandardBusinessDocument doc;
	private final EbmsForsendelse forsendelse;
	private final Jaxb2Marshaller marshaller;
	private final EbmsAktoer tekniskAvsender;
	private final EbmsAktoer tekniskMottaker;
	private final SDPDigitalPost digitalPost;
	private final SdpMeldingSigner signer;

	public ForsendelseSender(final SdpMeldingSigner signer, final EbmsAktoer tekniskAvsender, final EbmsAktoer tekniskMottaker, final StandardBusinessDocument doc, final EbmsForsendelse forsendelse, final Jaxb2Marshaller marshaller) {
		this.signer = signer;
		this.tekniskAvsender = tekniskAvsender;
		this.tekniskMottaker = tekniskMottaker;
		this.doc = doc;
		this.forsendelse = forsendelse;
		this.marshaller = marshaller;
		digitalPost = ((SDPDigitalPost) doc.getAny());
	}

	@Override
	public void doWithMessage(final WebServiceMessage message) throws IOException, TransformerException {
		SoapMessage soapMessage = (SoapMessage) message;
		attachFile(soapMessage);
		Mpc mpc = new Mpc(forsendelse.prioritet, null);
		if (digitalPost.getSignature() == null) {
			Document signedDoc = signer.sign(doc);
			Marshalling.marshal(signedDoc, soapMessage.getEnvelope().getBody().getPayloadResult());
		} else {
			Marshalling.marshal(marshaller, soapMessage.getEnvelope().getBody(), doc);
		}
		ebmsContext.addRequestStep(new AddUserMessageStep(mpc, forsendelse.messageId, null, doc, tekniskAvsender, tekniskMottaker, marshaller));
	}

	private void attachFile(final SoapMessage soapMessage) throws IOException {
		if (digitalPost.getDokumentpakkefingeravtrykk() == null) {
			byte[] hash = forsendelse.getDokumentpakke().getSHA256();
			digitalPost.withDokumentpakkefingeravtrykk(new Reference()
							.withDigestMethod(new DigestMethod().withAlgorithm(javax.xml.crypto.dsig.DigestMethod.SHA256))
							.withDigestValue(org.bouncycastle.util.encoders.Base64.encode(hash))
			);
		}
		DataHandler handler = new DataHandler(forsendelse.getDokumentpakke());
		soapMessage.addAttachment(generateContentId(), handler);
	}

	private String generateContentId() {
		return "<" + UUID.randomUUID().toString() + "@meldingsformidler.sdp.difi.no>";
	}

}