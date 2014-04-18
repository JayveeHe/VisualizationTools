package NodeDomain;

import java.util.ArrayList;

import android.util.Log;

public abstract class LogicalNode {

	String ID = "";// 该节点的ID
	ArrayList<String> ChilIDS = null;// 该节点的子节点ID
	String ParentID = "-1";// 该节点的母节点ID
	public final static String DEBUG_TAG = "LogicalNode";

	public void addChild(String ID) {
		this.ChilIDS.add(ID);
	}

	public void removeChild(String ID) {
		for (int i = 0; i < this.ChilIDS.size(); i++) {
			if (this.ChilIDS.get(i) == ID) {
				this.ChilIDS.remove(i);
				Log.d(DEBUG_TAG, "移除孩子节点ID成功!");
				return;
			}
		}
		Log.e(DEBUG_TAG, "移除孩子节点ID失败!检查ID是否正确!");
	}

	public ArrayList<String> getChildIDs() {
		return this.ChilIDS;
	}

	public String getID() {
		return this.ID;
	}

	public String getParentID() {
		return this.ParentID;
	}

	
	
}
