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

import no.digipost.api.exceptions.ebms.standard.processing.EmptyMessagePartitionChannelException;
import org.junit.Test;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;

import static no.digipost.api.exceptions.ebms.standard.processing.EmptyMessagePartitionChannelException.EMPTY_MPC_EBMS_CODE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class EbmsExceptionTest {

	@Test
	public void shouldTransformCorrectlyToError() {
		Error error = new EmptyMessagePartitionChannelException().toError();
		assertThat("EmptyMessagePartitionChannel", is(error.getShortDescription()));
		assertThat("ebMS", is(error.getOrigin()));
		assertThat("Communication", is(error.getCategory()));
		assertThat("warning", is(error.getSeverity()));
		assertThat(EMPTY_MPC_EBMS_CODE, is(error.getErrorCode()));
		assertThat("en", is(error.getDescription().getLang()));
		assertThat("There is no message available for pulling from this MPC at this moment.",
				is(error.getDescription().getValue()));
		assertThat(null, is(error.getErrorDetail()));
		assertThat(null, is(error.getRefToMessageInError()));
	}

	@Test
	public void shouldTransformCorrectlyToErrorWithDetail() {
		Error error = new EmptyMessagePartitionChannelException(new RuntimeException("WTF")).toError();
		assertThat(null, is(error.getErrorDetail()));
	}

	@Test
	public void shouldTransformCorrectlyToErrorWithRefToMessageInError() {
		Error error = new EmptyMessagePartitionChannelException("message-id").toError();
		assertThat("message-id", is(error.getRefToMessageInError()));
	}

}
