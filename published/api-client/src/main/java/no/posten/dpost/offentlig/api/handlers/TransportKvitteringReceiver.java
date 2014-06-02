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

import no.posten.dpost.offentlig.api.representations.TransportKvittering;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageExtractor;

import javax.xml.transform.TransformerException;

import java.io.IOException;

public class TransportKvitteringReceiver extends EbmsContextAware implements WebServiceMessageExtractor<TransportKvittering> {
	@Override
	public TransportKvittering extractData(final WebServiceMessage message) throws IOException, TransformerException {
		MessageInfo messageInfo = ebmsContext.receipts.get(0).getMessageInfo();
		return new TransportKvittering(messageInfo.getMessageId(), messageInfo.getRefToMessageId());
	}

}