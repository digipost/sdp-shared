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

import no.posten.dpost.offentlig.api.representations.EbmsContext;
import no.posten.dpost.offentlig.api.representations.Organisasjonsnummer;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.WebServiceConnection;

import java.io.IOException;

public class EbmsContextAwareWebServiceTemplate extends WebServiceTemplate {


	private final Organisasjonsnummer remoteParty;

	public EbmsContextAwareWebServiceTemplate(final SaajSoapMessageFactory factory, final Organisasjonsnummer remoteParty) {
		super(factory);
		this.remoteParty = remoteParty;
	}

	@Override
	protected <T> T doSendAndReceive(final MessageContext messageContext, final WebServiceConnection connection,
			final WebServiceMessageCallback requestCallback, final WebServiceMessageExtractor<T> responseExtractor) throws IOException {
		EbmsContext context = EbmsContext.from(messageContext);
		context.remoteParty = remoteParty;
		if (requestCallback instanceof EbmsContextAware) {
			((EbmsContextAware) requestCallback).setContext(context);
		}
		if (responseExtractor instanceof EbmsContextAware) {
			((EbmsContextAware) responseExtractor).setContext(context);
		}
		return super.doSendAndReceive(messageContext, connection, requestCallback, responseExtractor);
	}



}
