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
package no.digipost.api.exceptions.ebms.standard.security;

import no.digipost.api.exceptions.ebms.AbstractEbmsException;
import no.digipost.api.exceptions.ebms.Category;
import no.digipost.api.exceptions.ebms.Origin;
import no.digipost.api.exceptions.ebms.Severity;

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
		super(Origin.security, "0101", Severity.failure, Category.Processing, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "FailedAuthentication";
	}
}
