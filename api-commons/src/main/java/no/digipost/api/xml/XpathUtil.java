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
package no.digipost.api.xml;

import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.springframework.xml.xpath.Jaxp13XPathTemplate;
import org.springframework.xml.xpath.NodeMapper;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.transform.Source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XpathUtil {

	public static List<Node> evaluateXpath(final String expression, final Source source) {
		Jaxp13XPathTemplate template = new Jaxp13XPathTemplate();
		template.setNamespaces(getXpathNamespaces());
		return template.evaluate(expression, source, returnNode);
	}
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
		return map;
	}

	private static final NodeMapper<Node> returnNode = new NodeMapper<Node>() {
		@Override
		public Node mapNode(final Node node, final int nodeNum) throws DOMException {
			return node;
		}
	};
}
