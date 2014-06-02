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
package no.posten.dpost.offentlig.api.representations;

import org.joda.time.DateTime;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo;

public class EbmsOutgoingMessage extends EbmsMessage {
	public enum Prioritet {
		NORMAL("normal"),
		PRIORITERT("prioritert");
		private final String value;

		public String value() {
			return value;
		}

		Prioritet(final String value) {
			this.value = value;
		}

		public static Prioritet from(final String val) {
			for(Prioritet p : Prioritet.values()) {
				if (p.value.equals(val)) {
					return p;
				}
			}
			throw new IllegalArgumentException("Invalid Prioritet: " + val);
		}
	}
	public final Prioritet prioritet;
	protected EbmsAktoer ebmsMottaker;


	public EbmsOutgoingMessage(final EbmsAktoer ebmsMottaker, final String messageId, final String refToMessageId, final Prioritet prioritet) {
		super(messageId, refToMessageId);
		this.ebmsMottaker = ebmsMottaker;
		this.prioritet = prioritet != null ? prioritet : Prioritet.NORMAL;
	}
	public EbmsAktoer getEbmsMottaker() {
		return ebmsMottaker;
	}
	public MessageInfo createMessageInfo() {
		MessageInfo messageInfo = new MessageInfo()
			.withMessageId(messageId)
			.withRefToMessageId(refToMessageId)
			.withTimestamp(DateTime.now());
		return messageInfo;
	}


}
