
package no.digipost.api.representations;


import org.bouncycastle.jcajce.provider.digest.SHA256;

import javax.activation.DataSource;

import java.io.*;
import java.security.DigestOutputStream;
import java.security.MessageDigest;

public class Dokumentpakke implements DataSource {

    private final InputStream asicStream;
    private byte[] asicBytes;
	public static final String CONTENT_TYPE_KRYPTERT_DOKUMENTPAKKE = "application/cms";

    public Dokumentpakke(final InputStream asicStream) {
        this.asicStream = asicStream;
        this.asicBytes = null;
    }

    public Dokumentpakke(final byte[] asicBytes) {
        this.asicBytes = asicBytes;
        this.asicStream = new ByteArrayInputStream(asicBytes);
    }

    public byte[] getSHA256() throws IOException {
		MessageDigest digest = getDigest();
		if (asicBytes != null) {
			return digest.digest(asicBytes);
		}
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (InputStream asicToRead = asicStream; DigestOutputStream digestStream = new DigestOutputStream(baos, digest)) {
            copy(asicToRead, digestStream);
        }
		asicBytes = baos.toByteArray();
		return digest.digest();
    }

	protected MessageDigest getDigest() {
		return new SHA256.Digest();
	}


	@Override
	public String getContentType() {
		return Dokumentpakke.CONTENT_TYPE_KRYPTERT_DOKUMENTPAKKE;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (asicBytes != null) {
			return new ByteArrayInputStream(asicBytes);
		}
		return asicStream;
	}

	@Override
	public String getName() {
		return "asic.cms";
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new UnsupportedOperationException("Not supported by handler");
	}


    /**
     * Based on the {@code private} {@link java.nio.file.Files#copy(InputStream, OutputStream)}. This
     * implementation is included here to avoid depending on Apache Commons IO just for this simple case.
     */
    private static void copy(InputStream source, OutputStream sink) throws IOException {
        byte[] buf = new byte[8192];
        int n;
        while ((n = source.read(buf)) > 0) {
            sink.write(buf, 0, n);
        }
    }
}
