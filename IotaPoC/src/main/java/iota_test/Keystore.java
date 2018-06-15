package iota_test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
//VERY IMPORTANT.  SOME OF THESE EXIST IN MORE THAN ONE PACKAGE!
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

public class Keystore {

public static void keystore() throws Exception {

	//Put everything after here in your function.
	KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
	trustStore.load(null);//Make an empty store
	InputStream fis = new FileInputStream("../IotaPoC/src/main/resources/iotasalad.org.cer");
	BufferedInputStream bis = new BufferedInputStream(fis);

	CertificateFactory cf = CertificateFactory.getInstance("X.509");

	while (bis.available() > 0) {
	    Certificate cert = cf.generateCertificate(bis);
	    trustStore.setCertificateEntry("fiddler"+bis.available(), cert);
	    System.out.println("added certificate "+cert.toString());
	}
}

}
