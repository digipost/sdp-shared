package no.posten.dpost.offentlig.xml;

import org.joda.time.DateTime;
import org.junit.Test;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.BusinessScope;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.DocumentIdentification;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.Partner;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.PartnerIdentification;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.Scope;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.stream.StreamResult;

import java.io.StringWriter;

public class MarshallerTest {

	@Test
	public void testMarshallingAvSBDH() {
		StringWriter outWriter = new StringWriter();
		StreamResult result = new StreamResult(outWriter);

		Jaxb2Marshaller marshaller = Marshalling.createUnManaged();

		Partner partner = new Partner().withIdentifier(new PartnerIdentification("parner", "authority"));
		StandardBusinessDocument sbd = new StandardBusinessDocument()
				.withStandardBusinessDocumentHeader(
						new StandardBusinessDocumentHeader()
								.withHeaderVersion("1.0")
								.withSenders(partner)
								.withReceivers(partner)
								.withDocumentIdentification(
										new DocumentIdentification()
												.withStandard("standard")
												.withTypeVersion("typeVersion")
												.withInstanceIdentifier("instanceIdentifier")
												.withType("type")
												.withCreationDateAndTime(new DateTime())
								)
								.withBusinessScope(new BusinessScope()
										.withScopes(new Scope()
														.withType("type")
														.withInstanceIdentifier("instanceIdentifier").withIdentifier("identifier")
														.withIdentifier("identifier")
										))
				)
				.withAny(new Messaging());

		marshaller.marshal(sbd, result);
		StringBuffer sb = outWriter.getBuffer();
		System.out.println(sb);
	}


	@XmlRootElement
	private class Whatever {

	}


	//create a StringWriter for the output
}
