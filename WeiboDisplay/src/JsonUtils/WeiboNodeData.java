package JsonUtils;

import java.util.ArrayList;

import JsonUtilsDemo.WeiboData;
import SpreadUtils.PlotUtils.IDrawableNode;

public class WeiboNodeData implements IDrawableNode {
	public String Key;
	public float X;
	public float Y;
	public float Z;
	public float Radius;
	public int Group;
	public int color;
	public String imageURI;
	public float size = 0;
	public WeiboNodeData parent;
	public ArrayList<WeiboNodeData> childs;
	public String ID;

	public WeiboNodeData(float x, float y, float z, String key, String id,
			int color, float size, String imageURI) {
		this.X = x;
		this.Y = y;
		this.Z = z;
		this.Key = key;
		this.ID = id;
		this.color = color;
		this.imageURI = imageURI;
		this.size = size;
		childs = new ArrayList<WeiboNodeData>();
	}

	public WeiboNodeData(float x, float y, float z, int group, String key,
			String id) {
		this.X = x;
		this.Y = y;
		this.Z = z;
		this.Key = key;
		this.Group = group;
		this.ID = id;
		childs = new ArrayList<WeiboNodeData>();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ("Key=" + Key + "\tx=" + X + "\ty=" + Y + "\tz=" + Z
				+ "\tgroup=" + Group);
	}

	@Override
	public float getComputableX() {
		// TODO Auto-generated method stub
		return X;
	}

	@Override
	public float getComputableY() {
		// TODO Auto-generated method stub
		return Y;
	}

	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return ID;
	}

	@Override
	public String getParentID() {
		// TODO Auto-generated method stub
		return parent.ID;
	}

	@Override
	public ArrayList<String> getChildIDs() {
		// TODO Auto-generated method stub
		ArrayList<String> childIDs = new ArrayList<String>();
		for (WeiboNodeData child : childs) {
			childIDs.add(child.getID());
		}
		return childIDs;
	}

	@Override
	public int getColor() {
		// TODO Auto-generated method stub
		return color;
	}

	@Override
	public float getRadius() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return Key;
	}

	@Override
	public void setComputableX(float x) {
		// TODO Auto-generated method stub
		this.X = x;
	}

	@Override
	public void setComputableY(float y) {
		// TODO Auto-generated method stub
		this.Y = y;
	}

	@Override
	public void setColor(int color) {
		// TODO Auto-generated method stub
		this.color = color;
	}

	@Override
	public void setRadius(float radius) {
		// TODO Auto-generated method stub
		this.Radius = radius;
	}
}
