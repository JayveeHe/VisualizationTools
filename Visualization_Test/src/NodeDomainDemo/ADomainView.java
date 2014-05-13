package NodeDomainDemo;

import android.graphics.Canvas;

public abstract class ADomainView<D extends ADomainData<?>> {

	/**
	 * 画布对该节点对象的回调函数,进行相关的绘制功能
	 * 
	 * @param canvas
	 *            需要绘制在其上的画布
	 * @param MsgObj
	 *            相应的信息对象(可以为null)
	 */
	public abstract void OnDraw(Canvas canvas, Object MsgObj);
}
