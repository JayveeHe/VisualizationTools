package NodeDomain;

import surfaceview_test.LogicManager;
import android.R.color;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class NodeDomainView {

	NodeDomainData data;
	Paint p = new Paint();
	Paint textpaint = new Paint(Color.BLACK);

	public NodeDomainView(NodeDomainData data) {
		textpaint.setTextSize(18);
		this.data = data;
		p.setColor(data.color);
		// switch (data.group % 7) {
		// case 0:
		// p.setColor(Color.GRAY);
		// break;
		// case 1:
		// p.setColor(Color.GREEN);
		// break;
		// case 2:
		// p.setColor(Color.BLUE);
		// break;
		// case 3:
		// p.setColor(Color.CYAN);
		// break;
		// case 4:
		// p.setColor(Color.RED);
		// break;
		// case 5:
		// p.setColor(Color.YELLOW);
		// break;
		// case 6:
		// p.setColor(Color.DKGRAY);
		// break;
		// default:
		// p.setColor(Color.LTGRAY);
		// }
	}

	/**
	 * 画布对该节点对象的回调函数,进行相关的绘制功能
	 * 
	 * @param canvas
	 *            需要绘制在其上的画布
	 * @param MsgObj
	 *            相应的信息对象(可以为null)
	 */
	public void OnDraw(Canvas canvas, LogicManager logicManager, Object MsgObj) {
		// TODO Auto-generated method stub

		// if (Math.abs((data.getViewX())) > logicManager.iViewWidth
		// || Math.abs((data.getViewY())) > logicManager.iViewHeight) {
		// // Log.d("OnDraw", "有某些点超出了视图！！");
		// return;// 将要绘制的点超出了视图则不绘制
		// }

		// 线段的绘制，只有在移动、缩放结束后才进行绘制
		if (!logicManager.isChanging()) {
			if (data.getParentID() != "-1" && logicManager.fScaleRate > 1.2f)// 即有母节点，且缩放达到某个阈值，则进行线段的绘制
			{
				canvas.drawLine(data.getViewX(), data.getViewY(), logicManager
						.getDomainLogic(data.getParentID()).getData()
						.getViewX(),
						logicManager.getDomainLogic(data.getParentID())
								.getData().getViewY(), p);
			}
		}

		// 节点的绘制
		canvas.drawCircle(data.getViewX(), data.getViewY(), data.getRadius()
				* logicManager.fScaleRate, p);

		// 节点名字的绘制
		if (logicManager.fScaleRate > 3.5)// 缩放达到某个阈值进行名字显示
		{
			canvas.drawText(data.key, data.getViewX(), data.getViewY(),
					textpaint);
		}
		// textpaint.setTextSize(logicManager.fScaleRate*5);
	}
}
