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
