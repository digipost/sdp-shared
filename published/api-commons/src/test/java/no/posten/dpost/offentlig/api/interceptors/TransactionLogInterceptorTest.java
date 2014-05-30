package no.posten.dpost.offentlig.api.interceptors;

import no.posten.dpost.offentlig.api.PMode;
import no.posten.dpost.offentlig.api.config.TransaksjonsLogg;
import no.posten.dpost.offentlig.api.config.TransaksjonsLogg.Type;
import no.posten.dpost.offentlig.api.representations.EbmsContext;
import no.posten.dpost.offentlig.api.representations.EbmsOutgoingMessage.Prioritet;
import no.posten.dpost.offentlig.api.representations.Mpc;
import no.posten.dpost.offentlig.api.representations.Organisasjonsnummer;
import no.posten.dpost.offentlig.api.representations.SimpleStandardBusinessDocument;
import no.posten.dpost.offentlig.xml.Constants;
import no.posten.dpost.offentlig.xml.Marshalling;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PullRequest;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.context.DefaultMessageContext;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.MethodEndpoint;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import java.util.UUID;

import static no.posten.dpost.offentlig.api.exceptions.ebms.Severity.failure;
import static no.posten.dpost.offentlig.api.exceptions.ebms.standard.processing.EmptyMessagePartitionChannelException.EMPTY_MPC_EBMS_CODE;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class TransactionLogInterceptorTest {

	// Ref Ebms spec 4.3
	public static final String DEFAULT_CONVERSATION_ID = "1";
	@Mock
	private TransaksjonsLogg loggMock;

	private final TransactionLogInterceptor interceptorInner = TransactionLogInterceptor.createServerInterceptor(Marshalling.createUnManaged(), TransactionLogInterceptor.Phase.INSIDE_WSSEC);
	private final TransactionLogInterceptor interceptorOuter = TransactionLogInterceptor.createServerInterceptor(Marshalling.createUnManaged(), TransactionLogInterceptor.Phase.OUTSIDE_WSSEC);
	private final TransactionLogInterceptor interceptorClient = TransactionLogInterceptor.createClientInterceptor(Marshalling.createUnManaged());

	private MessageContext messageContext;

	private EbmsContext ebmsContext;

	private final String instanceIdentifier = "IID-" + UUID.randomUUID().toString();
	private final String conversationId = "KID-" + UUID.randomUUID().toString();
	@Mock
	SimpleStandardBusinessDocument doc;

	private SoapHeaderElement requestMessagingHeader;

	private SoapHeaderElement responseMessagingHeader;

	private Jaxb2Marshaller marshaller;

	private final String messageIdInn = "INN-" + UUID.randomUUID().toString();
	private final String messageIdUt = "UT-" + UUID.randomUUID().toString();

	private final Mpc mpc = new Mpc(Prioritet.NORMAL, null);
	private final String endpoint = "mf-api";

	MethodEndpoint mpEndpoint = new MethodEndpoint(endpoint, endpoint.getClass().getMethods()[0]);
	private final Organisasjonsnummer sender = new Organisasjonsnummer("123456789");
	private final Organisasjonsnummer receiver = new Organisasjonsnummer("123454321");
	private final Organisasjonsnummer ebmsSender = new Organisasjonsnummer("987654321");

	private final String userMessageLogValue = ebmsSender + " " + sender + " " + receiver;
	private final String signalMessageLogValue = ebmsSender.toString();

	@Before
	public void setup() throws Exception {
		initMocks(this);
		marshaller = Marshalling.createUnManaged();
		interceptorInner.setTransaksjonslogg(loggMock);
		interceptorOuter.setTransaksjonslogg(loggMock);
		interceptorClient.setTransaksjonslogg(loggMock);
		MessageFactory factory =
				MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
		messageContext = new DefaultMessageContext(new SaajSoapMessageFactory(factory));
		ebmsContext = EbmsContext.from(messageContext);
		ebmsContext.remoteParty = ebmsSender;
		when(doc.getInstanceIdentifier()).thenReturn(instanceIdentifier);
		when(doc.getConversationId()).thenReturn(conversationId);
		when(doc.getReceiver()).thenReturn(receiver);
		when(doc.getSender()).thenReturn(sender);
		requestMessagingHeader = ((SoapMessage) messageContext.getRequest()).getSoapHeader().addHeaderElement(Constants.MESSAGING_QNAME);
		responseMessagingHeader = ((SoapMessage) messageContext.getResponse()).getSoapHeader().addHeaderElement(Constants.MESSAGING_QNAME);
	}

	@Test
	public void skal_logge_innkommende_instanceid_og_conversationId_for_innkommmende_usermessage_og_utgående_kvittering() throws Exception {
		ebmsContext.sbd = doc;

		addUserMessage(requestMessagingHeader, messageIdInn, null);
		addTransportKvittering(responseMessagingHeader, messageIdUt, messageIdInn);

		interceptorOuter.handleRequest(messageContext, mpEndpoint);
		interceptorInner.handleRequest(messageContext, mpEndpoint);
		interceptorInner.handleResponse(messageContext, mpEndpoint);
		interceptorOuter.handleResponse(messageContext, mpEndpoint);

		verify(loggMock).innkommende(endpoint, userMessageLogValue, Type.USERMESSAGE, mpc.toString(), conversationId, instanceIdentifier, messageIdInn, null);
		verify(loggMock).utgående(endpoint, userMessageLogValue, Type.TRANSPORTKVITTERING, mpc.toString(), conversationId, instanceIdentifier, messageIdUt, messageIdInn);
	}

	@Test
	public void skal_logge_riktig_utgående_mottak_og_svar() throws Exception {
		ebmsContext.sbd = doc;

		addUserMessage(requestMessagingHeader, messageIdInn, null);
		addTransportKvittering(responseMessagingHeader, messageIdUt, messageIdInn);

		interceptorClient.handleRequest(messageContext);
		interceptorClient.handleResponse(messageContext);

		verify(loggMock).utgående("sender", userMessageLogValue, Type.USERMESSAGE, mpc.toString(), conversationId, instanceIdentifier, messageIdInn, null);
		verify(loggMock).innkommende("sender", userMessageLogValue, Type.TRANSPORTKVITTERING, mpc.toString(), conversationId, instanceIdentifier, messageIdUt, messageIdInn);
	}

	@Test
	public void skal_logge_enkel_transport_kvittering() throws Exception {
		ebmsContext.sbd = null;

		addTransportKvittering(requestMessagingHeader, messageIdUt, messageIdInn);

		interceptorOuter.handleRequest(messageContext, mpEndpoint);
		interceptorInner.handleRequest(messageContext, mpEndpoint);

		verify(loggMock).innkommende(endpoint, signalMessageLogValue, Type.TRANSPORTKVITTERING, null, null, null, messageIdUt, messageIdInn);
	}

	@Test
	public void skal_logge_pull_request_med_svar() throws Exception {

		addPullRequest(requestMessagingHeader, messageIdInn);
		addUserMessage(responseMessagingHeader, messageIdUt, messageIdInn);

		ebmsContext.sbd = null;
		interceptorOuter.handleRequest(messageContext, mpEndpoint);
		interceptorInner.handleRequest(messageContext, mpEndpoint);
		ebmsContext.sbd = doc;
		interceptorInner.handleResponse(messageContext, mpEndpoint);
		interceptorOuter.handleResponse(messageContext, mpEndpoint);

		verify(loggMock).innkommende(endpoint, signalMessageLogValue, Type.PULLREQUEST, mpc.toString(), null, null, messageIdInn, null);
		verify(loggMock).utgående(endpoint, userMessageLogValue, Type.USERMESSAGE, mpc.toString(), conversationId, instanceIdentifier, messageIdUt, messageIdInn);

	}

	@Test
	public void skal_logge_pull_request_med_svar_utgående() throws Exception {

		addPullRequest(requestMessagingHeader, messageIdInn);
		addUserMessage(responseMessagingHeader, messageIdUt, messageIdInn);

		ebmsContext.sbd = null;
		interceptorClient.handleRequest(messageContext);
		ebmsContext.sbd = doc;
		interceptorClient.handleResponse(messageContext);

		verify(loggMock).utgående("sender", signalMessageLogValue, Type.PULLREQUEST, mpc.toString(), null, null, messageIdInn, null);
		verify(loggMock).innkommende("sender", userMessageLogValue, Type.USERMESSAGE, mpc.toString(), conversationId, instanceIdentifier, messageIdUt, messageIdInn);

	}

	@Test
	public void skal_logge_pull_request_med_tom_kø() throws Exception {

		addPullRequest(requestMessagingHeader, messageIdInn);
		addTomKøMelding(responseMessagingHeader, messageIdUt, messageIdInn);

		ebmsContext.sbd = null;
		interceptorOuter.handleRequest(messageContext, mpEndpoint);
		interceptorInner.handleRequest(messageContext, mpEndpoint);
		interceptorInner.handleResponse(messageContext, mpEndpoint);
		interceptorOuter.handleResponse(messageContext, mpEndpoint);

		verify(loggMock).innkommende(endpoint, signalMessageLogValue, Type.PULLREQUEST, mpc.toString(), null, null, messageIdInn, null);
		verify(loggMock).utgående(endpoint, signalMessageLogValue, Type.TOMKØ, mpc.toString(), null, null, messageIdUt, messageIdInn);

	}

	private void addTomKøMelding(final SoapHeaderElement header, final String messageId, final String refToMessageId) {
		Error error = new Error()
				.withErrorCode(EMPTY_MPC_EBMS_CODE)
				.withShortDescription("EmptyMessagePartitionChannel")
				.withSeverity(failure.toString());
		SignalMessage signal = new SignalMessage()
				.withMessageInfo(new MessageInfo().withMessageId(messageId).withRefToMessageId(refToMessageId).withTimestamp(new DateTime()))
				.withErrors(error);
		Marshalling.marshal(marshaller, header, Constants.SIGNAL_MESSAGE_QNAME, signal);
	}

	private void addUserMessage(final SoapHeaderElement header, final String messageId, final String refToMessageId) {
		UserMessage userMessage = new UserMessage()
				.withMessageInfo(new MessageInfo()
						.withMessageId(messageId)
						.withRefToMessageId(refToMessageId)
						.withTimestamp(new DateTime()))
				.withPartyInfo(new PartyInfo()
						.withFrom(new From().withPartyIds(new PartyId("123456789", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:9908")).withRole("rolle1"))
						.withTo(new To().withPartyIds(new PartyId("234567890", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:9908")).withRole("rolle2")))
				.withCollaborationInfo(new CollaborationInfo()
						.withConversationId(DEFAULT_CONVERSATION_ID)
						.withAction(PMode.ACTION_FORMIDLE)
						.withService(new Service().withValue(PMode.SERVICE))
						.withAgreementRef(new AgreementRef()
								.withValue("http://begrep.difi.no/SikkerDigitalPost/Meldingsutveksling/FormidleDigitalPostForsendelse")))
				.withMpc(mpc.toString());
		Marshalling.marshal(marshaller, header, Constants.USER_MESSAGE_QNAME, userMessage);
	}

	private void addTransportKvittering(final SoapHeaderElement header, final String messageId, final String refToMessageId) {
		SignalMessage signal = new SignalMessage()
				.withMessageInfo(new MessageInfo().withMessageId(messageId).withRefToMessageId(refToMessageId).withTimestamp(new DateTime()));
		Marshalling.marshal(marshaller, header, Constants.SIGNAL_MESSAGE_QNAME, signal);
	}

	private void addPullRequest(final SoapHeaderElement header, final String messageId) {
		SignalMessage signal = new SignalMessage()
				.withMessageInfo(new MessageInfo().withMessageId(messageId).withTimestamp(new DateTime()))
				.withPullRequest(new PullRequest().withMpc(mpc.toString()));

		Marshalling.marshal(marshaller, header, Constants.SIGNAL_MESSAGE_QNAME, signal);
	}

}
