package NodeDomain;

import java.util.ArrayList;

public class FriendNodes extends LogicalNode {
	String name;

	public FriendNodes(String ID, String ParentID) {
		this.ID = ID;
		this.ParentID = ParentID;
		this.ChilIDS = new ArrayList<String>();
	}

	public void setName(String name) {
		this.name = name;
	}

}
