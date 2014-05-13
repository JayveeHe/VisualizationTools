package SpreadUtils;

import it.uniroma1.dis.wiserver.gexf4j.core.EdgeType;
import it.uniroma1.dis.wiserver.gexf4j.core.Gexf;
import it.uniroma1.dis.wiserver.gexf4j.core.Graph;
import it.uniroma1.dis.wiserver.gexf4j.core.Mode;
import it.uniroma1.dis.wiserver.gexf4j.core.Node;
import it.uniroma1.dis.wiserver.gexf4j.core.data.AttributeClass;
import it.uniroma1.dis.wiserver.gexf4j.core.data.AttributeList;
import it.uniroma1.dis.wiserver.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wiserver.gexf4j.core.impl.StaxGraphWriter;
import it.uniroma1.dis.wiserver.gexf4j.core.impl.data.AttributeListImpl;
import it.uniroma1.dis.wiserver.gexf4j.core.impl.viz.ColorImpl;
import it.uniroma1.dis.wiserver.gexf4j.core.impl.viz.PositionImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import SpreadUtils.PlotUtils.IDrawableNode;
import android.graphics.Color;

public class GexfUtils {
	/**
	 * 解析gexf文件
	 * 
	 * 
	 * 
	 * @param Filename
	 * @throws FileNotFoundException
	 */
	public static void gexfDecoder(String Filename)
			throws FileNotFoundException {
		SAXReader saxReader = new SAXReader();
		Document document = null;
		BufferedReader br = new BufferedReader(new FileReader(
				new File(Filename)));
		try {
			document = saxReader.read(br);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element root = document.getRootElement();
		Element graph = (Element) root.element("graph");
		List<Element> nodes = graph.element("nodes").elements("node");
		List<Element> edges = graph.elements("edges");
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
			Element position = viz.get(1);
			float x = Float.parseFloat(position.attribute("x").getText());
			float y = Float.parseFloat(position.attribute("y").getText());
			float z = Float.parseFloat(position.attribute("z").getText());
			Element size = viz.get(2);
			float value = Float.parseFloat(size.attribute("value").getText());
			Element image = viz.get(3);
			String imageURI = image.attribute("uri").getText();
			System.out.println(label);
		}
		for (int i = 0; i < edges.size(); i++) {
			Element edge = edges.get(i);

		}
	}

	/**
	 * 根据数据生成gexf文件
	 * 
	 * @param map
	 *            包含{@link IDrawableNode}接口的map
	 * @param output_path
	 *            输出文件的路径
	 * @author Jayvee
	 */
	public static void createGexf(Map<String, IDrawableNode> map,
			String output_path) {
		Gexf gexf = new GexfImpl();
		Calendar date = Calendar.getInstance();

		gexf.getMetadata().setLastModified(date.getTime())
				.setCreator("Jayvee_He").setDescription("微博传播分析图");
		gexf.setVisualization(true);

		Graph graph = gexf.getGraph();
		graph.setDefaultEdgeType(EdgeType.UNDIRECTED).setMode(Mode.STATIC);

		AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);
		graph.getAttributeLists().add(attrList);

		// 结点的设置
		int i = 0;
//		ArrayList<Node> nodelist = new ArrayList<Node>();
		Map<String, Node> nodemap = new HashMap<String, Node>();
		for (IDrawableNode data : map.values()) {
			Node node = graph.createNode(i + "");
			node.setLabel(data.getLabel());
			node.setColor(new ColorImpl(Color.red(data.getColor()), Color
					.green(data.getColor()), Color.blue(data.getColor())));
			node.setPosition(new PositionImpl(data.getComputableX(), data
					.getComputableY(), 0));
			node.setSize(data.getRadius());
			// nodelist.add(node);
			nodemap.put(data.getID(), node);
			i++;
		}
		// 边的设置
		for (IDrawableNode data : map.values()) {

			if (data.getChildIDs().size() != 0)// 即若有子节点
			{
				ArrayList<String> child_wids = data.getChildIDs();
				for (String child_wid : child_wids) {
					nodemap.get(data.getID()).connectTo(nodemap.get(child_wid));
				}
			}
		}

		StaxGraphWriter graphWriter = new StaxGraphWriter();
		File f = new File(output_path);
		Writer out;
		try {
			out = new FileWriter(f, false);
			graphWriter.writeToStream(gexf, out, "UTF-8");
			System.out.println(f.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
