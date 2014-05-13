package ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import surfaceview_Main.MySurfaceView;
import surfaceview_Main.SurfaceViewMain;
import jayvee.visualization_weibo.R;
import SpreadUtils.PlotUtils.IDrawableNode;
import SpreadUtils.WeiboSpreadUtils;
import SpreadUtils.gexftest;
import Utils.NetworkUtils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SpreadNaviActivity extends Activity {

	public static ProgressDialog progressDialog;
	public static String CachePath;
	public static Map<String, IDrawableNode> info_map;
	public static String dialogMSG;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_spreadnavi);
		progressDialog = new ProgressDialog(SpreadNaviActivity.this);
		CachePath = getExternalCacheDir().getPath();
		Button btn_start = (Button) findViewById(R.id.btn_startAnalysis);
		final EditText et_weiboUrl = (EditText) findViewById(R.id.et_weiboUrl);

		btn_start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String URL = et_weiboUrl.getText().toString();
				if (URL.equals("")) {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(SpreadNaviActivity.this,
									"输入的地址不能为空！", Toast.LENGTH_SHORT).show();
						}
					});
				} else {
					progressDialog.setMessage("正在分析……");
					progressDialog.setCanceledOnTouchOutside(false);
					progressDialog.setCancelable(true);
					progressDialog.show();
					new Thread(new Runnable() {
						public void run() {

							info_map = WeiboSpreadUtils.WeiboSpread(URL,
									CachePath + File.separator
											+ "weibospread.gexf", 5);
							progressDialog.dismiss();
							if (info_map != null) {
								runOnUiThread(new Runnable() {
									public void run() {
										Toast.makeText(getApplicationContext(),
												"微博传播分析成功！", Toast.LENGTH_SHORT)
												.show();
									}
								});
								String filePath = CachePath + File.separator
										+ "weibospread.gexf";
								Bundle bundle = new Bundle();
								Intent intent = new Intent(
										SpreadNaviActivity.this,
										SurfaceViewMain.class);
								bundle.putString("source", "SpreadNavi");
								bundle.putString("filepath", filePath);
								bundle.putBoolean("isSpread", true);
								intent.putExtras(bundle);
								startActivity(intent);
							} else {
								runOnUiThread(new Runnable() {
									public void run() {
										Toast.makeText(getApplicationContext(),
												"发生异常！", Toast.LENGTH_SHORT)
												.show();
										progressDialog.dismiss();
									}
								});
							}

						}
					}).start();

				}
			}
		});

	}

	public String getDialogMSG() {
		return dialogMSG;
	}

	public static void setDialogMSG(String dialogMSG) {
		progressDialog.setMessage(dialogMSG);
	}
}
