package no.digipost.api.xml;

import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Objects;

public final class SchemaResource {

    public static final SchemaResource fromClasspath(String resourceName) {
        URL resourceUrl = Objects.requireNonNull(SchemaResource.class.getResource(resourceName), resourceName);
        try (InputStream inputStream = resourceUrl.openStream()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            final int bufLen = 1024;
            byte[] buf = new byte[bufLen];
            int readLen;
            while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
                outputStream.write(buf, 0, readLen);
            return new SchemaResource(resourceUrl, outputStream.toByteArray());
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Unable to resolve " + resourceName + " from " + resourceUrl + ", " +
                    "because " + e.getClass().getSimpleName() + " '" + e.getMessage() + "'", e);
        }
    }


    private final URL url;
    private final byte[] contents;

    public SchemaResource(URL url, byte[] contents) {
        this.url = url;
        this.contents = contents;
    }

    public URL getURL() {
        return url;
    }

    public InputStream read() {
        return new ByteArrayInputStream(contents);
    }

    public byte[] asBytes() {
        return contents;
    }

    public InputSource asSaxInputSource() {
        InputSource source = new InputSource(new ByteArrayInputStream(asBytes()));
        source.setSystemId(url.toExternalForm());
        return source;
    }

    public int sizeInBytes() {
        return contents.length;
    }

    @Override
    public String toString() {
        return "SchemaResource url='" + url + "' (" + sizeInBytes() + " bytes)";
    }

}
