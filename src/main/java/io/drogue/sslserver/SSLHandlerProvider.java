package io.drogue.sslserver;

import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

public class SSLHandlerProvider {

    private static final String PROTOCOL = "TLS";
    private static final String ALGORITHM_SUN_X509="SunX509";
    private static final String ALGORITHM="ssl.KeyManagerFactory.algorithm";
    private static final String KEYSTORE= "keystore.jks";
    private static final String KEYSTORE_TYPE="JKS";
    private static final String KEYSTORE_PASSWORD= "123456";
    private static final String CERT_PASSWORD="123456";
    private  static SSLContext serverSSLContext =null;

    public static SslHandler getSSLHandler(){
        SSLEngine sslEngine=null;
        if(serverSSLContext ==null){
            System.err.println("Server SSL context is null");
            System.exit(-1);
        }else{
            sslEngine = serverSSLContext.createSSLEngine();
            sslEngine.setUseClientMode(false);
            sslEngine.setNeedClientAuth(false);

        }
        SslHandler handler = new SslHandler(sslEngine);
        handler.setHandshakeTimeout(60, TimeUnit.SECONDS);
        return handler;
    }

    public static void initSSLContext () {

        System.err.println("Initiating SSL context");
        String algorithm = Security.getProperty(ALGORITHM);
        if (algorithm == null) {
            algorithm = ALGORITHM_SUN_X509;
        }
        KeyStore ks = null;
        InputStream inputStream=null;
        try {
            //inputStream = new FileInputStream(SSLHandlerProvider.class.getClassLoader().getResource(KEYSTORE).getFile());
            inputStream = new FileInputStream( new File( KEYSTORE));
            ks = KeyStore.getInstance(KEYSTORE_TYPE);
            ks.load(inputStream,KEYSTORE_PASSWORD.toCharArray());
        } catch (IOException e) {
            System.err.println("Cannot load the keystore file");
            e.printStackTrace();
        } catch (CertificateException e) {
            System.err.println("Cannot get the certificate");
            e.printStackTrace();
        }  catch (NoSuchAlgorithmException e) {
            System.err.println("Somthing wrong with the SSL algorithm");
            e.printStackTrace();
        } catch (KeyStoreException e) {
            System.err.println("Cannot initialize keystore");
            e.printStackTrace();
        } finally {
            try {
                if ( inputStream != null ) {
                    inputStream.close();
                }
            } catch (IOException e) {
                System.err.println("Cannot close keystore file stream ");
                e.printStackTrace();
            }
        }
        try {

            // Set up key manager factory to use our key store
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(ks,CERT_PASSWORD.toCharArray());
            KeyManager[] keyManagers = kmf.getKeyManagers();
            // Setting trust store null since we don't need a CA certificate or Mutual Authentication
            TrustManager[] trustManagers = null;

            serverSSLContext = SSLContext.getInstance(PROTOCOL);
            serverSSLContext.init(keyManagers, trustManagers, null);


        } catch (Exception e) {
            System.err.println("Failed to initialize the server-side SSLContext");
            e.printStackTrace();
        }


    }


}
