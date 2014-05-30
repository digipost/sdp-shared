package no.posten.dpost.offentlig.xml;

import javax.xml.namespace.QName;

public class Constants {

	public static final String RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";

	public static final String EBMS_NAMESPACE = "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/";
	public static final String SIGNALS_NAMESPACE = "http://docs.oasis-open.org/ebxml-bp/ebbp-signals-2.0";

	public static final String SOAP_ENVELOPE12_NAMESPACE = "http://www.w3.org/2003/05/soap-envelope";
	public static final String DIGSIG_NAMESPACE = "http://www.w3.org/2000/09/xmldsig#";
	public static final String WSSEC_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
	public static final String WSSEC_UTILS_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";

	public static final String SBDH_NAMESPACE = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader";


	public static final QName MESSAGING_QNAME = new QName(EBMS_NAMESPACE, "Messaging", "eb");
	public static final QName SIGNAL_MESSAGE_QNAME = new QName(EBMS_NAMESPACE, "SignalMessage");
	public static final QName USER_MESSAGE_QNAME = new QName(EBMS_NAMESPACE, "UserMessage");
	public static final QName WSSEC_HEADER_QNAME = new QName(WSSEC_NAMESPACE, "Security");
	public static final QName ID_ATTRIBUTE_QNAME = new QName(WSSEC_UTILS_NAMESPACE, "Id");
	public static final QName TIMESTAMP = new QName(WSSEC_UTILS_NAMESPACE, "Timestamp");
}
