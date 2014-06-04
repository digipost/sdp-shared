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
package no.digipost.api.representations;

import org.joda.time.DateTime;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;

public class SimpleUserMessage {

	private final UserMessage message;

	public SimpleUserMessage(final UserMessage message) {
		this.message = message;
	}

	public Organisasjonsnummer getTo() {
		return Organisasjonsnummer.fromIso6523(message.getPartyInfo().getTo().getPartyIds().get(0).getValue());
	}

    public Organisasjonsnummer getFrom() {
		return Organisasjonsnummer.fromIso6523(message.getPartyInfo().getFrom().getPartyIds().get(0).getValue());
	}

    public String getMessageId() {
		return message.getMessageInfo().getMessageId();
	}

    public String getRefToMessageId() {
		return message.getMessageInfo().getRefToMessageId();
	}

    public DateTime getTimestamp() {
		return message.getMessageInfo().getTimestamp();
	}

	public String getAction() {
		return message.getCollaborationInfo().getAction();
	}

    public String getMpc() {
		return message.getMpc();
	}
}
