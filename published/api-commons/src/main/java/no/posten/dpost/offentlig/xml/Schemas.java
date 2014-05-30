package no.posten.dpost.offentlig.xml;

import org.springframework.core.io.ClassPathResource;

public class Schemas {

	public static final ClassPathResource SBDH_SCHEMA = new ClassPathResource("SBDH20040506-02/StandardBusinessDocumentHeader.xsd");
	public static final ClassPathResource SDP_SCHEMA = new ClassPathResource("sdp.xsd");
	public static final ClassPathResource EBMS_SCHEMA = new ClassPathResource("ebxml/ebms-header-3_0-200704.xsd");
	public static final ClassPathResource XMLDSIG_SCHEMA = new ClassPathResource("w3/xmldsig-core-schema.xsd");

}
