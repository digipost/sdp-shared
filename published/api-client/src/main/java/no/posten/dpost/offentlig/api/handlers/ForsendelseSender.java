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

import no.difi.begrep.sdp.schema_v10.SDPDigitalPost;
import no.posten.dpost.offentlig.api.SdpMeldingSigner;
import no.posten.dpost.offentlig.api.interceptors.steps.AddUserMessageStep;
import no.posten.dpost.offentlig.api.representations.EbmsForsendelse;
import no.posten.dpost.offentlig.api.representations.Mpc;
import no.posten.dpost.offentlig.api.representations.Organisasjonsnummer;
import no.posten.dpost.offentlig.xml.Marshalling;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jcajce.provider.digest.SHA3.Digest256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.w3.xmldsig.DigestMethod;
import org.w3.xmldsig.Reference;
import org.w3c.dom.Document;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.FileTypeMap;
import javax.xml.transform.TransformerException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.UUID;

public class ForsendelseSender extends EbmsContextAware implements WebServiceMessageCallback {

	private static final Logger LOG = LoggerFactory.getLogger(ForsendelseSender.class);

	private final StandardBusinessDocument doc;
	private final EbmsForsendelse forsendelse;
	private final Jaxb2Marshaller marshaller;
	private final Organisasjonsnummer tekniskAvsender;
	private final Organisasjonsnummer tekniskMottaker;
	private final SDPDigitalPost digitalPost;
	private File tempFile;
	private final SdpMeldingSigner signer;

	public ForsendelseSender(final SdpMeldingSigner signer, final Organisasjonsnummer tekniskAvsender, final Organisasjonsnummer tekniskMottaker, final StandardBusinessDocument doc, final EbmsForsendelse forsendelse, final Jaxb2Marshaller marshaller) {
		this.signer = signer;
		this.tekniskAvsender = tekniskAvsender;
		this.tekniskMottaker = tekniskMottaker;
		this.doc = doc;
		this.forsendelse = forsendelse;
		this.marshaller = marshaller;
		this.digitalPost = ((SDPDigitalPost) doc.getAny());
	}

	@Override
	public void doWithMessage(final WebServiceMessage message) throws IOException, TransformerException {
		SaajSoapMessage soapMessage = (SaajSoapMessage) message;
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

	private void attachFile(final SaajSoapMessage soapMessage) throws IOException {
		tempFile = File.createTempFile("ebms", "outgoing");
		LOG.debug("Kopierer vedlegg til tempfil: {}", tempFile.getAbsolutePath());
		MessageDigest digest = new Digest256();
        InputStream asicStream = forsendelse.getDokumentpakke().getAsicStream();
        FileOutputStream out = new FileOutputStream(tempFile);
		try{
            DigestOutputStream digestStream = new DigestOutputStream(out, digest);
            IOUtils.copy(asicStream, digestStream);
		}
        finally {
            IOUtils.closeQuietly(asicStream);
            IOUtils.closeQuietly(out);
        }

		if (digitalPost.getDokumentpakkefingeravtrykk() == null) {
			byte[] hash = digest.digest();
			digitalPost.withDokumentpakkefingeravtrykk(new Reference()
				.withDigestMethod(new DigestMethod().withAlgorithm(javax.xml.crypto.dsig.DigestMethod.SHA256))
				.withDigestValue(org.bouncycastle.util.encoders.Base64.encode(hash))
			);
		}

		FileDataSource fileDataSource = new FileDataSource(tempFile);
		fileDataSource.setFileTypeMap(fileTypeMap);
		DataHandler handler = new DataHandler(fileDataSource);
		soapMessage.addAttachment(generateContentId(), handler);
	}

	public void cleanTemp() {
		if (tempFile != null) {
			LOG.debug("Sletter tempfil: {}", tempFile.getAbsolutePath());
			FileUtils.deleteQuietly(tempFile);
		}
	}

	private static final FileTypeMap fileTypeMap = new FileTypeMap() {
		@Override
		public String getContentType(final File file) {
			return EbmsForsendelse.CONTENT_TYPE_KRYPTERT_DOKUMENTPAKKE;
		}
		@Override
		public String getContentType(final String path) {
			return EbmsForsendelse.CONTENT_TYPE_KRYPTERT_DOKUMENTPAKKE;
		}
	};

	private String generateContentId() {
		return "<" + UUID.randomUUID().toString() + "@meldingsformidler.sdp.difi.no>";
	}

}