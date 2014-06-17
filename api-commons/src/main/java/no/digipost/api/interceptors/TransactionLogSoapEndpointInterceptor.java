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
package no.digipost.api.interceptors;

import no.digipost.api.config.TransaksjonsLogg;
import no.digipost.api.representations.EbmsContext;
import no.digipost.api.representations.Organisasjonsnummer;
import no.digipost.api.security.OrgnummerExtractor;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.MethodEndpoint;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.server.SoapEndpointInterceptor;

import java.security.cert.X509Certificate;

import static no.digipost.api.config.TransaksjonsLogg.Retning.UTGÅENDE;

public class TransactionLogSoapEndpointInterceptor implements SoapEndpointInterceptor {

	private TransactionLogInterceptor transactionLogInterceptor;

	private static final String KEY = "translog.requestlogged";
	private final OrgnummerExtractor orgnrExtractor = new OrgnummerExtractor();


	private enum Phase {
		OUTSIDE_WSSEC,
		INSIDE_WSSEC
	}

	private final Phase phase;

	public static TransactionLogSoapEndpointInterceptor createOutsideServerInterceptor(final Jaxb2Marshaller jaxb2Marshaller) {
		return new TransactionLogSoapEndpointInterceptor(jaxb2Marshaller, Phase.OUTSIDE_WSSEC);
	}

	public static TransactionLogSoapEndpointInterceptor createInsideServerInterceptor(final Jaxb2Marshaller jaxb2Marshaller) {
		return new TransactionLogSoapEndpointInterceptor(jaxb2Marshaller, Phase.INSIDE_WSSEC);
	}

	private TransactionLogSoapEndpointInterceptor(Jaxb2Marshaller jaxb2Marshaller, Phase phase) {
		this.transactionLogInterceptor = new TransactionLogInterceptor(jaxb2Marshaller);
		this.phase = phase;
	}

	public void setTransaksjonslogg(TransaksjonsLogg transaksjonslogg) {
		this.transactionLogInterceptor.setTransaksjonslogg(transaksjonslogg);
	}

	@Override
	public boolean understands(final SoapHeaderElement header) {
		return true;
	}

	@Override
	public boolean handleRequest(final MessageContext messageContext, final Object endpoint) throws Exception {
		if (phase == Phase.INSIDE_WSSEC) {
			loggIncomingEndpointRequest(messageContext, getName(endpoint));
			messageContext.setProperty(KEY, true);
		}
		return true;
	}

	private String getName(final Object endpoint) {
		return ((MethodEndpoint) endpoint).getBean().toString();
	}

	private void loggIncomingEndpointRequest(final MessageContext messageContext, final String endpoint) {
		setOrgNummer(messageContext);
		transactionLogInterceptor.handleIncoming(EbmsContext.from(messageContext), (SoapMessage) messageContext.getRequest(), endpoint);
	}

	@Override
	public boolean handleResponse(final MessageContext messageContext, final Object endpoint) throws Exception {
		if (phase == Phase.OUTSIDE_WSSEC) {
			if (messageContext.getProperty(KEY) == null) {
				loggIncomingEndpointRequest(messageContext, getName(endpoint));
			}
			transactionLogInterceptor.handleOutgoing(EbmsContext.from(messageContext), (SoapMessage) messageContext.getResponse(), getName(endpoint));
		}
		return true;
	}

	@Override
	public boolean handleFault(final MessageContext messageContext, final Object endpoint) throws Exception {
		if (phase == Phase.OUTSIDE_WSSEC) {
			if (messageContext.getProperty(KEY) == null) {
				loggIncomingEndpointRequest(messageContext, getName(endpoint));
			}
			transactionLogInterceptor.handleFault(UTGÅENDE, EbmsContext.from(messageContext),
					(SoapMessage) messageContext.getResponse(),
					getName(endpoint));
		}
		return true;
	}

	@Override
	public void afterCompletion(final MessageContext messageContext, final Object endpoint, final Exception ex) throws Exception {
	}

	private void setOrgNummer(final MessageContext messageContext) {
		EbmsContext ebmsContext = EbmsContext.from(messageContext);
		if (ebmsContext.remoteParty != null) {
			return;
		}
		ebmsContext.remoteParty = Organisasjonsnummer.NULL;

		X509Certificate cert = (X509Certificate) messageContext.getProperty(Wss4jInterceptor.INCOMING_CERTIFICATE);
		if (cert != null) {
			Organisasjonsnummer orgnr = orgnrExtractor.tryParse(cert);
			if (orgnr != null) {
				ebmsContext.remoteParty = orgnr;
			}
		}
	}

}
