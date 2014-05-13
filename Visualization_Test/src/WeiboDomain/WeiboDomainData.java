package WeiboDomain;

import java.util.ArrayList;

import surfaceview_Main.LogicManager;
import JsonUtils.WeiboNodeData;
import NodeDomain.NodeDomainData;

public class WeiboDomainData extends NodeDomainData {
	private WeiboNodeData weiboData;

	public WeiboDomainData(String ID, String ParentID, ArrayList<String> ChildIDs,
			LogicManager logicManager) {
		super(ID, ParentID, ChildIDs, logicManager);
		// TODO Auto-generated constructor stub
	}

	public WeiboNodeData getWeiboData() {
		return weiboData;
	}

	public void setWeiboData(WeiboNodeData weiboData) {
		this.weiboData = weiboData;
	}

}
