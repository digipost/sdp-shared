package no.posten.dpost.offentlig.api.ebms.error;

import no.posten.dpost.offentlig.api.exceptions.ebms.standard.processing.EmptyMessagePartitionChannelException;
import org.junit.Test;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;

import static no.posten.dpost.offentlig.api.exceptions.ebms.standard.processing.EmptyMessagePartitionChannelException.EMPTY_MPC_EBMS_CODE;
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
		assertThat("WTF", is(error.getErrorDetail()));
	}

	@Test
	public void shouldTransformCorrectlyToErrorWithRefToMessageInError() {
		Error error = new EmptyMessagePartitionChannelException("message-id").toError();
		assertThat("message-id", is(error.getRefToMessageInError()));
	}

}
