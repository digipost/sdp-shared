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
package no.digipost.api.api.interceptors;

import org.apache.commons.lang3.StringUtils;
import org.apache.wss4j.common.ext.Attachment;
import org.apache.wss4j.common.ext.AttachmentRequestCallback;
import org.springframework.ws.soap.SoapMessage;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AttachmentCallbackHandler implements CallbackHandler {
	private final SoapMessage message;

	public AttachmentCallbackHandler(final SoapMessage message) {
		this.message = message;

	}

	@Override
	public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for (Callback c : callbacks) {
			if (c instanceof AttachmentRequestCallback) {
				AttachmentRequestCallback arg = (AttachmentRequestCallback) c;
				List<Attachment> attList = new ArrayList<Attachment>();
				if (StringUtils.isBlank(arg.getAttachmentId()) || arg.getAttachmentId().equals("Attachments")) {
					Iterator<org.springframework.ws.mime.Attachment> attz = message.getAttachments();
					while (attz.hasNext()) {
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
