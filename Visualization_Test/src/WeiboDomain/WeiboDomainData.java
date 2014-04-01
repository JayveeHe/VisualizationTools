package WeiboDomain;

import java.util.ArrayList;

import surfaceview_test.LogicManager;
import JsonUtils.WeiboData;
import NodeDomain.NodeDomainData;

public class WeiboDomainData extends NodeDomainData {
	private WeiboData weiboData;

	public WeiboDomainData(String ID, String ParentID, ArrayList<String> ChildIDs,
			LogicManager logicManager) {
		super(ID, ParentID, ChildIDs, logicManager);
		// TODO Auto-generated constructor stub
	}

	public WeiboData getWeiboData() {
		return weiboData;
	}

	public void setWeiboData(WeiboData weiboData) {
		this.weiboData = weiboData;
	}

}
