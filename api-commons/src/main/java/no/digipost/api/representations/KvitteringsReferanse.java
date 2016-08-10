package no.digipost.api.representations;

import no.digipost.api.xml.Marshalling;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.xml.transform.StringResult;
import org.w3.xmldsig.Reference;

import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

public class KvitteringsReferanse {

    private final String marshalled;
    private final Jaxb2Marshaller marshallerSingleton = Marshalling.getMarshallerSingleton();

    private KvitteringsReferanse(Reference reference) {
        StringResult marshalledReference = new StringResult();

        Jaxb2Marshaller marshallerSingleton = Marshalling.getMarshallerSingleton();
        Marshalling.marshal(marshallerSingleton, reference, marshalledReference);

        this.marshalled = marshalledReference.toString();
    }

    private KvitteringsReferanse(String marshalledReference) {
        marshalled = marshalledReference;
    }

    public static Builder builder(Reference reference) {
        return new Builder(reference);
    }

    public static Builder builder(String marshalledReference) {
        return new Builder(marshalledReference);
    }

    public String getMarshalled() {
        return marshalled;
    }

    public Reference getUnmarshalled() {
        return (Reference) marshallerSingleton.unmarshal(new StreamSource(new StringReader(marshalled)));
    }

    public static class Builder {
        private KvitteringsReferanse target;
        private boolean built = false;

        private Builder(Reference reference) {
            this.target = new KvitteringsReferanse(reference);
        }

        private Builder(String marshalledReference) {
            this.target = new KvitteringsReferanse(marshalledReference);
        }

        public KvitteringsReferanse build() {
            if (built) {
                throw new IllegalStateException("Kan ikke bygges flere ganger.");
            }
            built = true;
            return target;
        }

    }
}
