package Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

public class NetworkUtils {
	/**
	 * 以get方法从服务器获取返回值（字符串形式）
	 * @param URL
	 * @return String形式的返回值
	 * @author Jayvee
	 * @throws UnsupportedEncodingException 
	 */
	static public String get2Server(String URL) throws UnsupportedEncodingException {
		HttpGet httpGet = new HttpGet(URL);
		InputStream is = null;
		try {
			HttpResponse response = new DefaultHttpClient().execute(httpGet);
			is = response.getEntity().getContent();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[128];
		int iLen = -1;
		try {
			while (-1 != (iLen = is.read(buffer)))
				baos.write(buffer, 0, iLen);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final String strRet = new String(baos.toByteArray(),"utf-8");
		System.out.println("获取服务器返回数据：" + strRet);
		return strRet;
	}
	
	/**
	 * 以post方式提交参数到服务器
	 * 
	 * @param postURL
	 * @param params
	 * @return String形式的返回值
	 * @throws IOException
	 * @author Jayvee
	 */
	static public String post2server(String postURL, List<NameValuePair> params)
			throws IOException {
		final HttpPost httpPost = new HttpPost(postURL);
		InputStream is = null;
		httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		try {
			HttpResponse response = null;
			response = new DefaultHttpClient().execute(httpPost);
//			if (response.getStatusLine().getStatusCode() == 201) {
				is = response.getEntity().getContent();
//			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[128];
		int iLen = -1;
		while (-1 != (iLen = is.read(buffer)))
			baos.write(buffer, 0, iLen);
		final String strRet = new String(baos.toByteArray(),"utf-8");
		System.out.println("获取服务器返回数据：" + strRet);
		return strRet;
	}
	
	
	
	
}
