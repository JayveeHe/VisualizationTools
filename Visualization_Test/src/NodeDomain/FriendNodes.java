package NodeDomain;

import java.util.ArrayList;

public class FriendNodes extends LogicalNode {
	String name;

	public FriendNodes(long ID, long ParentID) {
		this.ID = ID;
		this.ParentID = ParentID;
		this.ChilIDS = new ArrayList<Long>();
	}

	public void setName(String name) {
		this.name = name;
	}

}
