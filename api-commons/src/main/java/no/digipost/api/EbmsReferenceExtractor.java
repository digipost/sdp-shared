package no.digipost.api;

import no.digipost.api.xml.Constants;
import no.digipost.api.xml.JaxbMarshaller;
import no.digipost.api.xml.Marshalling;
import no.digipost.api.xml.XpathUtil;
import no.digipost.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import no.digipost.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo;
import no.digipost.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import no.digipost.org.w3.xmldsig.Reference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.transform.dom.DOMSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EbmsReferenceExtractor {

    private final JaxbMarshaller jaxbMarshaller;

    public EbmsReferenceExtractor(JaxbMarshaller jaxb2Marshaller) {
        this.jaxbMarshaller = jaxb2Marshaller;
    }

    public Map<String, Reference> getReferences(final SoapMessage message) {
        List<String> hrefs = getHrefsToInclude(message);
        Map<String, Reference> references = new HashMap<String, Reference>();

        SoapHeaderElement wssec = message.getSoapHeader().examineHeaderElements(Constants.WSSEC_HEADER_QNAME).next();
        Element element = (Element) Marshalling.unmarshal(jaxbMarshaller, wssec, Object.class);

        Document doc = ((DOMSource) (message.getEnvelope().getSource())).getNode().getOwnerDocument();

        for (String href : hrefs) {

            List<Node> refs = XpathUtil.getDOMXPath("//ds:Reference[@URI='" + href + "']", element);
            if (refs.isEmpty()) {
                List<Node> parts = XpathUtil.getDOMXPath("//*[@Id='" + href.substring(1) + "']", message.getDocument().getDocumentElement());
                if (!parts.isEmpty()) {
                    String refId = parts.get(0).getAttributes().getNamedItemNS(Constants.WSSEC_UTILS_NAMESPACE, "Id").getNodeValue();
                    refs = XpathUtil.getDOMXPath("//ds:Reference[@URI='#" + refId + "']", element);
                }
            }
            if (!refs.isEmpty()) {
                Reference ref = Marshalling.unmarshal(jaxbMarshaller, refs.get(0), Reference.class);
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

    private List<String> getHrefsToInclude(final SoapMessage message) {
        Iterator<SoapHeaderElement> soapHeaderElementIterator = message.getSoapHeader().examineHeaderElements(Constants.MESSAGING_QNAME);
        if (!soapHeaderElementIterator.hasNext()) {
            throw new SecurityException("Missing ebMS Messaging header");
        }
        SoapHeaderElement incomingSoapHeaderElement = soapHeaderElementIterator.next();
        Messaging messaging = jaxbMarshaller.unmarshal(incomingSoapHeaderElement.getSource(), Messaging.class);
        if (messaging.getUserMessages().isEmpty()) {
            return new ArrayList<String>();
        }
        UserMessage userMessage = messaging.getUserMessages().get(0);
        List<String> hrefs = new ArrayList<String>();
        for (PartInfo part : userMessage.getPayloadInfo().getPartInfos()) {
            String href = part.getHref();
            if (href == null) {
                String attributeValue = message.getSoapBody().getAttributeValue(Constants.ID_ATTRIBUTE_QNAME);
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
