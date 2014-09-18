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
package no.digipost.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.dom.DOMSource;

import no.digipost.api.xml.Constants;
import no.digipost.api.xml.Marshalling;
import no.digipost.api.xml.XpathUtil;

import org.apache.commons.lang3.StringUtils;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.w3.xmldsig.Reference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class EbmsReferenceExtractor {

	private final Jaxb2Marshaller jaxb2Marshaller;

	public EbmsReferenceExtractor(final Jaxb2Marshaller jaxb2Marshaller) {
		this.jaxb2Marshaller = jaxb2Marshaller;
	}

	public Map<String, Reference> getReferences(final SoapMessage request) {
		List<String> hrefs = getHrefsToInclude(request);
		Map<String, Reference> references = new HashMap<String, Reference>();

		SoapHeaderElement wssec = request.getSoapHeader().examineHeaderElements(Constants.WSSEC_HEADER_QNAME).next();
		Element element = (Element) Marshalling.unmarshal(jaxb2Marshaller, wssec, Object.class);

		Document doc = ((DOMSource)(request.getEnvelope().getSource())).getNode().getOwnerDocument();

		for (String href : hrefs) {

			List<Node> refs = XpathUtil.getDOMXPath("//ds:Reference[@URI='" + href + "']", element);
			if (refs.size() == 0) {
				List<Node> parts = XpathUtil.getDOMXPath("//*[@Id='" + href.substring(1) + "']", request.getDocument().getDocumentElement());
				if (parts.size() > 0) {
					String refId = parts.get(0).getAttributes().getNamedItemNS(Constants.WSSEC_UTILS_NAMESPACE, "Id").getNodeValue();
					refs = XpathUtil.getDOMXPath("//ds:Reference[@URI='#" + refId + "']", element);
				}
			}
			if (refs.size() > 0) {
				Reference ref = Marshalling.unmarshal(jaxb2Marshaller, refs.get(0), Reference.class);
				String name = "attachment";
				Element elm = doc.getElementById(href.replace("#", ""));
				if (elm != null) {
					name = elm.getLocalName().toLowerCase();
				}
				references.put(name, ref);
			} else {
				throw new SecurityException("Missing reference for " + href);
			}
		}
		return references;
	}

	private List<String> getHrefsToInclude(final SoapMessage request) {
		Iterator<SoapHeaderElement> soapHeaderElementIterator = request.getSoapHeader().examineHeaderElements(Constants.MESSAGING_QNAME);
		if (!soapHeaderElementIterator.hasNext()) {
			throw new SecurityException("Missing ebMS Messaging header");
		}
		SoapHeaderElement incomingSoapHeaderElement = soapHeaderElementIterator.next();
		Messaging messaging = (Messaging) jaxb2Marshaller.unmarshal(incomingSoapHeaderElement.getSource());
		if (messaging.getUserMessages().size() == 0) {
			return new ArrayList<String>();
		}
		UserMessage userMessage = messaging.getUserMessages().get(0);
		List<String> hrefs = new ArrayList<String>();
		for (PartInfo part : userMessage.getPayloadInfo().getPartInfos()) {
			String href = part.getHref();
			if (href == null) {
				String attributeValue = request.getSoapBody().getAttributeValue(Constants.ID_ATTRIBUTE_QNAME);
				if (StringUtils.isBlank(attributeValue)) {
					throw new SecurityException("Missing reference for partInfo soapBody");
				}
				href = "#" + attributeValue;
			}
			hrefs.add(href);
		}
		return hrefs;
	}

}
