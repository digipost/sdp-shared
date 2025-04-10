package no.digipost.api.xml;

import no.difi.begrep.sdp.schema_v10.SDPDigitalPost;
import org.junit.jupiter.api.Test;

import javax.xml.transform.stream.StreamSource;

import java.io.InputStream;

public class UnmarshallingTest {

    @Test
    public void should_unmarshall_schema_valid_xml() {
        InputStream xml = getClass().getResourceAsStream("/difi2.xml");
        Marshalling.getMarshallerSingleton().unmarshal(new StreamSource(xml), SDPDigitalPost.class);
    }

}
