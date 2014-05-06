package Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLConnection;

public class NetworkUtils {

	/**
	 * 向指定url发送get请求
	 * 
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static String getReq(String url) throws MalformedURLException,
			IOException {
		URLConnection conn = new java.net.URL(url).openConnection();
		conn.setRequestProperty("accept", "*/*");
		conn.setRequestProperty("connection", "Keep-Alive");
		conn.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
		conn.setRequestProperty("Method", "GET");
		conn.connect();
		InputStream is = conn.getInputStream();

		ByteArrayOutputStream rbaos = new ByteArrayOutputStream();
		byte[] rbuffer = new byte[128];
		int riLen = -1;
		while (-1 != (riLen = is.read(rbuffer)))
			rbaos.write(rbuffer, 0, riLen);
		final String Ret = new String(rbaos.toByteArray());
		return Ret;
	}
}
