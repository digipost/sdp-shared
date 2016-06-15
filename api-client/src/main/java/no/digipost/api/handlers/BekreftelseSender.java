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

import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.w3.xmldsig.Reference;

import javax.xml.transform.TransformerException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BekreftelseSender extends EbmsContextAware implements WebServiceMessageCallback {

	private final KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering;
	private final Jaxb2Marshaller jaxb2Marshaller;

	public BekreftelseSender(final KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering, final Jaxb2Marshaller jaxb2Marshaller) {
		this.kanBekreftesSomBehandletKvittering = kanBekreftesSomBehandletKvittering;
		this.jaxb2Marshaller = jaxb2Marshaller;
	}

	@Override
	public void doWithMessage(final WebServiceMessage message) throws IOException, TransformerException {
		List<Reference> references = new ArrayList<Reference>();
		references.add(kanBekreftesSomBehandletKvittering.getReferanseTilMeldingSomKvitteres().getUnmarshalled());

		ebmsContext.addRequestStep(new AddReferencesStep(jaxb2Marshaller, kanBekreftesSomBehandletKvittering.getMeldingsId(), references));
	}

}
