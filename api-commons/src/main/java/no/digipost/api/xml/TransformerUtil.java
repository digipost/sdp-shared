
package no.digipost.api.xml;

import javax.xml.transform.*;

public class TransformerUtil {


	public static void transform(Source source, Result result) {
		transform(source, result, false);
	}
	public static void transform(Source source, Result result, boolean omitXmlHeader) {

		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, omitXmlHeader ? "yes" : "no");
			transformer.transform(source, result);
		} catch (TransformerException e) {
			// TODO lag en bedre exception
			throw new RuntimeException("Transformation failed", e);
		}

	}

}
