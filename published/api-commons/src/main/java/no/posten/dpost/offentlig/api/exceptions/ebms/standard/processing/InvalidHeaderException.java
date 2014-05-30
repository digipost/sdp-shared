package no.posten.dpost.offentlig.api.exceptions.ebms.standard.processing;

import no.posten.dpost.offentlig.api.exceptions.ebms.AbstractEbmsException;

import static no.posten.dpost.offentlig.api.exceptions.ebms.Category.UnPackaging;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Origin.ebMS;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Severity.failure;

public class InvalidHeaderException extends AbstractEbmsException {

	public static final String DEFAULT_DESCRIPTION = "The ebMS header is either not well formed as an XML document, " +
			"or does not conform to the ebMS packaging rules.";

	public InvalidHeaderException() {
		this(null, null, DEFAULT_DESCRIPTION);
	}

	public InvalidHeaderException(final Throwable cause) {
		this(null, cause, DEFAULT_DESCRIPTION);
	}

	public InvalidHeaderException(final String refToMessageInError, final String description) {
		this(refToMessageInError, null, description);
	}

	public InvalidHeaderException(final String refToMessageInError, final Throwable cause, final String description) {
		super(ebMS, "0009", failure, UnPackaging, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "InvalidHeader";
	}
}
