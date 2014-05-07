package FastForce;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import FastForce.Convert;

/**
 * 用于进行快速力引导布点算法的工具类
 * 
 * @author Tao
 * 
 */
public class FastForceDirected {
	private double[][] script(int[] adjacency, int nodeNum) throws IOException {
		YifanHuLayout yifanHuLayout = new YifanHuLayout(nodeNum);
		yifanHuLayout.resetPropertiesValues();
		yifanHuLayout.initAlgo(adjacency);
		for (int i = 0; i < 20000 && yifanHuLayout.canAlgo(); i++) {
			yifanHuLayout.goAlgo();
		}
		yifanHuLayout.endAlgo();
		double[] xcoor = yifanHuLayout.getX();
		double[] ycoor = yifanHuLayout.getY();

		if (nodeNum >= 1000) {
			// in case of the first object yifanHuLayout cannot acquire a nice
			// Graph
			YifanHuLayout yifanHuLayout2 = new YifanHuLayout(nodeNum);
			yifanHuLayout2.resetPropertiesValues();
			yifanHuLayout2.initAlgo(adjacency, xcoor, ycoor);
			for (int i = 0; i < 20000 && yifanHuLayout2.canAlgo(); i++)
				yifanHuLayout2.goAlgo();
			yifanHuLayout2.endAlgo();
			xcoor = yifanHuLayout2.getX();
			ycoor = yifanHuLayout2.getY();
		}

		if (nodeNum >= 3000) {
			// in case of the first object yifanHuLayout cannot acquire a nice
			// Graph
			YifanHuLayout yifanHuLayout3 = new YifanHuLayout(nodeNum);
			yifanHuLayout3.resetPropertiesValues();
			yifanHuLayout3.initAlgo(adjacency, xcoor, ycoor);
			for (int i = 0; i < 20000 && yifanHuLayout3.canAlgo(); i++)
				yifanHuLayout3.goAlgo();
			yifanHuLayout3.endAlgo();
			xcoor = yifanHuLayout3.getX();
			ycoor = yifanHuLayout3.getY();
		}

		int loopnumber = 0;
		double[][] result = new double[nodeNum][];
		for (int i = 0; i < 2; i++) {
			result[i] = new double[nodeNum];
		}

		for (int i = 0; i < nodeNum; i++) {
			// result[0][i]=xcoor[i]*5;
			// result[1][i]=ycoor[i]*5;
			result[0][i] = xcoor[i];
			result[1][i] = ycoor[i];
		}
		// writeFile("/home/weitao/matlab/budian/position.txt",result);
		return result;

		// for(IPositionComputeable n:this.Nodes)
		// {
		// n.setPositionX(xcoor[loopnumber]*5);
		// n.setPositionY(ycoor[loopnumber]*5);
		// loopnumber++;
		// }

		// double[][] coordinate = new double[2][nodeNum];
		// for (int i = 0; i < nodeNum; i++) {
		// coordinate[0][i] = xcoor[i];
		// coordinate[1][i] = ycoor[i];
		// }
		// return coordinate;
	}

	// public int[] Convert()
	// {
	// List<Integer> result=new ArrayList<Integer>();
	// int len=this.relationMatrix.getSize();
	// for(int i=0;i<len;i++)
	// {
	// for(int j=0;j<i;j++)
	// {
	// if(this.relationMatrix.IsLinked(i, j))
	// {
	// result.add(i);
	// result.add(j);
	// }
	// }
	// }
	// int resultlen=result.size();
	// int[] adjacency= new int[resultlen];
	// int loopnumber=0;
	// for(Object number:result)
	// {
	// adjacency[loopnumber++]=(int)number;
	// }
	// return adjacency;
	// }

	public void writeFile(String filePathAndName, double[][] input) {
		try {
			File f = new File(filePathAndName);
			if (!f.exists()) {
				f.createNewFile();
			}
			OutputStreamWriter write = new OutputStreamWriter(
					new FileOutputStream(f), "GB2312");
			BufferedWriter writer = new BufferedWriter(write);

			int nodenumber = input.length;
			// for(int i=0;i<nodenumber;i++){
			// int len=input[i].length;
			for (int j = 0; j < nodenumber; j++) {

				writer.write(input[0][j] + "\t" + input[1][j]);
				// System.out.println(i+" "+usercollection.get(i).getName());
				writer.append("\n");

				// output.append("\n");
			}
			writer.flush();
			writer.close();

			// writer.write(fileContent);
			// writer.close();

		} catch (Exception e) {
			System.out.println("写文件内容操作出错");
			e.printStackTrace();
		}
	}

	public void writeFile(String filePathAndName, int[][] input) {
		try {
			File f = new File(filePathAndName);
			if (!f.exists()) {
				f.createNewFile();
			}
			OutputStreamWriter write = new OutputStreamWriter(
					new FileOutputStream(f), "GB2312");
			BufferedWriter writer = new BufferedWriter(write);

			int nodenumber = input.length;
			for (int i = 0; i < nodenumber; i++) {
				int len = input[i].length;
				for (int j = 0; j < nodenumber; j++) {

					writer.write(input[i][j]);
					// System.out.println(i+" "+usercollection.get(i).getName());
					writer.append("\n");

					// output.append("\n");
				}
			}
			writer.flush();
			writer.close();

			// writer.write(fileContent);
			// writer.close();

		} catch (Exception e) {
			System.out.println("写文件内容操作出错");
			e.printStackTrace();
		}
	}

	public void writeFile(String filePathAndName, int[] input) {
		try {
			File f = new File(filePathAndName);
			if (!f.exists()) {
				f.createNewFile();
			}
			OutputStreamWriter write = new OutputStreamWriter(
					new FileOutputStream(f), "GB2312");
			BufferedWriter writer = new BufferedWriter(write);

			int nodenumber = input.length;
			// for(int i=0;i<nodenumber;i++){
			// int len=input[i].length;
			for (int j = 0; j < nodenumber; j++) {

				writer.write(input[j]);
				// System.out.println(i+" "+usercollection.get(i).getName());
				writer.append("\n");

				// output.append("\n");
			}
			writer.flush();
			writer.close();

			// writer.write(fileContent);
			// writer.close();

		} catch (Exception e) {
			System.out.println("写文件内容操作出错");
			e.printStackTrace();
		}
	}

	// public double[][] PositionComputeProcess(int[] adjacency)
	public double[][] PositionComputeProcess(int[][] input) {
		// writeFile("/home/weitao/matlab/budian/input.txt", input);
		int[] adjacency = Convert.RelationToAdjancy(input);

		// writeFile("/home/weitao/matlab/budian/adjace.txt", adjacency);
		int nodenumber = input.length;
		double[][] result = new double[2][];
		for (int i = 0; i < 2; i++) {
			result[i] = new double[nodenumber];
		}
		// int nodenumber=this.relationMatrix.getSize();
		// this.script(adjacency,nodenumber);
		try {
			result = this.script(adjacency, nodenumber);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print("计算完毕！");
//		for (int i = 0; i < nodenumber;) {
//			System.out.format("(%f,%f)", result[0][i], result[1][i]);
//			i++;
//			if (i % 10 == 0)
//				System.out.print("\n");
//		}
		return result;

		// TODO Auto-generated method stub
		// YifanAlgoDemo yifanAlgoDemo = new YifanAlgoDemo();
		// int nodeNum = 228;
		// int[] adjacency =
		// {8,2,12,7,14,0,16,12,17,12,19,12,22,0,23,0,24,0,27,0,28,0,29,11,29,21,30,3,30,5,30,9,34,30,36,29,38,18,39,30,45,0,46,0,47,0,48,2,49,30,51,26,51,43,57,12,58,51,60,30,62,31,62,32,63,0,64,30,65,12,67,62,68,62,70,0,71,0,72,62,73,0,74,0,76,0,77,30,78,62,82,62,83,62,88,79,91,2,92,75,93,88,95,69,96,56,101,30,103,42,103,44,107,62,109,104,109,105,110,40,110,80,110,84,115,110,119,109,121,12,122,30,124,75,125,75,126,75,127,51,128,2,129,62,131,109,132,0,134,99,135,62,138,41,139,98,140,0,141,62,142,62,143,62,144,62,145,0,146,62,147,134,148,0,149,30,150,51,151,59,152,134,153,51,154,0,156,30,157,98,158,30,159,103,162,62,164,130,166,87,166,163,167,151,168,30,169,164,170,0,172,166,173,30,174,75,175,18,176,85,177,54,179,164,180,117,181,180,182,138,183,85,184,164,185,81,186,54,187,94,188,187,189,136,189,137,190,189,191,118,192,166,193,30,194,30,195,165,197,0,198,187,199,69,200,189,201,30,204,195,206,166,207,96,208,166,209,30,210,134,212,30,213,134,214,166,215,166,217,96,218,30,219,2,221,166,222,191,224,189,225,53,225,202,226,225,227,0,227,2,227,12,227,18,227,29,227,30,227,51,227,54,227,62,227,69,227,75,227,81,227,85,227,88,227,96,227,98,227,103,227,109,227,110,227,134,227,138,227,151,227,164,227,166,227,180,227,187,227,189,227,191,227,195,227,225};
		// double[][] coor = yifanAlgoDemo.script(adjacency, nodeNum);
		// for (int i = 0; i < nodeNum; i++) {
		// System.out.print(coor[0][i] + ", " + coor[1][i] + "; ");
		// }
		// System.out.println();

	}
}
