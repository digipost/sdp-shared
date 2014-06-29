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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapMessage;

public class TransactionLogClientInterceptor extends TransactionLogInterceptor implements ClientInterceptor {
	private static final Logger LOG = LoggerFactory.getLogger(TransactionLogClientInterceptor.class);

	public TransactionLogClientInterceptor(final Jaxb2Marshaller jaxb2Marshaller) {
		super(jaxb2Marshaller);
	}

	@Override
	public boolean handleRequest(final MessageContext messageContext) throws WebServiceClientException {
		try {
			handleOutgoing(EbmsContext.from(messageContext), (SoapMessage) messageContext.getRequest(), "sender");
		} catch(Exception ex) {
			LOG.warn("Feil under klienttransaksjonslogging i handleRequest", ex);
		}
		return true;
	}

	@Override
	public boolean handleResponse(final MessageContext messageContext) throws WebServiceClientException {
		try {
			handleIncoming(EbmsContext.from(messageContext), (SoapMessage) messageContext.getResponse(), "sender");
		} catch(Exception ex) {
			LOG.warn("Feil under klienttransaksjonslogging i handleResponse", ex);
		}
		return true;
	}

	@Override
	public boolean handleFault(final MessageContext messageContext) throws WebServiceClientException {
		try {
			handleFault(TransaksjonsLogg.Retning.INNKOMMENDE, EbmsContext.from(messageContext), (SoapMessage) messageContext.getResponse(), "sender");
		} catch(Exception ex) {
			LOG.warn("Feil under klienttransaksjonslogging i handleFault", ex);
		}
		return true;
	}

	@Override
	public void afterCompletion(final MessageContext messageContext, final Exception ex) throws WebServiceClientException {
	}

}
