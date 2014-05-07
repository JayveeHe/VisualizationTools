package Main;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.json.JSONException;

import FastForce.FastForceDirected;
import Utils.GexfUtils;
import Utils.PlotUtils;
import Utils.WeiboSpreadUtils;
import Utils.PlotUtils.IDrawableNode;

public class Main {

	public static void main(String args[]) throws IOException, JSONException {
		// JsonDecoder jd = new JsonDecoder();
		// jd.getJsonFromFiles("姚晨同学.txt");
		// System.out.println(WeiboID.Base62Util.url2mid("B1WxukCTL"));

		// String url = "http://weibo.com/2803301701/B1PSX48Dn";
		//
		// String id;
		// String weiboURL = "";
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
		
		
		Map<String, SpreadNodeData> map = null;
		try {
			map = WeiboSpreadUtils
					.createMapByURL("http://weibo.com/1401880315/B3b0UtTCv");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("=========================");
		// Map<String, InfoNode> info_map = InfoNode.getInfoMapByDataMap(map);
		Map<String, IDrawableNode> info_map = PlotUtils
				.getInfoMapByDataMap(map);
		if (info_map == null) {
			return;
		}
		System.out.println("开始布点计算……");
		double time = System.currentTimeMillis();
		double[][] result = new FastForceDirected()
				.PositionComputeProcess(PlotUtils.transToFormat(info_map));
		int count = 0;
		for (IDrawableNode node : info_map.values()) {
			node.setComputableX(((float) result[0][count]));
			node.setComputableY((float) result[1][count]);
			count++;
		}
		time = System.currentTimeMillis() - time;
		System.out.println("用时：" + time / 1000 + "秒");
		GexfUtils.createGexf(info_map, System.currentTimeMillis() + "-"
				+ WeiboSpreadUtils.show_name + ".gexf");
	}
}
