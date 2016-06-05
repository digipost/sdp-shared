package no.digipost.api.representations;

import no.digipost.api.xml.Marshalling;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.xml.transform.StringResult;
import org.w3.xmldsig.Reference;

import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

public class Referanse {

	private final String marshaled;
	private final Jaxb2Marshaller marshallerSingleton = Marshalling.getMarshallerSingleton();

	public String getMarshaled() {
		return marshaled;
	}

	public Reference getUnmarshaled() {
		return (Reference) marshallerSingleton.unmarshal(new StreamSource(new StringReader(marshaled)));
	}

	private Referanse(Reference reference) {
		StringResult marshaledReference = new StringResult();

		Jaxb2Marshaller marshallerSingleton = Marshalling.getMarshallerSingleton();
		Marshalling.marshal(marshallerSingleton, reference, marshaledReference);

		this.marshaled = marshaledReference.toString();
	}

	public static Builder builder(Reference reference) {
		return new Builder(reference);
	}

	public static class Builder {
		private Referanse target;
		private boolean built = false;

		private Builder(Reference reference) {
			this.target = new Referanse(reference);
		}

		public Referanse build() {
			if (built) {
				throw new IllegalStateException("Can't build twice");
			}
			built = true;
			return target;
		}

	}
}
