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

import jayvee.visualization_weibo.R;

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

import surfaceview_Main.SurfaceViewMain;
import SQLiteUtils.DBhelper;
import Utils.FileUtils;
import Utils.ListData;
import Utils.NetworkUtils;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
	private final String access_token = "2.00t3nVnCe3dgkC63ac35576efRXPWC";

	private SQLiteDatabase db;// SQLite数据库实例

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
				bundle.putString("filename", data.getFilename());
				bundle.putBoolean("isSpread", false);
				bundle.putString("source", "list");
				intent.putExtras(bundle);
				startActivity(intent);
			} else {
				final String filename = data.getFilename();
				final ProgressDialog dialog = ProgressDialog.show(
						ListActivity.this, "正在下载……", "请等待");
				dialog.setCancelable(true);
				dialog.setCanceledOnTouchOutside(false);
				new Thread(new Runnable() {
					private String pull_resp;

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							// post方式获取下载文件
							String weiboName = filename;
							URL url = new URL(NaviActivity.labURL
									+ "download.do");
							final List<NameValuePair> params = new ArrayList<NameValuePair>();
							params.add(new BasicNameValuePair("weiboName",
									weiboName));
							String cachePath = getExternalCacheDir().getPath();
							byte[] filebytes = NetworkUtils.post2server(
									url.toString(), params).getBytes();
							FileUtils.byte2File(filebytes, cachePath, filename
									+ ".gexf");

							// 下载文件完毕后，获取该用户的相关参数


							pull_resp = NetworkUtils
									.post2server(
											NaviActivity.labURL+"androidpull.do",
											params);
							System.out.println(pull_resp);
							JSONTokener tokener = new JSONTokener(pull_resp);
							JSONObject root = (JSONObject) ((JSONArray) tokener
									.nextValue()).get(0);
							String userID = root.getString("userID");
							int Vtexin = root.getInt("Vtexin");
							int Vtexout = root.getInt("Vtexout");
							double ClusterCoefficient = root
									.getDouble("ClusterCoefficient");
							double truefollRatio = root
									.getDouble("truefollRatio");
							double BilateralRatio = root
									.getDouble("BilateralRatio");
							int woman = root.getInt("woman");
							int man = root.getInt("man");
							JSONArray province = (JSONArray) new JSONTokener(root.getString("province")).nextValue();
							
							
							JSONObject user_data = new JSONObject();
							user_data.put("userID", userID);
							user_data.put("userName", weiboName);
							user_data.put("Vtexin", Vtexin);
							user_data.put("Vtexout", Vtexout);
							user_data.put("woman", woman);
							user_data.put("man", man);
							user_data.put("ClusterCoefficient",
									ClusterCoefficient);
							user_data.put("truefollRatio", truefollRatio);
							user_data.put("BilateralRatio", BilateralRatio);
							user_data.put("province", province);


							// 根据uid获取最近转发的微博
							final String recentweibolistURL = NaviActivity.labURL+"recentweibolist.do?";
							String recentweibolist = NetworkUtils
									.get2Server(recentweibolistURL + "uid="
											+ userID);
							// userValues.put("recentweibolist",
							// recentweibolist);
							// db.insert("user_data", null, userValues);
							System.out.println("recentweibolist="
									+ recentweibolist);
							// 根据wid获取某条微博的转发曲线
							tokener = new JSONTokener(recentweibolist);
							JSONArray array = (JSONArray) tokener.nextValue();
							JSONArray recentWeibo = new JSONArray();
							for (int i = 0; i < array.length(); i++) {
								JSONObject object = (JSONObject) array.get(i);
								long wid = object.getLong("ID");
								String text = object.getString("Text");
								boolean Retweeted = object
										.getBoolean("Retweeted");
								JSONObject WeiboDetails = new JSONObject();
								WeiboDetails.put("wid", wid);
								WeiboDetails.put("Text", text);
								WeiboDetails.put("Retweeted", Retweeted);

								String weibocurveURL = NaviActivity.labURL+"weibocurve.do?";
								String STR_weibocurve = NetworkUtils
										.get2Server(weibocurveURL + "wid="
												+ wid);
								JSONArray repost_timeline = (JSONArray) new JSONTokener(
										STR_weibocurve).nextValue();
								WeiboDetails.put("repost_timeline",
										repost_timeline);
								recentWeibo.put(WeiboDetails);
							}
							user_data.put("recentWeibo", recentWeibo);
							File file = new File(getExternalCacheDir() + File.separator
											+ "datas"+ File.separator);
							file.mkdirs();
							System.out.println(file.isDirectory());
							FileUtils.byte2File(
									user_data.toString().getBytes("utf-8"),
									getExternalCacheDir() + File.separator
											+ "datas", weiboName + ".data");// 将信息保存成文件形式

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
//						 listDatas.remove(itemID);
//						 myListAdapter.notifyAll();
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

	private final class ViewHolder {
		public TextView text_filename;
		public TextView text_filesize;
		public ImageView img_logo;
	}

	/**
	 * 根据json格式的本地文件列表解析成可用的文件列表
	 * 
	 * @param strlist
	 * @return
	 */
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

	/**
	 * 根据json格式的网络文件列表解析成可用的文件列表
	 * 
	 * @param strlist
	 * @return
	 */
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

}
