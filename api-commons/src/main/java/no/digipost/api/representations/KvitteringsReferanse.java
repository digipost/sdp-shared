/**
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.digipost.api.representations;

import no.digipost.api.xml.Marshalling;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.xml.transform.StringResult;
import org.w3.xmldsig.Reference;

import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

public class KvitteringsReferanse {

	private final String marshaled;
	private final Jaxb2Marshaller marshallerSingleton = Marshalling.getMarshallerSingleton();

	public String getMarshaled() {
		return marshaled;
	}

	public Reference getUnmarshaled() {
		return (Reference) marshallerSingleton.unmarshal(new StreamSource(new StringReader(marshaled)));
	}

	private KvitteringsReferanse(Reference reference) {
		StringResult marshaledReference = new StringResult();

		Jaxb2Marshaller marshallerSingleton = Marshalling.getMarshallerSingleton();
		Marshalling.marshal(marshallerSingleton, reference, marshaledReference);

		this.marshaled = marshaledReference.toString();
	}

	private KvitteringsReferanse(String marshaledReference) {
		marshaled = marshaledReference;
	}

	public static Builder builder(Reference reference) {
		return new Builder(reference);
	}

	public static Builder builder(String marshaledReference) {
		return new Builder(marshaledReference);
	}


	public static class Builder {
		private KvitteringsReferanse target;
		private boolean built = false;

		private Builder(Reference reference) {
			this.target = new KvitteringsReferanse(reference);
		}

		private Builder(String marshaledReference){
			this.target = new KvitteringsReferanse(marshaledReference);
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
