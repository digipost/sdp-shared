package no.digipost.api.exceptions;

import java.io.IOException;

public class MessageSenderIOException extends MessageSenderException {

    public MessageSenderIOException(String message, IOException e) {
        super(message, e);
    }

}
