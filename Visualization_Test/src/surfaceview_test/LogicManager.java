package surfaceview_test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ForceDirect.ForceDirectMethod;
import NodeDomain.FriendNodes;
import NodeDomain.ADomainData;
import NodeDomain.ADomainLogic;
import NodeDomain.NodeDomainData;
import NodeDomain.NodeDomainLogic;
import android.util.Log;

public class LogicManager {
	// 相关的变量设置
	public final static String DEBUG_TAG = "LogicManager";
	public DrawingMatrix[][] drawingMatrixs = null;
	Map<Long, NodeDomainLogic> NodesMap = null;
	ArrayList<NodeDomainLogic> AtomNodeLogics = null;
	// 相关参数
	public int iViewHeight = 0;// 视图高
	public int iViewWidth = 0;// 视图宽
	public int iViewRow = 0;// 逻辑视图行数
	public int iViewCol = 0;// 逻辑视图列数

	public float fXOffset = 0;// X坐标的偏移量
	public float fYOffset = 0;// Y坐标的偏移量
	public float fScaleRate = 1.4f;// 缩放比例

	public float fGridHeight;// 逻辑一格的高度(像素)
	public float fGridWidth;// 逻辑一格的宽度(像素)

	/**
	 * LogicManger的构造函数,请务必在View创建完成后调用!
	 * 
	 * @param iViewHeight
	 * @param iViewWidth
	 * @param iViewRow
	 * @param iViewCol
	 */
	public LogicManager(int iViewHeight, int iViewWidth, int iViewRow,
			int iViewCol) {
		this.iViewHeight = iViewHeight;
		this.iViewWidth = iViewWidth;
		this.iViewRow = iViewRow;
		this.iViewCol = iViewCol;
		try {
			this.fGridHeight = iViewHeight / iViewRow;
			this.fGridWidth = iViewWidth / iViewCol;
		} catch (ArithmeticException e) {
			Log.e(DEBUG_TAG, e.toString());
		}
		Log.d(DEBUG_TAG, "获取的视图参数:" + this.toString());
		NodesMap = new HashMap<Long, NodeDomainLogic>();
		AtomNodeLogics = new ArrayList<NodeDomainLogic>();
		drawingMatrixs = new DrawingMatrix[iViewRow][iViewCol];
		for (int i = 0; i < iViewRow; i++)
			for (int j = 0; j < iViewCol; j++) {
				try {
					drawingMatrixs[i][j] = new DrawingMatrix();// 屏幕二维数组的初始化
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		Log.d(DEBUG_TAG, "屏幕数组初始化完成");
	}

	// //////////////////////////////////////////////////////
	// /FDA算法
	public void FDA() {
		ForceDirectMethod FDAmethod = new ForceDirectMethod(this);
		for (NodeDomainLogic logic_i : NodesMap.values()) {
			// 依次计算逻辑点j对逻辑点i的坐标影响
			float xi = logic_i.getData().getCurX();
			float yi = logic_i.getData().getCurY();
			for (NodeDomainLogic logic_j : NodesMap.values()) {// 进行全节点的遍历
				if (logic_i != logic_j) {
					float AttractiveForce = 0;
					float RepulsiveForce = 0;
					float xj = logic_j.getData().getCurX();
					float yj = logic_j.getData().getCurY();
					float distance = (float) Math.sqrt((xi - xj) * (xi - xj)
							+ (yi - yj) * (yi - yj));
					float angle = (float) Math.atan2(yi - yj, xi - xj);
					System.out.println("两点距离=" + distance);
					// atan2(y,x)返回的是y,x点与0,0连线与X轴的夹角（-pi,pi）
					if (logic_j.getID() == logic_i.getData().getParentID()
							|| logic_i.getData().getChildIDs()
									.contains(logic_j.getID()))
					// 判断i,j两点是否有关系
					{
						AttractiveForce = FDAmethod.F_attractive(distance);// 相关节点间引力大小
						logic_i.getData().Xdiffer += AttractiveForce
								* Math.cos(angle - Math.PI);
						logic_i.getData().Ydiffer += AttractiveForce
								* Math.sin(angle - Math.PI);

					}
					// if (distance > 1000)
					// continue;//距离超过一定值则忽略斥力作用
					RepulsiveForce = FDAmethod.F_repulsive(distance);// 不相关节点间斥力大小
					logic_i.getData().Xdiffer += RepulsiveForce
							* Math.cos(angle);
					logic_i.getData().Ydiffer += RepulsiveForce
							* Math.sin(angle);
				}
			}
			// System.out.println("X变动=" + logic_i.getData().Xdiffer);
			// System.out.println("Y变动=" + logic_i.getData().Ydiffer + "\n坐标："
			// + logic_i.getData().getCurX() + "="
			// + logic_i.getData().getCurY());
		}
	}

	/**
	 * 从逻辑管理器中,以ID获取逻辑对象
	 * 
	 * @param ID
	 *            待获取对象的ID
	 * @return 待获取的逻辑
	 */
	public NodeDomainLogic getDomainLogic(long ID) {
		return NodesMap.get(ID);
	}

	public void addDomainLogic(NodeDomainLogic domainLogic) {
		try {
			NodesMap.put(domainLogic.getID(), domainLogic);
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(DEBUG_TAG, e.toString());
		}
		NodeDomainData data = domainLogic.getData();
//		DrawingMatrix dm = transXY2CR(data.getCurX(), data.getCurY());
		// if(dm)
		try {
			transXY2CR(data.getCurX(), data.getCurY())
					.setLocation(data.getID());
		} catch (NullPointerException e) {
			Log.e(DEBUG_TAG, "addDomainlogic时发生错误");
		}
		AtomNodeLogics.add(domainLogic);
	}

	/**
	 * 为指定的母节点添加一个子节点
	 * 
	 * @param ParentLogic
	 * @param ChildLogic
	 */
	public void addChildDomainLogic(NodeDomainLogic ParentLogic,
			NodeDomainLogic ChildLogic) {
		// 首先修改母节点的距离信息,只有在母节点第一次添加子节点时调用
		if (!ParentLogic.isExpand()) {
			ParentLogic.getData().onModify(NodeDomainData.MODIFY_LEN,
					ParentLogic.getData().getLen() * 2, this);
			ParentLogic.setExpand(true);
		}
		try {
			// 在逻辑控制器中注册子节点逻辑
			NodesMap.put(ChildLogic.getID(), ChildLogic);
			// 然后在母节点的data对象中添加该子节点ID，确定关系
			ParentLogic.getData().addChildID(ChildLogic.getID());
			ChildLogic.getData().onModify(NodeDomainData.MODIFY_LEN,
					ParentLogic.getData().getLen() * 3 / 8, this);
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(DEBUG_TAG, e.toString());
		}

	}

	public void removeDomainLogic(NodeDomainLogic domainLogic) {
		try {
			NodeDomainData data = domainLogic.getData();
			transXY2CR(data.getCurX(), data.getCurY()).deleteLocation(
					data.getID());
			NodesMap.remove(domainLogic.getID());
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(DEBUG_TAG, e.toString());
		}
	}

	/**
	 * 从一个指定的母节点更新节点信息,并非每次都调用,只有在进行缩放、位移、节点增减时进行调用 有可能需要递归调用
	 * 
	 * @param parentNodeLogic
	 *            母节点的逻辑对象
	 */
	public void onUpdate(NodeDomainLogic parentNodeLogic) {
	}

	/**
	 * 全局更新函数，从元节点开始遍历更新信息，确保全图节点信息都更新到。 一般在进行位移、缩放操作后进行。
	 * 
	 * @param AtomLogicSet
	 *            元节点逻辑对象的集合
	 */
	public void onOverallUpdate() {
		for (NodeDomainLogic logic : NodesMap.values()) {
			NodeDomainData data = logic.getData();
			data.onModify(NodeDomainData.MODIFY_X, data.getCurX()
					+ data.Xdiffer, this);
			data.onModify(NodeDomainData.MODIFY_Y, data.getCurY()
					+ data.Ydiffer, this);
			data.Xdiffer = 0;
			data.Ydiffer = 0;// FDA偏移量每次计算过后就清零
			data.onCalculateViewXY(this);
			data.onLocationChanged(this);

		}
		Log.d(DEBUG_TAG, "全局更新完毕");
	}

	public Map<Long, NodeDomainLogic> getNodesMap() {
		return NodesMap;
	}

	// public void deleteDomainLogic(NodeDomainLogic domainLogic) {
	// try {
	// NodesMap.remove(domainLogic.getID());
	// } catch (Exception e) {
	// Log.e(DEBUG_TAG, "删除对象异常!");
	// }
	// }

	/**
	 * 将像素单位的坐标转换为行列单位的坐标并返回相应逻辑方块的引用
	 * 
	 * @param fX
	 * @param fY
	 * @return 相应逻辑方块的引用
	 */
	public DrawingMatrix transXY2CR(float fX, float fY) {
		int iRow, iCol;
		iCol = (int) (fX / fGridWidth);
		iRow = (int) (fY / fGridHeight);
		if (iCol >= this.iViewCol || iCol < 0 || iRow >= this.iViewRow
				|| iRow < 0)// 即数组越界
		{
			Log.e(DEBUG_TAG, "drawingMatrixs数组越界！");
			return null;
		}
		return drawingMatrixs[iRow][iCol];
	}

	public class DrawingMatrix {
		List<Long> NodeObjectID = new ArrayList<Long>();

		public void setLocation(long ID)// 设置进入这个区域的对象ID
		{
			if (!NodeObjectID.contains(ID)) {
				this.NodeObjectID.add(ID);
			}
		}

		public List<Long> getLocatedIDs() {
			return NodeObjectID;
		}

		public void deleteLocation(long ID)// 删除已经离开该区域的对象ID
		{
			for (int i = 0; i < NodeObjectID.size(); i++) {
				if (ID == NodeObjectID.get(i)) {
					NodeObjectID.remove(i);
					return;// 及时退出（因为没有寻找的必要了）
				}
			}
			Log.e(DEBUG_TAG, "移除对象ID时未发现该ID!");
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "视图高度:" + iViewHeight + "\t视图宽度:" + iViewWidth + "\n视图逻辑行数:"
				+ iViewRow + "\t视图逻辑列数" + iViewCol + "\n视图每格高度:" + fGridHeight
				+ "\t视图每格宽度:" + fGridWidth;
	}
}
