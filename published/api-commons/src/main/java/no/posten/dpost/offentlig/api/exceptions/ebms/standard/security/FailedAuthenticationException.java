package no.posten.dpost.offentlig.api.exceptions.ebms.standard.security;

import no.posten.dpost.offentlig.api.exceptions.ebms.AbstractEbmsException;

import static no.posten.dpost.offentlig.api.exceptions.ebms.Category.Processing;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Origin.security;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Severity.failure;

public class FailedAuthenticationException extends AbstractEbmsException {

	public static final String DEFAULT_DESCRIPTION = "The signature in the Security header intended for the \"ebms\" SOAP " +
			"actor, could not be validated by the Security module.";

	public FailedAuthenticationException() {
		this(null, null, DEFAULT_DESCRIPTION);
	}

	public FailedAuthenticationException(final Throwable cause) {
		this(null, cause, DEFAULT_DESCRIPTION);
	}

	public FailedAuthenticationException(final String refToMessageInError, final String description) {
		this(refToMessageInError, null, description);
	}

	public FailedAuthenticationException(final String refToMessageInError, final Throwable cause, final String description) {
		super(security, "0101", failure, Processing, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "FailedAuthentication";
	}
}
