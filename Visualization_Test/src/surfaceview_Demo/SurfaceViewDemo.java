package surfaceview_Demo;

import java.util.ArrayList;

import JsonUtilsDemo.JsonUtils;
import JsonUtilsDemo.WeiboData;
import NodeDomainDemo.ADomainData;
import NodeDomainDemo.ADomainLogic;
import NodeDomainDemo.ADomainView;
import NodeDomainDemo.NodeDomainLogicDemo;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;

public class SurfaceViewDemo extends Activity {

	float downX = 0;
	float downY = 0;
	float FirstdownX = 0;
	float FirstdownY = 0;
	float scaleRate = 1;
	float distance = 0;
	int TouchMod = 0;
	final static int TOUCH_MOD_DRAG = 1;
	final static int TOUCH_MOD_ZOOM = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		final MySurfaceViewDemo myView = new MySurfaceViewDemo(this);
		setContentView(myView);
		
		// test

		// myView.setOnTouchListener(new MyOnTouchListener());
		// myView.screenDrawLogic.addDomainLogic(nodeDomainLogic);

		// OnTouchListener mytouch = new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// // TODO Auto-generated method stub
		// System.out.println("触控点数" + event.getPointerCount());
		// // event.getp
		// switch (event.getAction()) {
		// case MotionEvent.ACTION_DOWN:
		// downX = event.getX();
		// downY = event.getY();
		// System.out.println("测试长按" + "按下！" + downX + "=" + downY);
		// myView.onTouchSetXY(downX, downY);
		// break;
		// case MotionEvent.ACTION_MOVE:
		// // System.out.println("测试长按" + "移动！");
		// break;
		// case MotionEvent.ACTION_UP:
		// float xx = event.getX() - downX;
		// float yy = event.getY() - downY;
		// myView.onDrag(xx, yy);
		// System.out.println("测试长按" + "起来！");
		// break;
		// default:
		// break;
		// }
		//
		// return true;
		// }
		// };

		OnTouchListener Mytouch = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					System.out.println("单指按下");
					System.out.println(event.getX(0));
					downX = event.getX();
					downY = event.getY();
					FirstdownX = event.getX();
					FirstdownY = event.getY();

					TouchMod = TOUCH_MOD_DRAG;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					System.out.println("多指按下");
					System.out.println(event.getX(0) + "++++" + event.getX(1));
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
//						myView.logicManager.onOverallUpdate();
					} else if (TouchMod == TOUCH_MOD_ZOOM) {
						// System.out.println("两指距离=" + distance(event));
						myView.logicManager.fScaleRate = myView.logicManager.fScaleRate
								* distance(event) / distance;
						// myView.onScale(scaleRate);
						myView.logicManager.onOverallUpdate();
						distance = distance(event);
					}
					break;
				case MotionEvent.ACTION_UP:
					TouchMod = 0;
					distance = 0;
					System.out.println("单指起来");
					if (Math.sqrt((FirstdownX - event.getX())
							* (FirstdownX - event.getX())
							+ (FirstdownY - event.getY())
							* (FirstdownY - event.getY())) < 20) {
						myView.onTouchSetXY(downX, downY);
					}
					myView.logicManager.onOverallUpdate();
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
				float ssss = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
				return ssss;
			}
		};

		myView.setOnTouchListener(Mytouch);

		// myView.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// // TODO Auto-generated method stub
		// if (event.getAction() == MotionEvent.ACTION_DOWN) {
		// myView.onTouchSetXY(event.getX(), event.getY());
		// }
		// return false;
		// }
		// });

	}
}
