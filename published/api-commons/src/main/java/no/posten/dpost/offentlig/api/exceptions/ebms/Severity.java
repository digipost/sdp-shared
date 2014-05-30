package no.posten.dpost.offentlig.api.exceptions.ebms;

public enum Severity {

	failure,
	warning;

	@Override
	public String toString() {
		return this.name();
	}

}