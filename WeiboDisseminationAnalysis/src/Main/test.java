package Main;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Map<String, Integer> map = new HashMap<String, Integer>();
//		int[][] edges = new int[10][10];
		List<Integer> list = new LinkedList<Integer>();
		int count = 0;
		for (int i = 0; i < 20; i++) {
			map.put(i + "", i);

		}
		for (int i : map.values()) {
			System.out.println(i);
			list.add(i);
		}
		System.out.println("");
		// map.put(2342+"", 21312);

		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}

		int[] adjacency = { 8, 2, 12, 7, 14, 0, 16, 12, 17, 12, 19, 12, 22, 0,
				23, 0, 24, 0, 27, 0, 28, 0, 29, 11, 29, 21, 30, 3, 30, 5, 30,
				9, 34, 30, 36, 29, 38, 18, 39, 30, 45, 0, 46, 0, 47, 0, 48, 2,
				49, 30, 51, 26, 51, 43, 57, 12, 58, 51, 60, 30, 62, 31, 62, 32,
				63, 0, 64, 30, 65, 12, 67, 62, 68, 62, 70, 0, 71, 0, 72, 62,
				73, 0, 74, 0, 76, 0, 77, 30, 78, 62, 82, 62, 83, 62, 88, 79,
				91, 2, 92, 75, 93, 88, 95, 69, 96, 56, 101, 30, 103, 42, 103,
				44, 107, 62, 109, 104, 109, 105, 110, 40, 110, 80, 110, 84,
				115, 110, 119, 109, 121, 12, 122, 30, 124, 75, 125, 75, 126,
				75, 127, 51, 128, 2, 129, 62, 131, 109, 132, 0, 134, 99, 135,
				62, 138, 41, 139, 98, 140, 0, 141, 62, 142, 62, 143, 62, 144,
				62, 145, 0, 146, 62, 147, 134, 148, 0, 149, 30, 150, 51, 151,
				59, 152, 134, 153, 51, 154, 0, 156, 30, 157, 98, 158, 30, 159,
				103, 162, 62, 164, 130, 166, 87, 166, 163, 167, 151, 168, 30,
				169, 164, 170, 0, 172, 166, 173, 30, 174, 75, 175, 18, 176, 85,
				177, 54, 179, 164, 180, 117, 181, 180, 182, 138, 183, 85, 184,
				164, 185, 81, 186, 54, 187, 94, 188, 187, 189, 136, 189, 137,
				190, 189, 191, 118, 192, 166, 193, 30, 194, 30, 195, 165, 197,
				0, 198, 187, 199, 69, 200, 189, 201, 30, 204, 195, 206, 166,
				207, 96, 208, 166, 209, 30, 210, 134, 212, 30, 213, 134, 214,
				166, 215, 166, 217, 96, 218, 30, 219, 2, 221, 166, 222, 191,
				224, 189, 225, 53, 225, 202, 226, 225, 227, 0, 227, 2, 227, 12,
				227, 18, 227, 29, 227, 30, 227, 51, 227, 54, 227, 62, 227, 69,
				227, 75, 227, 81, 227, 85, 227, 88, 227, 96, 227, 98, 227, 103,
				227, 109, 227, 110, 227, 134, 227, 138, 227, 151, 227, 164,
				227, 166, 227, 180, 227, 187, 227, 189, 227, 191, 227, 195,
				227, 225 };
		int ttt = 0;
		for (int i = 0; i < adjacency.length; i++) {
			for (int j = 0; j < adjacency.length; j++) {
				if (i == adjacency[j])
					ttt++;
			}
		}
		System.out.println(adjacency.length+"==========="+ttt);

	}
}
