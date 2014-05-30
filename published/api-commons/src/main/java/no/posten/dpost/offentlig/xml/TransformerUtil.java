package no.posten.dpost.offentlig.xml;

import javax.xml.transform.*;

public class TransformerUtil {


	public static void transform(Source source, Result result) {

		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.transform(source, result);
		} catch (TransformerException e) {
			// TODO lag en bedre exception
			throw new RuntimeException("Transformation failed", e);
		}

	}

}
