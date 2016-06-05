package no.digipost.api.representations;

import org.junit.Test;
import org.w3.xmldsig.DigestMethod;
import org.w3.xmldsig.Reference;
import org.w3.xmldsig.Transform;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class ReferanseTest {

	@Test
	public void testBuilder() throws Exception {
		Referanse referanse = Referanse.builder(ObjectMother.getReference()).build();

		assertThat(referanse.getMarshaled()).isNotNull();
	}

	@Test
	public void  testGetUnmarshaled() {
		Reference reference = ObjectMother.getReference();
		Referanse referanse = Referanse.builder(reference).build();

		assertThat(referanse.getUnmarshaled()).isEqualTo(reference);
	}
}