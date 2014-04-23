package surfaceview_test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import JsonUtils.JsonUtils;
import JsonUtils.WeiboData;
import NodeDomain.FriendNodes;
import NodeDomain.NodeDomainData;
import NodeDomain.NodeDomainLogic;
import Utils.GexfUtils;
import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.*;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class MySurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {
	final static String DEBUG_TAG = "MySurfaceView";

	String filename = "";
	String clickname = "";
	File gexffile = null;
	InputStream ipstrm = null;
	private boolean bScreenshot = false;
	String screenshot_name = "";
	// 相关参数
	int iViewHeight = 0;// 视图高
	int iViewWidth = 0;// 视图宽
	int iViewRow = 15;// 逻辑视图行数
	int iViewCol = 10;// 逻辑视图列数
	int iLogicalRow = 15;
	int iLogicalCol = 10;

	SurfaceHolder holder;
	protected MyThread myThread;
	private Canvas canvas;
	// float x = 0, y = 0;
	// Paint pp = new Paint();
	LogicManager logicManager;
	Builder builder;

	public MySurfaceView(Context context, String filename) {
		super(context);
		// TODO Auto-generated constructor stub
		this.filename = filename;
		holder = this.getHolder();
		holder.addCallback(this);
		myThread = new MyThread(holder);
		// pp.setColor(Color.RED);
		setFocusable(true);
	}

	public MySurfaceView(Context context, File gexffile) {
		super(context);
		// TODO Auto-generated constructor stub
		this.gexffile = gexffile;
		holder = this.getHolder();
		holder.addCallback(this);
		// pp.setColor(Color.RED);
		setFocusable(true);
	}

	public MySurfaceView(Context context, InputStream ipstrm) {
		super(context);
		// TODO Auto-generated constructor stub
		this.ipstrm = ipstrm;
		holder = this.getHolder();
		holder.addCallback(this);
		myThread = new MyThread(holder);
		// pp.setColor(Color.RED);
		setFocusable(true);
	}

	public void onDrag(float Xoffset, float Yoffset) {
		logicManager.fXOffset += Xoffset;
		logicManager.fYOffset += Yoffset;
		// logicManager.onOverallUpdate();
		logicManager.onViewRefresh();
	}

	public void onChangeComplete() {
		logicManager.onOverallUpdate();
	}

	public String onTouchSetXY(float x, float y) {
		System.out.println("xy=" + x + "===" + y);
		List<String> IDs = logicManager.transXY2CR(x, y).getLocatedIDs();
		System.out.println("附近的ID数" + IDs.size());

		if (IDs.size() != 0) {
			for (String id : IDs) {
				System.out.println("ID=" + id);
				NodeDomainLogic domainLogic = logicManager.NodesMap.get(id);
				float tempX = domainLogic.getData().getCurX();
				float tempY = domainLogic.getData().getCurY();
				double t = Math.pow((x - tempX), 2f);
				double k = Math.pow((y - tempY), 2f);
				double kk = Math.cbrt(t + k);
				Random r = new Random();
				// if (kk
				// <domainLogic.getData().getRadius()*logicManager.fScaleRate)
				if (kk <= ((logicManager.fGridWidth + logicManager.fGridHeight))) {
					Log.d(DEBUG_TAG, "点到了某个点");
					clickname = logicManager.getDomainLogic(id).getData().key;
					return id;
				}

			}
		}
		return null;
	}

	/**
	 * 进行当前画布截图并保存
	 * 
	 * @param screenshot_name
	 *            保存的文件路径
	 * @return 保存的路径位置
	 */
	protected String onScreenshot(String screenshot_name) {
		setbScreenshot(true);
		this.screenshot_name = screenshot_name;
		return (SurfaceViewMain.scrsFileRootPath + screenshot_name + ".png");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		this.canvas = holder.lockCanvas();
		if (null != canvas) {
			iViewHeight = canvas.getHeight();
			iViewWidth = canvas.getWidth();
			Log.d(DEBUG_TAG, "获取的视图高宽=" + iViewHeight + "x" + iViewWidth);
		}
		holder.unlockCanvasAndPost(canvas);
		logicManager = new LogicManager(iViewHeight, iViewWidth, iViewRow,
				iViewCol);
		Map<String, WeiboData> map = GexfUtils.gexfDecoder(gexffile);
		ArrayList<NodeDomainLogic> logiclist = NodeDomainLogic
				.creatDomainLogicByMap(map, logicManager);
		for (int i = 0; i < logiclist.size(); i++) {
			logicManager.addDomainLogic(logiclist.get(i));
		}
		myThread = new MyThread(holder);
		builder = new Builder(getContext());
		myThread.isRun = true;
		myThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		myThread.isRun = false;
		Log.d(DEBUG_TAG, "surfacedestroyed");
	}

	/**
	 * ` MySurfaceView视图创建完毕的回调函数
	 */
	public void onCompleted() {
//		Message msg = new Message();
	}

	public boolean isbScreenshot() {
		return bScreenshot;
	}

	public void setbScreenshot(boolean bScreenshot) {
		this.bScreenshot = bScreenshot;
	}

	// 线程内部类
	class MyThread extends Thread {
		private SurfaceHolder holder;
		public boolean isRun;

		public MyThread(SurfaceHolder holder) {
			this.holder = holder;
			isRun = true;
		}

		@Override
		public void run() {
			// int count = 0;
			Paint p = new Paint(); // 创建画笔
			p.setColor(Color.BLUE);
			p.setTextSize(20);
			logicManager.fXOffset = logicManager.iViewWidth / 2;
			logicManager.fYOffset = logicManager.iViewHeight / 2;
			logicManager.onOverallUpdate();
			while (isRun) {
				Canvas canvas = null;
				boolean bCanvasLock = true;
				try {
					synchronized (holder) {
						// 截图相关
						Bitmap bitmap = null;
						if (isbScreenshot()) {
							setbScreenshot(false);
							bCanvasLock = false;
							bitmap = Bitmap.createBitmap(getWidth(),
									getHeight(), Bitmap.Config.ARGB_8888);
							canvas = new Canvas(bitmap);
						} else
							canvas = holder.lockCanvas();// 锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。
						canvas.drawColor(Color.WHITE);// 设置画布背景颜色
						if (logicManager != null)
							for (NodeDomainLogic logic : logicManager
									.getNodesMap().values()) {
								// 遍历整个逻辑表
								logic.getView().OnDraw(canvas, logicManager,
										null);
							}
						canvas.drawText("文件名：" + gexffile.getName() + "\n共绘制"
								+ logicManager.NodesMap.size() + "个点", 0, 20, p);

						
						// 截图相关
						if (null != bitmap) {
							String strFilePath;
							buildDrawingCache();
							System.out.println("截图：" + bitmap);
							bitmap.compress(
									CompressFormat.PNG,
									100,
									new FileOutputStream(
											strFilePath = SurfaceViewMain.scrsFileRootPath
													+ screenshot_name + ".png"));
							System.out.println("输出图片" + strFilePath);
							bitmap = null;
						}

					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				} finally {// 无论如何都要提交
					if (canvas != null && bCanvasLock) {
						holder.unlockCanvasAndPost(canvas);// 结束锁定画图，并提交改变。

					}
				}
			}
		}
	}

}
