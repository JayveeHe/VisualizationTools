package NodeDomainDemo;

import java.util.ArrayList;
import java.util.Random;

import surfaceview_Demo.LogicManagerDemo;
import android.util.Log;

public class NodeDomainDataDemo {
	private long ID;
	private long ParentID = -1;
	private ArrayList<Long> ChildIDs;
	protected Class<?> Obj;
	private float fCurX = 0;// XY为绘制在视图上的坐标,需要通过下面的
	private float fCurY = 0;
	private float fViewX = 0;// 该点在视图上的坐标
	private float fViewY = 0;
	private float fRadius = 25;// 该节点自己的圆半径
	private float fLen = 80;// 该节点与母节点的距离
	private float fAngle = (float) (Math.PI * 2);// 该节点相对于母节点与法线的角度
	public int color;
	public int group;
	public String key;

	public int iLastRow = 0;
	public int iLastCol = 0;

	public float Xdiffer = 0;// FDA算法在X坐标上的修正量
	public float Ydiffer = 0;

	public final static String DEBUG_TAG = "NodeDomainData";

	public final static int MODIFY_X = 10001;
	public final static int MODIFY_Y = 10002;
	public final static int MODIFY_RADIUS = 10003;
	public final static int MODIFY_LEN = 10004;
	public final static int MODIFY_ANGLE = 10005;

	public NodeDomainDataDemo(long ID, ArrayList<Long> ChildIDs,
			LogicManagerDemo logicManager) {
		this.ID = ID;
		this.setChildIDs(ChildIDs);
	}

	public NodeDomainDataDemo(long ID, long ParentID, ArrayList<Long> ChildIDs,
			LogicManagerDemo logicManager) {
		this.ID = ID;
		this.ParentID = ParentID;
		this.setChildIDs(ChildIDs);
		Random r = new Random();
		if (ParentID != -1) {
			this.fRadius = (float) (logicManager.getDomainLogic(ParentID)
					.getData().getRadius() * 0.8);
			this.fCurX = logicManager.getDomainLogic(ParentID).getData()
					.getCurX()
					+ r.nextFloat() * 10 + 1;
			this.fCurY = logicManager.getDomainLogic(ParentID).getData()
					.getCurY()
					+ r.nextFloat() * 10 + 1;
		}
	}

	/**
	 * 指定某个变量进行修改
	 * 
	 * @param ModifyKey
	 *            需要修改的指定变量类型，使用该类的静态常量
	 * @param value
	 *            修改成的值
	 * @param logicManager
	 *            外部逻辑控制器
	 */
	public void onModify(int ModifyKey, float value, LogicManagerDemo logicManager) {
		// TODO Auto-generated method stub
		switch (ModifyKey) {
		case MODIFY_X:
			this.fCurX = value;
			break;
		case MODIFY_Y:
			this.fCurY = value;
			break;
		case MODIFY_RADIUS:
			this.fRadius = value;
			break;
		case MODIFY_LEN:
			this.fLen = value;
			break;
		case MODIFY_ANGLE:
			this.fAngle = value;
			break;
		default:
			Log.d(DEBUG_TAG, "modify无效,没有填入适当的Key值");
			break;
		}
		onLocationChanged(logicManager);
	}

	// 此处有数组越界问题！！！
	@SuppressWarnings(value = { "logicManager.drawingMatrixs" })
	public void onLocationChanged(LogicManagerDemo logicManager) {
		logicManager.drawingMatrixs[this.iLastRow][this.iLastCol]
				.deleteLocation(this.ID);
		int tempCol, tempRow;
		tempCol = (int) (fViewX / logicManager.fGridWidth);
		tempRow = (int) (fViewY / logicManager.fGridHeight);
		if (tempCol >= logicManager.iViewCol || tempCol < 0
				|| tempRow >= logicManager.iViewRow || tempRow < 0)// 即数组越界
		{
			// Log.e(DEBUG_TAG, "drawingMatrixs数组越界！");
			return;
		}
		this.iLastRow = tempRow;
		this.iLastCol = tempCol;
		logicManager.drawingMatrixs[this.iLastRow][this.iLastCol]
				.setLocation(this.ID);
	}

	public void onRefresh(float fCurX, float fCurY, float fRadius, float fLen,
			float fAngle) {
		// TODO Auto-generated method stub
		this.fCurX = fCurX;
		this.fCurY = fCurY;
		this.fRadius = fRadius;
		this.fLen = fLen;
		this.fAngle = fAngle;
	}

	/**
	 * 通过已有的信息重新计算逻辑XY值
	 * 
	 * @param logicManager
	 */
	public void onCalculateViewXY(LogicManagerDemo logicManager) {
		this.fViewX = (fCurX * logicManager.fScaleRate + logicManager.fXOffset);
		this.fViewY = (fCurY * logicManager.fScaleRate + logicManager.fYOffset);
		// onLocationChanged(logicManager);
	}

	public float getRadius() {
		return fRadius;
	}

	public float getLen() {
		return fLen;
	}

	public float getAngle() {
		return fAngle;
	}

	public float getCurX() {
		return fCurX;
	}

	public float getCurY() {
		return fCurY;
	}

	public float getViewX() {
		return fViewX;
	}

	public float getViewY() {
		return fViewY;
	}

	public long getID() {
		return ID;
	}

	public long getParentID() {
		return ParentID;
	}

	public ArrayList<Long> getChildIDs() {
		return ChildIDs;
	}

	public void setChildIDs(ArrayList<Long> childIDs) {
		ChildIDs = childIDs;
	}

	public void addChildID(long ID) {
		if (!ChildIDs.contains(ID))// 若子节点ID中还没有该ID则添加，否则不操作
		{
			ChildIDs.add(ID);
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "节点ID=" + this.ID + "\t母节点ID=" + this.ParentID + "\n距离len="
				+ this.fLen;
	}

}
