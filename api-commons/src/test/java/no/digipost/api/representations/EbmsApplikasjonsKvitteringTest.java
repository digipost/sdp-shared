package no.digipost.api.representations;

import no.digipost.api.PMode;
import org.apache.wss4j.dom.action.Action;
import org.junit.Assert;
import org.junit.Test;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.w3.xmldsig.Reference;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.fest.assertions.api.Assertions.assertThat;


public class EbmsApplikasjonsKvitteringTest {

	@Test
	public void testGetMeldingsId() throws Exception {
		EbmsApplikasjonsKvittering ebmsApplikasjonskvittering = getEbmsApplikasjonskvittering();

		assertThat(ebmsApplikasjonskvittering.getMeldingsId()).isEqualTo(ebmsApplikasjonskvittering.messageId);

	}

	@Test
	public void testGetReferanse() throws Exception {
		EbmsApplikasjonsKvittering ebmsApplikasjonskvittering = getEbmsApplikasjonskvittering();

		assertThat(ebmsApplikasjonskvittering.getReferanse()).isNotNull();
	}

	private EbmsApplikasjonsKvittering getEbmsApplikasjonskvittering() {
		List<Reference> references = new ArrayList<Reference>();
		references.add(ObjectMother.getReference());

		return EbmsApplikasjonsKvittering.create(EbmsAktoer.avsender("999999999"), EbmsAktoer.avsender("88888888"), null)
				.withReferences(references)
				.build();
	}

}