package no.digipost.api.representations;

import no.digipost.api.PMode;

public class EbmsPullRequest extends EbmsOutgoingMessage {

    public EbmsPullRequest(final EbmsAktoer mottaker) {
        this(mottaker, Prioritet.NORMAL);
    }

    public EbmsPullRequest(final EbmsAktoer mottaker, final String mpcId) {
        this(mottaker, Prioritet.NORMAL, mpcId);
    }

    public EbmsPullRequest(final EbmsAktoer mottaker, final Prioritet prioritet) {
        this(mottaker, prioritet, null);
    }

    public EbmsPullRequest(final EbmsAktoer mottaker, final Prioritet prioritet, final String mpcId) {
        super(mottaker, newId(), null, PMode.Action.KVITTERING, prioritet, mpcId);
    }

}
