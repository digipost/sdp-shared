package no.digipost.api.xml;

import org.junit.Ignore;
import org.junit.Test;

import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

@Ignore
public class UnmarshallingTest {

    @Test
    public void should_unmashall_schema_valid_xml() {
        InputStream xml = getClass().getResourceAsStream("/difi2.xml");
        Marshalling.getMarshallerSingleton().unmarshal(new StreamSource(xml));
    }

}
