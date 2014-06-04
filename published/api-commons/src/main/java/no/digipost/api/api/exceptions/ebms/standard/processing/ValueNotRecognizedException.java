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
package no.digipost.api.api.exceptions.ebms.standard.processing;

import no.digipost.api.api.exceptions.ebms.Origin;
import no.digipost.api.api.exceptions.ebms.AbstractEbmsException;

import static no.digipost.api.api.exceptions.ebms.Category.Content;
import static no.digipost.api.api.exceptions.ebms.Origin.ebMS;
import static no.digipost.api.api.exceptions.ebms.Severity.failure;

public class ValueNotRecognizedException extends AbstractEbmsException {

	public static final String DEFAULT_DESCRIPTION = "Although the message document is well formed and schema valid, " +
			"some element/attribute contains a value that could not be recognized and therefore could not be used by the MSH.";

	public ValueNotRecognizedException() {
		this(null, null, DEFAULT_DESCRIPTION);
	}

	public ValueNotRecognizedException(final Throwable cause) {
		this(null, cause, DEFAULT_DESCRIPTION);
	}

	public ValueNotRecognizedException(final String refToMessageInError, final String description) {
		this(refToMessageInError, null, description);
	}
	public ValueNotRecognizedException(final String refToMessageInError, final Throwable cause, final String description) {
		super(Origin.ebMS, "0001", failure, Content, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "ValueNotRecognized";
	}
}
