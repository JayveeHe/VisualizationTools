package Main;

import java.awt.Canvas;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import FastForce.FastForceDirected;
import Utils.FileUtils;
import Utils.ForceDirectMethod;
import Utils.GexfUtils;
import Utils.JsonDecoder;
import Utils.NetworkUtils;
import Utils.PlotUtils;
import Utils.WeiboIDUtils;
import Utils.WeiboSpreadUtils;

public class Main {

	public static void main(String args[]) throws IOException, JSONException {
		// JsonDecoder jd = new JsonDecoder();
		// jd.getJsonFromFiles("姚晨同学.txt");
		// System.out.println(WeiboID.Base62Util.url2mid("B1WxukCTL"));

		String url = "http://weibo.com/3229125510/B20nZyVtX?mod=weibotime";

		String id;
		String weiboURL = "";
		// URLConnection conn = new java.net.URL().openConnection();

		// System.out.println("当前分析的微博uid："
		// + WeiboIDUtils.Base62Util.fullUrl2uid(url));
		// String swid = WeiboIDUtils.Base62Util.fullUrl2uid(url);// 分析的源头微博id
		// String repost_resp = NetworkUtils.getReq(retl_URL + "access_token="
		// + ACCESS_TOKEN + "&id="
		// + WeiboIDUtils.Base62Util.fullUrl2mid(url)
		// + "&page=1&count=200");
		// System.out.println(repost_resp.length());
		// String show_resp = NetworkUtils.getReq(show_URL + "access_token="
		// + ACCESS_TOKEN + "&id="
		// + WeiboIDUtils.Base62Util.fullUrl2mid(url));
		// System.out.println(show_resp.length());
		// new FileOutputStream(new File("repost_resp.txt")).write(repost_resp
		// .getBytes());
		// new FileOutputStream(new File("show_resp.txt")).write(show_resp
		// .getBytes());

		// System.out.println("输出成功");
		// System.out.println(response);

		// FileInputStream fis = new FileInputStream(new
		// File("repost_resp.txt"));
		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// byte[] buffer = new byte[128];
		// int iLen = -1;
		// // while (-1 != (iLen = is.read(buffer)))
		// while (-1 != (iLen = fis.read(buffer)))
		// baos.write(buffer, 0, iLen);
		// Map<String, NodeData> map = WeiboSpreadUtils
		// .createMapByURL("http://weibo.com/1653689003/B2TVTfImj?ref=home");
		Map<String, NodeData> map = WeiboSpreadUtils
				.createMapByURL("http://weibo.com/1764222885/B35U2ovTK?type=repost");
		System.out.println("=========================");
		// for (NodeData data : map.values()) {
		// System.out.println(data);

		// }
		Map<String, InfoNode> info_map = InfoNode.getNodeMapByDataMap(map);
		if (info_map == null) {
			return;
		}

		// int[] adjacency = PlotUtils
		// .transFormat(info_map);
		// int ttt = 0;
		// for (int i = 0; i < adjacency.length; i++) {
		// for (int j = 0; j < adjacency.length; j++) {
		// if (i == adjacency[j])
		// ttt++;
		// }
		// }
		// System.out.println(adjacency.length+"==========="+ttt);
		System.out.println("开始布点计算……");
		double time = System.currentTimeMillis();
		FastForceDirected ffd = new FastForceDirected();
		double[][] result = ffd.PositionComputeProcess(PlotUtils
				.transFormat(info_map));

		int count = 0;
		for (InfoNode node : info_map.values()) {
			node.setX((float) result[0][count]);
			node.setY((float) result[1][count]);
			count++;
		}

		float entropy = 0;
		
		float temp;
		// for (NodeData data : map.values()) {
		// System.out.println(data);
		// }
		// entropy = PlotUtils.FDA(info_map);
		// int count = 0;
		// do {
		// count++;
		// temp = entropy;
		// entropy = PlotUtils.FDA(info_map);
		// System.out.println("熵值=" + entropy);
		// } while (Math.abs(entropy) >= 0.1f
		// && Math.abs(entropy - temp) >= ForceDirectMethod.k * 0.00001f
		// && count < 1000);
		// }while(entropy>0);
		// for(InfoNode node: info_map.values())
		// {
		// if(node.getParent_wid()=="-1")
		// {
		// System.out.println("根节点半径====="+node.getRadius());
		// System.out.println(node);
		//
		// }
		// }
//		for (int i = 0; i < 200; i++) {
//			temp = entropy;
//			entropy = PlotUtils.FDA(info_map);
//			System.out.println("熵值=" + entropy);
//			// if (Math.abs(entropy) <= 0.01f || Math.abs(entropy - temp) <
//			// 0.001f)
//			// break;
//		}
		 time = System.currentTimeMillis() - time;
		 System.out.println("用时：" + time);
		// System.out.println("k=" + ForceDirectMethod.k);
		GexfUtils.createGexf(info_map, System.currentTimeMillis()
				+ "-"+ WeiboSpreadUtils.show_name  + ".gexf");
	}
}
