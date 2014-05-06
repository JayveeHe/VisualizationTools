package Utils;

import java.util.ArrayList;

public class LogicalNode {
	long UID;// 该节点的标识ID
	ArrayList<Long> ChilIDS;// 该节点的子节点ID

	public LogicalNode(long UID) {
		this.UID = UID;
	}
}
