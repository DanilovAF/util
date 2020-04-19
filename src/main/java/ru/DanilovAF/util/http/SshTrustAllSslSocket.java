package ru.DanilovAF.util.http;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * Created by Aleksandr.Danilov on 15.01.2020.
 * Класс в которм есть доп фунции для работы с SSH для циско.
 * Получить возможность принять самоподписанный сертификат, принять любой хост
 *
 */
public class SshTrustAllSslSocket {

	private static final TrustManager[] trustAllCerts = new TrustManager[] {
			new X509TrustManager() {
				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return new java.security.cert.X509Certificate[]{};
				}
			}
	};
	private static final SSLContext trustAllSslContext;
	static {
		try {
			trustAllSslContext = SSLContext.getInstance("TLS");
			trustAllSslContext.init(null, trustAllCerts, new java.security.SecureRandom());
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			throw new RuntimeException(e);
		}
	}
	private static final SSLSocketFactory trustAllSslSocketFactory = trustAllSslContext.getSocketFactory();

	public static SSLSocketFactory getTrustAllSslSocketFactory() {
		return trustAllSslSocketFactory;
	}
	public Socket getNewSslSocket(String host, int port) throws IOException {
		return trustAllSslSocketFactory.createSocket(host, port);
	}
}
