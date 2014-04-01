package Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import JsonUtils.WeiboData;
import android.content.res.Resources;
import android.graphics.Color;

public class GexfUtils {
	public Map<Long, WeiboData> gexfDecoder(Resources res, String Filename) {
		SAXReader saxReader = new SAXReader();
		Document document = null;
		Map<Long, WeiboData> map = new HashMap<Long, WeiboData>();
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
			long id = Long.parseLong(node.attribute("id").getText());
			String label = node.attribute("label").getText();
			List<Element> viz = node.content();
			Element color = viz.get(0);
			int color_b = Integer.parseInt(color.attribute("b").getText());
			int color_g = Integer.parseInt(color.attribute("g").getText());
			int color_r = Integer.parseInt(color.attribute("r").getText());
			int color_int = Color.argb(255, color_r, color_g, color_b);
			Element position = viz.get(1);
			float x = Float.parseFloat(position.attribute("x").getText());
			float y = Float.parseFloat(position.attribute("y").getText());
			float z = Float.parseFloat(position.attribute("z").getText());
			Element size = viz.get(2);
			float value = Float.parseFloat(size.attribute("value").getText());
			Element image = viz.get(3);
			String imageURI = image.attribute("uri").getText();
			System.out.println(label);
			WeiboData weibodata = new WeiboData(x, y, z, label, id, color_int,
					value, imageURI);
			map.put(id, weibodata);
		}
		for (int i = 0; i < edges.size(); i++) {
			Element edge = edges.get(i);
			long source_id = Long.parseLong(edge.attribute("source").getText());
			long target_id = Long.parseLong(edge.attribute("target").getText());
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
