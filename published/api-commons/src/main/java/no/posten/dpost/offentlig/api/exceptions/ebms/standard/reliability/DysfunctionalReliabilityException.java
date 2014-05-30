package no.posten.dpost.offentlig.api.exceptions.ebms.standard.reliability;

import no.posten.dpost.offentlig.api.exceptions.ebms.AbstractEbmsException;

import static no.posten.dpost.offentlig.api.exceptions.ebms.Category.Processing;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Origin.reliability;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Severity.failure;

public class DysfunctionalReliabilityException extends AbstractEbmsException {

	public static final String DEFAULT_DESCRIPTION = "Some reliability function as implemented by the Reliability module, " +
			"is not operational, or the reliability state associated with this message sequence is not valid.";

	public DysfunctionalReliabilityException() {
		this(null, null, DEFAULT_DESCRIPTION);
	}

	public DysfunctionalReliabilityException(final Throwable cause) {
		this(null, cause, DEFAULT_DESCRIPTION);
	}

	public DysfunctionalReliabilityException(final String refToMessageInError, final String description) {
		this(refToMessageInError, null, description);
	}

	public DysfunctionalReliabilityException(final String refToMessageInError, final Throwable cause, final String description) {
		super(reliability, "0201", failure, Processing, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "DysfunctionalReliability";
	}
}
