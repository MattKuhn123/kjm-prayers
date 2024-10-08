package org.mlk.kjm.ocr;

import org.mlk.kjm.shared.ApplicationProperties;

import java.util.Base64;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.*;
import javax.net.ssl.HttpsURLConnection;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.io.InputStream;
import java.util.Optional;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class OcrService {
    private final String apiKey;
    private final String rawUrl = "https://vision.googleapis.com/v1/images:annotate?key=REPLACE_ME";

    public OcrService(ApplicationProperties appProps) {
        this.apiKey = appProps.getApiKey();
    }

    public Optional<String> getTextFromImage(byte[] bytes) {
        String targetURL = getTargetURL();
        String params = getRequestBody(bytes);
        Optional<String> result = executePost(targetURL, params);
        return result;
    }

    private String getRequestBody(byte[] bytes) {
        String imageString = Base64.getEncoder().encodeToString(bytes);
        String raw = "{\"requests\": [{\"features\": [{\"type\": \"DOCUMENT_TEXT_DETECTION\"}],\"image\": {\"content\": \"REPLACE_ME\"}}]}";
        String result = raw.replace("REPLACE_ME", imageString);
        return result;
    }

    private String getTargetURL() {
        String result = rawUrl.replace("REPLACE_ME", apiKey);
        return result;
    }

    private static Optional<String> executePost(String targetURL, String params) {
        HttpURLConnection con = null;

        try {
            URL url = new URL(targetURL);
            SSLContext sc = SSLContext.getInstance("TLS");
            con = (HttpsURLConnection) url.openConnection();

            sc.init(null, getTrustAllCerts(), new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            con.setRequestProperty("Content-Length", Integer.toString(params.getBytes().length));
            con.setRequestProperty("Content-Language", "en-US");

            con.setUseCaches(false);
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(params);
            wr.close();

            InputStream is = con.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            String prefix = "\"description\": \"";
            String suffix = ",\"";
            while ((line = rd.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(prefix)) {
                    int length = line.length() - suffix.length();
                    line = line.substring(prefix.length(), length);

                    String target = "\\n";
                    String replacement = " ";
                    line = line.replace(target, replacement);
                    return Optional.of(line);
                }
            }

            rd.close();
            return Optional.empty();
        } catch (IOException e) {
            e.printStackTrace();

            InputStream is = con.getErrorStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            try {
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
            } catch (IOException ioe) {
            }

            String result = response.toString();
            System.out.println(result);
            return Optional.empty();
        } catch (KeyManagementException e) {
            e.printStackTrace();
            return Optional.empty();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return Optional.empty();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    private static TrustManager[] getTrustAllCerts() {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    }

                    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        return trustAllCerts;
    }
}
