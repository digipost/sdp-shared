package no.posten.dpost.offentlig.api.exceptions.ebms.standard.processing;

import no.posten.dpost.offentlig.api.exceptions.ebms.AbstractEbmsException;

import static no.posten.dpost.offentlig.api.exceptions.ebms.Category.Communication;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Origin.ebMS;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Severity.warning;

public class EmptyMessagePartitionChannelException extends AbstractEbmsException {

	public static final String DEFAULT_DESCRIPTION = "There is no message available for pulling from this MPC at this moment.";

	public static final String EMPTY_MPC_CODE = "0006";
	public static final String EMPTY_MPC_EBMS_CODE = EBMS_STANDARD_ERROR_CODE_PREFIX + ":" + EMPTY_MPC_CODE;

	public EmptyMessagePartitionChannelException() {
		this(null, null);
	}

	public EmptyMessagePartitionChannelException(final String refToMessageInError) {
		this(refToMessageInError, null);
	}

	public EmptyMessagePartitionChannelException(final Throwable cause) {
		this(null, cause);
	}

	public EmptyMessagePartitionChannelException(final String refToMessageInError, final Throwable cause) {
		super(ebMS, EMPTY_MPC_CODE, warning, Communication, DEFAULT_DESCRIPTION, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "EmptyMessagePartitionChannel";
	}

	@Override
	public boolean loggable() {
		return false;
	}
}
