package surfaceview_Main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ui.SpreadNaviActivity;
import ui.SpreadTimelineActivity;
import ui.ViewpagerActivity;
import jayvee.visualization_weibo.R;
import NodeDomain.NodeDomainData;
import NodeDomain.NodeDomainLogic;
import android.R.color;
//import jayvee.visualization_test.R
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
	private Builder readmeBuilder;
	public static String scrsFileRootPath;
	private static final String DEBUG_TAG = "SurfaceViewMain";

	String filename;
	String filepath;
	String source;
	boolean isSpread;// 是否用于显示传播图
	private long touchtime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		filename = bundle.getString("filename");
		filepath = bundle.getString("filepath");
		source = bundle.getString("source");
		isSpread = bundle.getBoolean("isSpread");
		// final MySurfaceView myView = new MySurfaceView(this,filename);
		File gexffile = new File(filepath);
		scrsFileRootPath = Environment.getExternalStorageDirectory()
				+ "/数据可视化截图/";
		File file = new File(scrsFileRootPath);
		if (!file.exists())
			file.mkdir();
		if (source.equals("SpreadNavi")) {
			myView = new MySurfaceView(this, SpreadNaviActivity.info_map);
		} else {
			myView = new MySurfaceView(this, gexffile);
		}
		setContentView(myView);

		// 对话框设置
		builder = new Builder(this);
		readmeBuilder = new Builder(SurfaceViewMain.this);
		// 说明对话框
		if (source.equals("list")) {
			readmeBuilder
					.setMessage("本界面展示的是该用户的好友关系图，关系密切的好友会聚在一起并用相同的颜色表示。使用手势进行缩放、拖动，长按图上某点可以查看该点的群组用户名称，点击名称可以定位至指定用户处。更多功能请点击“菜单键”查看。");
		} else {
			// 若不是从ListActivity而来则不是好友聚类图
			readmeBuilder
					.setMessage("本界面展示的是该微博的传播图，每一个点代表一个转发用户，以颜色区分转发源。使用手势进行缩放、拖动，长按图上某点可以查看该点的群组用户名称，点击名称可以定位至指定用户处。更多功能请点击“菜单键”查看。");
		}
		TextView text_readme = new TextView(SurfaceViewMain.this);
		text_readme.setText("使用说明");
		text_readme.setTextSize(26);
		text_readme.setTextColor(Color.parseColor("#33B5E5"));
		text_readme.setGravity(Gravity.CENTER_HORIZONTAL);
		readmeBuilder.setCustomTitle(text_readme);
		readmeBuilder.setCancelable(true);
		readmeBuilder.setNeutralButton("返回", null);
		if (!isSpread)
			readmeBuilder.create().show();

		// 手势设置
		OnTouchListener Mytouch = new OnTouchListener() {
			float midX = 0, midY = 0;

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

					midX = ((event.getX(0) + event.getX(1)) / 2 - myView.logicManager.fXOffset)
							/ myView.logicManager.fScaleRate;
					midY = ((event.getY(0) + event.getY(1)) / 2 - myView.logicManager.fYOffset)
							/ myView.logicManager.fScaleRate;
					myView.logicManager.fstartmidLogicX = midX;
					myView.logicManager.fstartmidLogicY = midY;
					// myView.logicManager.onStartZoom();
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
						float TouchViewmidX = (event.getX(0) + event.getX(1)) / 2;
						float TouchViewmidY = (event.getY(0) + event.getY(1)) / 2;

						myView.logicManager.onScaleLocated(midX, midY,
								TouchViewmidX, TouchViewmidY);

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
							Log.d("Touch",
									"点到的ID="
											+ clickID
											+ "\tparentID="
											+ myView.logicManager
													.getDomainLogic(clickID)
													.getData().getParentID());

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
						final String id = itemlist.get(which).get("id");
						final String key = itemlist.get(which).get("key");
						runOnUiThread(new Runnable() {
							public void run() {
								NodeDomainData data = myView.logicManager
										.getDomainLogic(id).getData();
								// 首先设置一个定位后的缩放比例，尽量大一些，让定位点更显眼
								myView.logicManager.fScaleRate = 12f;
								myView.logicManager.onScaleLocated(
										data.getCurX(), data.getCurY(),
										myView.getWidth() / 2,
										myView.getHeight() / 2);
								myView.logicManager.onViewRefresh();
								Toast.makeText(SurfaceViewMain.this, key,
										Toast.LENGTH_SHORT).show();
								myView.logicManager.setChanging(false);
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
		if (!source.equals("fragment")) {
			getMenuInflater().inflate(R.menu.my_menu, menu);
			menu.getItem(0).setOnMenuItemClickListener(screenshotListener);
			menu.getItem(1).setOnMenuItemClickListener(paramsListener);
			menu.getItem(2).setOnMenuItemClickListener(readmeListener);
			menu.getItem(3).setOnMenuItemClickListener(searchListener);
		} else {
			getMenuInflater().inflate(R.menu.menu_repost, menu);
			menu.getItem(0).setOnMenuItemClickListener(screenshotListener);
			menu.getItem(1).setOnMenuItemClickListener(searchListener);
		}

		return true;
	}

	// 监听器设置
	// 截图监听器
	OnMenuItemClickListener screenshotListener = new OnMenuItemClickListener() {
		@Override
		public boolean onMenuItemClick(MenuItem arg0) {

			final EditText edittext = new EditText(SurfaceViewMain.this);
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
						screenshot_name = edittext.getText().toString();
					} catch (Exception e) {
						// TODO: handle exception
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(SurfaceViewMain.this,
										"请输入正确的文件名！", Toast.LENGTH_SHORT)
										.show();
							}
						});
						return;
					}
					if (screenshot_name.equals("")) {
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(SurfaceViewMain.this, "请输入文件名！",
										Toast.LENGTH_SHORT).show();
							}
						});
					} else {
						final String str = myView.onScreenshot(screenshot_name);
						if (str != null)
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(SurfaceViewMain.this,
											"截图成功！输出文件于：" + str,
											Toast.LENGTH_SHORT).show();
								}
							});
					}
				}
			});

			builder.create().show();// 创建alertdialog
			return false;
		}
	};

	// 属性参数监听器
	OnMenuItemClickListener paramsListener = new OnMenuItemClickListener() {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			// TODO Auto-generated method stub
			if (!source.equals("SpreadNavi")) {
				Intent intent = new Intent(SurfaceViewMain.this,
						ViewpagerActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("userName", filename.replaceAll(".gexf", ""));
				intent.putExtras(bundle);
				startActivity(intent);
			} else {
				// 从传播分析而来
				Intent intent = new Intent(SurfaceViewMain.this,
						SpreadTimelineActivity.class);
				Bundle bundle = new Bundle();
				// bundle.putString("userName",
				// filename.replaceAll(".gexf", ""));
				intent.putExtras(bundle);
				startActivity(intent);
			}
			return false;
		}
	};

	// 说明监听器
	OnMenuItemClickListener readmeListener = new OnMenuItemClickListener() {

		@Override
		public boolean onMenuItemClick(MenuItem arg0) {

			// 对话框设置
			readmeBuilder = new Builder(SurfaceViewMain.this);
			// 说明对话框
			if (source.equals("list")) {
				readmeBuilder
						.setMessage("本界面展示的是该用户的好友关系图，关系密切的好友会聚在一起并用相同的颜色表示。使用手势进行缩放、拖动，长按图上某点可以查看该点的群组用户名称，点击名称可以定位至指定用户处。更多功能请点击“菜单键”查看。");
			} else {
				// 若不是从ListActivity而来则不是好友聚类图
				readmeBuilder
						.setMessage("本界面展示的是该微博的传播图，每一个点代表一个转发用户，以颜色区分转发源。使用手势进行缩放、拖动，长按图上某点可以查看该点的群组用户名称，点击名称可以定位至指定用户处。更多功能请点击“菜单键”查看。");
			}
			TextView text_readme = new TextView(SurfaceViewMain.this);
			text_readme.setText("使用说明");
			text_readme.setTextSize(26);
			text_readme.setTextColor(Color.parseColor("#33B5E5"));
			text_readme.setGravity(Gravity.CENTER_HORIZONTAL);
			readmeBuilder.setCustomTitle(text_readme);
			readmeBuilder.setCancelable(true);
			readmeBuilder.setNeutralButton("返回", null);
			readmeBuilder.show();
			return false;
		}
	};

	// 搜索监听器
	OnMenuItemClickListener searchListener = new OnMenuItemClickListener() {

		@Override
		public boolean onMenuItemClick(MenuItem arg0) {
			// TODO Auto-generated method stub
			final EditText edittext = new EditText(SurfaceViewMain.this);
			builder.setView(edittext);
			builder.setTitle("请输入想查找的名字");
			builder.setCancelable(true);
			builder.setNegativeButton("取消", null);
			builder.setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					String search_name = null;
					try {
						search_name = edittext.getText().toString();
					} catch (Exception e) {
						// TODO: handle exception
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(SurfaceViewMain.this,
										"请输入正确的节点名！", Toast.LENGTH_SHORT)
										.show();
							}
						});
						return;
					}
					if (search_name.equals("")) {
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(SurfaceViewMain.this, "请输入节点名！",
										Toast.LENGTH_SHORT).show();
							}
						});
					} else {
						Map<String, NodeDomainLogic> namesMap = myView.logicManager.NamesMap;
						if (!namesMap.containsKey(search_name)) {
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(SurfaceViewMain.this,
											"找不到相应的节点！", Toast.LENGTH_SHORT)
											.show();
								}
							});
						} else {
							NodeDomainLogic search_logic = namesMap
									.get(search_name);
							// 首先设置一个定位后的缩放比例，尽量大一些，让定位点更显眼
							myView.logicManager.fScaleRate = 12f;
							myView.logicManager.onScaleLocated(search_logic
									.getData().getCurX(), search_logic
									.getData().getCurY(),
									myView.getWidth() / 2,
									myView.getHeight() / 2);
							myView.logicManager.onViewRefresh();
							Toast.makeText(SurfaceViewMain.this, search_name,
									Toast.LENGTH_SHORT).show();
							myView.logicManager.setChanging(false);
						}
					}
				}
			});
			builder.create().show();
			return false;
		}
	};
}
