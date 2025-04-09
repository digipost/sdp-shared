package no.digipost.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

public class TransaksjonsLogg {

    public static final String MF_LOGGER_TRANSLOG = "mf.logger.translog";
    private static final String N_A = "";
    private static final Logger LOG = LoggerFactory.getLogger(MF_LOGGER_TRANSLOG);

    public void log(final String endpoint, final String orgnr, final Retning retning, final Type type, final String mpc, final String conversationId, final String instanceIdentifier, final String messageId, final String refToMessageId, final String extra) {
        String ref = refToMessageId != null ? "ref:[" + refToMessageId + "]" : "";
        LOG.info("[{}] [{}] {} {} mpc:[{}] conversationId:[{}] instanceId:[{}] messageId:[{}] {} {}",
                endpoint, toLoggable(orgnr), retning, type, toLoggable(mpc), toLoggable(conversationId), toLoggable(instanceIdentifier), messageId, ref, extra);
    }

    private String toLoggable(final String s) {
        return defaultIfEmpty(s, N_A);
    }
    
    public enum Retning {
        INNKOMMENDE, UTGÅENDE
    }

    public enum Type {
        USERMESSAGE_SDP, USERMESSAGE_FLYTT, USERMESSAGE_FYSISK, PULLREQUEST, TRANSPORTKVITTERING, APPLIKASJONSKVITTERING, TOMKØ, EBMSFEIL, SOAPFAULT
    }
}
