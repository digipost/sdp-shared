package no.posten.dpost.offentlig.api.exceptions.ebms.standard.reliability;

import no.posten.dpost.offentlig.api.exceptions.ebms.AbstractEbmsException;

import static no.posten.dpost.offentlig.api.exceptions.ebms.Category.Communication;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Origin.reliability;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Severity.failure;

public class DeliveryFailureException extends AbstractEbmsException {

	public static final String DEFAULT_DESCRIPTION = "Although the message was sent under Guaranteed delivery requirement," +
			" the Reliability module could not get assurance that the message was properly delivered, in spite of resending efforts.";

	public DeliveryFailureException() {
		this(null, null, DEFAULT_DESCRIPTION);
	}

	public DeliveryFailureException(final Throwable cause) {
		this(null, cause, DEFAULT_DESCRIPTION);
	}

	public DeliveryFailureException(final String refToMessageInError, final String description) {
		this(refToMessageInError, null, description);
	}

	public DeliveryFailureException(final String refToMessageInError, final Throwable cause, final String description) {
		super(reliability, "0202", failure, Communication, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "DeliveryFailure";
	}
}
