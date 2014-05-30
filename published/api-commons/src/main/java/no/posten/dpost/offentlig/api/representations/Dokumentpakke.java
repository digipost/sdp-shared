package no.posten.dpost.offentlig.api.representations;

import java.io.InputStream;

public class Dokumentpakke {

    private final InputStream asicStream;
    private final byte[] asicBytes;

    public Dokumentpakke(InputStream asicStream) {
        this.asicBytes = null;
        this.asicStream = asicStream;
    }

    public Dokumentpakke(byte[] asicBytes) {
        this.asicBytes = asicBytes;
        this.asicStream = null;
    }

    public InputStream getAsicStream() {
        return asicStream;
    }
}
