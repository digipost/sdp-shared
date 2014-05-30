package no.posten.dpost.offentlig.api.representations;

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
