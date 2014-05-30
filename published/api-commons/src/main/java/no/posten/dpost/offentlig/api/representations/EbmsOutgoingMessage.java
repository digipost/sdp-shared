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
	protected EbmsMottaker ebmsMottaker;


	public EbmsOutgoingMessage(final EbmsMottaker ebmsMottaker, final String messageId, final String refToMessageId, final Prioritet prioritet) {
		super(messageId, refToMessageId);
		this.ebmsMottaker = ebmsMottaker;
		this.prioritet = prioritet != null ? prioritet : Prioritet.NORMAL;
	}
	public EbmsMottaker getEbmsMottaker() {
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
