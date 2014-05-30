package no.posten.dpost.offentlig.dsig;

import no.motif.f.Fn;
import org.w3.xmldsig.Reference;
import org.w3.xmldsig.Signature;
import org.w3.xmldsig.SignedInfo;

public final class DSigFns {

	public static final Fn<Signature, SignedInfo> signatureInfo = new Fn<Signature, SignedInfo>() {
		@Override public SignedInfo $(Signature signature) { return signature.getSignedInfo(); }};


	public static final Fn<Reference, String> uri = new Fn<Reference, String>() {
		@Override public String $(Reference ref) { return ref.getURI(); }};


	public static final Fn<Reference, byte[]> digestValue = new Fn<Reference, byte[]>() {
		@Override public byte[] $(Reference ref) { return ref.getDigestValue(); }};


	private DSigFns() {}
}
