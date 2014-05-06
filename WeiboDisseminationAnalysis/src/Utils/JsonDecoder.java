package Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonDecoder {
	/**
	 * 对Json格式数据的解析工具类
	 * @throws JSONException 
	 */

	// 相关参数的定义


	
	
	public void getJsonFromFiles(String filename)
			throws IOException, JSONException {
		BufferedReader br = new BufferedReader(new FileReader(
				new File(filename)));
		// if (null != br) {
		// System.out.println("文件打开成功！");
		// } else {
		// System.out.println("文件打开失败");
		// return;
		// }
		StringBuffer stringBuffer = new StringBuffer();
		String temp = br.readLine();
		while (null != temp) {
			stringBuffer.append(temp);
			temp = br.readLine();
		}
		br.close();
		System.out.println(stringBuffer.toString());
		String jsonstring = stringBuffer.toString();
		JSONTokener jsonTokener = new JSONTokener(jsonstring);
		JSONObject networks_data = (JSONObject) jsonTokener.nextValue();
		JSONArray Nodes = networks_data.getJSONArray("Nodes");
		JSONArray classname= ((JSONObject) Nodes.get(0)).names();
//		classname.toString().getClass();
//		System.out.println(((JSONObject) Nodes.get(0)).names());
		System.out.println(classname.length());
		System.out.println(classname.get(1));
		
//		System.out.println(networks_data.names().length());
//		JSONArray Nodes = networks_data.getJSONArray(networks_data.names().get(networks_data.names().length()-));
//		JSONObject 	Paths = (JSONObject) jsonTokener.nextValue();
//		System.out.println(Nodes);
	}
}
