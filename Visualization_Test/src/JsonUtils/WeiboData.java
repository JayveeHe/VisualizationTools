package JsonUtils;

import java.util.ArrayList;

public class WeiboData {
	public String Key;
	public float X;
	public float Y;
	public float Z;
	public int Group;
	public int color;
	public String imageURI;
	public float size =0;
	public WeiboData parent;
	public ArrayList<WeiboData> childs;
	public String ID;

	public WeiboData(float x, float y, float z, String key, String id,
			int color, float size, String imageURI) {
		this.X = x;
		this.Y = y;
		this.Z = z;
		this.Key = key;
		this.ID = id;
		this.color = color;
		this.imageURI = imageURI;
		this.size = size;
		childs = new ArrayList<WeiboData>();
	}

	public WeiboData(float x, float y, float z, int group, String key, String id) {
		this.X = x;
		this.Y = y;
		this.Z = z;
		this.Key = key;
		this.Group = group;
		this.ID = id;
		childs = new ArrayList<WeiboData>();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ("Key=" + Key + "\tx=" + X + "\ty=" + Y + "\tz=" + Z
				+ "\tgroup=" + Group);
	}
}
