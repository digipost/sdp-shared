package no.posten.dpost.offentlig.api.exceptions.ebms.standard.processing;

import no.posten.dpost.offentlig.api.exceptions.ebms.AbstractEbmsException;

import static no.posten.dpost.offentlig.api.exceptions.ebms.Category.Communication;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Origin.ebMS;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Severity.failure;

public class ConnectionFailureException extends AbstractEbmsException {

	public static final String DEFAULT_DESCRIPTION = "The MSH is experiencing temporary or permanent failure in trying " +
			"to open a transport connection with a remote MSH.";

	public ConnectionFailureException() {
		this(null, null, DEFAULT_DESCRIPTION);
	}

	public ConnectionFailureException(final Throwable cause) {
		this(null, cause, DEFAULT_DESCRIPTION);
	}

	public ConnectionFailureException(final String refToMessageInError, final String description) {
		this(refToMessageInError, null, description);
	}

	public ConnectionFailureException(final String refToMessageInError, final Throwable cause, final String description) {
		super(ebMS, "0005", failure, Communication, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "ConnectionFailure";
	}
}
