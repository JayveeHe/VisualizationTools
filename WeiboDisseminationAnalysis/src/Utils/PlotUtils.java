package Utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import FastForce.FastForceDirected;
import Main.InfoNode;
import Main.SpreadNodeData;

public class PlotUtils {

	/**
	 * 力导向算法
	 * 
	 * @param map
	 * @return 该次力导向后的系统熵
	 * @author Jayvee
	 */

	public static float FDA(Map<String, InfoNode> map) {
		// ForceDirectMethod FDAmethod = new ForceDirectMethod(map);
		float entropy = 0;// 系统的熵
		for (InfoNode data_i : map.values()) {
			// 依次计算逻辑点j对逻辑点i的坐标影响
			if (data_i.getParent_wid() == "-1") {
				// System.out.println("根节点的半径：" + data_i.getRadius());
				continue;// 根节点不做改变
			}
			float xi = data_i.getX();
			float yi = data_i.getY();
			for (InfoNode data_j : map.values()) {// 进行全节点的遍历
				if (!data_i.getWid().equals(data_j.getWid())) {
					float AttractiveForce = 0;
					float RepulsiveForce = 0;
					float xj = data_j.getX();
					float yj = data_j.getY();
					float distance = (float) Math.sqrt((xi - xj) * (xi - xj)
							+ (yi - yj) * (yi - yj));
					float angle = (float) Math.atan2(yi - yj, xi - xj);
					// System.out.println("两点距离=" + distance);
					// atan2(y,x)返回的是y,x点与0,0连线与X轴的夹角（-pi,pi）
					if (data_j.getWid().equals(data_i.getParent_wid())
							|| data_i.getChilds_wid().contains(data_j.getWid()))
					// if (data_j.getChilds_wid().contains(data_i.getWid()))

					// if (logic_j.getID() == logic_i.getData().getParentID()
					// || logic_i.getData().getChildIDs()
					// .contains(logic_j.getID()))
					// 判断i,j两点是否有关系
					{
						AttractiveForce = ForceDirectMethod
								.F_attractive(distance) * 0.001f;// 减缓位移变化量
						// * (1 + data_j.getChilds_wid().size() * 0.1f);//
						// 相关节点间引力大小
						entropy -= AttractiveForce;
						data_i.setXdiffer((float) (data_i.getXdiffer() + AttractiveForce
								* Math.cos(angle - Math.PI)));
						data_i.setYdiffer((float) (data_i.getYdiffer() + AttractiveForce
								* Math.sin(angle - Math.PI)));

					}
					RepulsiveForce = ForceDirectMethod.F_repulsive(distance) * 0.001f;// 不相关节点间斥力大小
					entropy += RepulsiveForce;
					if (distance > 1000)
						continue;// 距离超过一定值则忽略斥力作用,但是熵值的计算必须计入
					data_i.setXdiffer((float) (data_i.getXdiffer() + RepulsiveForce
							* Math.cos(angle)));
					data_i.setYdiffer((float) (data_i.getYdiffer() + RepulsiveForce
							* Math.sin(angle)));
					// if(AttractiveForce>RepulsiveForce)
					// {
					// System.out.println(data_i.getName()+"受到的引力大于斥力");
					// }else System.out.println(data_i.getName()+"受到的引力小于斥力");
				}
			}
			// System.out.println("X变动=" + logic_i.getData().Xdiffer);
			// System.out.println("Y变动=" + logic_i.getData().Ydiffer + "\n坐标："
			// + logic_i.getData().getCurX() + "="
			// + logic_i.getData().getCurY());

			// 进行坐标清算
			for (InfoNode node : map.values()) {
				node.onFDA();
			}

		}
		return entropy / map.size() * 100;

	}

	/**
	 * 用于实现布点计算的抽象类
	 * 
	 * @author Jayvee
	 * 
	 */
	public static abstract class AComputable {
		public abstract float getComputableX();

		public abstract float getComputableY();

		public abstract String getID();

		public abstract String getParentID();

		public abstract ArrayList<String> getChildIDs();

		public abstract void setComputableX(float x);

		public abstract void setComputableY(float y);
	}

	/**
	 * 用于实现布点计算的接口
	 * 
	 * @author Jayvee
	 * 
	 */
	public static interface IDrawableNode {
		public float getComputableX();

		public float getComputableY();

		public String getID();

		public String getParentID();

		public ArrayList<String> getChildIDs();

		public Color getColor();

		public float getRadius();

		public String getLabel();

		public void setComputableX(float x);

		public void setComputableY(float y);

		public void setColor(Color color);

		public void setRadius(float radius);
	}

	/**
	 * 生成用于制作gexf的map
	 * 
	 * @param map
	 * @return 以nodeID为键值，
	 */
	public static Map<String, IDrawableNode> getInfoMapByDataMap(
			Map<String, SpreadNodeData> map) {
		if (null == map || map.size() == 0) {
			return null;
		}
		Map<String, IDrawableNode> nodeMap = new HashMap<String, IDrawableNode>();
		Random r = new Random(System.currentTimeMillis());
		for (SpreadNodeData data : map.values()) {
			InfoNode node = new InfoNode(data.getName(), data.getWid(),
					data.getParent_wid(), data.getChilds_wid());
			if (data.getParent_wid() != "-1") {
				int len = r.nextInt(30) - 15;
				double dAngle = r.nextDouble();
				node.setX((float) (len * Math.cos(dAngle)));
				node.setY((float) (len * Math.sin(dAngle)));
				// node.x = r.nextFloat() * 100 +
				// Long.parseLong(data.getWid())%10;
				// node.y = r.nextFloat() * 100 +
				// Long.parseLong(data.getWid())%12;

			} else {
				node.setX(0);
				node.setY(0);
			}
			node.setLastX(node.getX());
			node.setLastY(node.getY());
			nodeMap.put(node.getWid(), node);
		}
		// 设置颜色 ----------此处应该可以优化
		for (IDrawableNode node : nodeMap.values()) {
			if (nodeMap.size() > 1 && node.getParentID() != "-1"
					&& node.getChildIDs().size() == 0)// 即为叶子节点
			{

				IDrawableNode parent = nodeMap.get(node.getParentID());
				node.setColor(parent.getColor());
				// node.x = r.nextFloat() + parent.x;
				// node.y = r.nextFloat() + parent.y;
				int len = r.nextInt(4) - 2;
				double dAngle = r.nextDouble();
				node.setComputableX((float) ((len) * Math.cos(dAngle))
						+ parent.getComputableX());
				node.setComputableY((float) ((len) * Math.sin(dAngle))
						+ parent.getComputableY());
			}
		}
		return nodeMap;
	}

//	/**
//	 * 为适应快速力引导算法的格式转换函数
//	 * 
//	 * @param map
//	 * @return
//	 */
//	public static int[][] transFormat(Map<String, InfoNode> map) {
//		int nodeNum = map.size();
//		int[][] edges = new int[nodeNum][nodeNum];
//		List<InfoNode> nodelist = new LinkedList<InfoNode>();// 生成一个有序的list
//		// 创建边关系
//		int count = 0;
//		Map<String, Integer> matchMap = new HashMap<String, Integer>();
//		// matchMap用于实现数组序号的对应关系，其中String存储的是wid，Integer存储数组序号
//		for (InfoNode node : map.values()) {
//			// 按照map的顺序进行list创建
//			matchMap.put(node.getWid(), count);
//			nodelist.add(node);
//			count++;
//		}
//		for (int i = 0; i < nodelist.size(); i++) {
//			InfoNode node = nodelist.get(i);
//			String parent_wid = node.getParent_wid();
//			if (node.getChilds_wid().size() != 0) {
//				for (String wid : node.getChilds_wid()) {
//					// 完成该点的子节点配对
//					int j = matchMap.get(wid);
//					edges[i][j] = 1;
//				}
//			}
//			if (parent_wid != "-1") {
//				// 完成母节点的配对，排除了根节点和孤立节点的情况
//				int j = matchMap.get(parent_wid);
//				edges[i][j] = 1;
//			}
//		}
//		return edges;
//	}

	/**
	 * 为适应快速力引导算法的格式转换函数
	 * 
	 * @param map
	 *            包含继承了{@link AComputable}抽象类的待计算类
	 * @return 符合{@link FastForceDirected}布点计算的类型
	 */
	public static <T extends AComputable> int[][] trans2Format(
			Map<String, T> map) {
		int nodeNum = map.size();
		int[][] edges = new int[nodeNum][nodeNum];
		List<T> nodelist = new LinkedList<T>();// 生成一个有序的list
		// 创建边关系
		int count = 0;
		Map<String, Integer> matchMap = new HashMap<String, Integer>();
		// matchMap用于实现数组序号的对应关系，其中String存储的是wid，Integer存储数组序号
		for (T node : map.values()) {
			// 按照map的顺序进行list创建
			matchMap.put(node.getID(), count);
			nodelist.add(node);
			count++;
		}
		for (int i = 0; i < nodelist.size(); i++) {
			T node = nodelist.get(i);
			String parent_wid = node.getParentID();
			if (node.getChildIDs().size() != 0) {
				for (String wid : node.getChildIDs()) {
					// 完成该点的子节点配对
					int j = matchMap.get(wid);
					edges[i][j] = 1;
				}
			}
			if (parent_wid != "-1") {
				// 完成母节点的配对，排除了根节点和孤立节点的情况
				int j = matchMap.get(parent_wid);
				edges[i][j] = 1;
			}
		}
		return edges;
	}

	/**
	 * 为适应快速力引导算法的格式转换函数
	 * 
	 * @param map
	 *            包含继承了{@link IComputable}接口的待计算类
	 * @return 符合{@link FastForceDirected}布点计算的类型
	 */
	public static int[][] transToFormat(Map<String, IDrawableNode> map) {
		int nodeNum = map.size();
		int[][] edges = new int[nodeNum][nodeNum];
		List<IDrawableNode> nodelist = new LinkedList<IDrawableNode>();// 生成一个有序的list
		// 创建边关系
		int count = 0;
		Map<String, Integer> matchMap = new HashMap<String, Integer>();
		// matchMap用于实现数组序号的对应关系，其中String存储的是wid，Integer存储数组序号
		for (IDrawableNode node : map.values()) {
			// 按照map的顺序进行list创建
			matchMap.put(node.getID(), count);
			nodelist.add(node);
			count++;
		}
		for (int i = 0; i < nodelist.size(); i++) {
			IDrawableNode node = nodelist.get(i);
			String parent_wid = node.getParentID();
			if (node.getChildIDs().size() != 0) {
				for (String wid : node.getChildIDs()) {
					// 完成该点的子节点配对
					int j = matchMap.get(wid);
					edges[i][j] = 1;
				}
			}
			if (parent_wid != "-1") {
				// 完成母节点的配对，排除了根节点和孤立节点的情况
				int j = matchMap.get(parent_wid);
				edges[i][j] = 1;
			}
		}
		return edges;
	}

}
