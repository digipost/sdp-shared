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

import no.digipost.api.exceptions.ebms.standard.processing.InvalidHeaderException;
import org.junit.Test;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;

import static no.digipost.api.exceptions.ebms.standard.processing.InvalidHeaderException.INVALID_HEADER_EBMS_CODE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

public class EbmsExceptionTest {

	@Test
	public void shouldTransformCorrectlyToError() {
		Error error = new InvalidHeaderException().toError();
		assertThat(error.getShortDescription(), is("InvalidHeader"));
		assertThat(error.getOrigin(), is("ebMS"));
		assertThat(error.getCategory(), is("UnPackaging"));
		assertThat(error.getSeverity(), is("failure"));
		assertThat(error.getErrorCode(), is(INVALID_HEADER_EBMS_CODE));
		assertThat("en", is(error.getDescription().getLang()));
		assertThat(error.getDescription().getValue(),
				is("The ebMS header is either not well formed as an XML document, or does not conform to the ebMS packaging rules."));
		assertThat(error.getErrorDetail(), is(nullValue()));
		assertThat(error.getRefToMessageInError(), is(nullValue()));
	}

	@Test
	public void shouldTransformCorrectlyToErrorWithDetail() {
		Error error = new InvalidHeaderException(new RuntimeException("WTF")).toError();
		assertThat(error.getErrorDetail(), is(nullValue()));
	}

	@Test
	public void shouldTransformCorrectlyToErrorWithRefToMessageInError() {
		Error error = new InvalidHeaderException("message-id", "some description").toError();
		assertThat(error.getRefToMessageInError(), is("message-id"));
	}

}
