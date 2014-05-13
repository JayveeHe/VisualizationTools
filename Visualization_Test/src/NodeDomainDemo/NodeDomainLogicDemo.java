package NodeDomainDemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import surfaceview_Demo.LogicManagerDemo;
import JsonUtilsDemo.WeiboData;
import android.util.Log;

public class NodeDomainLogicDemo {

	private final long ID; // 相关变量设置
	private final NodeDomainDataDemo data;
	private final NodeDomainViewDemo view;
	public static final String DEBUG_TAG = "领域对象调试";

	private boolean isAtomNode = false;
	private boolean isExpand = false;

	public NodeDomainLogicDemo(long ID, NodeDomainViewDemo DomainView,
			NodeDomainDataDemo DomainData, boolean isAtomNode) {
		// TODO Auto-generated constructor stub
		this.ID = ID;
		this.data = DomainData;
		this.view = DomainView;
		this.setAtomNode(isAtomNode);
		if (null != getData() && null != getView()) {
			Log.d(DEBUG_TAG, "领域对象" + ID + "创建完成!");
			Log.d(DEBUG_TAG, this.toString());
		} else
			Log.e(DEBUG_TAG, "领域对象创建失败!检查data与view是否为空");

	}

	public static NodeDomainLogicDemo creatDomainLogicByFriendNode(
			FriendNodes friendNodes, LogicManagerDemo logicManager) {
		NodeDomainDataDemo data = new NodeDomainDataDemo(friendNodes.ID,
				friendNodes.ParentID, friendNodes.ChilIDS, logicManager);
		NodeDomainViewDemo view = new NodeDomainViewDemo(data);
		boolean isAtom;
		if (data.getParentID() == -1) {
			isAtom = true;
		} else {
			isAtom = false;
		}
		return new NodeDomainLogicDemo(data.getID(), view, data, isAtom);
	}

	/**
	 * 根据list创建相应的逻辑list
	 * 
	 * @param arrayList
	 * @param logicManager
	 * @return
	 */
	public static ArrayList<NodeDomainLogicDemo> creatDomainLogicByList(
			ArrayList<WeiboData> arrayList, LogicManagerDemo logicManager) {
		ArrayList<NodeDomainLogicDemo> list = new ArrayList<NodeDomainLogicDemo>();
		for (int i = 0; i < arrayList.size(); i++) {
			WeiboData weibodata = arrayList.get(i);
			NodeDomainDataDemo data = new NodeDomainDataDemo(i, 0, null, logicManager);
			data.onRefresh(weibodata.X * 20, weibodata.Y * 20, 5f, 0, 0);
			data.group = weibodata.Group;
			NodeDomainViewDemo view = new NodeDomainViewDemo(data);
			list.add(new NodeDomainLogicDemo(i, view, data, true));
		}
		return list;

	}

	/**
	 * 根据包含weibodata的map创建一个包含所有map中logic的arraylist
	 * 
	 * @param map
	 * @param logicManager
	 * @return
	 */
	public static ArrayList<NodeDomainLogicDemo> creatDomainLogicByMap(
			Map<?, WeiboData> map, LogicManagerDemo logicManager) {
		ArrayList<NodeDomainLogicDemo> list = new ArrayList<NodeDomainLogicDemo>();
		for (WeiboData weibodata : map.values()) {
			ArrayList<Long> childIDs = new ArrayList<Long>();
			for (int i = 0; i < weibodata.childs.size(); i++) {
				childIDs.add(weibodata.childs.get(i).ID);
			}
			NodeDomainDataDemo data;
			if (weibodata.parent != null) {
				data = new NodeDomainDataDemo(weibodata.ID, weibodata.parent.ID,
						childIDs, logicManager);
			} else {
				data = new NodeDomainDataDemo(weibodata.ID, childIDs, logicManager);
			}
			if (weibodata.size != 0) {
				data.onRefresh(weibodata.X , weibodata.Y ,
						weibodata.size, 0, 0);
			} else {
				data.onRefresh(weibodata.X * 20, weibodata.Y * 20, 4f, 0, 0);
			}
			data.group = weibodata.Group;
			data.key = weibodata.Key;
			data.color = weibodata.color;
			NodeDomainViewDemo view = new NodeDomainViewDemo(data);
			list.add(new NodeDomainLogicDemo(weibodata.ID, view, data, true));
		}
		return list;

	}

	public boolean isAtomNode() {
		return isAtomNode;
	}

	public void setAtomNode(boolean isAtomNode) {
		this.isAtomNode = isAtomNode;
	}

	public boolean isExpand() {
		return isExpand;
	}

	public void setExpand(boolean isExpand) {
		this.isExpand = isExpand;
	}

	public NodeDomainDataDemo getData() {
		return data;
	}

	public NodeDomainViewDemo getView() {
		return view;
	}

	public long getID() {
		return ID;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getData().toString();
	}
}
