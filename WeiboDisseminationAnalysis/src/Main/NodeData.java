package Main;

import java.util.ArrayList;

/**
 * 定义用于微博传播分析的基本节点信息类
 * 
 * @author Jayvee
 * 
 */
public class NodeData {

	int level;// 该节点的转发级数
	String name;// 该节点的用户名
	String parent_name;// 该节点的母节点用户名
	String text;// 该节点的用户所发的内容
	String wid;// 该节点代表的微博的id，此值唯一，应该作为主键
	String uid;// 该节点用户的id，唯一
	private String post_time;// 该节点微博发送的时间
	ArrayList<String> repost_link;// 转发路径链，最小大小为1

	String parent_wid = "-1";// 默认母节点id为-1，作为检测哨兵
	ArrayList<String> childs_wid = new ArrayList<String>();

	/**
	 * NodeData的完整构造函数
	 * 
	 * @param level
	 *            该节点的转发级数
	 * @param name
	 *            该节点的用户名
	 * @param parent_name
	 *            该节点的母节点用户名
	 * @param text
	 *            该节点的用户所发的内容
	 * @param wid
	 *            该节点代表的微博的id，此值唯一，应该作为主键
	 * @param uid
	 *            该节点用户的id，唯一
	 * @param post_time
	 *            该节点微博发送的时间
	 * @param ArrayList
	 *            <String> repost_link 转发链，最小大小为1
	 * @author Jayvee
	 */
	public NodeData(int level, String name, String parent_name, String text,
			String wid, String uid, String post_time,
			ArrayList<String> repost_link) {
		super();
		this.level = level;
		this.name = name;
		this.parent_name = parent_name;
		this.text = text;
		this.wid = wid;
		this.uid = uid;
		this.post_time = post_time;
		this.repost_link = repost_link;
	}

	public NodeData(String wid) {
		this.wid = wid;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParent_name() {
		return parent_name;
	}

	public void setParent_name(String parent_name) {
		this.parent_name = parent_name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getWid() {
		return wid;
	}

	public void setWid(String wid) {
		this.wid = wid;
	}

	public String getParent_wid() {
		return parent_wid;
	}

	public void setParent_wid(String parent_wid) {
		this.parent_wid = parent_wid;
	}

	public ArrayList<String> getChilds_wid() {
		return childs_wid;
	}

	public void setChilds_wid(ArrayList<String> childs_wid) {
		this.childs_wid = childs_wid;
	}
	
	public void addChild(String child_id)
	{
		this.childs_wid.add(child_id);
	}

	public String getPost_time() {
		return post_time;
	}

	public void setPost_time(String post_time) {
		this.post_time = post_time;
	}

	@Override
	public String toString() {
		return "NodeData [level=" + level + ", name=" + name + ", parent_name="
				+ parent_name + ", text=" + text + ", wid=" + wid + ", uid="
				+ uid + ", post_time=" + post_time + ", repost_link="
				+ repost_link + ", parent_wid=" + parent_wid + ", childs_wid="
				+ childs_wid + "]";
	}


}
