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
package no.digipost.api;

import no.digipost.api.handlers.ApplikasjonsKvitteringReceiver;
import no.digipost.api.handlers.BekreftelseSender;
import no.digipost.api.handlers.EbmsContextAwareWebServiceTemplate;
import no.digipost.api.handlers.EmptyReceiver;
import no.digipost.api.handlers.ForsendelseSender;
import no.digipost.api.handlers.KvitteringSender;
import no.digipost.api.handlers.PullRequestSender;
import no.digipost.api.handlers.TransportKvitteringReceiver;
import no.digipost.api.interceptors.EbmsClientInterceptor;
import no.digipost.api.interceptors.EbmsReferenceValidatorInterceptor;
import no.digipost.api.interceptors.KeyStoreInfo;
import no.digipost.api.interceptors.RemoveContentLengthInterceptor;
import no.digipost.api.interceptors.TransactionLogClientInterceptor;
import no.digipost.api.interceptors.WsSecurityInterceptor;
import no.digipost.api.representations.EbmsAktoer;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.representations.EbmsForsendelse;
import no.digipost.api.representations.EbmsPullRequest;
import no.digipost.api.representations.TransportKvittering;
import no.digipost.api.xml.Marshalling;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;

public class MessageSender {

	private static final Logger LOG = LoggerFactory.getLogger(MessageSender.class);

	private WebServiceTemplate meldingTemplate;
	private final Jaxb2Marshaller marshaller;
	private EbmsAktoer tekniskAvsender;
	private final String uri;
	private EbmsAktoer tekniskMottaker;
	private SdpMeldingSigner signer;

	protected MessageSender(final String uri, final Jaxb2Marshaller marshaller) {
		if (marshaller == null) {
			throw new AssertionError("marshaller kan ikke være null");
		}
		this.uri = uri;
		this.marshaller = marshaller;
	}

	public TransportKvittering send(final EbmsForsendelse forsendelse) {
		ForsendelseSender sender = new ForsendelseSender(signer, tekniskAvsender, tekniskMottaker, forsendelse, marshaller);
		LOG.info("Sender forsendelse til : {} ", uri);
		return meldingTemplate.sendAndReceive(uri, sender, new TransportKvitteringReceiver());
	}

	public EbmsApplikasjonsKvittering hentKvittering(final EbmsPullRequest pullRequest) {
		return hentKvittering(pullRequest, null);
	}

	public EbmsApplikasjonsKvittering hentKvittering(final EbmsPullRequest pullRequest, final EbmsApplikasjonsKvittering tidligereKvitteringSomSkalBekreftes) {
		return meldingTemplate.sendAndReceive(uri, new PullRequestSender(pullRequest, marshaller, tidligereKvitteringSomSkalBekreftes), new ApplikasjonsKvitteringReceiver(marshaller));
	}

	public void bekreft(final EbmsApplikasjonsKvittering appKvittering) {
		meldingTemplate.sendAndReceive(uri, new BekreftelseSender(appKvittering, marshaller), new EmptyReceiver());
	}

	public void send(final EbmsApplikasjonsKvittering appKvittering) {
		meldingTemplate.sendAndReceive(uri, new KvitteringSender(signer, tekniskAvsender, tekniskMottaker, appKvittering, marshaller), new EmptyReceiver());
	}

	public static Builder create(final String uri, final KeyStoreInfo keystoreInfo, final EbmsAktoer tekniskAvsenderId, final EbmsAktoer tekniskMottaker) {
		WsSecurityInterceptor wssecMelding = new WsSecurityInterceptor(keystoreInfo, null);
		wssecMelding.afterPropertiesSet();
		return create(uri, keystoreInfo, wssecMelding, tekniskAvsenderId, tekniskMottaker);
	}

	public static Builder create(final String uri, final KeyStoreInfo keystoreInfo, final WsSecurityInterceptor wsSecInterceptor, final EbmsAktoer tekniskAvsenderId, final EbmsAktoer tekniskMottaker) {
		return new Builder(uri, tekniskAvsenderId, tekniskMottaker, wsSecInterceptor, keystoreInfo);
	}

	private static HashMap<String, Object> getMessageProperties() {
		HashMap<String, Object> messageProperties = new HashMap<String, Object>();
		// Removed this in order to avoid issues occurring when not using internal saaj-impl
		//messageProperties.put("saaj.lazy.soap.body", "true");
		return messageProperties;
	}

	private static WebServiceTemplate createTemplate(final SaajSoapMessageFactory factory, final Jaxb2Marshaller marshaller, final EbmsAktoer remoteParty) {
		EbmsContextAwareWebServiceTemplate template = new EbmsContextAwareWebServiceTemplate(factory, remoteParty);
		template.setMarshaller(marshaller);
		template.setUnmarshaller(marshaller);
		return template;
	}

	public static class Builder {

        private static Jaxb2Marshaller defaultMarshaller;

		public static final int DEFAULT_MAX_PER_ROUTE = 10;

		private final String endpointUri;
		private final EbmsAktoer tekniskAvsenderId;
		private final EbmsAktoer tekniskMottaker;
		private final WsSecurityInterceptor wsSecurityInterceptor;
		private final KeyStoreInfo keystoreInfo;
		private Jaxb2Marshaller marshaller;
		private final List<InsertInterceptor> interceptorBefore = new ArrayList<InsertInterceptor>();

		// Network config
		private int maxTotal = DEFAULT_MAX_PER_ROUTE;
		private int defaultMaxPerRoute = DEFAULT_MAX_PER_ROUTE;
		private HttpHost httpHost;
		private int socketTimeout = 30000;
		private int connectTimeout = 10000;
		private int connectionRequestTimeout = 10000;
		private final List<HttpRequestInterceptor> httpRequestInterceptors = new ArrayList<HttpRequestInterceptor>();
		private final List<HttpResponseInterceptor> httpResponseInterceptors = new ArrayList<HttpResponseInterceptor>();

		private Builder(final String endpointUri, final EbmsAktoer tekniskAvsenderId, final EbmsAktoer tekniskMottaker,
		                final WsSecurityInterceptor wsSecurityInterceptor, final KeyStoreInfo keystoreInfo) {
			this.endpointUri = endpointUri;
			this.tekniskAvsenderId = tekniskAvsenderId;
			this.tekniskMottaker = tekniskMottaker;
			this.wsSecurityInterceptor = wsSecurityInterceptor;
			this.keystoreInfo = keystoreInfo;
		}

		public Builder withMeldingInterceptorBefore(final Class clazz, final ClientInterceptor interceptor) {
			interceptorBefore.add(new InsertInterceptor(clazz, interceptor));
			return this;
		}

		public Builder withMarshaller(final Jaxb2Marshaller marshaller) {
			this.marshaller = marshaller;
			return this;
		}

		public Builder withMaxTotal(final int maxTotal) {
			this.maxTotal = maxTotal;
			return this;
		}

		public Builder withDefaultMaxPerRoute(final int defaultMaxPerRoute) {
			this.defaultMaxPerRoute = defaultMaxPerRoute;
			return this;
		}

		public Builder withSocketTimeout(final int socketTimeout) {
			this.socketTimeout = socketTimeout;
			return this;
		}

		public Builder withConnectTimeout(final int connectTimeout) {
			this.connectTimeout = connectTimeout;
			return this;
		}

		public Builder withConnectionRequestTimeout(final int connectionRequestTimeout) {
			this.connectionRequestTimeout = connectionRequestTimeout;
			return this;
		}

		public Builder withHttpProxy(final String proxyHost, final int proxyPort) {
			httpHost = new HttpHost(proxyHost, proxyPort, "https");
			return this;
		}
		public Builder withHttpProxy(final String proxyHost, final int proxyPort, final String scheme) {
			httpHost = new HttpHost(proxyHost, proxyPort, scheme);
			return this;
		}

		public Builder withHttpRequestInterceptors(final HttpRequestInterceptor... httpRequestInterceptors) {
			this.httpRequestInterceptors.addAll(asList(httpRequestInterceptors));
			return this;
		}

		public Builder withHttpResponseInterceptors(final HttpResponseInterceptor... httpResponseInterceptors) {
			this.httpResponseInterceptors.addAll(asList(httpResponseInterceptors));
			return this;
		}

		public MessageSender build() {

			if (marshaller == null) {
				marshaller = getDefaultMarshaller();
			}
			HttpComponentsMessageSender httpSender = getHttpComponentsMessageSender();

			MessageSender sender = new MessageSender(endpointUri, marshaller);
			sender.tekniskAvsender = tekniskAvsenderId;
			sender.tekniskMottaker = tekniskMottaker;

            SaajSoapMessageFactory factory;
            try {
                factory = new SaajSoapMessageFactory(MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL));
                factory.setMessageProperties(getMessageProperties());
                factory.setSoapVersion(SoapVersion.SOAP_12);
                factory.afterPropertiesSet();
            } catch (SOAPException e) {
                throw new RuntimeException("Unable to initialize SoapMessageFactory", e);
            }

			WebServiceTemplate wsTemplate = createTemplate(factory, marshaller, tekniskMottaker);

			sender.signer = new SdpMeldingSigner(keystoreInfo, marshaller);
			sender.meldingTemplate = wsTemplate;
			wsTemplate.setMessageSender(httpSender);

			List<ClientInterceptor> meldingInterceptors = new ArrayList<ClientInterceptor>();
			meldingInterceptors.add(new EbmsClientInterceptor(marshaller, tekniskMottaker));
			meldingInterceptors.add(wsSecurityInterceptor);
			meldingInterceptors.add(new EbmsReferenceValidatorInterceptor(marshaller));
			meldingInterceptors.add(new TransactionLogClientInterceptor(marshaller));

			for (InsertInterceptor insertInterceptor : interceptorBefore) {
				insertInterceptor(meldingInterceptors, insertInterceptor);
			}

			wsTemplate.setInterceptors(meldingInterceptors.toArray(new ClientInterceptor[meldingInterceptors.size()]));

			return sender;
		}

		private HttpComponentsMessageSender getHttpComponentsMessageSender() {
			PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
			connectionManager.setMaxTotal(maxTotal);
			connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);

			RequestConfig.Builder requestConfigBuilder = RequestConfig
					.copy(RequestConfig.DEFAULT)
					.setSocketTimeout(socketTimeout)
					.setConnectTimeout(connectTimeout)
					.setConnectionRequestTimeout(connectionRequestTimeout)
					.setCookieSpec(CookieSpecs.IGNORE_COOKIES);

			if (httpHost != null) {
				requestConfigBuilder.setProxy(httpHost);
			}

			HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

			for (HttpRequestInterceptor httpRequestInterceptor : this.httpRequestInterceptors) {
				httpClientBuilder.addInterceptorFirst(httpRequestInterceptor);
			}

			for (HttpResponseInterceptor httpResponseInterceptor : this.httpResponseInterceptors) {
				httpClientBuilder.addInterceptorFirst(httpResponseInterceptor);
			}

			CloseableHttpClient client = httpClientBuilder
					.addInterceptorFirst(new RemoveContentLengthInterceptor())
					.setConnectionManager(connectionManager)
					.setDefaultRequestConfig(requestConfigBuilder.build())
					.build();

			return new HttpComponentsMessageSender(client);
		}

		private void insertInterceptor(final List<ClientInterceptor> meldingInterceptors, final InsertInterceptor insertInterceptor) {
			for (ClientInterceptor c : meldingInterceptors) {
				if (insertInterceptor.clazz.isAssignableFrom(c.getClass())) {
					meldingInterceptors.add(meldingInterceptors.indexOf(c), insertInterceptor.interceptor);
					return;
				}
			}
			throw new IllegalArgumentException("Could not find interceptor of class " + insertInterceptor.clazz);
		}

        protected static synchronized Jaxb2Marshaller getDefaultMarshaller() {
            if (defaultMarshaller == null) {
                defaultMarshaller = Marshalling.getMarshallerSingleton();
            }
            return defaultMarshaller;
        }

	}

	public static class InsertInterceptor {

		private final Class clazz;
		private final ClientInterceptor interceptor;

		public InsertInterceptor(final Class clazz, final ClientInterceptor interceptor) {
			this.clazz = clazz;
			this.interceptor = interceptor;
		}
	}

	public Jaxb2Marshaller getMarshaller() {
		return marshaller;
	}

    public WebServiceTemplate getMeldingTemplate() {
        return meldingTemplate;
    }
}
