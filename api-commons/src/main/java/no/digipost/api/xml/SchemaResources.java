package no.digipost.api.xml;

import org.xml.sax.XMLReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.joining;

public class SchemaResources {

    public static final SchemaResource SBDH_SCHEMA = SchemaResource.fromClasspath("/SBDH20040506-02/StandardBusinessDocumentHeader.xsd");
    public static final SchemaResource SDP_SCHEMA = SchemaResource.fromClasspath("/sdp.xsd");
    public static final SchemaResource SDP_MANIFEST_SCHEMA = SchemaResource.fromClasspath("/sdp-manifest.xsd");
    public static final SchemaResource EBMS_SCHEMA = SchemaResource.fromClasspath("/ebxml/ebms-header-3_0-200704.xsd");
    public static final SchemaResource XMLDSIG_SCHEMA = SchemaResource.fromClasspath("/w3/xmldsig-core-schema.xsd");
    public static final SchemaResource XADES_SCHEMA = SchemaResource.fromClasspath("/etsi/XAdES.xsd");
    public static final SchemaResource ASICE_SCHEMA = SchemaResource.fromClasspath("/asic-e/ts_102918v010201.xsd");
    public static final SchemaResource ASICE_SCHEMA2 = SchemaResource.fromClasspath("/asic-e/ts_102918v010201_2.xsd");

    public static final SchemaResource LENKE_SCHEMA = SchemaResource.fromClasspath("/utvidelser/lenke.xsd");
    public static final SchemaResource BEVIS_SCHEMA = SchemaResource.fromClasspath("/utvidelser/bevis.xsd");
    public static final SchemaResource ARRANGEMENT_SCHEMA = SchemaResource.fromClasspath("/utvidelser/arrangement.xsd");

    public static List<SchemaResource> all() {
        return asList(SDP_SCHEMA, SDP_MANIFEST_SCHEMA, SBDH_SCHEMA, EBMS_SCHEMA, XMLDSIG_SCHEMA, XADES_SCHEMA, ASICE_SCHEMA, ASICE_SCHEMA2, BEVIS_SCHEMA, LENKE_SCHEMA, ARRANGEMENT_SCHEMA);
    }

    public static List<SchemaResource> sbdOnly() {
        return asList(SBDH_SCHEMA, SDP_SCHEMA, XMLDSIG_SCHEMA);
    }

    public static Schema createSchema(SchemaResource resource) {
        return createSchema(singleton(resource));
    }

    public static Schema createSchema(Collection<SchemaResource> resources) {
        if (resources == null || resources.isEmpty()) {
            throw new IllegalArgumentException("No resources to create schema from (resources=" + resources + ")");
        }
        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setNamespaceAware(true);
            parserFactory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);

            SAXParser saxParser = parserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            Source[] schemaSources = resources.stream()
                    .map(SchemaResource::asSaxInputSource)
                    .map(schemaInputSource -> new SAXSource(xmlReader, schemaInputSource))
                    .toArray(Source[]::new);

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(schemaSources);
            return schema;
        } catch (Exception e) {
            throw new RuntimeException("Could not create schema from [" + resources.stream().map(SchemaResource::toString).collect(joining(", ")) + "]", e);
        }
    }
}
