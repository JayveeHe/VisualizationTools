package ui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpConnection;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jayvee.visualization_test.R;
import Utils.FileUtils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class NaviActivity extends Activity {

	public final static String strURL = "http://getgexf.nat123.net";
	public final static String localURL = "http://10.202.10.176:8080";
	public final static String labURL = "http://10.108.192.119:8080";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_navi);
		Button btn_weblist = (Button) findViewById(R.id.btn_weblist);
		Button btn_locallist = (Button) findViewById(R.id.btn_locallist);
//		Button btn_test = (Button) findViewById(R.id.btn_test);
//		btn_test.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				new Thread(new Runnable() {
//					public void run() {
//						try {
//							// String cachePath =
//							// getExternalCacheDir().getPath();
//							// FileUtils
//							// .byte2File(test(), cachePath, "wujun.gexf");
//							// runOnUiThread(new Runnable() {
//							// public void run() {
//							// Toast.makeText(NaviActivity.this,
//							// "文件下载完成！请到本地文件列表查看",
//							// Toast.LENGTH_LONG).show();
//							// }
//							// });
//							test();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//
//					}
//				}).start();
//			}
//		});

		btn_weblist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// dialog.setCancelable(false);
				// dialog.setCanceledOnTouchOutside(false);
				final ProgressDialog dialog = ProgressDialog.show(
						NaviActivity.this, "正在读取列表", "请等待……");
				dialog.setCancelable(true);
				dialog.setCanceledOnTouchOutside(false);
				new Thread(new Runnable() {
					public void run() {
						try {
							String list = getWeblist(new URL(labURL
									+ "/MicroBlogDisplay/namelist.do"));
							// String list = getWeblist(new URL(localURL
							// + "/Android/GexfServlet/getGexf"));
							if (null != list) {
								// 将从服务器获取的文件列表以json格式的string传送到下一个activity
								Bundle bundle = new Bundle();
								Intent intent = new Intent(NaviActivity.this,
										ListActivity.class);
								bundle.putBoolean("isLocal", false);
								bundle.putString("list", list);
								intent.putExtras(bundle);
								runOnUiThread(new Runnable() {
									public void run() {
										dialog.dismiss();
									}
								});
								startActivity(intent);
							}
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(NaviActivity.this,
											"读取列表发生异常，连接超时！",
											Toast.LENGTH_SHORT).show();
									dialog.dismiss();
								}
							});
						} catch (URISyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();

			}
		});

		btn_locallist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String list = getLocallist();
				Bundle bundle = new Bundle();
				Intent intent = new Intent(NaviActivity.this,
						ListActivity.class);
				bundle.putBoolean("isLocal", true);
				bundle.putString("list", list);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	/**
	 * 以GET方法从服务器获取已有的gexf文件列表，并返回相应的string
	 * 
	 * @param url
	 *            服务器相应的servlet地址
	 * @return json格式的文件列表
	 * @throws IOException
	 * @author Jayvee
	 * @throws URISyntaxException
	 */
	private String getWeblist(URL url) throws IOException, URISyntaxException {
		// final HttpPost httpPost = new HttpPost(url.toURI());
		final HttpGet httpGet = new HttpGet(url.toURI());
		// List<NameValuePair> paramsdata = new ArrayList<NameValuePair>();
		// paramsdata.add(new BasicNameValuePair("weiboName", "吴军"));
		// paramsdata.add(new BasicNameValuePair("isAndroid", "true"));
		// paramsdata.add(new BasicNameValuePair("req_type", "0"));
		// httpPost.setEntity(new UrlEncodedFormEntity(paramsdata, HTTP.UTF_8));
		InputStream is = null;
		try {
			HttpResponse response = null;
			response = new DefaultHttpClient().execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				is = response.getEntity().getContent();
			}
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
		final String strRet = new String(baos.toByteArray());
		System.out.println("获取服务器返回数据：" + strRet);
		return strRet;
	}

	/**
	 * 获取本地的gexf文件列表的json格式字符串，并返回相应的string
	 * 
	 * @return json格式的文件列表
	 * @author Jayvee
	 */
	public String getLocallist() {
		File file = new File(getExternalCacheDir().getPath());
		JSONArray array = new JSONArray();
		// 创建本地的文件列表json字符串
		for (File f : file.listFiles()) {
			JSONObject object = new JSONObject();
			try {
				object.put("filename", f.getName());
				object.put("filesize", f.length() / 1024 + "KB");
				object.put("filepath", f.getPath());
				array.put(object);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return array.toString();
	}

	private byte[] test() throws IOException {
		// HttpPost httpPost = new HttpPost(
		// "http://10.108.192.119:8080/MicroBlogDisplay/download.do");
		String weiboName = "吴军";
		weiboName = URLEncoder.encode(weiboName, "utf-8");
		// HttpGet httpGet = new HttpGet(
		// "http://10.108.192.119:8080/MicroBlogDisplay/download.do?weiboName="
		// + weiboName);
		String str = "http://10.202.10.176:8080/Android/GexfServlet/getGexf";
		HttpGet httpGet = new HttpGet(
				"http://getgexf.nat123.net/Android/GexfServlet/getGexf");
		// HttpGet httpGet = new HttpGet(
		// "http://getgexf.nat123.net/Android/GexfServlet/getGexf?weiboName=无菌"
		// );
		// HttpGet httpGet = new HttpGet(
		// "http://10.108.200.186:8080/MicroBlogDisplay/download.do");
		// HttpPost httpPost = new HttpPost(
		// "http://10.108.192.119:8080/MicroBlogDisplay/pull.do");
		// List<NameValuePair> params = new ArrayList<NameValuePair>();
		// params.add(new BasicNameValuePair("weiboName", "吴军"));
		// httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		// httpGet.getParams().setParameter("weiboName", "吴军");

		// URL url = new URL(labURL + "/MicroBlogDisplay/download.do");
		URL url = new URL(
				"http://getgexf.nat123.net/Android/GexfServlet/getGexf");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		String urlParameters = "weiboName=adfasdfasdf";
		conn.setRequestProperty("Content-Length", "" + 
	               Integer.toString(urlParameters.getBytes().length));
		conn.setRequestMethod("GET");
		conn.setReadTimeout(5000);
		conn.setDoOutput(true);
		OutputStream os = conn.getOutputStream();
//		os.write(urlParameters.getBytes());
		DataOutputStream out = new DataOutputStream(os);
		out.writeBytes(urlParameters);
		
//		os.flush();
//		 out.print("weiboName=吴军");
		// os.
		// // conn.set
		// params.setParameter("weiboName", "吴军");
//		HttpClient client = new DefaultHttpClient();
//		HttpParams params = client.getParams();
//		params.setParameter("weiboName", "吴军");
//		httpGet.setParams(params);
		// conn.ge
		// InputStream is = conn.getInputStream();
		// List<NameValuePair> params = new ArrayList<NameValuePair>();
		// params.add(new BasicNameValuePair("weiboName", "吴军"));

		InputStream is = null;
		try {
			HttpResponse response = null;
			// response = new DefaultHttpClient().execute(httpPost);
//			response = client.execute(httpGet);
			// if (response.getStatusLine().getStatusCode() == 200) {
//			is = response.getEntity().getContent();
			is=conn.getInputStream();
			// response.getEntity().
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[128];
			int iLen = -1;
			while (-1 != (iLen = is.read(buffer)))
				baos.write(buffer, 0, iLen);
			final String strRet = new String(baos.toByteArray());
			System.out.println("获取服务器返回数据：" + strRet);
			return strRet.getBytes();
			// }
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
}
