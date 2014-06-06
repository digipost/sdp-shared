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


import org.apache.commons.io.IOUtils;
import org.bouncycastle.jcajce.provider.digest.SHA3.Digest256;

import javax.activation.DataSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;

public class Dokumentpakke implements DataSource {

    private InputStream asicStream;
    private final byte[] asicBytes;
	public static final String CONTENT_TYPE_KRYPTERT_DOKUMENTPAKKE = "application/cms";

    public Dokumentpakke(final InputStream asicStream) {
        this.asicStream = asicStream;
        asicBytes = null;
    }

    public Dokumentpakke(final byte[] asicBytes) {
        this.asicBytes = asicBytes;
        asicStream = new ByteArrayInputStream(asicBytes);
    }

    public byte[] getSHA256() throws IOException {
		MessageDigest digest = new Digest256();
		if (asicBytes != null) {
			return digest.digest(asicBytes);
		}
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DigestOutputStream digestStream = null;
		try{
            digestStream = new DigestOutputStream(baos, digest);
            IOUtils.copy(asicStream, digestStream);
		} finally {
			IOUtils.closeQuietly(digestStream);
        }
		asicStream = new ByteArrayInputStream(baos.toByteArray());
		return digest.digest();
    }


	@Override
	public String getContentType() {
		return Dokumentpakke.CONTENT_TYPE_KRYPTERT_DOKUMENTPAKKE;
	}

	@Override
	public InputStream getInputStream() throws IOException {
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
}
