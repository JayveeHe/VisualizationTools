package surfaceview_test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import JsonUtils.WeiboData;
import android.util.Log;

public class JsonDataManager {

	final static String DEBUG_TAG = "JsonDataManger";

	public Map<String, WeiboData> ReadWeiboNodesFromStr(String jstr) {
		JSONTokener jsonTokener = new JSONTokener(jstr);
//		ArrayList<WeiboData> datalist = new ArrayList<WeiboData>();
		Map<String, WeiboData> wbdtmap = new HashMap<String, WeiboData>();
		try {
			JSONObject networks_data = (JSONObject) jsonTokener.nextValue();
			JSONArray Nodes = networks_data.getJSONArray("Nodes");
			for (int i = 0; i < Nodes.length(); i++) {
				JSONObject node = Nodes.getJSONObject(i);
				float x = (float) node.getDouble("X");
				float y = (float) node.getDouble("Y");
				float z = (float) node.getDouble("Z");
				int group = node.getInt("Group");
				String key = node.getString("Key");
				WeiboData wd = new WeiboData(x, y, z, group, key,String.valueOf(i));
//				datalist.add(wd);
				wbdtmap.put(key, wd);// 在hash表中注册该weibodata
			}
			JSONArray Paths = networks_data.getJSONArray("Paths");
			for (int i = 0; i < Paths.length(); i++) {
				JSONObject path = Paths.getJSONObject(i);
				String Node1 = path.getString("Node1");
				String Node2 = path.getString("Node2");
				WeiboData parent = wbdtmap.get(Node1);
				WeiboData child = wbdtmap.get(Node2);
				parent.childs.add(child);
				child.parent = parent;// 建立母节点和子节点之间的关系
			}
			
		} catch (JSONException e) {
			Log.e(DEBUG_TAG, "读取Json串的时候发生错误" + "\n" + e);
		}
		return wbdtmap;

	}
}
