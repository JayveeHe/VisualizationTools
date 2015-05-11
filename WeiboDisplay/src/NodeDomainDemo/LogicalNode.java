package NodeDomainDemo;

import java.util.ArrayList;

import android.util.Log;

public abstract class LogicalNode {

	long ID = 0;// 该节点的ID
	ArrayList<Long> ChilIDS = null;// 该节点的子节点ID
	long ParentID = 0;// 该节点的母节点ID
	public final static String DEBUG_TAG = "LogicalNode";

	public void addChild(long ID) {
		this.ChilIDS.add(ID);
	}

	public void removeChild(long ID) {
		for (int i = 0; i < this.ChilIDS.size(); i++) {
			if (this.ChilIDS.get(i) == ID) {
				this.ChilIDS.remove(i);
				Log.d(DEBUG_TAG, "移除孩子节点ID成功!");
				return;
			}
		}
		Log.e(DEBUG_TAG, "移除孩子节点ID失败!检查ID是否正确!");
	}

	public ArrayList<Long> getChildIDs() {
		return this.ChilIDS;
	}

	public long getID() {
		return this.ID;
	}

	public long getParentID() {
		return this.ParentID;
	}

	
	
}
