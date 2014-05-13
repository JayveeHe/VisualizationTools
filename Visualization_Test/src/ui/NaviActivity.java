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

import surfaceview_Main.SurfaceViewMain;
import surfaceview_Demo.*;
import jayvee.visualization_weibo.R;
import Utils.FileUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
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
		Button btn_forcedemo = (Button) findViewById(R.id.btn_ForceDemo);
		Button btn_spread = (Button) findViewById(R.id.btn_spread);

		btn_spread.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NaviActivity.this,
						SpreadNaviActivity.class);
				startActivity(intent);
			}
		});

		btn_forcedemo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(NaviActivity.this,
						SurfaceViewDemo.class);
				startActivity(intent);
			}
		});

		btn_weblist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				final ProgressDialog dialog = ProgressDialog.show(
						NaviActivity.this, "正在读取列表", "请等待……");
				dialog.setCancelable(true);
				dialog.setCanceledOnTouchOutside(false);
				new Thread(new Runnable() {
					public void run() {
						try {
							String list = getWeblist(new URL(labURL
									+ "/MicroBlogDisplay/namelist.do"));
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
							e.printStackTrace();
						} catch (IOException e) {
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
							e.printStackTrace();
						}
					}
				}).start();

			}
		});

		btn_locallist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.menu_readme, menu);
		menu.getItem(0).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem arg0) {
						AlertDialog.Builder readmeBuilder = new Builder(
								NaviActivity.this);
						TextView title = new TextView(NaviActivity.this);
						title.setGravity(Gravity.CENTER_HORIZONTAL);
						title.setText("关于本软件");
						title.setTextSize(30);
						readmeBuilder.setCustomTitle(title);
						View view = View.inflate(NaviActivity.this,
								R.layout.layout_readme, null);
						TextView text_readme = (TextView) view
								.findViewById(R.id.text_readme);
						text_readme
								.setText("\t\t本客户端由ITTC数据挖掘团队出品,如有疑问可联系作者。\n\n地址：北邮新科研楼706\n邮件：jayveehe@gmail.com");
						readmeBuilder.setView(view);
						readmeBuilder.setNeutralButton("返回", null);
						readmeBuilder.create().show();
						return false;
					}
				});
		return super.onCreateOptionsMenu(menu);
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
				if (f.isFile()) {
					object.put("filename", f.getName());
					object.put("filesize", f.length() / 1024 + "KB");
					object.put("filepath", f.getPath());
					array.put(object);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return array.toString();
	}

}
