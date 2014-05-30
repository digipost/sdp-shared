package no.posten.dpost.offentlig.api.interceptors;

import org.apache.commons.lang3.StringUtils;
import org.apache.wss4j.common.ext.Attachment;
import org.apache.wss4j.common.ext.AttachmentRequestCallback;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AttachmentCallbackHandler implements CallbackHandler {
	private final SaajSoapMessage message;

	public AttachmentCallbackHandler(final SaajSoapMessage message) {
		this.message = message;

	}

	@Override
	public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for(Callback c : callbacks) {
			if (c instanceof AttachmentRequestCallback) {
				AttachmentRequestCallback arg = (AttachmentRequestCallback)c;
				List<Attachment> attList = new ArrayList<Attachment>();
				if (StringUtils.isBlank(arg.getAttachmentId()) || arg.getAttachmentId().equals("Attachments")) {
					Iterator<org.springframework.ws.mime.Attachment> attz = message.getAttachments();
					while(attz.hasNext()) {
						attList.add(convert(attz.next()));
					}
				} else {
					org.springframework.ws.mime.Attachment attachment = message.getAttachment("<" + arg.getAttachmentId() + ">");
					if (attachment == null) {
						throw new IllegalArgumentException("No such attachment: " + arg.getAttachmentId());
					}
					attList.add(convert(attachment));
				}
				arg.setAttachments(attList);
			}
		}
	}


	protected static Attachment convert(final org.springframework.ws.mime.Attachment n) throws IOException {
		Attachment e = new Attachment();
		e.setId(n.getContentId().replaceFirst("<", "").replace(">", ""));
		e.setMimeType(n.getContentType());
		e.setSourceStream(n.getInputStream());
		return e;
	}
}
