package no.posten.dpost.offentlig.api.exceptions.ebms.standard.security;

import no.posten.dpost.offentlig.api.exceptions.ebms.AbstractEbmsException;

import static no.posten.dpost.offentlig.api.exceptions.ebms.Category.Processing;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Origin.security;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Severity.failure;

public class PolicyNoncomplianceException extends AbstractEbmsException {

	public static final String DEFAULT_DESCRIPTION = "The processor determined that the message's security methods, " +
			"parameters, scope or other security policy-level requirements or agreements were not satisfied.";

	public PolicyNoncomplianceException() {
		this(null, null, DEFAULT_DESCRIPTION);
	}

	public PolicyNoncomplianceException(final Throwable cause) {
		this(null, cause, DEFAULT_DESCRIPTION);
	}

	public PolicyNoncomplianceException(final String refToMessageInError, final String description) {
		this(refToMessageInError, null, description);
	}

	public PolicyNoncomplianceException(final String refToMessageInError, final Throwable cause, final String description) {
		super(security, "0103", failure, Processing, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "PolicyNoncompliance";
	}
}
