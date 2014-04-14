package ui;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import jayvee.visualization_test.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import surfaceview_test.SurfaceViewMain;
import Utils.FileUtils;
import Utils.ListData;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListActivity extends Activity {
	// 相关变量
	private ArrayList<ListData> listDatas;
	private MyListAdapter myListAdapter = null;
	private ListView listView;

	private boolean isLocal = false;

	final static String DEBUG_TAG = "ListActivity";
	private Builder builder;

	NaviActivity naviActivity = new NaviActivity();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		builder = new Builder(this);
		setContentView(R.layout.layout_list);
		Bundle bundle = getIntent().getExtras();
		String strlist = bundle.getString("list");
		isLocal = bundle.getBoolean("isLocal");
		listView = (ListView) findViewById(R.id.listView);
		if (isLocal) {
			listDatas = JsonlocallistDecoder(strlist);
			listView.setOnItemLongClickListener(myItemLongClickListener);// 只有在本地列表模式才有长按删除选项
		} else {
			listDatas = JsonweblistDecoder(strlist);
		}
		myListAdapter = new MyListAdapter();
		// listView = (ListView) findViewById(R.id.listview);
		listView.setAdapter(myListAdapter);
		listView.setOnItemClickListener(myItemClickListener);
	}

	private OnItemClickListener myItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			ListData data = (ListData) arg0.getItemAtPosition(arg2);
			System.out.println("点到的文件名：" + data.getFilename());

			// 此处进行分别对待，对于处理本地列表和网络列表加以区分
			if (isLocal) {
				Intent intent = new Intent(ListActivity.this,
						SurfaceViewMain.class);
				Bundle bundle = new Bundle();
				System.out.println(data.getFilepath());
				bundle.putString("filepath", data.getFilepath());
				intent.putExtras(bundle);
				startActivity(intent);
			} else {
				final String filename = data.getFilename();
				final ProgressDialog dialog = ProgressDialog.show(
						ListActivity.this, "正在下载！", "请等待");
				dialog.setCancelable(true);
				dialog.setCanceledOnTouchOutside(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							// post方式获取下载文件
							String weiboName = filename;
							// String weiboName = null;
							// try {
							// weiboName = URLEncoder
							// .encode(filename, "utf-8");
							// } catch (UnsupportedEncodingException e1) {
							// // TODO Auto-generated catch block
							// e1.printStackTrace();
							// }
							// URL url = new URL(NaviActivity.labURL
							// + "/MicroBlogDisplay/download.do"
							// + "?weiboName=" + weiboName);
							URL url = new URL(NaviActivity.labURL
									+ "/MicroBlogDisplay/download.do");
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							params.add(new BasicNameValuePair("weiboName",
									weiboName));
							String cachePath = getExternalCacheDir().getPath();
							byte[] filebytes = post2server(url.toString(), params).getBytes();
//							FileUtils.byte2File(get2server(url.toString())
//									.getBytes(), cachePath, filename + ".gexf");
							FileUtils.byte2File(filebytes, cachePath, filename + ".gexf");
							runOnUiThread(new Runnable() {
								public void run() {
									dialog.dismiss();
									Toast.makeText(
											ListActivity.this,
											"文件：" + filename
													+ ".gexf 下载完成！请到本地文件列表查看",
											Toast.LENGTH_LONG).show();
								}
							});
						} catch (SocketTimeoutException e) {
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(ListActivity.this,
											"下载文件连接超时,请重新点击",
											Toast.LENGTH_SHORT).show();
									dialog.dismiss();
								}
							});
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(ListActivity.this,
											"下载文件错误,请检查参数", Toast.LENGTH_SHORT)
											.show();
									dialog.dismiss();
								}
							});
						}
					}
				}).start();
			}
		}
	};

	private OnItemLongClickListener myItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			int position = arg2;
			final long itemID = arg0.getItemIdAtPosition(position);
			final ListData data = (ListData) arg0.getItemAtPosition(position);
			builder.setCancelable(false);
			builder.setTitle("注意！");
			builder.setMessage("确定要删除文件：" + data.getFilename() + "？");
			builder.setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					File file = new File(data.getFilepath());
					if (file.delete()) {
						// listDatas.remove(itemID);
						// listDatas.notifyAll();
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(ListActivity.this,
										"文件:" + data.getFilename() + " 删除成功！",
										Toast.LENGTH_SHORT).show();
							}
						});
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(ListActivity.this,
										"文件:" + data.getFilename() + " 删除失败！",
										Toast.LENGTH_SHORT).show();
							}
						});
					}
				}
			});
			builder.setNegativeButton("取消", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

				}
			});
			builder.create().show();
			return false;
		}
	};

	private class MyListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listDatas.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return listDatas.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			// 获取list的item对象
			if (null == convertView) {
				convertView = View.inflate(ListActivity.this,
						R.layout.list_item, null);
				holder = new ViewHolder();
				holder.img_logo = (ImageView) convertView
						.findViewById(R.id.img_logo);
				holder.text_filename = (TextView) convertView
						.findViewById(R.id.text_filename);
				holder.text_filesize = (TextView) convertView
						.findViewById(R.id.text_filesize);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.text_filename.setText(listDatas.get(position).getFilename());
			holder.text_filesize.setText(listDatas.get(position).getFilesize());

			return convertView;
		}

	}

	public final class ViewHolder {
		public TextView text_filename;
		public TextView text_filesize;
		public ImageView img_logo;
	}

	private ArrayList<ListData> JsonlocallistDecoder(String strlist) {
		if (strlist == "") {
			Log.e(DEBUG_TAG, "解析json时传入了空字符串！");
			return null;
		}
		ArrayList<ListData> listDatas = new ArrayList<ListData>();
		JSONTokener jsonTokener = new JSONTokener(strlist);
		try {
			JSONArray list = (JSONArray) jsonTokener.nextValue();
			for (int i = 0; i < list.length(); i++) {
				ListData data = new ListData();
				JSONObject obj = list.getJSONObject(i);
				data.setFilename(obj.getString("filename"));
				data.setFilesize(obj.getString("filesize"));
				data.setFilepath(obj.getString("filepath"));
				data.setId(i);
				listDatas.add(data);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listDatas;
	}

	private ArrayList<ListData> JsonweblistDecoder(String strlist) {
		if (strlist == "") {
			Log.e(DEBUG_TAG, "解析json时传入了空字符串！");
			return null;
		}
		ArrayList<ListData> listDatas = new ArrayList<ListData>();
		JSONTokener jsonTokener = new JSONTokener(strlist);
		try {
			JSONArray list = (JSONArray) jsonTokener.nextValue();
			for (int i = 0; i < list.length(); i++) {
				ListData data = new ListData();
				JSONObject object = list.getJSONObject(i);
				JSONArray name = object.names();
				String filename = name.getString(0);
				String filesize = object.getInt(filename) + "";
				data.setFilename(filename);
				data.setFilesize(filesize + "KB");
				data.setFilepath("/");
				listDatas.add(data);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listDatas;
	}

	/**
	 * 以post方式提交参数到服务器
	 * 
	 * @param postURL
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public String post2server(String postURL, List<NameValuePair> params)
			throws IOException {
		final HttpPost httpPost = new HttpPost(postURL);
		InputStream is = null;
		httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		try {
			HttpResponse response = null;
			response = new DefaultHttpClient().execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 201) {
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
	 * 以get方式提交参数到服务器
	 * 
	 * @param getURL
	 * @param params
	 * @return 服务器返回的string形式
	 * @author Jayvee
	 * @throws Exception
	 */
	public String get2server(String getURL) throws Exception {
		final HttpGet httpGet = new HttpGet(getURL);
		InputStream is = null;
		try {
			HttpResponse response = null;
			response = new DefaultHttpClient().execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 201) {
				is = response.getEntity().getContent();
			} else {
				Exception e = new Exception("服务端错误！");
				throw (e);
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

}
