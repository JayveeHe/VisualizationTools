package NodeDomainDemo;

import surfaceview_Demo.LogicManagerDemo;
import android.R.color;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class NodeDomainViewDemo {

	NodeDomainDataDemo data;
	Paint p = new Paint();
	Paint textpaint = new Paint(Color.BLACK);
	public NodeDomainViewDemo(NodeDomainDataDemo data) {
		textpaint.setTextSize(14);
		this.data = data;
//		p.setColor(data.color);
		p.setColor(Color.DKGRAY);
		int id = (int) data.getID();
		switch (id% 7) {
		case 0:
			p.setColor(Color.GRAY);
			break;
		case 1:
			p.setColor(Color.GREEN);
			break;
		case 2:
			p.setColor(Color.BLUE);
			break;
		case 3:
			p.setColor(Color.CYAN);
			break;
		case 4:
			p.setColor(Color.RED);
			break;
		case 5:
			p.setColor(Color.YELLOW);
			break;
		case 6:
			p.setColor(Color.DKGRAY);
			break;
		default:
			p.setColor(Color.DKGRAY);
		}
	}

	/**
	 * 画布对该节点对象的回调函数,进行相关的绘制功能
	 * 
	 * @param canvas
	 *            需要绘制在其上的画布
	 * @param MsgObj
	 *            相应的信息对象(可以为null)
	 */
	public void OnDraw(Canvas canvas, LogicManagerDemo logicManager, Object MsgObj) {
		// TODO Auto-generated method stub

		if (Math.abs((data.getViewX())) > logicManager.iViewWidth
				|| Math.abs((data.getViewY())) > logicManager.iViewHeight) {
			// Log.d("OnDraw", "有某些点超出了视图！！");
			return;// 将要绘制的点超出了视图则不绘制
		}
		if (data.getParentID() != -1)// 即有母节点，则进行线段的绘制
		{
			try {
				
				canvas.drawLine(data.getViewX(), data.getViewY(), logicManager
						.getDomainLogic(data.getParentID()).getData().getViewX(),
						logicManager.getDomainLogic(data.getParentID()).getData()
						.getViewY(), new Paint(Color.BLUE));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		canvas.drawCircle(data.getViewX(), data.getViewY(), data.getRadius()
				* logicManager.fScaleRate, p);
//		if (logicManager.fScaleRate > 3) {
//			canvas.drawText(data.key, data.getViewX(), data.getViewY(),
//					textpaint);
//		}
		// textpaint.setTextSize(logicManager.fScaleRate*5);
	}
}
