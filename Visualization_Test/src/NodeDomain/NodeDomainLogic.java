package NodeDomain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import surfaceview_test.LogicManager;
import JsonUtils.WeiboData;
import android.util.Log;

public class NodeDomainLogic {

	private final String ID; // 相关变量设置
	private final NodeDomainData data;
	private final NodeDomainView view;
	public static final String DEBUG_TAG = "领域对象调试";

	private boolean isAtomNode = false;
	private boolean isExpand = false;

	public NodeDomainLogic(String ID, NodeDomainView DomainView,
			NodeDomainData DomainData, boolean isAtomNode) {
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

	public static NodeDomainLogic creatDomainLogicByFriendNode(
			FriendNodes friendNodes, LogicManager logicManager) {
		NodeDomainData data = new NodeDomainData(friendNodes.ID,
				friendNodes.ParentID, friendNodes.ChilIDS, logicManager);
		NodeDomainView view = new NodeDomainView(data);
		boolean isAtom;
		if (data.getParentID() == "-1") {
			isAtom = true;
		} else {
			isAtom = false;
		}
		return new NodeDomainLogic(data.getID(), view, data, isAtom);
	}

//	/**
//	 * 根据list创建相应的逻辑list
//	 * 
//	 * @param arrayList
//	 * @param logicManager
//	 * @return
//	 */
//	public static ArrayList<NodeDomainLogic> creatDomainLogicByList(
//			ArrayList<WeiboData> arrayList, LogicManager logicManager) {
//		ArrayList<NodeDomainLogic> list = new ArrayList<NodeDomainLogic>();
//		for (int i = 0; i < arrayList.size(); i++) {
//			WeiboData weibodata = arrayList.get(i);
//			NodeDomainData data = new NodeDomainData(i, 0, null, logicManager);
//			data.onRefresh(weibodata.X * 20, weibodata.Y * 20, 5f, 0, 0);
//			data.group = weibodata.Group;
//			NodeDomainView view = new NodeDomainView(data);
//			list.add(new NodeDomainLogic(i, view, data, true));
//		}
//		return list;
//
//	}

	/**
	 * 根据包含weibodata的map创建一个包含所有map中logic的arraylist
	 * 
	 * @param map
	 * @param logicManager
	 * @return
	 */
	public static ArrayList<NodeDomainLogic> creatDomainLogicByMap(
			Map<?, WeiboData> map, LogicManager logicManager) {
		ArrayList<NodeDomainLogic> list = new ArrayList<NodeDomainLogic>();
		for (WeiboData weibodata : map.values()) {
			ArrayList<String> childIDs = new ArrayList<String>();
			for (int i = 0; i < weibodata.childs.size(); i++) {
				childIDs.add(weibodata.childs.get(i).ID);
			}
			NodeDomainData data;
			if (weibodata.parent != null) {
				data = new NodeDomainData(weibodata.ID, weibodata.parent.ID,
						childIDs, logicManager);
			} else {
				data = new NodeDomainData(weibodata.ID, childIDs, logicManager);
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
			NodeDomainView view = new NodeDomainView(data);
			list.add(new NodeDomainLogic(weibodata.ID, view, data, true));
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

	public NodeDomainData getData() {
		return data;
	}

	public NodeDomainView getView() {
		return view;
	}

	public String getID() {
		return ID;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getData().toString();
	}
}
