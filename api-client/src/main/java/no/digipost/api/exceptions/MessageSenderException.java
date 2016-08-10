
package no.digipost.api.exceptions;

public abstract class MessageSenderException extends RuntimeException {

	public MessageSenderException(String message) {
		super(message);
	}
	public MessageSenderException(String message, Throwable cause) {
		super(message, cause);
	}

}
