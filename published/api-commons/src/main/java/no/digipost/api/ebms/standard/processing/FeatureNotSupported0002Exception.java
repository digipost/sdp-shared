/**
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.digipost.api.ebms.standard.processing;

import no.digipost.api.ebms.Origin;
import no.digipost.api.ebms.AbstractEbmsException;
import no.digipost.api.ebms.Category;

import static no.digipost.api.ebms.Severity.warning;

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
		super(Origin.ebMS, FEATURE_NOT_SUPPORTED_0002_CODE, warning, Category.Content, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "FeatureNotSupported";
	}
}
