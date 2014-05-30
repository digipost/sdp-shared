package no.posten.dpost.offentlig.api;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;

import java.util.List;

public class EbmsClientException extends RuntimeException {
	List<Error> errors;

	public EbmsClientException(final String message, final List<Error> errors) {
		super(message);
		this.errors = errors;
	}

	public List<Error> getErrors() {
		return errors;
	}
}
