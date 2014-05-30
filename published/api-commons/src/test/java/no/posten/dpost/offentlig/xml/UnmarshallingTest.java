package no.posten.dpost.offentlig.xml;

import org.junit.Ignore;
import org.junit.Test;

import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

@Ignore
public class UnmarshallingTest {

	@Test
	public void test() {
		InputStream xml = getClass().getResourceAsStream("/difi2.xml");
		Marshalling.createUnManaged().unmarshal(new StreamSource(xml));
	}


}
