package no.posten.dpost.offentlig.api.interceptors;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/* Denne klassen er en hyllest til Frode Nerbråten */

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
