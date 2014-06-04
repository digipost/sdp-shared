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
import no.digipost.api.ebms.Severity;

public class ValueInconsistentException extends AbstractEbmsException {

	private static final String DEFAULT_DESCRIPTION =
			"Although the message document is well formed and schema valid, " +
					"some element/attribute value is inconsistent either with the content of other element/attribute, " +
					"or with the processing mode of the MSH, or with the normative requirements of the ebMS specification.";

	public ValueInconsistentException() {
		this(null, null, DEFAULT_DESCRIPTION);
	}

	public ValueInconsistentException(final Throwable cause) {
		this(null, cause, DEFAULT_DESCRIPTION);
	}

	public ValueInconsistentException(final String refToMessageInError, final String description) {
		this(refToMessageInError, null, description);
	}
	public ValueInconsistentException(final String refToMessageInError, final Throwable cause, final String description) {
		super(Origin.ebMS, "0003", Severity.failure, Category.Content, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "ValueInconsistent";
	}
}
