package surfaceview_test;

import java.io.File;
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
import android.content.Context;
import android.graphics.*;
import android.net.Uri;
import android.os.Bundle;
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
	InputStream ipstrm=null;
	

	// 相关参数
	int iViewHeight = 0;// 视图高
	int iViewWidth = 0;// 视图宽
	int iViewRow = 15;// 逻辑视图行数
	int iViewCol = 10;// 逻辑视图列数
	int iLogicalRow = 15;
	int iLogicalCol = 10;

	SurfaceHolder holder;
	private MyThread myThread;
	// float x = 0, y = 0;
	// Paint pp = new Paint();
	LogicManager logicManager;

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
	
	public MySurfaceView(Context context,File gexffile)
	{
		super(context);
		// TODO Auto-generated constructor stub
		this.gexffile = gexffile;
		holder = this.getHolder();
		holder.addCallback(this);
		myThread = new MyThread(holder);
		// pp.setColor(Color.RED);
		setFocusable(true);
	}
	public MySurfaceView(Context context,InputStream ipstrm)
	{
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

	public void onTouchSetXY(float x, float y) {
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
					return;
				}

			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Canvas canvas = holder.lockCanvas();
		if (null != canvas) {
			iViewHeight = canvas.getHeight();
			iViewWidth = canvas.getWidth();
			Log.d(DEBUG_TAG, "获取的视图高宽=" + iViewHeight + "x" + iViewWidth);
		}
		holder.unlockCanvasAndPost(canvas);
		logicManager = new LogicManager(iViewHeight, iViewWidth, iViewRow,
				iViewCol);
//		Map<String, WeiboData> map = GexfUtils.gexfDecoder(getResources(),
//				filename + ".gexf");
		Map<String, WeiboData> map = GexfUtils.gexfDecoder(gexffile);
//		Map<String, WeiboData> map = GexfUtils.gexfDecoder(ipstrm);
		ArrayList<NodeDomainLogic> logiclist = NodeDomainLogic
				.creatDomainLogicByMap(map, logicManager);
		for (int i = 0; i < logiclist.size(); i++) {
			logicManager.addDomainLogic(logiclist.get(i));
		}

		myThread.isRun = true;
		myThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		myThread.isRun = false;
	}

	/**
	 * MySurfaceView视图创建完毕的回调函数
	 */
	public void onCompleted() {
		Message msg = new Message();
		// msg.obtain();
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
				try {
					synchronized (holder) {
						canvas = holder.lockCanvas();// 锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。
						canvas.drawColor(Color.WHITE);// 设置画布背景颜色
						// Rect r = new Rect(100, 50, 300, 250);
						// c.drawRect(r, p);
						// c.drawText("这是第" + (count++) + "秒", 100, 310, p);
						// logicManager.FDA();
						// logicManager.onOverallUpdate();
						if (logicManager != null)
							for (NodeDomainLogic logic : logicManager
									.getNodesMap().values()) {
								// NodeDomainData data = logic.getData();
								// data.onModify(NodeDomainData.MODIFY_X,
								// data.getCurX()
								// + data.Xdiffer, logicManager);
								// data.onModify(NodeDomainData.MODIFY_Y,
								// data.getCurY()
								// + data.Ydiffer, logicManager);
								// data.Xdiffer = 0;
								// data.Ydiffer = 0;// FDA偏移量每次计算过后就清零
								// data.onCalculateViewXY(logicManager);
								// data.onLocationChanged(logicManager);
								// 遍历整个逻辑表
								logic.getView().OnDraw(canvas, logicManager,
										null);
							}
						canvas.drawText("共绘制" + logicManager.NodesMap.size()
								+ "个点", 0, 50, p);
						canvas.drawText("点到了" + clickname, 0, 20, p);

						// screenDrawLogic.getDomainLogic(123).getView().OnDraw(c,
						// null);
						// c.drawCircle(x, y, 30, pp);
						// Thread.sleep(1000);// 睡眠时间为1秒
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				} finally {// 无论如何都要提交
					if (canvas != null) {
						holder.unlockCanvasAndPost(canvas);// 结束锁定画图，并提交改变。

					}
				}
			}
		}
	}

}
