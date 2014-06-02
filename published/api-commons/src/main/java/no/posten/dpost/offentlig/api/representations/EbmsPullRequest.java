package no.posten.dpost.offentlig.api.representations;

public class EbmsPullRequest extends EbmsOutgoingMessage {

	public EbmsPullRequest(final EbmsAktoer mottaker) {
		this(mottaker, Prioritet.NORMAL);
	}
	public EbmsPullRequest(final EbmsAktoer mottaker, final Prioritet prioritet) {
		super(mottaker, newId(), null, prioritet);
	}


}
