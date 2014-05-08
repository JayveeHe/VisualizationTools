package SpreadUtils;

import java.util.Map;

import Nodes.InfoNode;

public class ForceDirectMethod {
	int area;
	// float k = 2000f;
	// float g = 2000f;
	float rate = 0.001f;
	static float resist_rate_a = 300f;// 引力因子
	static float resist_rate_r = 100f;// 斥力因子
	public static float k;

	public ForceDirectMethod(Map<String, InfoNode> logicManager) {
		// int iViewWidth = logicManager.iViewWidth;
		// int iViewHeight = logicManager.iViewHeight;
		// area = iViewHeight * iViewWidth;// 计算区域面积
		area = 50000;
		k = (float) Math.sqrt(area / logicManager.size()) * 0.1f;//
		// k=1f;
		// k为点密度的开根号。
		// System.out.println("FDA方法创建，K=" + k);
	}

	/**
	 * 根据距离计算引力
	 * 
	 * @param distance
	 * @return
	 */
	public static float F_attractive(float distance) {
		// float temp = distance * distance / resist_rate_a;
		float temp = distance * distance / k;
		return temp >= 100 ? 100 : temp;
		// return temp;
		// return (distance * distance / resist_rate_a);
	}

	/**
	 * 根据距离计算斥力
	 * 
	 * @param distance
	 * @return
	 */
	public static float F_repulsive(float distance) {
		// float temp = resist_rate_r / (distance * distance);
		if (distance == 0)//防止距离为0出现无穷大
			distance = 0.1f;
		float temp = k * k / distance;
		return temp >= 100 ? 100 : temp;
		// return temp;
		// return (resist_rate_r / (distance * distance));
	}
}
