package no.posten.dpost.offentlig.api.representations;

public class EbmsPullRequest extends EbmsOutgoingMessage {

	public EbmsPullRequest(final EbmsMottaker mottaker) {
		this(mottaker, Prioritet.NORMAL);
	}
	public EbmsPullRequest(final EbmsMottaker mottaker, final Prioritet prioritet) {
		super(mottaker, newId(), null, prioritet);
	}


}
