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
package no.digipost.api.ebms.error;


import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;

import static no.digipost.api.ebms.Category.Communication;
import static no.digipost.api.ebms.Category.Content;
import static no.digipost.api.ebms.Origin.ebMS;
import static no.digipost.api.ebms.Severity.warning;

/**
 * Bruk denne klassen for å generere ebMS errors med secerity warning
 */
public class EbmsError {

	public static final String LANGUAGE_CODE_ERROR_DESCRIPTION = "en";

	public static final String EBMS_STANDARD_ERROR_CODE_PREFIX = "EBMS";

	public static final String EMPTY_MPC_EBMS_CODE = EBMS_STANDARD_ERROR_CODE_PREFIX + ":0006";
	public static final String FEATURE_NOT_SUPPORTED_EBMS_CODE = EBMS_STANDARD_ERROR_CODE_PREFIX + ":0002";


	private EbmsError() {

	}

	public static Error createEmptyMessagePartitionChannelError(String refToMessageInError) {
		return new Error()
				.withOrigin(ebMS.toString())
				.withErrorCode(EMPTY_MPC_EBMS_CODE)
				.withSeverity(warning.toString())
				.withCategory(Communication.toString())
				.withShortDescription("EmptyMessagePartitionChannel")
				.withDescription(
						new Description("There is no message available for pulling from this MPC at this moment.",
								LANGUAGE_CODE_ERROR_DESCRIPTION)
				)
				.withRefToMessageInError(refToMessageInError);
	}

	public static Error createFeatureNotSupported(String refToMessageInError) {
		return new Error()
				.withOrigin(ebMS.toString())
				.withErrorCode(FEATURE_NOT_SUPPORTED_EBMS_CODE)
				.withSeverity(warning.toString())
				.withCategory(Content.toString())
				.withShortDescription("FeatureNotSupported")
				.withDescription(
						new Description("Although the message document is well formed and schema valid, " +
							"some element/attribute value cannot be processed as expected because the related feature is not supported by the MSH.",
								LANGUAGE_CODE_ERROR_DESCRIPTION)
				)
				.withRefToMessageInError(refToMessageInError);
	}

}
