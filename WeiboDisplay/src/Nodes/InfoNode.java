package Nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.graphics.Color;
import SpreadUtils.PlotUtils.IDrawableNode;

public class InfoNode implements IDrawableNode {

	String name;
	String wid;
	String parent_wid;
	ArrayList<String> childs_wid = new ArrayList<String>();
	float x;
	float y;
	private float radius;
	private int color;

	private static Random r = new Random(System.currentTimeMillis());
	// 力导向布局所用
	private float Xdiffer = 0;
	private float Ydiffer = 0;
	private float lastX;
	private float lastY;

	public InfoNode(String name, String wid, String parent_wid,
			ArrayList<String> childs_wid) {
		// super();
		this.name = name;
		this.wid = wid;
		this.parent_wid = parent_wid;
		this.childs_wid = childs_wid;
		int alpha = r.nextInt(127)+127;
		int red = r.nextInt(255);
		int green = r.nextInt(255);
		int blue = r.nextInt(255);
		// 防止颜色透明或者白色
		while ((alpha == 255 && red == 255 && green == 255 && blue == 255)) {
			alpha = r.nextInt(127)+127;
			red = r.nextInt(255);
			green = r.nextInt(255);
			blue = r.nextInt(255);
		}
		System.out.println("节点ARGB值:alpha=" + alpha + "red=" + red + "green="
				+ green + "blue=" + blue);
		this.color = Color.argb(alpha, red, green, blue);
		this.setColor(color);
		this.setRadius((float) (5 + 0.1 * this.childs_wid.size()));
		// System.out.println(this.color);
	}

	// /**
	// * 生成用于制作gexf的map
	// *
	// * @param map
	// * @return
	// */
	// public static Map<String, InfoNode> getNodeMapByDataMap(
	// Map<String, NodeData> map) {
	// if (null == map || map.size() == 0) {
	// return null;
	// }
	// Map<String, InfoNode> nodeMap = new HashMap<String, InfoNode>();
	// Random r = new Random(System.currentTimeMillis());
	// for (NodeData data : map.values()) {
	// InfoNode node = new InfoNode(data.getName(), data.getWid(),
	// data.getParent_wid(), data.getChilds_wid());
	// if (data.getParent_wid() != "-1") {
	// int len = r.nextInt(30) - 15;
	// double dAngle = r.nextDouble();
	// node.x = (float) (len * Math.cos(dAngle));
	// node.y = (float) (len * Math.sin(dAngle));
	// // node.x = r.nextFloat() * 100 +
	// // Long.parseLong(data.getWid())%10;
	// // node.y = r.nextFloat() * 100 +
	// // Long.parseLong(data.getWid())%12;
	//
	// } else {
	// node.x = 0;
	// node.y = 0;
	// }
	// node.lastX = node.x;
	// node.lastY = node.y;
	// nodeMap.put(node.getWid(), node);
	// }
	// // 设置颜色 ----------此处应该可以优化
	// for (InfoNode node : nodeMap.values()) {
	// if (nodeMap.size() > 1 && node.parent_wid != "-1"
	// && node.getChilds_wid().size() == 0)// 即为叶子节点
	// {
	//
	// InfoNode parent = nodeMap.get(node.parent_wid);
	// node.setColor(parent.getColor());
	// int len = r.nextInt(4) - 2;
	// double dAngle = r.nextDouble();
	// node.x = (float) ((len) * Math.cos(dAngle)) + parent.x;
	// node.y = (float) ((len) * Math.sin(dAngle)) + parent.y;
	// }
	// }
	// return nodeMap;
	// }

	/**
	 * 每次FDA算法结束后回调，进行偏移量的清算
	 */
	public void onFDA() {
		this.x += this.Xdiffer < 2000 ? this.Xdiffer : 2000;
		this.y += this.Ydiffer < 2000 ? this.Ydiffer : 2000;
		this.Xdiffer = 0;
		this.Ydiffer = 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWid() {
		return wid;
	}

	public void setWid(String wid) {
		this.wid = wid;
	}

	public String getParent_wid() {
		return parent_wid;
	}

	public void setParent_wid(String parent_wid) {
		this.parent_wid = parent_wid;
	}

	public ArrayList<String> getChilds_wid() {
		return childs_wid;
	}

	public void setChilds_wid(ArrayList<String> childs_wid) {
		this.childs_wid = childs_wid;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getXdiffer() {
		return Xdiffer;
	}

	public void setXdiffer(float xdiffer) {
		Xdiffer = xdiffer;
	}

	public float getYdiffer() {
		return Ydiffer;
	}

	public void setYdiffer(float ydiffer) {
		Ydiffer = ydiffer;
	}

	public float getLastY() {
		return lastY;
	}

	public void setLastY(float lastY) {
		this.lastY = lastY;
	}

	public float getLastX() {
		return lastX;
	}

	public void setLastX(float lastX) {
		this.lastX = lastX;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	@Override
	public String toString() {
		return "InfoNode [name=" + name + ", wid=" + wid + ", parent_wid="
				+ parent_wid + ", childs_wid=" + childs_wid + ", x=" + x
				+ ", y=" + y + ", radius=" + getRadius() + ", color="
				+ getColor() + "]";
	}

	// 实现的接口方法
	@Override
	public float getComputableX() {
		return this.x;
	}

	@Override
	public float getComputableY() {
		return this.y;
	}

	@Override
	public String getID() {
		return this.wid;
	}

	@Override
	public String getParentID() {
		return this.parent_wid;
	}

	@Override
	public ArrayList<String> getChildIDs() {
		return this.childs_wid;
	}

	@Override
	public void setComputableX(float x) {
		this.x = x;
	}

	@Override
	public void setComputableY(float y) {
		this.y = y;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public void setColor(int color) {
		this.color = color;
	}

	@Override
	public int getColor() {
		// TODO Auto-generated method stub
		return this.color;
	}

}
