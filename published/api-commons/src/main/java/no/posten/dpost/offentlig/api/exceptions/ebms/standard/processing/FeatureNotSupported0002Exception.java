package no.posten.dpost.offentlig.api.exceptions.ebms.standard.processing;

import no.posten.dpost.offentlig.api.exceptions.ebms.AbstractEbmsException;

import static no.posten.dpost.offentlig.api.exceptions.ebms.Category.Content;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Origin.ebMS;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Severity.warning;

public class FeatureNotSupported0002Exception extends AbstractEbmsException {

	public static final String DEFAULT_DESCRIPTION = "Although the message document is well formed and schema valid, " +
			"some element/attribute value cannot be processed as expected because the related feature is not supported by the MSH.";

    public static final String FEATURE_NOT_SUPPORTED_0002_CODE = "0002";
    public static final String FEATURE_NOT_SUPPORTED_0002_EBMS_CODE = EBMS_STANDARD_ERROR_CODE_PREFIX + ":" + FEATURE_NOT_SUPPORTED_0002_CODE;

	public FeatureNotSupported0002Exception() {
		this(null, null, DEFAULT_DESCRIPTION);
	}

	public FeatureNotSupported0002Exception(final Throwable cause) {
		this(null, cause, DEFAULT_DESCRIPTION);
	}

	public FeatureNotSupported0002Exception(final String refToMessageInError, final String description) {
		this(refToMessageInError, null, description);
	}

	public FeatureNotSupported0002Exception(final String refToMessageInError, final Throwable cause, final String description) {
		super(ebMS, FEATURE_NOT_SUPPORTED_0002_CODE, warning, Content, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "FeatureNotSupported";
	}
}
