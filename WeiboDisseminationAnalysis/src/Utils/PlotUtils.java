package Utils;

import java.util.Map;

import Main.InfoNode;
import Main.NodeData;

public class PlotUtils {

	/**
	 * 力导向算法
	 * 
	 * @param map
	 * @return 该次力导向后的系统熵
	 */

	public static float FDA(Map<String, InfoNode> map) {
		ForceDirectMethod FDAmethod = new ForceDirectMethod(map);
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
								.F_attractive(distance) * 0.001f;//减缓位移变化量
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
//					if(AttractiveForce>RepulsiveForce)
//					{
//						System.out.println(data_i.getName()+"受到的引力大于斥力");
//					}else 	System.out.println(data_i.getName()+"受到的引力小于斥力");
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
		return entropy/map.size()*100;

	}

}
