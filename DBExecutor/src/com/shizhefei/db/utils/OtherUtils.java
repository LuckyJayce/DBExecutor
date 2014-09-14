package com.shizhefei.db.utils;

import android.text.TextUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.protocol.HTTP;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.cert.X509Certificate;

/**
 * Created by wyouflf on 13-8-30.
 */
public class OtherUtils {
	private OtherUtils() {
	}

	public static boolean isSupportRange(final HttpResponse response) {
		if (response == null)
			return false;
		Header header = response.getFirstHeader("Accept-Ranges");
		if (header != null) {
			return "bytes".equals(header.getValue());
		}
		header = response.getFirstHeader("Content-Range");
		if (header != null) {
			String value = header.getValue();
			return value != null && value.startsWith("bytes");
		}
		return false;
	}

	public static String getFileNameFromHttpResponse(final HttpResponse response) {
		if (response == null)
			return null;
		String result = null;
		Header header = response.getFirstHeader("Content-Disposition");
		if (header != null) {
			for (HeaderElement element : header.getElements()) {
				NameValuePair fileNamePair = element
						.getParameterByName("filename");
				if (fileNamePair != null) {
					result = fileNamePair.getValue();
					// try to get correct encoding str
					result = CharsetUtils.toCharset(result, HTTP.UTF_8,
							result.length());
					break;
				}
			}
		}
		return result;
	}

	public static String getCharsetFromHttpResponse(final HttpResponse response) {
		if (response == null)
			return null;
		String result = null;
		Header header = response.getEntity().getContentType();
		if (header != null) {
			for (HeaderElement element : header.getElements()) {
				NameValuePair charsetPair = element
						.getParameterByName("charset");
				if (charsetPair != null) {
					result = charsetPair.getValue();
					break;
				}
			}
		}

		boolean isSupportedCharset = false;
		if (!TextUtils.isEmpty(result)) {
			try {
				isSupportedCharset = Charset.isSupported(result);
			} catch (Throwable e) {
			}
		}

		return isSupportedCharset ? result : null;
	}

	private static final int STRING_BUFFER_LENGTH = 100;

	public static long sizeOfString(final String str, String charset)
			throws UnsupportedEncodingException {
		if (TextUtils.isEmpty(str)) {
			return 0;
		}
		int len = str.length();
		if (len < STRING_BUFFER_LENGTH) {
			return str.getBytes(charset).length;
		}
		long size = 0;
		for (int i = 0; i < len; i += STRING_BUFFER_LENGTH) {
			int end = i + STRING_BUFFER_LENGTH;
			end = end < len ? end : len;
			String temp = getSubString(str, i, end);
			size += temp.getBytes(charset).length;
		}
		return size;
	}

	// get the sub string for large string
	public static String getSubString(final String str, int start, int end) {
		return new String(str.substring(start, end));
	}

	public static StackTraceElement getCurrentStackTraceElement() {
		return Thread.currentThread().getStackTrace()[3];
	}

	public static StackTraceElement getCallerStackTraceElement() {
		return Thread.currentThread().getStackTrace()[4];
	}

	private static TrustManager[] trustAllCerts;

	public static void trustAllSSLForHttpsURLConnection() {
		// Create a trust manager that does not validate certificate chains
		if (trustAllCerts == null) {
			trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs,
						String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs,
						String authType) {
				}
			} };
		}
		// Install the all-trusting trust manager
		final SSLContext sslContext;
		try {
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustAllCerts, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
					.getSocketFactory());
		} catch (Throwable e) {
			LogUtils.e(e.getMessage(), e);
		}
		HttpsURLConnection
				.setDefaultHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	}
}
