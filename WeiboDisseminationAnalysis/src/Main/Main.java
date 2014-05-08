package Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import FastForce.FastForceDirected;
import SpreadUtils.GexfUtils;
import SpreadUtils.PlotUtils;
import SpreadUtils.WeiboSpreadUtils;
import SpreadUtils.PlotUtils.IDrawableNode;

public class Main {

	public static void main(String args[]) throws IOException {
		int interval = 5;
		String url = "http://weibo.com/1086233511/B3myg7bpm?ref=home";
		int[] count = WeiboSpreadUtils.WeiboSpread(url,
				System.currentTimeMillis() + ".gexf", interval);
		File file = new File(System.currentTimeMillis() + "-count.txt");
		FileOutputStream os = new FileOutputStream(file);
		FileWriter fw = new FileWriter(file);
		fw.append("url="+url+"\n");
		fw.append("interval="+interval+"\n");
		fw.append("counts=\n");
		for (int i = 0; i < count.length; i++) {
			String str = String.valueOf(count[i]) + " ";
			fw.append(str);
		}
		os.flush();
		os.close();
	}

}
