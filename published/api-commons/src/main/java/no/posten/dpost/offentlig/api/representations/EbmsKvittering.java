package no.posten.dpost.offentlig.api.representations;



public abstract class EbmsKvittering extends EbmsMessage {

	public EbmsKvittering(final String messageId, final String refToMessageId) {
		super(messageId, refToMessageId);
	}
}
