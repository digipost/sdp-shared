package no.digipost.api.interceptors;

import no.digipost.api.PMode;
import no.digipost.api.config.TransaksjonsLogg;
import no.digipost.api.config.TransaksjonsLogg.Retning;
import no.digipost.api.config.TransaksjonsLogg.Type;
import no.digipost.api.representations.EbmsContext;
import no.digipost.api.representations.Organisasjonsnummer;
import no.digipost.api.representations.SimpleStandardBusinessDocument;
import no.digipost.api.representations.SimpleUserMessage;
import no.digipost.api.xml.Constants;
import no.digipost.api.xml.JaxbMarshaller;
import no.digipost.api.xml.Marshalling;
import no.digipost.api.xml.MessagingMarshalling;
import no.digipost.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;
import no.digipost.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo;
import no.digipost.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import no.digipost.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage;
import no.digipost.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import no.digipost.org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapMessage;

import static no.digipost.api.config.TransaksjonsLogg.EMPTY_MESSAGE_PARTITION_CHANNEL_EBMS_ERROR_CODE;
import static no.digipost.api.config.TransaksjonsLogg.Type.APPLIKASJONSKVITTERING;
import static no.digipost.api.config.TransaksjonsLogg.Type.EBMSFEIL;
import static no.digipost.api.config.TransaksjonsLogg.Type.PULLREQUEST;
import static no.digipost.api.config.TransaksjonsLogg.Type.TRANSPORTKVITTERING;
import static no.digipost.api.config.TransaksjonsLogg.Type.USERMESSAGE_FLYTT;
import static no.digipost.api.config.TransaksjonsLogg.Type.USERMESSAGE_FYSISK;
import static no.digipost.api.config.TransaksjonsLogg.Type.USERMESSAGE_SDP;

public class TransactionLog {

    private final JaxbMarshaller jaxb2Marshaller;
    private TransaksjonsLogg logg = new TransaksjonsLogg();


    public TransactionLog(final JaxbMarshaller jaxb2Marshaller) {
        this.jaxb2Marshaller = jaxb2Marshaller;
    }

    public void setTransaksjonslogg(final TransaksjonsLogg logg) {
        this.logg = logg;
    }

    public void handleIncoming(final EbmsContext context, final SoapMessage soapMessage, final String endpoint) {
        decorate(context, soapMessage);

        Messaging msg = MessagingMarshalling.getMessaging(jaxb2Marshaller, soapMessage);
        for (UserMessage userMessage : msg.getUserMessages()) {
            SimpleUserMessage u = new SimpleUserMessage(userMessage);
            context.mpcMap.put(u.getMessageId(), u.getMpc());
            logg.innkommende(endpoint, getOrgNr(context), getType(u), u.getMpc(), getConversationId(context), getInstanceIdentifier(context), u.getMessageId(), u.getRefToMessageId());
        }
        for (SignalMessage signalMessage : msg.getSignalMessages()) {
            MessageInfo messageInfo = signalMessage.getMessageInfo();
            context.mpcMap.put(messageInfo.getMessageId(), getMpcFromSignal(context, signalMessage));
            logg.innkommende(endpoint, getOrgNr(context), getType(signalMessage), getMpcFromSignal(context, signalMessage), getConversationId(context), getInstanceIdentifier(context), messageInfo.getMessageId(), messageInfo.getRefToMessageId());
        }
    }

    public void handleOutgoing(final EbmsContext context, final SoapMessage soapMessage, final String endpoint) {
        decorate(context, soapMessage);

        Messaging msg = MessagingMarshalling.getMessaging(jaxb2Marshaller, soapMessage);
        for (UserMessage userMessage : msg.getUserMessages()) {
            SimpleUserMessage u = new SimpleUserMessage(userMessage);
            context.mpcMap.put(u.getMessageId(), u.getMpc());
            logg.utgående(endpoint, getOrgNr(context), getType(u), u.getMpc(), getConversationId(context), getInstanceIdentifier(context), u.getMessageId(), u.getRefToMessageId());
        }
        for (SignalMessage signalMessage : msg.getSignalMessages()) {
            MessageInfo messageInfo = signalMessage.getMessageInfo();
            context.mpcMap.put(messageInfo.getMessageId(), getMpcFromSignal(context, signalMessage));
            logg.utgående(endpoint, getOrgNr(context), getType(signalMessage), getMpcFromSignal(context, signalMessage), getConversationId(context), getInstanceIdentifier(context), messageInfo.getMessageId(), messageInfo.getRefToMessageId());
        }
    }

    public void handleFault(final Retning retning, final EbmsContext context, final SoapMessage soapMessage, final String endpoint) {
        SoapBody soapBody = soapMessage.getSoapBody();
        SoapFault soapFault = soapBody.getFault();
        logg.soapfault(endpoint, getOrgNr(context), soapFault);

        if (soapMessage.getSoapHeader().examineHeaderElements(Constants.MESSAGING_QNAME).hasNext()) {
            Messaging messaging = MessagingMarshalling.getMessaging(jaxb2Marshaller, soapMessage);
            for (SignalMessage signalMessage : messaging.getSignalMessages()) {
                for (Error error : signalMessage.getErrors()) {
                    logg.ebmserror(endpoint, getOrgNr(context), retning, error, signalMessage.getMessageInfo(), getMpcFromSignal(context, signalMessage), getConversationId(context), getInstanceIdentifier(context));
                }
            }
        }
    }

    // Private

    private String getMpcFromSignal(final EbmsContext context, final SignalMessage signalMessage) {
        String mpc = null;
        if (signalMessage.getPullRequest() != null) {
            mpc = signalMessage.getPullRequest().getMpc();
        }
        String refToMessageId = signalMessage.getMessageInfo().getRefToMessageId();
        if (refToMessageId != null && context.mpcMap.containsKey(refToMessageId)) {
            return context.mpcMap.get(refToMessageId);
        }
        return mpc;
    }

    private Type getType(final SimpleUserMessage u) {
        if (u.getAction().equals(PMode.Action.FORMIDLE_DIGITAL.value)) {
            return USERMESSAGE_SDP;
        } else if (u.getAction().equals(PMode.Action.FLYTT.value)) {
            return USERMESSAGE_FLYTT;
        } else if (u.getAction().equals(PMode.Action.FORMIDLE_FYSISK.value)) {
            return USERMESSAGE_FYSISK;
        } else {
            return APPLIKASJONSKVITTERING;
        }
    }

    private String getInstanceIdentifier(final EbmsContext context) {
        if (context.sbd != null) {
            return context.sbd.getInstanceIdentifier();
        }
        return null;
    }

    private String getConversationId(final EbmsContext context) {
        if (context.sbd != null) {
            return context.sbd.getConversationUuid();
        }
        return null;
    }

    private String getOrgNr(final EbmsContext context) {
        StringBuilder builder = new StringBuilder();
        builder.append(context.remoteParty.map(Organisasjonsnummer::getOrganisasjonsnummer).orElse("-"));

        if (context.sbd != null) {
            builder.append(" ");
            builder.append(context.sbd.getSender());
            builder.append(" ");
            builder.append(context.sbd.getReceiver());
        }
        return builder.toString();
    }

    private Type getType(final SignalMessage signalMessage) {
        if (signalMessage.getPullRequest() != null) {
            return PULLREQUEST;
        } else if (!signalMessage.getErrors().isEmpty()) {
            if (signalMessage.getErrors().size() == 1 && EMPTY_MESSAGE_PARTITION_CHANNEL_EBMS_ERROR_CODE.equals(signalMessage.getErrors().get(0).getErrorCode())) {
                return Type.TOMKØ;
            }
            return EBMSFEIL;
        } else {
            return TRANSPORTKVITTERING;
        }
    }

    private void decorate(final EbmsContext context, final SoapMessage soapMessage) {
        if (context.sbd == null && soapMessage.getSoapBody().getPayloadSource() != null) {
            StandardBusinessDocument sbd = Marshalling.unmarshal(jaxb2Marshaller, soapMessage.getSoapBody(), StandardBusinessDocument.class);
            if (sbd != null && sbd.getStandardBusinessDocumentHeader() != null) {
                context.sbd = new SimpleStandardBusinessDocument(sbd);
            }
        }
    }


}
