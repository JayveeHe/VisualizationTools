package ForceDirect;

import surfaceview_Main.LogicManager;
import NodeDomain.NodeDomainLogic;

/**
 * Barnes Hut算法的工具类
 * 
 * @author Jayvee
 * 
 */
public class BarnesHutTree {
	float fieldx_1;// 该树所指代区域的左X轴坐标
	float fieldx_2;// 该树所指代区域的右X轴坐标
	float fieldy_1;// 该树所指代区域的上Y轴坐标
	float fieldy_2;// 该树所指代区域的下Y轴坐标
	NodeDomainLogic logic = null;
	BarnesHutTree childTree;

	/**
	 * 创建指定区域的BH树
	 * 
	 * @param fieldx_1
	 *            该树所指代区域的左X轴坐标
	 * @param fieldx_2
	 *            该树所指代区域的右X轴坐标
	 * @param fieldy_1
	 *            该树所指代区域的上Y轴坐标
	 * @param fieldy_2
	 *            该树所指代区域的下Y轴坐标
	 * @param logic
	 *            需要添加进树的逻辑对象
	 */
	public BarnesHutTree(float fieldx_1, float fieldx_2, float fieldy_1,
			float fieldy_2, NodeDomainLogic logic) {
		// TODO Auto-generated constructor stub
		this.fieldx_1 = fieldx_1;
		this.fieldx_2 = fieldx_2;
		this.fieldy_1 = fieldy_1;
		this.fieldy_2 = fieldy_2;
		this.logic = logic;
	}

	public void buildTree(float fieldx_1, float fieldx_2, float fieldy_1,
			float fieldy_2, LogicManager logicManager) {
		float viewWidth = logicManager.iViewWidth;
		float viewHeight = logicManager.iViewHeight;
		BarnesHutTree BHtree = new BarnesHutTree(0, viewWidth, 0, viewHeight,
				null);
		for (NodeDomainLogic logic : logicManager.getNodesMap().values()) {
			// BHtree.logic
		}
	}
}
