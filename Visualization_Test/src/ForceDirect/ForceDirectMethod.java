package ForceDirect;

import surfaceview_Main.LogicManager;

public class ForceDirectMethod {
	int area;
	// float k = 2000f;
	// float g = 2000f;
	float rate = 0.001f;
	float resist_rate_a = 3000f;// 引力因子
	float resist_rate_r = 1000f;// 斥力因子
	float k;

	public ForceDirectMethod(LogicManager logicManager) {
		int iViewWidth = logicManager.iViewWidth;
		int iViewHeight = logicManager.iViewHeight;
		area = iViewHeight * iViewWidth;// 计算区域面积
		k = (float) Math.sqrt(area / logicManager.getNodesMap().size());// k为点密度的开根号。
		System.out.println("FDA方法创建，K=" + k);
	}

	/**
	 * 根据距离计算引力
	 * 
	 * @param distance
	 * @return
	 */
	public float F_attractive(float distance) {

		return (distance * distance / resist_rate_a);
	}

	/**
	 * 根据距离计算斥力
	 * 
	 * @param distance
	 * @return
	 */
	public float F_repulsive(float distance) {
		return (resist_rate_r / (distance * distance));
	}
}
