package no.digipost.api.xml;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.xml.transform.stream.StreamSource;

import java.io.InputStream;

@Disabled
public class UnmarshallingTest {

    @Test
    public void should_unmashall_schema_valid_xml() {
        InputStream xml = getClass().getResourceAsStream("/difi2.xml");
        Marshalling.getMarshallerSingleton().unmarshal(new StreamSource(xml));
    }

}
