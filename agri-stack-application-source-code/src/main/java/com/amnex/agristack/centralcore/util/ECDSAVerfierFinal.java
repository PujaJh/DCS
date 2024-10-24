package com.amnex.agristack.centralcore.util;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
public class ECDSAVerfierFinal {

	private String title;

	private String uniqueIdentifier;

	private String DNQualifier;

	public String getTitle() {
		return title;
	}

	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	public String getDNQualifier() {
		return DNQualifier;
	}

	public boolean PKCS7Verifier(String documentHash, String pkcs7Response) {
//		String pkcs7Response = "MIILDQYJKoZIhvcNAQcCoIIK/jCCCvoCAQExDzANBglghkgBZQMEAgEFADALBgkqhkiG9w0BBwGg\r\\ngglyMIIDzzCCAregAwIBAgIJAMxt5h7OM6WEMA0GCSqGSIb3DQEBBQUAMH4xCzAJBgNVBAYTAklO\\r\\nMRQwEgYDVQQIDAtNYWhhcmFzaHRyYTENMAsGA1UEBwwEUFVORTEOMAwGA1UECgwFQy1EQUMxIjAg\\r\\nBgNVBAsMGVRlc3QgQ2VydGlmeWluZyBBdXRob3JpdHkxFjAUBgNVBAMMDVRlc3QgQy1EQUMgQ0Ew\\r\\nHhcNMTgwMTEwMTEzOTM1WhcNMjgwMTA4MTEzOTM1WjB+MQswCQYDVQQGEwJJTjEUMBIGA1UECAwL\\r\\nTWFoYXJhc2h0cmExDTALBgNVBAcMBFBVTkUxDjAMBgNVBAoMBUMtREFDMSIwIAYDVQQLDBlUZXN0\\r\\nIENlcnRpZnlpbmcgQXV0aG9yaXR5MRYwFAYDVQQDDA1UZXN0IEMtREFDIENBMIIBIjANBgkqhkiG\\r\\n9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzeJIAmzyhl49G+KfQPQmP5zg/Zoz6TDZImel43VklbKHRc4a\\r\\nWEAZR9Mp4pwsVXaWeDd+GWpBexzCv8KcBRat1Vv4ZyR7RgDwMJ8MSQkOkIo5oZ31sSnLlehbHC2d\\r\\nDUzOW66O1pzqFtvKyf6QIUxEpYRdn0bbLaZYOfHWKUW6LTCWRZ5S+HWilTaFI2aOIrG3Vg/Hf+3L\\r\\nQkJu4H7Urmr92Yjxd3Z7DKxVkjES4kexUe5PUMY5wmYfDC1PWOkv9GyKu1/sZEmQ+GUcUR/TNDnQ\\r\\noLbHvbmQaQ7TiyyiTCzY1kipAHOTs4YtSgdhLqqQK6jWe7WthGYPp0ejXCUg81bZeQIDAQABo1Aw\\r\\nTjAdBgNVHQ4EFgQUNdt2Xzx7JRnr58wo475nPP2MSQUwHwYDVR0jBBgwFoAUNdt2Xzx7JRnr58wo\\r\\n475nPP2MSQUwDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOCAQEATjibTJJGHMMNrU8u/D7D\\r\\nUe2VTBtRqprWvLTC9w825KKl/doPsP1Y1YTVS13U+do6SLGFEKd/rKwIvBwThuPUTXNOKocYfwrP\\r\\n8qC2RfcqVa7Xl1el3qg4bvVmZ+ST9GLBB5e5CAdY9yTDbYXOmMqv7DCN+1BRbq+AY520kZtpMWS4\\r\\nqoVdKMI9g3s3t59jgc89jRoXom5eszC8HfUiPZkHt4kaTZqKcUW2dFW7y3yVooB2CaCi5HHatN3E\\r\\nQn0tb3WQqvRL3ZkQ3mihVlXybvCCpSASTpW7MNqR05hM3d3+OybiHOih22+iceW69RGC12aU1yGN\\r\\nSavhQCQPky++Xx93MzCCBZswggSDoAMCAQICAwOe8TANBgkqhkiG9w0BAQsFADB+MQswCQYDVQQG\\r\\nEwJJTjEUMBIGA1UECAwLTWFoYXJhc2h0cmExDTALBgNVBAcMBFBVTkUxDjAMBgNVBAoMBUMtREFD\\r\\nMSIwIAYDVQQLDBlUZXN0IENlcnRpZnlpbmcgQXV0aG9yaXR5MRYwFAYDVQQDDA1UZXN0IEMtREFD\\r\\nIENBMB4XDTI0MDIxNjA4NTEzOFoXDTI0MDIxNjA5MjEzOFowggFEMQ4wDAYDVQQGEwVJbmRpYTEU\\r\\nMBIGA1UECBMLU3RhdGUgU2V2ZW4xETAPBgNVBAoTCFBlcnNvbmFsMRowGAYDVQQDExFQZXJzb24g\\r\\nU2V2ZW4gTmFtZTEPMA0GA1UEERMGOTAwMDA3MVIwUAYDVQQtA0kAOGg5ajBrMWEyYjNjNGQ1ZTZm\\r\\nN2c4aDlqMGsxYTJiM2M0ZDVlNmY3ZzhoOWowazEyMzQ1Njc4OTAxMjFhMmIzYzRkNWU2ZjdnMSkw\\r\\nJwYDVQRBEyBjNmQ0YTg1NGZlN2Q0YjUwYTQ5ODRmMjI3MDAxNjdmYTENMAsGA1UEDBMENjc4OTFO\\r\\nMEwGA1UELhNFMTk3N0ZiNTMzOTFiNGEzNGE1OTI2NGUxZGJkYTkxM2UyYjEzOThlYjdmNmU0ZDM2\\r\\nMWFlYTJkNTI2YTY0NTkxNzIwZjQyMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAESuNq3RmZ9zzn\\r\\n2yK95u+Cq1qmDZ5FuVeGzZSBX/Lz5zk8mMH4WXi9BjEp9xCFD/bHD50u4sARtzIoNGFwtDqQXaOC\\r\\nAiMwggIfMAkGA1UdEwQCMAAwHQYDVR0OBBYEFDu/fcDeRBiL0y3g97PQ0Ucq0LtQMB8GA1UdIwQY\\r\\nMBaAFA58oZXW2swg8yhPlL1306H0MIsWMA4GA1UdDwEB/wQEAwIGwDA5BgNVHR8EMjAwMC6gLKAq\\r\\nhihodHRwczovL2VzaWduLmNkYWMuaW4vY2EvZXNpZ25DQTIwMTQuY3JsMIIBPwYDVR0gBIIBNjCC\\r\\nATIwggEBBgdggmRkAQkCMIH1MDAGCCsGAQUFBwIBFiRodHRwczovL2VzaWduLmNkYWMuaW4vY2Ev\\r\\nQ1BTL0NQUy5wZGYwgcAGCCsGAQUFBwICMIGzMD4WOkNlbnRyZSBmb3IgRGV2ZWxvcG1lbnQgb2Yg\\r\\nQWR2YW5jZWQgQ29tcHV0aW5nIChDLURBQyksIFB1bmUwABpxVGhpcyBDUFMgaXMgb3duZWQgYnkg\\r\\nQy1EQUMgYW5kIHVzZXJzIGFyZSByZXF1ZXN0ZWQgdG8gcmVhZCBDUFMgYmVmb3JlIHVzaW5nIHRo\\r\\nZSBDLURBQyBDQSdzIGNlcnRpZmljYXRpb24gc2VydmljZXMwKwYHYIJkZAIEATAgMB4GCCsGAQUF\\r\\nBwICMBIaEEFhZGhhYXIgZUtZQy1PVFAwRAYIKwYBBQUHAQEEODA2MDQGCCsGAQUFBzAChihodHRw\\r\\nczovL2VzaWduLmNkYWMuaW4vY2EvQ0RBQy1DQTIwMTQuZGVyMA0GCSqGSIb3DQEBCwUAA4IBAQB5\\r\\nh7WiYJlOSSB4fa6leLXsUXIwk4a7brroWOxTHF50iq5br5/8eKCjbGrTKuzTchwdec6QkQVGKbUp\\r\\nP5ioX2aJ/ay6itQSgi9pjPnW2hB2YDzvq7oHJzMV1KJ2jR6FCTxzIjxHEUrowMTr+EjvDv2O0dPr\\r\\n77fNMwn7dCb7dqhljn5W/ZPYtknfob/unKz5cBmNAfEzUaSZKI6WukZvRQZVrS3esDRkC8yFYjdS\\r\\nQpB5V1vMdAZFRDTmzCc0sECX3lopHyYuVZSQQyjbO2mLLVYDyv4kWT2AdOtbzalqwaBJQh3dM9xz\\r\\nSJJOT4k+ZrZiDAi+uFT7rChgQybSsC3jkCmXMYIBXzCCAVsCAQEwgYUwfjELMAkGA1UEBhMCSU4x\\r\\nFDASBgNVBAgMC01haGFyYXNodHJhMQ0wCwYDVQQHDARQVU5FMQ4wDAYDVQQKDAVDLURBQzEiMCAG\\r\\nA1UECwwZVGVzdCBDZXJ0aWZ5aW5nIEF1dGhvcml0eTEWMBQGA1UEAwwNVGVzdCBDLURBQyBDQQID\\r\\nA57xMA0GCWCGSAFlAwQCAQUAoGkwGAYJKoZIhvcNAQkDMQsGCSqGSIb3DQEHATAcBgkqhkiG9w0B\\r\\nCQUxDxcNMjQwMjE2MDg1MTM4WjAvBgkqhkiG9w0BCQQxIgQgV4EWS0OLjfgCViE42+fON7mGbHRJ\\r\\n8ZRE42TfJFZzdAMwDAYIKoZIzj0EAwIFAARGMEQCIHh2ScPJPAf98Z4fsQpsA10ohAkPZ0+NTKIX\\r\\nuxZK2R3PAiBXa8dd1HG2APq8qhPHDraT8PVzFl7hcYZ9WvpK5FtT4g==";
		System.out.println(documentHash);
		System.out.println(pkcs7Response);
		System.out.println(pkcs7Response.replace("\\r\\n", "\n"));
		pkcs7Response = pkcs7Response.replace("\\r\\n", "\n");
		SignerInformation siinfo = null;
		byte[] signatureEcc = null;
		Security.addProvider(new BouncyCastleProvider());
		byte[] respBytes;
		try {
			respBytes = pkcs7Response.getBytes();

			CMSSignedData cms = new CMSSignedData(Base64.decode(respBytes));
			Collection<SignerInformation> si = cms.getSignerInfos()
					.getSigners();

			if (si.iterator().hasNext()) {
				siinfo = si.iterator().next();
				AttributeTable attributes = siinfo.getSignedAttributes();
				signatureEcc = siinfo.getSignature();

				if (attributes != null) {

					Attribute messageDigestAttribute = attributes
							.get(CMSAttributes.messageDigest);
					DEROctetString derocHash = (DEROctetString) messageDigestAttribute
							.getAttrValues().getObjectAt(0);
					String hashToMatch = derocHash.toString().replace("#", "");
					if (hashToMatch.equals(documentHash)) {
						System.out.println("hash matched");
						Store cs = cms.getCertificates();

						Collection certCollection = cs.getMatches(siinfo
								.getSID());
						X509CertificateHolder cert = (X509CertificateHolder) certCollection
								.iterator().next();
						X509Certificate certFromSignedData = new JcaX509CertificateConverter()
								.setProvider(new BouncyCastleProvider())
								.getCertificate(cert);

						getUniqueIdentifierFromCertificate(certFromSignedData);
						getTitleFromCertificate(certFromSignedData); // get
																		// Title
						getDNQualifierFromCertificate(certFromSignedData); // get
																			// DNQualifier

						Signature ecdsaVerify = Signature.getInstance(
								"SHA256withECDSA", new BouncyCastleProvider());
						ecdsaVerify.initVerify(certFromSignedData
								.getPublicKey());
						ecdsaVerify.update(siinfo.getEncodedSignedAttributes());

						boolean value = ecdsaVerify.verify(signatureEcc);
						if (value == true) {
							System.out.println("verified");
							return true;
						} else {
							System.out.println("verification failed");
							return false;
						}
					} else {
						System.out.println("hash verification failed");
						return false;
					}

				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public String getUniqueIdentifierFromCertificate(
			X509Certificate certFromSignedData)
			throws CertificateEncodingException, IOException {
		X500Name x500name = new JcaX509CertificateHolder(certFromSignedData)
				.getSubject();
		RDN cn1 = x500name.getRDNs(BCStyle.UNIQUE_IDENTIFIER)[0];
		DERBitString derBitString = (DERBitString) cn1.getFirst().getValue();
		byte[] bitstringBytes = derBitString.getBytes();
		String unique_identifier = new String(bitstringBytes);
		System.out.println(unique_identifier + " length:: " + unique_identifier.length());
		return unique_identifier;

	}

	public void getTitleFromCertificate(X509Certificate certFromSignedData)
			throws CertificateEncodingException {
		X500Name x500name = new JcaX509CertificateHolder(certFromSignedData)
				.getSubject();
		RDN cn1 = x500name.getRDNs(BCStyle.UNIQUE_IDENTIFIER)[0];
		RDN cn12 = x500name.getRDNs(BCStyle.T)[0];
		title = IETFUtils.valueToString(cn12.getFirst().getValue());

		// System.out.println(IETFUtils.valueToString(cn.getFirst().getValue()));
		// System.out.println(IETFUtils.valueToString(cn1.getFirst().getValue())+"
		// length:: "+IETFUtils.valueToString(cn1.getFirst().getValue()).length());
	}

	public void getDNQualifierFromCertificate(X509Certificate certFromSignedData)
			throws CertificateEncodingException {
		X500Name x500name = new JcaX509CertificateHolder(certFromSignedData)
				.getSubject();
		RDN cn1 = x500name.getRDNs(BCStyle.DN_QUALIFIER)[0];
		DNQualifier = IETFUtils.valueToString(cn1.getFirst().getValue());
		System.out.println("DNQualifier  ::: " + DNQualifier);

		// System.out.println(IETFUtils.valueToString(cn.getFirst().getValue()));
		// System.out.println(IETFUtils.valueToString(cn1.getFirst().getValue())+"
		// length:: "+IETFUtils.valueToString(cn1.getFirst().getValue()).length());
	}

	public static void main(String[] args) throws JsonMappingException, JsonProcessingException {
		ECDSAVerfierFinal pks = new ECDSAVerfierFinal();

		// System.out.println(pks.PKCS7Verifier("5e58da39e07f3e64a663e62a4f3d292e4efe1be87c970a1488da74857aea922e",
		// "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"));
		String consentString = "{\"meta\":{\"status\":\"ACTIVE\",\"timestamp\":\"2023-12-27T10:47:55.202Z\"},\"main\":{\"farmerId\":\"15130004480\",\"aipId\":\"AIP_02\",\"aiuId\":\"TEST_4\",\"purposeCode\":\"CREDIT_KCC\",\"validUntil\":\"2024-03-31\",\"attributes\":{\"farmer_name\":\"GRANTED\",\"asset_artifact\":\"sdYMyeTdksPRoR/jdlV5A6HPXA9/5ZcVUpLvy+BhfxD/NyjQsXdHqIR2vEz30AbP7sZ5ja1NiYiScnmk1O9pC3txgI/KRyvloogbpOstInb5ZI4rc5GeWf7Wq9lGhx6i\",\"crop_asset_data\":\"GRANTED\",\"asset_data_details\":\"GRANTED\"}}}";
		System.out.println(consentString);
		String generatedhash = generateHash(consentString);
		System.out.println(generatedhash);
		// System.out.println(pks.PKCS7Verifier("789e783169296270f5dd4877cdf6c7ce5b0deb72a7c0d2bc00bbec54bbe3ba51",
		// "MIILDAYJKoZIhvcNAQcCoIIK/TCCCvkCAQExDzANBglghkgBZQMEAgEFADALBgkqhkiG9w0BBwGg\r\ngglwMIIDzzCCAregAwIBAgIJAMxt5h7OM6WEMA0GCSqGSIb3DQEBBQUAMH4xCzAJBgNVBAYTAklO\r\nMRQwEgYDVQQIDAtNYWhhcmFzaHRyYTENMAsGA1UEBwwEUFVORTEOMAwGA1UECgwFQy1EQUMxIjAg\r\nBgNVBAsMGVRlc3QgQ2VydGlmeWluZyBBdXRob3JpdHkxFjAUBgNVBAMMDVRlc3QgQy1EQUMgQ0Ew\r\nHhcNMTgwMTEwMTEzOTM1WhcNMjgwMTA4MTEzOTM1WjB+MQswCQYDVQQGEwJJTjEUMBIGA1UECAwL\r\nTWFoYXJhc2h0cmExDTALBgNVBAcMBFBVTkUxDjAMBgNVBAoMBUMtREFDMSIwIAYDVQQLDBlUZXN0\r\nIENlcnRpZnlpbmcgQXV0aG9yaXR5MRYwFAYDVQQDDA1UZXN0IEMtREFDIENBMIIBIjANBgkqhkiG\r\n9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzeJIAmzyhl49G+KfQPQmP5zg/Zoz6TDZImel43VklbKHRc4a\r\nWEAZR9Mp4pwsVXaWeDd+GWpBexzCv8KcBRat1Vv4ZyR7RgDwMJ8MSQkOkIo5oZ31sSnLlehbHC2d\r\nDUzOW66O1pzqFtvKyf6QIUxEpYRdn0bbLaZYOfHWKUW6LTCWRZ5S+HWilTaFI2aOIrG3Vg/Hf+3L\r\nQkJu4H7Urmr92Yjxd3Z7DKxVkjES4kexUe5PUMY5wmYfDC1PWOkv9GyKu1/sZEmQ+GUcUR/TNDnQ\r\noLbHvbmQaQ7TiyyiTCzY1kipAHOTs4YtSgdhLqqQK6jWe7WthGYPp0ejXCUg81bZeQIDAQABo1Aw\r\nTjAdBgNVHQ4EFgQUNdt2Xzx7JRnr58wo475nPP2MSQUwHwYDVR0jBBgwFoAUNdt2Xzx7JRnr58wo\r\n475nPP2MSQUwDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOCAQEATjibTJJGHMMNrU8u/D7D\r\nUe2VTBtRqprWvLTC9w825KKl/doPsP1Y1YTVS13U+do6SLGFEKd/rKwIvBwThuPUTXNOKocYfwrP\r\n8qC2RfcqVa7Xl1el3qg4bvVmZ+ST9GLBB5e5CAdY9yTDbYXOmMqv7DCN+1BRbq+AY520kZtpMWS4\r\nqoVdKMI9g3s3t59jgc89jRoXom5eszC8HfUiPZkHt4kaTZqKcUW2dFW7y3yVooB2CaCi5HHatN3E\r\nQn0tb3WQqvRL3ZkQ3mihVlXybvCCpSASTpW7MNqR05hM3d3+OybiHOih22+iceW69RGC12aU1yGN\r\nSavhQCQPky++Xx93MzCCBZkwggSBoAMCAQICAwNzXTANBgkqhkiG9w0BAQsFADB+MQswCQYDVQQG\r\nEwJJTjEUMBIGA1UECAwLTWFoYXJhc2h0cmExDTALBgNVBAcMBFBVTkUxDjAMBgNVBAoMBUMtREFD\r\nMSIwIAYDVQQLDBlUZXN0IENlcnRpZnlpbmcgQXV0aG9yaXR5MRYwFAYDVQQDDA1UZXN0IEMtREFD\r\nIENBMB4XDTIzMTIxOTEyNTg1OFoXDTIzMTIxOTEzMjg1OFowggFCMQ4wDAYDVQQGEwVJbmRpYTET\r\nMBEGA1UECBMKU3RhdGUgRml2ZTERMA8GA1UEChMIUGVyc29uYWwxGTAXBgNVBAMTEFBlcnNvbiBG\r\naXZlIE5hbWUxDzANBgNVBBETBjkwMDAwNTFSMFAGA1UELQNJADZmN2c4aDlqMGsxYTJiM2M0ZDVl\r\nNmY3ZzhoOWowazFhMmIzYzRkNWU2ZjdnOGg5ajBrMTIzNDU2Nzg5MDEyMWEyYjNjNGQ1ZTEpMCcG\r\nA1UEQRMgYzZkNGE4NTRmZTdkNGI1MGE0OTg0ZjIyNzAwMTY3ZmExDTALBgNVBAwTBDY3ODkxTjBM\r\nBgNVBC4TRTE5NzVGYjUzMzkxYjRhMzRhNTkyNjRlMWRiZGE5MTNlMmIxMzk4ZWI3ZjZlNGQzNjFh\r\nZWEyZDUyNmE2NDU5MTcyMGY0MjBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABErjat0Zmfc859si\r\nvebvgqtapg2eRblXhs2UgV/y8+c5PJjB+Fl4vQYxKfcQhQ/2xw+dLuLAEbcyKDRhcLQ6kF2jggIj\r\nMIICHzAJBgNVHRMEAjAAMB0GA1UdDgQWBBQ7v33A3kQYi9Mt4Pez0NFHKtC7UDAfBgNVHSMEGDAW\r\ngBQOfKGV1trMIPMoT5S9d9Oh9DCLFjAOBgNVHQ8BAf8EBAMCBsAwOQYDVR0fBDIwMDAuoCygKoYo\r\naHR0cHM6Ly9lc2lnbi5jZGFjLmluL2NhL2VzaWduQ0EyMDE0LmNybDCCAT8GA1UdIASCATYwggEy\r\nMIIBAQYHYIJkZAEJAjCB9TAwBggrBgEFBQcCARYkaHR0cHM6Ly9lc2lnbi5jZGFjLmluL2NhL0NQ\r\nUy9DUFMucGRmMIHABggrBgEFBQcCAjCBszA+FjpDZW50cmUgZm9yIERldmVsb3BtZW50IG9mIEFk\r\ndmFuY2VkIENvbXB1dGluZyAoQy1EQUMpLCBQdW5lMAAacVRoaXMgQ1BTIGlzIG93bmVkIGJ5IEMt\r\nREFDIGFuZCB1c2VycyBhcmUgcmVxdWVzdGVkIHRvIHJlYWQgQ1BTIGJlZm9yZSB1c2luZyB0aGUg\r\nQy1EQUMgQ0EncyBjZXJ0aWZpY2F0aW9uIHNlcnZpY2VzMCsGB2CCZGQCBAEwIDAeBggrBgEFBQcC\r\nAjASGhBBYWRoYWFyIGVLWUMtT1RQMEQGCCsGAQUFBwEBBDgwNjA0BggrBgEFBQcwAoYoaHR0cHM6\r\nLy9lc2lnbi5jZGFjLmluL2NhL0NEQUMtQ0EyMDE0LmRlcjANBgkqhkiG9w0BAQsFAAOCAQEAksuX\r\nsQnwQU7mzKrvC8T0hrbHYGWhTsoL7QivLab+BSTrT53h9BcCHq0fxSB8rpDNcoXPKRGRSMYmm/+T\r\nxsoP6oyvDkBRYcvshX9ENyB80KLYhRjpYwlcdKmHs+cVm7smuSEU3g2o95ajwCg4ofWc/e0IC//H\r\naFl0RQKJxbR3mFTpiYARZ5jAygrz+Z+VefW7rhBSn9F4t0X9xpuntOisysH/r5RXhSvCS/SnsFuE\r\nVV6LcBWo7Iz0ATD+9o/I0mmDmMwS+7giXXAN5jQrgdsp+SFaqLeVvDZLRD3vilo9fJzx4DEYIXFo\r\n+mYQrnmmcSv0N17EnrAEyQBhhdsh5+eMUTGCAWAwggFcAgEBMIGFMH4xCzAJBgNVBAYTAklOMRQw\r\nEgYDVQQIDAtNYWhhcmFzaHRyYTENMAsGA1UEBwwEUFVORTEOMAwGA1UECgwFQy1EQUMxIjAgBgNV\r\nBAsMGVRlc3QgQ2VydGlmeWluZyBBdXRob3JpdHkxFjAUBgNVBAMMDVRlc3QgQy1EQUMgQ0ECAwNz\r\nXTANBglghkgBZQMEAgEFAKBpMBgGCSqGSIb3DQEJAzELBgkqhkiG9w0BBwEwHAYJKoZIhvcNAQkF\r\nMQ8XDTIzMTIxOTEyNTg1OFowLwYJKoZIhvcNAQkEMSIEINhIrw2WRMnp7gEQxvM2vDyJOkjf5EP5\r\nMiidMrn1PRGYMAwGCCqGSM49BAMCBQAERzBFAiEAo/wfRXd38+PMLRDTqNWkqtUItvS2CYK2CjeZ\r\nH0oKsrkCIEvfTur8IGymenayRuoBIDTsj9M7j9fbFQqtIZ1wyHdc"));
	}

	public static String generateHash(String documentString) throws JsonMappingException, JsonProcessingException {
		return DigestUtils.sha256Hex(documentString);
	}
}
