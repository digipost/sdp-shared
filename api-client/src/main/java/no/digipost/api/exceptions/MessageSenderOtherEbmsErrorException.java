
package no.digipost.api.exceptions;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;
import org.springframework.ws.soap.SoapMessage;

import static java.lang.String.format;

public class MessageSenderOtherEbmsErrorException extends MessageSenderEbmsErrorException {

	public static final String OTHER_ERROR_CODE = "EBMS:0004";
	public static final String OTHER_SHORT_DESCRIPTION = "Other";
	public static final String OTHER_SEVERITY = "failure";

	public MessageSenderOtherEbmsErrorException(SoapMessage soapMessage, Error error) {
		super(soapMessage, error);
		if (!isOtherError(error)) {
			String message = format("Error not of correct type, expected %s %s %s, got %s %s %s",
					OTHER_ERROR_CODE, OTHER_SHORT_DESCRIPTION, OTHER_SEVERITY,
					error.getErrorCode(), error.getShortDescription(), error.getSeverity());
			throw new IllegalArgumentException(message);
		}
	}

	public static boolean isOtherError(Error error) {
		return OTHER_ERROR_CODE.equals(error.getErrorCode()) &&
				OTHER_SHORT_DESCRIPTION.equals(error.getShortDescription()) &&
				OTHER_SEVERITY.equals(error.getSeverity());
	}

}
