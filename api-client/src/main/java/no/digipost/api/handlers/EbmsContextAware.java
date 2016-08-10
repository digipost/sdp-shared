package no.digipost.api.handlers;

import no.digipost.api.representations.EbmsContext;

public abstract class EbmsContextAware {
    protected EbmsContext ebmsContext;

    public void setContext(final EbmsContext ebmsContext) {
        this.ebmsContext = ebmsContext;

    }

}
