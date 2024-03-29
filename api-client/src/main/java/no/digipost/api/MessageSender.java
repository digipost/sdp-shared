package no.digipost.api;


import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPException;
import no.digipost.api.exceptions.MessageSenderFaultMessageResolver;
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
import no.digipost.api.interceptors.SoapLog;
import no.digipost.api.interceptors.SoapLog.LogLevel;
import no.digipost.api.interceptors.SoapLogClientInterceptor;
import no.digipost.api.interceptors.TransactionLogClientInterceptor;
import no.digipost.api.interceptors.WsSecurityInterceptor;
import no.digipost.api.representations.EbmsAktoer;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.representations.EbmsForsendelse;
import no.digipost.api.representations.EbmsPullRequest;
import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;
import no.digipost.api.representations.TransportKvittering;
import no.digipost.api.xml.JaxbMarshaller;
import no.digipost.api.xml.Marshalling;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;


public interface MessageSender {

    public static Builder create(EbmsEndpointUriBuilder uri, KeyStoreInfo keystoreInfo, EbmsAktoer databehandler,
                                 EbmsAktoer tekniskMottaker) {
        WsSecurityInterceptor wssecMelding = new WsSecurityInterceptor(keystoreInfo, null);
        wssecMelding.afterPropertiesSet();

        return create(uri, keystoreInfo, wssecMelding, databehandler, tekniskMottaker);
    }

    public static Builder create(EbmsEndpointUriBuilder uri, KeyStoreInfo keystoreInfo, WsSecurityInterceptor wsSecInterceptor,
                                 EbmsAktoer databehandler, EbmsAktoer tekniskMottaker) {
        return new Builder(uri, databehandler, tekniskMottaker, wsSecInterceptor, keystoreInfo);
    }

    TransportKvittering send(EbmsForsendelse forsendelse);

    void send(EbmsApplikasjonsKvittering appKvittering);

    EbmsApplikasjonsKvittering hentKvittering(EbmsPullRequest pullRequest, KanBekreftesSomBehandletKvittering tidligereKvitteringSomSkalBekreftes);

    default EbmsApplikasjonsKvittering hentKvittering(EbmsPullRequest pullRequest) {
        return hentKvittering(pullRequest, null);
    }

    void bekreft(KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering);

    WebServiceTemplate getMeldingTemplate();

    JaxbMarshaller getMarshaller();

    @FunctionalInterface
    public static interface ClientInterceptorWrapper {
        ClientInterceptor wrap(ClientInterceptor clientInterceptor);
    }

    public static class Builder {

        public static final int DEFAULT_MAX_PER_ROUTE = 10;

        private static final Logger LOG = LoggerFactory.getLogger(MessageSender.Builder.class);

        private static JaxbMarshaller defaultMarshaller;
        private final EbmsEndpointUriBuilder endpointUri;
        private final EbmsAktoer databehandler;
        private final EbmsAktoer tekniskMottaker;
        private final WsSecurityInterceptor wsSecurityInterceptor;
        private final KeyStoreInfo keystoreInfo;
        private final List<InsertInterceptor> interceptorBefore = new ArrayList<InsertInterceptor>();
        private final List<HttpRequestInterceptor> httpRequestInterceptors = new ArrayList<HttpRequestInterceptor>();
        private final List<HttpResponseInterceptor> httpResponseInterceptors = new ArrayList<HttpResponseInterceptor>();
        private JaxbMarshaller marshaller;
        // Network config
        private int maxTotal = DEFAULT_MAX_PER_ROUTE;
        private int defaultMaxPerRoute = DEFAULT_MAX_PER_ROUTE;
        private HttpHost httpHost;
        private int socketTimeout = 30000;
        private int connectTimeout = 10000;
        private int connectionRequestTimeout = 10000;
        private Duration validateAfterInactivity = Duration.of(2, ChronoUnit.SECONDS);
        private SoapLog.LogLevel logLevel = LogLevel.NONE;
        private ClientInterceptorWrapper clientInterceptorWrapper = interceptor -> interceptor;
        private MessageFactorySupplier messageFactorySupplier;


        private Builder(EbmsEndpointUriBuilder uri, EbmsAktoer databehandler, EbmsAktoer tekniskMottaker,
                        WsSecurityInterceptor wsSecurityInterceptor, KeyStoreInfo keystoreInfo) {
            this.endpointUri = uri;
            this.databehandler = databehandler;
            this.tekniskMottaker = tekniskMottaker;
            this.wsSecurityInterceptor = wsSecurityInterceptor;
            this.keystoreInfo = keystoreInfo;
        }


        protected static synchronized JaxbMarshaller getDefaultMarshaller() {
            if (defaultMarshaller == null) {
                defaultMarshaller = Marshalling.getMarshallerSingleton();
            }
            return defaultMarshaller;
        }

        private static void insertInterceptor(final List<ClientInterceptor> meldingInterceptors,
                                              final InsertInterceptor insertInterceptor) {
            for (ClientInterceptor c : meldingInterceptors) {
                if (insertInterceptor.clazz.isAssignableFrom(c.getClass())) {
                    meldingInterceptors.add(meldingInterceptors.indexOf(c), insertInterceptor.interceptor);
                    return;
                }
            }
            throw new IllegalArgumentException("Could not find interceptor of class " + insertInterceptor.clazz);
        }

        private static WebServiceTemplate createTemplate(SaajSoapMessageFactory factory, JaxbMarshaller marshaller, EbmsAktoer remoteParty,
                                                         HttpComponentsMessageSender httpSender, ClientInterceptor[] interceptors) {

            final class SpringOxmMarshaller implements Marshaller, Unmarshaller {
                @Override
                public boolean supports(Class<?> clazz) {
                    return true;
                }

                @Override
                public Object unmarshal(Source source) {
                    return marshaller.unmarshal(source, Object.class);
                }

                @Override
                public void marshal(Object graph, Result result) {
                    marshaller.marshal(graph, result);
                }
            }
            SpringOxmMarshaller oxmMarshaller = new SpringOxmMarshaller();

            EbmsContextAwareWebServiceTemplate template = new EbmsContextAwareWebServiceTemplate(factory, remoteParty);
            template.setMarshaller(oxmMarshaller);
            template.setUnmarshaller(oxmMarshaller);
            template.setFaultMessageResolver(new MessageSenderFaultMessageResolver(marshaller));
            template.setMessageSender(httpSender);
            template.setInterceptors(interceptors);
            return template;
        }

        public Builder withMeldingInterceptorBefore(final Class<?> clazz, final ClientInterceptor interceptor) {
            interceptorBefore.add(new InsertInterceptor(clazz, interceptor));
            return this;
        }

        public Builder withValidateAfterInactivity(final Duration validateAfterInactivity) {
            this.validateAfterInactivity = validateAfterInactivity;
            return this;
        }

        public Builder withMarshaller(JaxbMarshaller marshaller) {
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

        public Builder withClientInterceptorWrapper(final ClientInterceptorWrapper clientInterceptorWrapper) {
            this.clientInterceptorWrapper = clientInterceptorWrapper;
            return this;
        }

        public Builder withMessageLogLevel(final LogLevel logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public Builder withSoapMessageFactorySupplier(MessageFactorySupplier messageFactorySupplier) {
            this.messageFactorySupplier = messageFactorySupplier;
            return this;
        }

        public MessageSender build() {
            if (marshaller == null) {
                marshaller = getDefaultMarshaller();
            }

            List<ClientInterceptor> meldingInterceptors = new ArrayList<ClientInterceptor>();
            meldingInterceptors.add(new EbmsClientInterceptor(marshaller, tekniskMottaker));
            meldingInterceptors.add(wsSecurityInterceptor);
            meldingInterceptors.add(new EbmsReferenceValidatorInterceptor(marshaller));
            meldingInterceptors.add(new TransactionLogClientInterceptor(marshaller));
            meldingInterceptors.add(new SoapLogClientInterceptor(logLevel));

            for (InsertInterceptor insertInterceptor : interceptorBefore) {
                insertInterceptor(meldingInterceptors, insertInterceptor);
            }

            ClientInterceptor[] clientInterceptors = new ClientInterceptor[meldingInterceptors.size()];
            for (int i = 0; i < meldingInterceptors.size(); i++) {
                clientInterceptors[i] = clientInterceptorWrapper.wrap(meldingInterceptors.get(i));
            }

            SaajSoapMessageFactory factory;
            try {
                MessageFactory messageFactory = MessageFactorySupplier.defaultIfNull(messageFactorySupplier).createMessageFactory();
                LOG.info("Using instance of {} as {}", messageFactory.getClass().getName(), MessageFactory.class.getSimpleName());
                factory = new SaajSoapMessageFactory(messageFactory);
                factory.setSoapVersion(SoapVersion.SOAP_12);
                factory.afterPropertiesSet();
            } catch (SOAPException e) {
                throw new RuntimeException(
                        "Unable to initialize SoapMessageFactory because " +
                        e.getClass().getSimpleName() + ": '" + e.getMessage() + "'", e);
            }

            DefaultMessageSender sender = new DefaultMessageSender(endpointUri, marshaller);

            sender.databehandler = databehandler;
            sender.tekniskMottaker = tekniskMottaker;
            sender.meldingTemplate = createTemplate(
                    factory, marshaller, tekniskMottaker, getHttpComponentsMessageSender(), clientInterceptors);
            sender.signer = new SdpMeldingSigner(keystoreInfo, marshaller);

            return sender;
        }


        private HttpComponentsMessageSender getHttpComponentsMessageSender() {
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
            connectionManager.setValidateAfterInactivity((int)validateAfterInactivity.toMillis());
            connectionManager.setMaxTotal(maxTotal);
            connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);

            RequestConfig.Builder requestConfigBuilder = RequestConfig.copy(RequestConfig.DEFAULT).setSocketTimeout(socketTimeout)
                    .setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectionRequestTimeout)
                    .setCookieSpec(CookieSpecs.IGNORE_COOKIES);

            if (httpHost != null) {
                requestConfigBuilder.setProxy(httpHost);
            }

            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

            SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(connectTimeout).build();
            httpClientBuilder.setDefaultSocketConfig(socketConfig);

            for (HttpRequestInterceptor httpRequestInterceptor : httpRequestInterceptors) {
                httpClientBuilder.addInterceptorFirst(httpRequestInterceptor);
            }

            for (HttpResponseInterceptor httpResponseInterceptor : httpResponseInterceptors) {
                httpClientBuilder.addInterceptorFirst(httpResponseInterceptor);
            }

            CloseableHttpClient client = httpClientBuilder.addInterceptorFirst(new RemoveContentLengthInterceptor())
                    .setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfigBuilder.build()).build();

            return new HttpComponentsMessageSender(client);
        }

    }

    public static class InsertInterceptor {

        private final Class<?> clazz;
        private final ClientInterceptor interceptor;


        public InsertInterceptor(final Class<?> clazz, final ClientInterceptor interceptor) {
            this.clazz = clazz;
            this.interceptor = interceptor;
        }
    }

    static class DefaultMessageSender implements MessageSender {

        private static final Logger LOG = LoggerFactory.getLogger(DefaultMessageSender.class);
        private final JaxbMarshaller marshaller;
        private final EbmsEndpointUriBuilder uri;
        private WebServiceTemplate meldingTemplate;
        private EbmsAktoer databehandler;
        private EbmsAktoer tekniskMottaker;
        private SdpMeldingSigner signer;


        protected DefaultMessageSender(EbmsEndpointUriBuilder uri, JaxbMarshaller marshaller) {
            if (marshaller == null) {
                throw new AssertionError("marshaller kan ikke være null");
            }

            this.uri = uri;
            this.marshaller = marshaller;
        }

        @Override
        public TransportKvittering send(final EbmsForsendelse forsendelse) {
            ForsendelseSender sender = new ForsendelseSender(signer, databehandler, tekniskMottaker, forsendelse, marshaller);

            String targetUri = uri.build(databehandler, forsendelse.getAvsender()).toString();
            LOG.info("Sender forsendelse til : {} ", targetUri);
            return meldingTemplate.sendAndReceive(targetUri, sender, new TransportKvitteringReceiver());
        }

        @Override
        public EbmsApplikasjonsKvittering hentKvittering(final EbmsPullRequest pullRequest,
                                                         final KanBekreftesSomBehandletKvittering tidligereKvitteringSomSkalBekreftes) {
            return meldingTemplate.sendAndReceive(uri.getBaseUri().toString(),
                    new PullRequestSender(pullRequest, marshaller, tidligereKvitteringSomSkalBekreftes),
                    new ApplikasjonsKvitteringReceiver(marshaller));
        }

        @Override
        public void bekreft(final KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering) {
            meldingTemplate.sendAndReceive(uri.getBaseUri().toString(),
                    new BekreftelseSender(kanBekreftesSomBehandletKvittering, marshaller), new EmptyReceiver());
        }

        @Override
        public void send(final EbmsApplikasjonsKvittering appKvittering) {
            meldingTemplate.sendAndReceive(uri.build(databehandler, appKvittering.avsender).toString(),
                    new KvitteringSender(signer, databehandler, tekniskMottaker, appKvittering, marshaller), new EmptyReceiver());
        }

        @Override
        public JaxbMarshaller getMarshaller() {
            return marshaller;
        }

        @Override
        public WebServiceTemplate getMeldingTemplate() {
            return meldingTemplate;
        }
    }

}
