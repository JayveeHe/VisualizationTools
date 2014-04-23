package surfaceview_test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ui.Viewpager_main;
import jayvee.visualization_test.R;
//import jayvee.visualization_test.R
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class SurfaceViewMain extends Activity {

	float downX = 0;
	float downY = 0;
	float FirstdownX = 0;
	float FirstdownY = 0;
	float scaleRate = 1;
	float distance = 0;
	int TouchMod = 0;
	final static int TOUCH_MOD_DRAG = 1;
	final static int TOUCH_MOD_ZOOM = 2;
	MySurfaceView myView;
	private Builder builder;
	public static String scrsFileRootPath;
	private static final String DEBUG_TAG = "SurfaceViewMain";

	private long touchtime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		String filename = bundle.getString("filename");
		String filepath = bundle.getString("filepath");
		// final MySurfaceView myView = new MySurfaceView(this,filename);
		File gexffile = new File(filepath);
		scrsFileRootPath = Environment.getExternalStorageDirectory()
				+ "/数据可视化截图/";
		File file = new File(scrsFileRootPath);
		if (!file.exists())
			file.mkdir();
		builder = new Builder(this);
		myView = new MySurfaceView(this, gexffile);
		setContentView(myView);

		// Button btn_save =

		OnTouchListener Mytouch = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					Log.d("Touch", "单指按下");
					// System.out.println(event.getX(0));
					downX = event.getX();
					downY = event.getY();
					FirstdownX = event.getX();
					FirstdownY = event.getY();
					touchtime = System.currentTimeMillis();
					TouchMod = TOUCH_MOD_DRAG;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					Log.d("Touch", "多指按下");
					// System.out.println(event.getX(0) + "++++" +
					// event.getX(1));
					distance = distance(event);
					TouchMod = TOUCH_MOD_ZOOM;
					break;
				case MotionEvent.ACTION_MOVE:
					if (TouchMod == TOUCH_MOD_DRAG) {
						float xx = event.getX() - downX;
						float yy = event.getY() - downY;
						downX = event.getX();
						downY = event.getY();
						myView.onDrag(xx, yy);
						// myView.logicManager.onOverallUpdate();
					} else if (TouchMod == TOUCH_MOD_ZOOM) {
						// System.out.println("两指距离=" + distance(event));
						myView.logicManager.fScaleRate = myView.logicManager.fScaleRate
								* distance(event) / distance;
						// myView.onScale(scaleRate);
						// myView.logicManager.onOverallUpdate();
						myView.logicManager.onViewRefresh();
						distance = distance(event);
					}
					break;
				case MotionEvent.ACTION_UP:
					TouchMod = 0;
					distance = 0;
					Log.d("Touch", "单指起来");
					touchtime = System.currentTimeMillis() - touchtime;// 手指按下并停留的时间
					Log.d("Touch", "手指停留时间" + touchtime);
					if (Math.sqrt((FirstdownX - event.getX())
							* (FirstdownX - event.getX())
							+ (FirstdownY - event.getY())
							* (FirstdownY - event.getY())) < 20
							&& touchtime > 1000) {// 手指没有大幅移动且按下时间大于2秒则进入长按选项
						final String clickID = myView
								.onTouchSetXY(downX, downY);
						if (null != clickID) {
							Log.d("Touch", "点到的ID=" + clickID+"\tparentID="+myView.logicManager.getDomainLogic(clickID).getData().getParentID());
							
							showGroupNames(clickID, myView);
						}
					}
					myView.onChangeComplete();
					break;
				case MotionEvent.ACTION_POINTER_UP:
					// distance(event);
					// distance = 0;
					System.out.println("多指起来");
					TouchMod = -1;
					break;
				default:
					break;
				}
				return true;
			}

			private float distance(MotionEvent event) {
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				float dist = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
				return dist;
			}
		};

		myView.setOnTouchListener(Mytouch);

	}

	/**
	 * 显示群组名字
	 * 
	 * @param clickID
	 *            点到的用户ID
	 */
	private void showGroupNames(final String clickID, final MySurfaceView myView) {
		myView.builder.setMessage("确定查看"
				+ myView.logicManager.getDomainLogic(clickID).getData().key
				+ "的群组？");
		final List<Map<String, String>> itemlist = new ArrayList<Map<String, String>>();
		ArrayList<String> idlist = myView.logicManager.getGroupDomain(clickID);
		for (String id : idlist) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", id);
			map.put("key", myView.logicManager.getDomainLogic(id).getData().key);
			itemlist.add(map);
		}

		final SimpleAdapter simpleAdapter = new SimpleAdapter(this, itemlist,
				R.layout.layout_alertlist, new String[] { "key" },
				new int[] { R.id.text_alertitem });

		OnClickListener myListener = new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Builder listbuilder = new Builder(SurfaceViewMain.this);
				listbuilder.setTitle(myView.logicManager
						.getDomainLogic(clickID).getData().key + "的群组用户");
				listbuilder.setNeutralButton("返回", null);
				listbuilder.setAdapter(simpleAdapter, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String id = itemlist.get(which).get("id");
						final String key = itemlist.get(which).get("key");
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(SurfaceViewMain.this, key,
										Toast.LENGTH_SHORT).show();
							}
						});
					}
				});
				AlertDialog listDialog = listbuilder.create();
				listDialog.show();
				listDialog.getWindow().setLayout(myView.getWidth() * 3 / 4,
						myView.getHeight() * 3 / 5);

			}
		};

		myView.builder.setPositiveButton("确定", myListener);
		myView.builder.setNegativeButton("取消", null);
		myView.builder.create().show();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(DEBUG_TAG, "onresume");
		// myView.myThread.onResume();
		// setContentView(myView);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(DEBUG_TAG, "onpause");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.my_menu, menu);
		menu.getItem(0).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem arg0) {
						// TODO Auto-generated method stub

						final EditText edittext = new EditText(
								SurfaceViewMain.this);
						builder.setView(edittext);
						builder.setTitle("请输入保存截图的文件名");
						builder.setCancelable(true);
						builder.setNegativeButton("取消", null);
						builder.setPositiveButton("确定", new OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								String screenshot_name = null;
								try {
									screenshot_name = edittext.getText()
											.toString();
								} catch (Exception e) {
									// TODO: handle exception
									runOnUiThread(new Runnable() {
										public void run() {
											Toast.makeText(
													SurfaceViewMain.this,
													"请输入正确的文件名！",
													Toast.LENGTH_SHORT).show();
										}
									});
									return;
								}
								if (screenshot_name.equals("")) {
									runOnUiThread(new Runnable() {
										public void run() {
											Toast.makeText(
													SurfaceViewMain.this,
													"请输入文件名！",
													Toast.LENGTH_SHORT).show();
										}
									});
								} else {
									final String str = myView
											.onScreenshot(screenshot_name);
									if (str != null)
										runOnUiThread(new Runnable() {
											public void run() {
												Toast.makeText(
														SurfaceViewMain.this,
														"截图成功！输出文件于：" + str,
														Toast.LENGTH_SHORT)
														.show();
											}
										});
								}
							}
						});

						builder.create().show();// 创建alertdialog

						return false;
					}
				});

		menu.getItem(1).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(SurfaceViewMain.this,
								Viewpager_main.class);
						startActivity(intent);
						return false;
					}
				});

		return true;

	}
}
