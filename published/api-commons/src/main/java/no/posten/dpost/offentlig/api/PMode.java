package no.posten.dpost.offentlig.api;

import java.util.List;

import static java.util.Arrays.asList;

public interface PMode {

	// ROLES
	public static final String ROLE_MELDINGSFORMIDLER = "urn:sdp:meldingsformidler";
	public static final String ROLE_AVSENDER = "urn:sdp:avsender";
	public static final String ROLE_POSTKASSE = "urn:sdp:postkasseleverandør";


	// PARTY IDs
	public static final String PARTY_ID_TYPE = "urn:oasis:names:tc:ebcore:partyid-type:iso6523:9908";


	// COLLABORATION INFO
	public static final String AGREEMENT_REF = "http://begrep.difi.no/SikkerDigitalPost/Meldingsutveksling/FormidleDigitalPostForsendelse";
	public static final String SERVICE = "SDP";

	public static final String ACTION_FORMIDLE = "FormidleDigitalPost";
	public static final String ACTION_KVITTERING = "KvitteringsForespoersel";
	public static final List<String> VALID_ACTIONS = asList(ACTION_FORMIDLE, ACTION_KVITTERING);

}
