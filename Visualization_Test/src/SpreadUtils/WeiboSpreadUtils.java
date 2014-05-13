package SpreadUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import ui.SpreadNaviActivity;
import ui.SpreadTimelineActivity;
import FastForce.FastForceDirected;
import Nodes.SpreadNodeData;
import SpreadUtils.PlotUtils.IDrawableNode;

public class WeiboSpreadUtils {
	public static String ACCESS_TOKEN = "2.00t3nVnCe3dgkC63ac35576efRXPWC";

	// private static String show_wid;
	public static String show_name;
	private static int max_count = 1950;

	private static Context context;
	
	private static JSONObject root;// 用于保存整体传播分析的信息

	/**
	 * 进行单条微博的传播分析
	 * 
	 * @param URL
	 *            该条微博的url地址
	 * @param filename
	 *            输出gexf文件的路径及名字
	 * @param time_interval
	 *            转发曲线的时间间隔，以分钟为单位
	 * @return int[]形式的转发曲线坐标
	 */
	public static Map<String, IDrawableNode> WeiboSpread(String URL,
			String filename, int time_interval) {

		Map<String, SpreadNodeData> map = null;
		try {
			map = WeiboSpreadUtils.createMapByURL(URL);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		// 进行转发时间统计
		SpreadTimelineActivity.curve = WeiboSpreadUtils.countCurve(map,
				time_interval);
		// for (int i = 0; i < curve.length; i++) {
		// System.out.println("第" + i + "个时间段的转发数=" + curve[i]);
		// }
		System.out.println("=========================");
		Map<String, IDrawableNode> info_map = PlotUtils
				.getInfoMapByDataMap(map);
		if (info_map == null) {
			System.out.println("生成InfoMap发生错误！");
			return null;
		}
		System.out.println("开始布点计算……");
		// SpreadNaviActivity.setDialogMSG("开始布点计算");
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
		// GexfUtils.createGexf(info_map, filename);
		return info_map;
	}

	/**
	 * 根据单条微博的url生成相应的转发微博map；并将该条微博内容传给SpreadTimelineActivity
	 * 
	 * @param url
	 *            单条微博的完整url
	 * @return
	 * @author Jayvee
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws Exception
	 * @throws WeiboSpreadException
	 *             所分析的转发微博应大于1条且不应该超过1900条
	 * @throws JSONException
	 * 
	 */
	public static Map<String, SpreadNodeData> createMapByURL(String url)
			throws WeiboSpreadException, MalformedURLException, IOException,
			JSONException {
		// 调用API的地址，GET方式
		String rptl_URL = "https://api.weibo.com/2/statuses/repost_timeline.json?";
		String show_URL = "https://api.weibo.com/2/statuses/show.json?";

		String show_wid = WeiboIDUtils.Base62Util.fullUrl2mid(url);
		Map<String, SpreadNodeData> map = new HashMap<String, SpreadNodeData>();
		String show_resp = NetworkUtils.getReq(show_URL + "access_token="
				+ ACCESS_TOKEN + "&id=" + show_wid);
		// //首先获取该条微博的信息
		JSONTokener tokener_show = new JSONTokener(show_resp);
		JSONObject object_show = (JSONObject) tokener_show.nextValue();
		String show_text = object_show.getString("text");
		String show_created_at = object_show.getString("created_at");
		show_name = object_show.getJSONObject("user").getString("screen_name");

		SpreadTimelineActivity.weiboText = show_name + ":" + show_text;// 将该条微博内容传给SpreadTimelineActivity

		int show_repost_count = object_show.getInt("reposts_count");
		System.out.println("转发总数：" + show_repost_count);
		int max_page = 0;
		if (show_repost_count > max_count) {
			System.out.println("转发数大于" + max_count + ",可能丢失应有的转发关系！");
			// throw new WeiboSpreadException(
			// WeiboSpreadException.REPOST_COUNT_ERROR);
			max_page = 10;
		} else {
			max_page = (int) Math.ceil(show_repost_count / 200f);// 向上取整
		}
		if (show_repost_count == 0) {
			// System.out.println("转发数为0！");
			throw new WeiboSpreadException(
					WeiboSpreadException.NONE_REPOST_ERROR);
		}
		String show_uid = object_show.getJSONObject("user").getString("idstr");
		boolean isRepost = object_show.optJSONObject("retweeted_status") != null;
		ArrayList<String> show_repost_link = new ArrayList<String>();
		show_repost_link.add(show_name);
		SpreadNodeData showData = new SpreadNodeData(0, show_name, show_name,
				show_text, show_wid, show_uid, show_created_at,
				show_repost_link, show_repost_count);
		map.put(show_name + "-0", showData);// 首先在map中加入根节点信息
		System.out.println(showData.getName() + ":" + showData.getText());
		// int page = 1;
		// long max_id = 0;
		System.out.println(show_repost_count / 200 + "|||max_page=" + max_page);
		for (int page = 1; page <= max_page; page++) {
			System.out.println("第" + page + "页读取开始");
			// Toast.makeText(,"总共" + max_page
			// + "页，正在读取第" + page + "页,请稍后。", Toast.LENGTH_SHORT);
			// SpreadNaviActivity.setDialogMSG("总共" + max_page + "页，正在读取第" +
			// page
			// + "页,请稍后。");
			
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
			// System.out.println("max_id=" + max_id);
			// max_id = object.getLong("next_cursor");
			JSONArray reposts_list = object.getJSONArray("reposts");
			// SpreadNaviActivity.progressDialog.setMessage("正在处理转发微博……");
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
				String province = repost.getJSONObject("user").getString(
						"province");
				int repost_count = repost.getInt("reposts_count");// 该节点的转发总数
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
						SpreadNodeData nodeData = new SpreadNodeData(level,
								repost_name, parent_name, text, wid, uid,
								post_time, repost_link, repost_count);
						map.put(repost_name + "-" + level, nodeData);
						continue;
					}
				}
				// 首先判断是否为二次转发（忽略用户自己删除掉@符号的情形）
				if (Pattern.compile("//@").matcher(temp).find()) {
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
				SpreadNodeData nodeData = new SpreadNodeData(level,
						repost_name, parent_name, text, wid, uid, post_time,
						repost_link, repost_count);
				map.put(repost_name + "-" + level, nodeData);
			}
		}
		setRelationship(map);
		System.out.println("总共处理了" + map.size() + "个转发微博");
		return map;
	}

	private static void setRelationship(Map<String, SpreadNodeData> map) {
		int count = 0;
		for (SpreadNodeData data : map.values()) {
			// 首先获取该节点的母节点wid
			if (data.getLevel() != 0)
				try {
					SpreadNodeData parent_data = map.get(data.getParent_name()
							+ "-" + (data.getLevel() - 1));
					data.setParent_wid(parent_data.getWid());
					if (!parent_data.getChilds_wid().contains(data.getWid())) {
						parent_data.addChild(data.getWid());
					}
				} catch (Exception e) {
					count++;
					// 如果没有找到母节点，则将母节点设为根节点
					SpreadNodeData parent_data = map.get(show_name + "-0");
					data.setParent_wid(parent_data.getWid());
					if (!parent_data.getChilds_wid().contains(data.getWid())) {
						parent_data.addChild(data.getWid());
					}
				}
		}
		System.out.println("无法找到母节点的点个数：" + count);
	}

	/**
	 * 统计转发的时间曲线;将转发数排名传给SpreadTimelineActivity
	 * 
	 * @param map
	 * @param interval
	 *            时间间隔，以分钟计
	 */
	@SuppressWarnings("unchecked")
	public static int[] countCurve(Map<String, SpreadNodeData> map, int interval) {
		// 转发数统计排名
		ArrayList<SpreadNodeData> arrayCountList = new ArrayList<SpreadNodeData>(
				map.values());
		Collections.sort(arrayCountList,
				new SpreadNodeData.DescRepostCountComparator());
		for (int i = 0; i < arrayCountList.size(); i++) {
			arrayCountList.get(i).repost_rank = i + 1;// 设置转发排名
		}
		SpreadTimelineActivity.repostRank = (ArrayList<SpreadNodeData>) arrayCountList
				.clone();
		// 发送时间排序
		// ArrayList<SpreadNodeData> arrayTimeList = new
		// ArrayList<>(map.values());
		Collections.sort(arrayCountList);// 以发送时间进行排序
		final long time_interval = interval * 60000;// 统计的时间间隔为5分钟
		List<Long> timelist = new ArrayList<Long>();
		for (SpreadNodeData data : arrayCountList) {
			long nodeTime = parseTime(data.getPost_time());
			timelist.add(nodeTime);
		}
		long startTime = timelist.get(0);
		SpreadTimelineActivity.startTime = startTime;
		long endTime = timelist.get(timelist.size() - 1);
		// double temp = Math.ceil((endTime - startTime) / time_interval);
		int arraylen = (int) Math.ceil((endTime - startTime) / time_interval);
		int[] countArray = new int[arraylen + 1];
		for (int i = 0; i < timelist.size(); i++) {
			int number = (int) (Math.floor((timelist.get(i) - startTime)
					/ time_interval));
			countArray[number]++;
		}
		return countArray;
	}

	/**
	 * 由新浪微博返回的字符串形式的时间转换为毫秒形式的时间
	 * 
	 * @param time
	 *            类似“Wed May 07 13:01:00 +0800 2014”格式的时间
	 * @return long型的毫秒形式时间
	 */
	public static long parseTime(String time) {
		Date date = new Date(time);
		// System.out.println(sdf.parse(a));
		// System.out.println(date.getTime());
		return date.getTime();
	}

	public static Context getContext() {
		return context;
	}

	public static void setContext(Context context) {
		WeiboSpreadUtils.context = context;
	}

	static class WeiboSpreadException extends Exception {
		/**
		 * 自定义的微博传播分析异常
		 */
		private static final long serialVersionUID = 5280258533480586329L;
		public static final String REPOST_COUNT_ERROR = "微博转发数大于限定值！";
		public static final String NONE_REPOST_ERROR = "微博没有转发数！";

		public WeiboSpreadException(String str) {
			// TODO Auto-generated constructor stub
			super(str);
		}

	}
}
