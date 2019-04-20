package no.digipost.api.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toList;

public class XpathUtil {

    public static List<Node> getDOMXPath(final String expression, final Element element) {
        try {
            XPathExpression xpathExpression = xpathFactory.newXPath().compile(expression);
            NodeList nodes = (NodeList) xpathExpression.evaluate(element, XPathConstants.NODESET);
            int nodesFound = nodes.getLength();
            return IntStream.range(0, nodesFound).mapToObj(nodes::item).collect(toList());
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to evaluate xpath '" + expression + "' on node " + element + ", " +
                    "because " + e.getClass().getSimpleName() + ": '" + e.getMessage() + "'", e);
        }
    }

    private static final class XPathSynchronizedFactory {

        private final XPathFactory xpathFactory = XPathFactory.newInstance();

        synchronized XPath newXPath() {
            XPath xpath = xpathFactory.newXPath();
            xpath.setNamespaceContext(ebmsNamespaceContext);
            return xpath;
        }
    }

    private static final XPathSynchronizedFactory xpathFactory = new XPathSynchronizedFactory();


    private static final NamespaceContext ebmsNamespaceContext = new NamespaceContext() {

        @Override
        public String getNamespaceURI(String prefix) {
            return XPATH_NAMESPACES.get(prefix);
        }

        @Override
        public Iterator<String> getPrefixes(String namespaceURI) {
            throw new UnsupportedOperationException("getPrefixes() method is not supported");
        }

        @Override
        public String getPrefix(String namespaceURI) {
            throw new UnsupportedOperationException("getPrefix() method is not supported");
        }

    };

    private static final Map<String, String> XPATH_NAMESPACES; static {
        Map<String, String> map = new HashMap<String, String>();
        map.put("env", Constants.SOAP_ENVELOPE12_NAMESPACE);
        map.put("eb", Constants.EBMS_NAMESPACE);
        map.put("ebbp", Constants.SIGNALS_NAMESPACE);
        map.put("ds", Constants.DIGSIG_NAMESPACE);
        map.put("wsse", Constants.WSSEC_NAMESPACE);
        map.put("wsu", Constants.WSSEC_UTILS_NAMESPACE);
        map.put("sbd", Constants.SBDH_NAMESPACE);
        map.put("sdp", Constants.SDP_NAMESPACE);
        XPATH_NAMESPACES = unmodifiableMap(map);
    }

    public static Map<String, String> getXpathNamespaces() {
        return XPATH_NAMESPACES;
    }
}
