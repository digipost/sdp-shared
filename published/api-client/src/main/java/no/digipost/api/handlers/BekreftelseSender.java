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

import no.digipost.api.interceptors.steps.AddReferencesStep;

import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;

import javax.xml.transform.TransformerException;

import java.io.IOException;

public class BekreftelseSender extends EbmsContextAware implements WebServiceMessageCallback {

	private final EbmsApplikasjonsKvittering appKvittering;
	private final Jaxb2Marshaller jaxb2Marshaller;

	public BekreftelseSender(final EbmsApplikasjonsKvittering appKvittering, final Jaxb2Marshaller jaxb2Marshaller) {
		this.appKvittering = appKvittering;
		this.jaxb2Marshaller = jaxb2Marshaller;
	}

	@Override
	public void doWithMessage(final WebServiceMessage message) throws IOException, TransformerException {
		ebmsContext.addRequestStep(new AddReferencesStep(jaxb2Marshaller, appKvittering.messageId, appKvittering.references));
	}

}
