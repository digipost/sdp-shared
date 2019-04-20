package no.digipost.api.xml;

import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XpathUtil {

    @SuppressWarnings("unchecked")
    public static List<Node> getDOMXPath(final String expression, final Element element) {
        try {
            DOMXPath xpath = new DOMXPath(expression);
            Map<String, String> xpathNamespaces = getXpathNamespaces();
            for (String s : xpathNamespaces.keySet()) {
                xpath.addNamespace(s, xpathNamespaces.get(s));
            }
            return xpath.selectNodes(element);
        } catch (JaxenException e) {
            throw new RuntimeException(e);
        }

    }

    public static Map<String, String> getXpathNamespaces() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("env", Constants.SOAP_ENVELOPE12_NAMESPACE);
        map.put("eb", Constants.EBMS_NAMESPACE);
        map.put("ebbp", Constants.SIGNALS_NAMESPACE);
        map.put("ds", Constants.DIGSIG_NAMESPACE);
        map.put("wsse", Constants.WSSEC_NAMESPACE);
        map.put("wsu", Constants.WSSEC_UTILS_NAMESPACE);
        map.put("sbd", Constants.SBDH_NAMESPACE);
        map.put("sdp", Constants.SDP_NAMESPACE);
        return map;
    }
}
