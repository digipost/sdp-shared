package no.posten.dpost.offentlig.api.exceptions.ebms.standard.processing;

import no.posten.dpost.offentlig.api.exceptions.ebms.AbstractEbmsException;

import static no.posten.dpost.offentlig.api.exceptions.ebms.Category.UnPackaging;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Origin.ebMS;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Severity.failure;

public class FeatureNotSupported0008Exception extends AbstractEbmsException {

	public static final String DEFAULT_DESCRIPTION = "Although the message document is well formed and schema valid, " +
			"the presence or absence of some element/ attribute is not consistent with the capability of the MSH, " +
			"with respect to supported features.";

	public FeatureNotSupported0008Exception() {
		this(null, null, DEFAULT_DESCRIPTION);
	}

	public FeatureNotSupported0008Exception(final Throwable cause) {
		this(null, cause, DEFAULT_DESCRIPTION);
	}

	public FeatureNotSupported0008Exception(final String refToMessageInError, final String description) {
		this(refToMessageInError, null, description);
	}

	public FeatureNotSupported0008Exception(final String refToMessageInError, final Throwable cause, final String description) {
		super(ebMS, "0008", failure, UnPackaging, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "FeatureNotSupported";
	}
}
