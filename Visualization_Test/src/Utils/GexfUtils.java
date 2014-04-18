package Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream.GetField;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import JsonUtils.WeiboData;
import android.content.res.Resources;
import android.graphics.Color;
import android.provider.OpenableColumns;
import android.text.GetChars;
import android.util.Log;

public class GexfUtils {
	private final static String DEBUG_TAG = "Gexf工具";

	/**
	 * 以resources中assets资源名为参数进行解析gexf文件
	 * 
	 * @param res
	 * @param Filename
	 * @return
	 * @author Jayvee
	 */
	public static Map<String, WeiboData> gexfDecoder(Resources res,
			String Filename) {

		SAXReader saxReader = new SAXReader();
		Document document = null;
		Map<String, WeiboData> map = new HashMap<String, WeiboData>();
		try {
			InputStream inpt_strm = res.getAssets().open(Filename);
			document = saxReader.read(inpt_strm);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Element root = document.getRootElement();
		Element graph = (Element) root.element("graph");
		List<Element> nodes = graph.element("nodes").elements("node");
		List<Element> edges = graph.element("edges").elements("edge");
		// 读取节点信息
		for (int i = 0; i < nodes.size(); i++) {
			Element node = nodes.get(i);
			// String sss = node.attribute("id").getText();
			String id = node.attribute("id").getText();
			String label = "";
			try {
				label = node.attribute("label").getText();
			} catch (Exception e) {
				Log.e(DEBUG_TAG, "读取label错误");
			}
			// List<Element> viz = node.content();
			Element color = node.element("color");
			// Element color = viz.get(0);
			int color_b = Integer.parseInt(color.attribute("b").getText());
			int color_g = Integer.parseInt(color.attribute("g").getText());
			int color_r = Integer.parseInt(color.attribute("r").getText());
			int color_int = Color.argb(255, color_r, color_g, color_b);
			Element position = node.element("position");
			float x = Float.parseFloat(position.attribute("x").getText());
			float y = Float.parseFloat(position.attribute("y").getText());
			Element size = node.element("size");
			float value = Float.parseFloat(size.attribute("value").getText());
			String imageURI = "null";
			try {
				Element other = node.element("node-shape");
				imageURI = other.attribute("uri").getText();
			} catch (Exception e) {
				// e.printStackTrace();
				Log.e(DEBUG_TAG, "读取附加对象错误");
			}
			System.out.println(label);
			WeiboData weibodata = new WeiboData(x, y, 0, label, id, color_int,
					value, imageURI);
			map.put(id, weibodata);
		}
		// 读取边信息（即节点关系）
		for (int i = 0; i < edges.size(); i++) {
			Element edge = edges.get(i);
			String source_id = edge.attribute("source").getText();
			String target_id = edge.attribute("target").getText();
			WeiboData source = map.get(source_id);
			WeiboData target = map.get(target_id);
			if (source.parent == null)// 即还没有找到母节点
			{
				source.parent = target;
				target.childs.add(source);
			} else {
				target.parent = source;
				source.childs.add(target);
			}
		}
		return map;
	}

	/**
	 * 以文件形式进行解析gexf文件
	 * 
	 * @param file
	 * @return 符合view需求的map
	 * @author Jayvee
	 */
	public static Map<String, WeiboData> gexfDecoder(File file) {

		SAXReader saxReader = new SAXReader();
		Document document = null;
		Map<String, WeiboData> map = new HashMap<String, WeiboData>();
		try {
//			 InputStream inpt_strm = ;
			document = saxReader.read(file);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Element root = document.getRootElement();
		Element graph = (Element) root.element("graph");
		List<Element> nodes = graph.element("nodes").elements("node");
		List<Element> edges = graph.element("edges").elements("edge");
		// 读取节点信息
		for (int i = 0; i < nodes.size(); i++) {
			Element node = nodes.get(i);
			// String sss = node.attribute("id").getText();
			String id = node.attribute("id").getText();
			String label = "";
			try {
				label = node.attribute("label").getText();
			} catch (Exception e) {
				Log.e(DEBUG_TAG, "读取label错误");
			}
			// List<Element> viz = node.content();
			Element color = node.element("color");
			// Element color = viz.get(0);
			int color_b = Integer.parseInt(color.attribute("b").getText());
			int color_g = Integer.parseInt(color.attribute("g").getText());
			int color_r = Integer.parseInt(color.attribute("r").getText());
			int color_int = Color.argb(255, color_r, color_g, color_b);
			Element position = node.element("position");
			float x = Float.parseFloat(position.attribute("x").getText());
			float y = Float.parseFloat(position.attribute("y").getText());
			Element size = node.element("size");
			float value = Float.parseFloat(size.attribute("value").getText());
			String imageURI = "null";
			try {
				Element other = node.element("node-shape");
				imageURI = other.attribute("uri").getText();
			} catch (Exception e) {
				// e.printStackTrace();
				Log.e(DEBUG_TAG, "读取附加对象错误");
			}
//			System.out.println(label);
			WeiboData weibodata = new WeiboData(x, y, 0, label, id, color_int,
					value, imageURI);
			map.put(id, weibodata);
		}
		// 读取边信息（即节点关系）
		for (int i = 0; i < edges.size(); i++) {
			Element edge = edges.get(i);
			String source_id = edge.attribute("source").getText();
			String target_id = edge.attribute("target").getText();
			WeiboData source = map.get(source_id);
			WeiboData target = map.get(target_id);
			if (source.parent == null)// 即还没有找到母节点
			{
				source.parent = target;
				target.childs.add(source);
			} else {
				target.parent = source;
				source.childs.add(target);
			}
		}
		return map;
	}
	
	public static Map<String, WeiboData> gexfDecoder(InputStream inpt_strm) {

		SAXReader saxReader = new SAXReader();
		Document document = null;
		Map<String, WeiboData> map = new HashMap<String, WeiboData>();
		try {
			document = saxReader.read(inpt_strm);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Element root = document.getRootElement();
		Element graph = (Element) root.element("graph");
		List<Element> nodes = graph.element("nodes").elements("node");
		List<Element> edges = graph.element("edges").elements("edge");
		// 读取节点信息
		for (int i = 0; i < nodes.size(); i++) {
			Element node = nodes.get(i);
			// String sss = node.attribute("id").getText();
			String id = node.attribute("id").getText();
			String label = "";
			try {
				label = node.attribute("label").getText();
			} catch (Exception e) {
				Log.e(DEBUG_TAG, "读取label错误");
			}
			// List<Element> viz = node.content();
			Element color = node.element("color");
			// Element color = viz.get(0);
			int color_b = Integer.parseInt(color.attribute("b").getText());
			int color_g = Integer.parseInt(color.attribute("g").getText());
			int color_r = Integer.parseInt(color.attribute("r").getText());
			int color_int = Color.argb(255, color_r, color_g, color_b);
			Element position = node.element("position");
			float x = Float.parseFloat(position.attribute("x").getText());
			float y = Float.parseFloat(position.attribute("y").getText());
			Element size = node.element("size");
			float value = Float.parseFloat(size.attribute("value").getText());
			String imageURI = "null";
			try {
				Element other = node.element("node-shape");
				imageURI = other.attribute("uri").getText();
			} catch (Exception e) {
				// e.printStackTrace();
				Log.e(DEBUG_TAG, "读取附加对象错误");
			}
			System.out.println(label);
			WeiboData weibodata = new WeiboData(x, y, 0, label, id, color_int,
					value, imageURI);
			map.put(id, weibodata);
		}
		// 读取边信息（即节点关系）
		for (int i = 0; i < edges.size(); i++) {
			Element edge = edges.get(i);
			String source_id = edge.attribute("source").getText();
			String target_id = edge.attribute("target").getText();
			WeiboData source = map.get(source_id);
			WeiboData target = map.get(target_id);
			if (source.parent == null)// 即还没有找到母节点
			{
				source.parent = target;
				target.childs.add(source);
			} else {
				target.parent = source;
				source.childs.add(target);
			}
		}
		return map;
	}

}
