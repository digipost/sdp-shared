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
package no.digipost.api.interceptors;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * See https://jira.springsource.org/browse/SWS-563?focusedCommentId=67885&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-67885
 */
public class RemoveContentLengthInterceptor implements HttpRequestInterceptor {

	@Override
	public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
		if (request instanceof HttpEntityEnclosingRequest) {
			if (request.containsHeader(HTTP.TRANSFER_ENCODING)) {
				request.removeHeaders(HTTP.TRANSFER_ENCODING);
			}
			if (request.containsHeader(HTTP.CONTENT_LEN)) {
				request.removeHeaders(HTTP.CONTENT_LEN);
			}
		}
	}
}
