package no.posten.dpost.offentlig.api.handlers;

import no.posten.dpost.offentlig.api.representations.EbmsContext;

public abstract class EbmsContextAware {
	protected EbmsContext ebmsContext;

	public void setContext(final EbmsContext ebmsContext) {
		this.ebmsContext = ebmsContext;

	}
}
