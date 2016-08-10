
package no.digipost.api.representations;

import java.util.UUID;

public abstract class EbmsMessage {

	public final String messageId;
	public final String refToMessageId;

	public EbmsMessage(final String messageId, final String refToMessageId) {
		this.messageId = messageId;
		this.refToMessageId = refToMessageId;
	}

	public static String newId() {
		return UUID.randomUUID().toString();
	}
}
