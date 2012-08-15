/*
 * This class is part of the white paper entitled
 * "Digital Signatures for PDF documents"
 * written by Bruno Lowagie
 * 
 * For more info, go to: http://itextpdf.com/sales
 */
package signatures.chapter04;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.log.LoggerFactory;
import com.itextpdf.text.log.SysoLogger;
import com.itextpdf.text.pdf.security.CrlClient;
import com.itextpdf.text.pdf.security.CrlClientOffline;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;

public class C4_05_SignWithCRLOffline extends C4_01_SignWithCAcert {
	public static final String SRC = "src/main/resources/hello.pdf";
	public static final String CRL = "src/main/resources/revoke.crl";
	public static final String DEST = "results/chapter4/hello_cacert_crl_offline.pdf";
	
	public static void main(String[] args) throws IOException, GeneralSecurityException, DocumentException {
		LoggerFactory.getInstance().setLogger(new SysoLogger());
		Properties properties = new Properties();
		properties.load(new FileInputStream("c:/home/blowagie/key.properties"));
    	String path = properties.getProperty("PRIVATE");
        char[] pass = properties.getProperty("PASSWORD").toCharArray();

		BouncyCastleProvider provider = new BouncyCastleProvider();
		Security.addProvider(provider);
        KeyStore ks = KeyStore.getInstance("pkcs12", provider.getName());
		ks.load(new FileInputStream(path), pass);
        String alias = (String)ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, pass);
        Certificate[] chain = ks.getCertificateChain(alias);
        FileInputStream is = new FileInputStream(CRL);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        while (is.read(buf) != -1) baos.write(buf);
        CrlClient crlClient = new CrlClientOffline(baos.toByteArray());
        List<CrlClient> crlList = new ArrayList<CrlClient>();
        crlList.add(crlClient);
        C4_05_SignWithCRLOffline app = new C4_05_SignWithCRLOffline();
		app.sign(pk, chain, SRC, DEST, provider.getName(), "Test", "Ghent", DigestAlgorithms.SHA256, CryptoStandard.CMS,
				crlList, null, null, 0);
	}
  
}
