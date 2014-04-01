package NodeDomain;

import android.util.Log;

/**
 * 此领域逻辑对象负责管理维护相应的领域视图和领域数据
 * 
 * @author Jayvee
 * 
 * @param <D>
 *            领域数据对象的实类
 * @param <V>
 *            领域视图对象的实类
 */

public abstract class ADomainLogic<D extends ADomainData<?>, V extends ADomainView<?>> {

	// 相关变量设置
	private D data;
	private V view;
	public long ID;
	static final String DEBUG_TAG = "领域对象调试";

	private boolean isAtomNode = false;
	private boolean isExpand = false;

	/**
	 * 领域对象逻辑的构造函数,与相应的视图和数据对象有着一一对应的关系.
	 * 
	 * @param DomainView
	 *            与之对应的领域对象视图
	 * @param DomainData
	 *            与之对应的领域对象数据
	 */
	public ADomainLogic(long ID, V DomainView, D DomainData, boolean isAtomNode) {
		this.ID = ID;
		this.data = DomainData;
		this.view = DomainView;
		this.setAtomNode(isAtomNode);
		if (null != getData() && null != getView()) {
			Log.d(DEBUG_TAG, "领域对象" + ID + "创建完成!");
		} else
			Log.e(DEBUG_TAG, "领域对象创建失败!检查data与view是否为空");

	}

//	public abstract ADomainLogic<D, V> onAddChildLogic(long ID);

	public D getData() {
		return data;
	}

	public V getView() {
		return view;
	}

	public abstract void onRefresh();

	public boolean isExpand() {
		return isExpand;
	}

	public void setExpand(boolean isExpand) {
		this.isExpand = isExpand;
	}

	public boolean isAtomNode() {
		return isAtomNode;
	}

	public void setAtomNode(boolean isAtomNode) {
		this.isAtomNode = isAtomNode;
	}

}
