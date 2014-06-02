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
package no.posten.dpost.offentlig.api;

import no.posten.dpost.offentlig.api.handlers.ApplikasjonsKvitteringReceiver;
import no.posten.dpost.offentlig.api.handlers.BekreftelseSender;
import no.posten.dpost.offentlig.api.handlers.EbmsContextAwareWebServiceTemplate;
import no.posten.dpost.offentlig.api.handlers.EmptyReceiver;
import no.posten.dpost.offentlig.api.handlers.ForsendelseSender;
import no.posten.dpost.offentlig.api.handlers.KvitteringSender;
import no.posten.dpost.offentlig.api.handlers.PullRequestSender;
import no.posten.dpost.offentlig.api.handlers.TransportKvitteringReceiver;
import no.posten.dpost.offentlig.api.interceptors.EbmsClientInterceptor;
import no.posten.dpost.offentlig.api.interceptors.EbmsReferenceValidatorInterceptor;
import no.posten.dpost.offentlig.api.interceptors.KeyStoreInfo;
import no.posten.dpost.offentlig.api.interceptors.RemoveContentLengthInterceptor;
import no.posten.dpost.offentlig.api.interceptors.TransactionLogInterceptor;
import no.posten.dpost.offentlig.api.interceptors.WsSecurityInterceptor;
import no.posten.dpost.offentlig.api.representations.EbmsApplikasjonsKvittering;
import no.posten.dpost.offentlig.api.representations.EbmsForsendelse;
import no.posten.dpost.offentlig.api.representations.EbmsPullRequest;
import no.posten.dpost.offentlig.api.representations.Organisasjonsnummer;
import no.posten.dpost.offentlig.api.representations.TransportKvittering;
import no.posten.dpost.offentlig.xml.Marshalling;
import org.apache.http.HttpHost;
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

import static no.posten.dpost.offentlig.api.exceptions.ebms.standard.processing.EmptyMessagePartitionChannelException.EMPTY_MPC_EBMS_CODE;

public class MessageSender {

	private static final Logger LOG = LoggerFactory.getLogger(MessageSender.class);

	protected WebServiceTemplate meldingTemplate;
	private final Jaxb2Marshaller marshaller;
	private Organisasjonsnummer tekniskAvsender;
	private final String uri;
	private Organisasjonsnummer tekniskMottaker;
	private SdpMeldingSigner signer;

	protected MessageSender(final String uri, final Jaxb2Marshaller marshaller) {
		if (marshaller == null) {
			throw new AssertionError("marshaller kan ikke være null");
		}
		this.uri = uri;
		this.marshaller = marshaller;
	}

	public TransportKvittering send(final EbmsForsendelse forsendelse) {
		ForsendelseSender sender = new ForsendelseSender(signer, tekniskAvsender, tekniskMottaker, forsendelse.doc, forsendelse, marshaller);
		LOG.info("Sender forsendelse til : {} ", uri);
		try {
			return meldingTemplate.sendAndReceive(uri, sender, new TransportKvitteringReceiver());
		} finally {
			sender.cleanTemp();
		}
	}

	public EbmsApplikasjonsKvittering hentKvittering(final EbmsPullRequest pullRequest) {
		return hentKvittering(pullRequest, null);
	}

	public EbmsApplikasjonsKvittering hentKvittering(final EbmsPullRequest pullRequest, final EbmsApplikasjonsKvittering tidligereKvitteringSomSkalBekreftes) {
		try {
			return meldingTemplate.sendAndReceive(uri, new PullRequestSender(pullRequest, marshaller, tidligereKvitteringSomSkalBekreftes), new ApplikasjonsKvitteringReceiver(marshaller));
		} catch (EbmsClientException ex) {
			if (ex.getErrors().size() == 1 && ex.getErrors().get(0).getErrorCode().equals(EMPTY_MPC_EBMS_CODE)) {
				return null;
			}
			throw ex;
		}
	}

	public void bekreft(final EbmsApplikasjonsKvittering appKvittering) {
		meldingTemplate.sendAndReceive(uri, new BekreftelseSender(appKvittering, marshaller), new EmptyReceiver());
	}

	public void send(final EbmsApplikasjonsKvittering appKvittering) {
		meldingTemplate.sendAndReceive(uri, new KvitteringSender(signer, tekniskAvsender, tekniskMottaker, appKvittering, marshaller), new EmptyReceiver());
	}

	public static Builder create(final String uri, final KeyStoreInfo keystoreInfo, final Organisasjonsnummer tekniskAvsenderId, final Organisasjonsnummer tekniskMottaker) {
		WsSecurityInterceptor wssecMelding = new WsSecurityInterceptor(keystoreInfo, null);
		wssecMelding.afterPropertiesSet();
		return create(uri, keystoreInfo, wssecMelding, tekniskAvsenderId, tekniskMottaker);
	}

	public static Builder create(final String uri, final KeyStoreInfo keystoreInfo, final WsSecurityInterceptor wsSecInterceptor, final Organisasjonsnummer tekniskAvsenderId, final Organisasjonsnummer tekniskMottaker) {
		return new Builder(uri, tekniskAvsenderId, tekniskMottaker, wsSecInterceptor, keystoreInfo);
	}

	private static HashMap<String, Object> getMessageProperties() {
		HashMap<String, Object> messageProperties = new HashMap<String, Object>();
		messageProperties.put("saaj.lazy.soap.body", "true");
		return messageProperties;
	}

	private static WebServiceTemplate createTemplate(final SaajSoapMessageFactory factory, final Jaxb2Marshaller marshaller, final Organisasjonsnummer remoteParty) {
		EbmsContextAwareWebServiceTemplate template = new EbmsContextAwareWebServiceTemplate(factory, remoteParty);
		template.setMarshaller(marshaller);
		template.setUnmarshaller(marshaller);
		return template;
	}

	public static class Builder {

        private static Jaxb2Marshaller defaultMarshaller;

		public static final int DEFAULT_MAX_PER_ROUTE = 10;

		private final String endpointUri;
		private final Organisasjonsnummer tekniskAvsenderId;
		private final Organisasjonsnummer tekniskMottaker;
		private final WsSecurityInterceptor wssecMelding;
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

		private Builder(final String endpointUri, final Organisasjonsnummer tekniskAvsenderId, final Organisasjonsnummer tekniskMottaker,
		                final WsSecurityInterceptor wssecMelding, final KeyStoreInfo keystoreInfo) {
			this.endpointUri = endpointUri;
			this.tekniskAvsenderId = tekniskAvsenderId;
			this.tekniskMottaker = tekniskMottaker;
			this.wssecMelding = wssecMelding;
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
			meldingInterceptors.add(wssecMelding);
			meldingInterceptors.add(new EbmsReferenceValidatorInterceptor(marshaller));
			meldingInterceptors.add(TransactionLogInterceptor.createClientInterceptor(marshaller));

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
					.setConnectionRequestTimeout(connectionRequestTimeout);

			if (httpHost != null) {
				requestConfigBuilder.setProxy(httpHost);
			}

			CloseableHttpClient client = HttpClientBuilder
					.create()
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
                defaultMarshaller = Marshalling.createUnManaged();
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




}
