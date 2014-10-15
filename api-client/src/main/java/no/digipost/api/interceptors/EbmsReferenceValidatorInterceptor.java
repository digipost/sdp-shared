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

import java.util.Map;

import no.digipost.api.EbmsReferenceExtractor;
import no.digipost.api.interceptors.steps.ReferenceValidatorStep;
import no.digipost.api.representations.EbmsContext;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapMessage;
import org.w3.xmldsig.Reference;

public class EbmsReferenceValidatorInterceptor implements ClientInterceptor {

	private final EbmsReferenceExtractor extractor;
	private final Jaxb2Marshaller jaxb2Marshaller;

	public EbmsReferenceValidatorInterceptor(final Jaxb2Marshaller jaxb2Marshaller) {
		this.jaxb2Marshaller = jaxb2Marshaller;
		extractor = new EbmsReferenceExtractor(jaxb2Marshaller);
	}

	@Override
	public boolean handleRequest(final MessageContext messageContext) throws WebServiceClientException {
		EbmsContext context = EbmsContext.from(messageContext);
		Map<String, Reference> references = extractor.getReferences((SoapMessage) messageContext.getRequest());
		if (references.size() > 0) {
			context.addResponseStep(new ReferenceValidatorStep(jaxb2Marshaller, references.values()));
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
