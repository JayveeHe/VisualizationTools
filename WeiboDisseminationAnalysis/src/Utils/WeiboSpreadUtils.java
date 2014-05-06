package Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import Main.InfoNode;
import Main.NodeData;

public class WeiboSpreadUtils {
	public static String ACCESS_TOKEN = "2.00t3nVnCe3dgkC63ac35576efRXPWC";

	// private static String show_wid;
	public static String show_name;
	private static int max_count = 1800;

	/**
	 * 根据单条微博的url生成相应的转发微博map
	 * 
	 * @param url
	 * @return
	 * @throws JSONException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static Map<String, NodeData> createMapByURL(String url)
			throws JSONException, MalformedURLException, IOException {

		String rptl_URL = "https://api.weibo.com/2/statuses/repost_timeline.json?";
		String show_URL = "https://api.weibo.com/2/statuses/show.json?";

		String show_wid = WeiboIDUtils.Base62Util.fullUrl2mid(url);
		Map<String, NodeData> map = new HashMap<String, NodeData>();
		String show_resp = NetworkUtils.getReq(show_URL + "access_token="
				+ ACCESS_TOKEN + "&id=" + show_wid);
		// //首先获取该条微博的信息
		JSONTokener tokener_show = new JSONTokener(show_resp);
		JSONObject object_show = (JSONObject) tokener_show.nextValue();
		String show_text = object_show.getString("text");
		String show_created_at = object_show.getString("created_at");

		show_name = object_show.getJSONObject("user").getString("screen_name");
		int show_repost_count = object_show.getInt("reposts_count");
		System.out.println("转发总数：" + show_repost_count);
		if (show_repost_count > max_count) {
			System.out.println("不支持转发数大于" + max_count + "条的微博分析！");
			return null;
		}
		String show_uid = object_show.getJSONObject("user").getString("idstr");
		boolean isRepost = object_show.optJSONObject("retweeted_status") != null;
		ArrayList<String> show_repost_link = new ArrayList<String>();
		show_repost_link.add(show_name);
		NodeData showData = new NodeData(0, show_name, show_name, show_text,
				show_wid, show_uid, show_created_at, show_repost_link);
		map.put(show_name + "-0", showData);// 首先在map中加入根节点信息
		System.out.println(showData.getName() + ":" + showData.getText());
		// int page = 1;
		long max_id = 0;
		int max_page = (int) Math.ceil(show_repost_count / 200f);
		System.out.println(show_repost_count / 200 + "|||max_page=" + max_page);
		for (int page = 1; page <= max_page; page++) {
			System.out.println("第" + page + "页读取开始");
			String repost_resp = NetworkUtils.getReq(rptl_URL + "access_token="
					+ ACCESS_TOKEN + "&id=" + show_wid + "&count=200"
					// + "&max_id=" + max_id
					+ "&page=" + page);
			System.out.println("第" + page + "页读取结束");
			// final String strRepost = FileUtils.File2str("repost_resp.txt");
			// final String strShow = FileUtils.File2str("show_resp.txt");
			// 由Json字符串获取NodeData
			// //获取该条微博的转发信息
			JSONTokener tokener = new JSONTokener(repost_resp);
			JSONObject object = (JSONObject) tokener.nextValue();
			System.out.println("max_id=" + max_id);
			max_id = object.getLong("next_cursor");
			JSONArray reposts_list = object.getJSONArray("reposts");
			for (int i = 0; i < reposts_list.length(); i++) {
				JSONObject repost = reposts_list.getJSONObject(i);
				// 单个节点的信息变量
				String wid = repost.getString("idstr");
				String repost_name = repost.getJSONObject("user").getString(
						"screen_name");
				String uid = repost.getJSONObject("user").getString("idstr");
				String post_time = repost.getString("created_at");
				String temp = repost.getString("text");
				String text = "";
				ArrayList<String> repost_link = new ArrayList<String>();
				String parent_name = "";
				int level;
				int count = 0;
				// int user_count = i + (page - 1) * 200;
				// System.out.println("第" + user_count + "个用户的原始微博：" + temp);
				if (isRepost) {
					// 如果该指定分析的微博也是一条转发微博，则进行处理（删掉源头内容信息）
					if (Pattern.compile("//@" + show_name + ".*").matcher(temp)
							.find()) {
						// 若用户没有对转发的内容进行修改则继续：
						temp = Pattern.compile("//@" + show_name + ".*")
								.matcher(temp).replaceAll("");

					} else {
						repost_link.add(show_name);// 默认的转发链中有指定分析的微博
						level = count + 1;// 转发级数
						text = temp;
						parent_name = show_name;
						NodeData nodeData = new NodeData(level, repost_name,
								parent_name, text, wid, uid, post_time,
								repost_link);
						map.put(repost_name + "-" + level, nodeData);
						continue;
					}
				}
				// 首先判断是否为二次转发（忽略用户自己删除掉@符号的情形）
				if (Pattern.compile("//@").matcher(temp).find()) {
					// System.out.println("第" + user_count + "个用户的处理前的微博：" +
					// temp);
					// 提取该用户自己添加的内容text
					Pattern p1 = Pattern
							.compile("^[^(//@)|^\\s].+?[^\u4e00-\u9fa5]?//@");// 终于凑出的匹配表达式
					Matcher m1 = p1.matcher(temp);
					if (m1.find()) {
						// System.out.println("第" + user_count + "个用户的匹配内容："
						// + m1.group());
						text = Pattern.compile("//@").matcher(m1.group())
								.replaceAll("");
					} else {
						// System.out.println("第" + user_count + "个用户的匹配内容：" +
						// "");
					}

					// 提取此节点为几次转发，并提出母节点的用户名
					Pattern p2 = Pattern
							.compile("//@[\u4e00-\u9fa5|\\w].+?[:|：]");
					Matcher m2 = p2.matcher(temp);
					while (m2.find()) {
						count++;
						String t = Pattern.compile("//@").matcher(m2.group())
								.replaceAll("");
						t = Pattern.compile("[:|：]").matcher(t).replaceAll("");
						repost_link.add(t);
						// System.out.println("匹配到的转发名：" + t);
						if (count == 1) {
							parent_name = t;
						}
					}

				} else {
					text = temp;
					parent_name = show_name;
					// System.out.println("第" + user_count + "个用户的处理前的微博：" +
					// temp);
					// System.out.println("第" + user_count + "个用户的匹配内容：" +
					// temp);
				}
				// System.out.println("第" + user_count + "个用户的微博：" + text);//
				// 所得结果为正确的
				repost_link.add(show_name);// 默认的转发链中有指定分析的微博
				level = count + 1;// 转发级数
				NodeData nodeData = new NodeData(level, repost_name,
						parent_name, text, wid, uid, post_time, repost_link);
				map.put(repost_name + "-" + level, nodeData);
			}
			if (page < max_page)
				max_id = reposts_list.getJSONObject(reposts_list.length() - 1)
						.getLong("id");
		}
		setRelationship(map);
		// int n = 0;
		// for (NodeData data : map.values()) {
		// System.out.println("第" + ((n++) + 1) + "个数据：" + data);
		// }
		System.out.println("总共处理了" + map.size() + "个转发微博");
		return map;
	}

	private static void setRelationship(Map<String, NodeData> map) {
		int count = 0;
		for (NodeData data : map.values()) {
			// 首先获取该节点的母节点wid
			if (data.getLevel() != 0)
				try {
					NodeData parent_data = map.get(data.getParent_name() + "-"
							+ (data.getLevel() - 1));
					data.setParent_wid(parent_data.getWid());
					if (!parent_data.getChilds_wid().contains(data.getWid())) {
						parent_data.addChild(data.getWid());
					}
				} catch (Exception e) {
					// TODO: handle exception
					count++;
					NodeData parent_data = map.get(show_name + "-0");
					data.setParent_wid(parent_data.getWid());
					if (!parent_data.getChilds_wid().contains(data.getWid())) {
						parent_data.addChild(data.getWid());
					}
				}
		}
		System.out.println("无法找到母节点的点个数：" + count);
	}

	/**
	 * 统计转发的时间曲线
	 * 
	 * @param map
	 */
	public void countCurve(Map<String, NodeData> map) {
		final long time_interval = 5 * 60000;// 统计的时间间隔为5分钟
		// 首先找到根节点的信息
		String startTime = map.get(show_name + "-0").getPost_time();
		List<Long> timelist = new ArrayList<Long>();
		for (NodeData data : map.values()) {
			long nodeTime = parseTime(data.getPost_time());
			timelist.add(nodeTime);
		}
	}

	public static long parseTime(String time) {
		Date date = new Date(time);
		// System.out.println(sdf.parse(a));
		System.out.println(date.getTime());
		return date.getTime();
	}
}
